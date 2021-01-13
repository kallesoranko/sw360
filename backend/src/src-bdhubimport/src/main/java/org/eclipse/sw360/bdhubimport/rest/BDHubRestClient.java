/*
 * Copyright (c) Verifa Oy, 2018-2019. Part of the SW360 Project.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.sw360.bdhubimport.rest;


import org.apache.http.HttpException;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.eclipse.sw360.datahandler.thrift.projectimport.BDHubCredentials;
import org.eclipse.sw360.datahandler.thrift.projectimport.TokenCredentials;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import java.io.IOException;

/**
 * @author: ksoranko@verifa.io
 */
public class BDHubRestClient {

    private static final Logger LOGGER = LogManager.getLogger(BDHubRestClient.class);

    BDHubRestClient() {
    }

    private HttpClient getConfiguredHttpClient() {
        return HttpClientBuilder
                .create()
                .build();
    }

    String getData(String requestURL, String acceptsHeader, String contentTypeHeader, String bearerToken) throws IOException, HttpException {
        LOGGER.info("Making REST request to " + requestURL;
        HttpClient httpClient = getConfiguredHttpClient();

        HttpUriRequest request = RequestBuilder.get()
                .setUri(requestURL)
                .setHeader(HttpHeaders.CONTENT_TYPE, contentTypeHeader)
                .setHeader(HttpHeaders.ACCEPT, acceptsHeader)
                .setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + bearerToken)
                .build();
        HttpResponse response = httpClient.execute(request);

        // int statusCode = response.getStatusLine().getStatusCode();

        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            return IOUtils.toString(response.getEntity().getContent(), "UTF-8");
        } else {
            LOGGER.info("Request unsuccessful: " + response.getStatusLine().getReasonPhrase());
            throw new HttpException("Response code from Whitesource not OK");
        }
    }
}
