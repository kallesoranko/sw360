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

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import org.apache.http.HttpException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.sw360.bdhubimport.domain.*;
import org.eclipse.sw360.datahandler.thrift.projectimport.BDHubCredentials;
import org.eclipse.sw360.datahandler.thrift.projectimport.TokenCredentials;
import org.eclipse.sw360.wsimport.domain.*;
import org.eclipse.sw360.wsimport.utility.WsTokenType;

import java.io.IOException;
import java.util.Arrays;

import static org.eclipse.sw360.wsimport.utility.TranslationConstants.GET_PROJECT_VITALS;
import static org.eclipse.sw360.wsimport.utility.TranslationConstants.GET_PROJECT_LICENSES;
import static org.eclipse.sw360.wsimport.utility.TranslationConstants.GET_ORGANIZATION_PROJECT_VITALS;

/**
 * @author ksoranko@verifa.io
 */
public class BDHubImportService {

    private static final Logger LOGGER = LogManager.getLogger(BDHubImportService.class);
    private static final BDHubRestClient restClient = new BDHubRestClient();
    private static final Gson gson = new Gson();

    public BDHubProjectVersion getBDHubProjectVersion(String projectVersionHref, BDHubCredentials credentials) throws JsonSyntaxException {
        String responseString;
        BDHubProjectVersionsResponse response = null;

        String[] arrOfStr = projectVersionHref.split("/", -1);
        String[] newArray = Arrays.copyOfRange(arrOfStr, 0, arrOfStr.length-1);
        String projectVersionLink = String.join("/", newArray);

        try {
            responseString = restClient.getData(projectVersionLink,
                    "application/vnd.blackducksoftware.project-detail-5+json",
                    "application/vnd.blackducksoftware.project-detail-5+json",
                    credentials.bearerToken);
        } catch (IOException | HttpException e) {
            LOGGER.error("Exception with REST request to " + projectVersionLink + ": " + e);
            return null;
        }

        try {
            response = gson.fromJson(responseString, BDHubProjectVersionsResponse.class);
        } catch (JsonSyntaxException e) {
            LOGGER.error("Exception with reading BDHubProjectVersion JSON to object: " + e);
        }

        for (BDHubProjectVersion projectVersion : response.getItems()) {
            if (projectVersion.get_meta().getHref().equals(projectVersionHref)) {
                return projectVersion;
            }
        }
        LOGGER.warn("BDHubProjectVersion with self link " + projectVersionHref + " not found");

        return null;
    }

    public BDHubComponent[] getProjectVersionComponents(String projectVersionComponentsLink, BDHubCredentials credentials) {
        String responseString = null;
        BDHubComponentsResponse response = null;

        try {
            responseString = restClient.getData(projectVersionComponentsLink,
                    "application/vnd.blackducksoftware.bill-of-materials-6+json",
                    "application/json",
                    credentials.bearerToken);
        } catch (IOException | HttpException e) {
            LOGGER.error("Exception with REST request to " + projectVersionComponentsLink + ": " + e);
            return null;
        }

        try {
            response = gson.fromJson(responseString, BDHubComponentsResponse.class);
        } catch (JsonSyntaxException e) {
            LOGGER.error("Exception with reading BDHubProjectVersion JSON to object: " + e);
        }


        return response.getItems();
    }

    public BDHubProject getProjectWithProjectVersion(BDHubProjectVersion projectVersion, BDHubCredentials credentials) {
        String projectLink = null;
        for (BDHubLink link : projectVersion.get_meta().getLinks()) {
            if (link.getRel().equals("project")) {
                projectLink = link.getHref();
            }
        }

        String responseString = null;
        BDHubProject response = null;

        try {
            responseString = restClient.getData(projectLink,
                    "application/vnd.blackducksoftware.project-detail-5+json",
                    "application/vnd.blackducksoftware.project-detail-5+json",
                    credentials.bearerToken);
        } catch (IOException | HttpException e) {
            LOGGER.error("Exception with REST request to " + projectLink + ": " + e);
            return null;
        }

        try {
            response = gson.fromJson(responseString, BDHubProject.class);
        } catch (JsonSyntaxException e) {
            LOGGER.error("Exception with reading BDHubProjectVersion JSON to object: " + e);
        }
        return response;
    }
}
