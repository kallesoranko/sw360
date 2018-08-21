/*
 * Copyright Siemens AG, 2018. Part of the SW360 Portal Project.
 *
 * SPDX-License-Identifier: EPL-1.0
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.eclipse.sw360.portal.rest;

import com.liferay.portal.kernel.json.JSONFactoryUtil;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.log4j.Logger;
import org.eclipse.sw360.datahandler.thrift.RestApiToken;
import org.eclipse.sw360.datahandler.thrift.users.User;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.stream.Collectors;

import static org.eclipse.sw360.portal.common.PortalConstants.AUTHORIZATION_REST_API_TOKEN_URL;

public class RestTokenGenerator {

    private static final String CONTENT_TYPE_JSON = "application/json";
    private static final Logger LOGGER = Logger.getLogger(RestTokenGenerator.class);

    public static RestApiToken generateToken(User user) throws IOException {
        DefaultHttpClient client = new DefaultHttpClient();
        RestApiToken restApiToken = null;

        HttpResponse response = client.execute(preparePost(user));
        if (response.getStatusLine().getStatusCode() == 201) {
            restApiToken = parseResponse(response);
        } else {
            LOGGER.error("Generate token failed by http response code " + response.getStatusLine().getStatusCode());
        }

        client.getConnectionManager().shutdown();
        return restApiToken;
    }

    private static HttpPost preparePost(User user) throws UnsupportedEncodingException {
        HttpPost post = new HttpPost(AUTHORIZATION_REST_API_TOKEN_URL);
        String userJsonString = JSONFactoryUtil.looseSerialize(user);
        StringEntity postBody = new StringEntity(userJsonString);
        postBody.setContentType(CONTENT_TYPE_JSON);
        post.setEntity(postBody);
        return post;
    }

    private static RestApiToken parseResponse(HttpResponse response) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader((response.getEntity().getContent())));
        String tokenJsonString = br.lines().collect(Collectors.joining());
        return JSONFactoryUtil.looseDeserialize(tokenJsonString, RestApiToken.class);
    }
}
