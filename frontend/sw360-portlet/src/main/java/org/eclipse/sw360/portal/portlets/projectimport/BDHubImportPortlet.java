/*
 * Copyright (c) xyz, 2021.
 * Part of the SW360 Portal Project.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.sw360.portal.portlets.projectimport;

import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.thrift.TException;
import org.eclipse.sw360.datahandler.thrift.RequestStatus;
import org.eclipse.sw360.datahandler.thrift.ThriftClients;
import org.eclipse.sw360.datahandler.thrift.importstatus.ImportStatus;
import org.eclipse.sw360.datahandler.thrift.projectimport.ProjectImportService;
import org.eclipse.sw360.datahandler.thrift.projectimport.TokenCredentials;
import org.eclipse.sw360.datahandler.thrift.users.User;
import org.eclipse.sw360.portal.common.PortalConstants;
import org.eclipse.sw360.portal.portlets.Sw360Portlet;
import org.eclipse.sw360.portal.users.UserCacheHolder;
import org.osgi.service.component.annotations.ConfigurationPolicy;

import javax.portlet.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

import static com.google.common.base.Strings.nullToEmpty;

@org.osgi.service.component.annotations.Component(
        immediate = true,
        properties = {
                "/org/eclipse/sw360/portal/portlets/base.properties",
                "/org/eclipse/sw360/portal/portlets/default.properties"
        },
        property = {
                "javax.portlet.name=" + PortalConstants.PROJECT_BDHUBIMPORT_PORTLET_NAME,

                "javax.portlet.display-name=BlackDuck Hub Import",
                "javax.portlet.info.short-title=BlackDuck Hub Import",
                "javax.portlet.info.title=BlackDuck Hub Import",
                "javax.portlet.resource-bundle=content.Language",
                "javax.portlet.init-param.view-template=/html/projects/bdhubimport.jsp",
        },
        service = Portlet.class,
        configurationPolicy = ConfigurationPolicy.REQUIRE
)

public class BDHubImportPortlet extends Sw360Portlet {

        private static final Logger LOGGER = LogManager.getLogger(BDHubImportPortlet.class);
        private static ProjectImportService.Iface projectImportClient = new ThriftClients().makeBDHubImportClient();

        @Override
        public void doView(RenderRequest request, RenderResponse response) throws IOException, PortletException {
                super.doView(request, response);
        }

        private BDHubCredentials getBDHubCredentialsFromSession(PortletSession session) {
                String apiToken = (String) session.getAttribute(ProjectImportConstants.API_TOKEN);
                String server = (String) session.getAttribute(ProjectImportConstants.SERVER_URL);
                String bearerToken = (String) session.getAttribute(ProjectImportConstants.BEARER_TOKEN);
                return new BDHubCredentials()
                        .setApiToken(apiToken)
                        .setServerUrl(server)
                        .setBearerToken(bearerToken);
        }

        private void putBDHubCredentialsIntoSession(PortletSession session, BDHubCredentials credentials) {
                session.setAttribute(ProjectImportConstants.API_TOKEN, nullToEmpty(credentials.getApiToken()));
                session.setAttribute(ProjectImportConstants.SERVER_URL, nullToEmpty(credentials.getServerUrl()));
                session.setAttribute(ProjectImportConstants.BEARER_TOKEN, nullToEmpty(credentials.getBearerToken()));
        }

        private List<String> getProjectVersionHrefsForImport(PortletRequest request) throws IOException {
                String[] checked = request.getParameterValues("checked[]");
                List<String> checkedHrefs = Arrays.asList(checked);

                return checkedHrefs;
        }

        private boolean isImportSuccessful(ImportStatus importStatus) {
                return (importStatus.isSetRequestStatus() && importStatus.getRequestStatus().equals(RequestStatus.SUCCESS) && importStatus.getFailedIds().isEmpty());
        }



        @Override
        public void serveResource(ResourceRequest request, ResourceResponse response) throws IOException, PortletException {
                String requestedAction = request.getParameter(ProjectImportConstants.USER_ACTION__IMPORT);
                JSONObject responseData = handleRequestedAjaxAction(requestedAction, request);
                PrintWriter writer = response.getWriter();
                writer.write(responseData.toString());
        }

        public JSONObject handleRequestedAjaxAction(String requestedAction, ResourceRequest request) throws IOException, PortletException {
                PortletSession session = request.getPortletSession();
                BDHubCredentials credentials = getBDHubCredentialsFromSession(session);
                JSONObject responseData = JSONFactoryUtil.createJSONObject();

                switch (requestedAction) {
                        case ProjectImportConstants.USER_ACTION__IMPORT_DATA:
                                User user = UserCacheHolder.getUserFromRequest(request);
                                List<String> selectedHrefs = getProjectVersionHrefsForImport(request);
                                importBDHubProjectVersions(user, selectedHrefs, responseData, credentials);
                                break;
                        case ProjectImportConstants.USER_ACTION__NEW_IMPORT_SOURCE:
                                BDHubCredentials newCredentials = new BDHubCredentials()
                                        .setApiToken(request.getParameter(ProjectImportConstants.API_TOKEN))
                                        .setServerUrl(request.getParameter(ProjectImportConstants.SERVER_URL))
                                        .setBearerToken(request.getParameter(ProjectImportConstants.BEARER_TOKEN));
                                String serverUrl = nullToEmpty(newCredentials.getServerUrl());
                                if (serverUrl.isEmpty()) {
                                        responseData.put(ProjectImportConstants.RESPONSE__STATUS,
                                                ProjectImportConstants.RESPONSE__DB_URL_NOT_SET);
                                } else {
                                        putTokenCredentialsIntoSession(session, newCredentials);
                                        responseData.put(ProjectImportConstants.RESPONSE__STATUS,
                                                ProjectImportConstants.RESPONSE__DB_CHANGED);
                                        responseData.put(ProjectImportConstants.RESPONSE__DB_URL, serverUrl);
                                }
                                break;
                        default:
                                break;
                }
                return responseData;
        }

        private void importBDHubProjectVersions(User user, List<String> selectedHrefs, JSONObject responseData, BDHubCredentials tokenCredentials) throws PortletException, IOException {
                ImportStatus importStatus = importBDHubProjectVersionsData(selectedHrefs, user, tokenCredentials);
                JSONObject jsonFailedIds = JSONFactoryUtil.createJSONObject();
                JSONArray jsonSuccessfulIds = JSONFactoryUtil.createJSONArray();

                if (importStatus.isSetRequestStatus() && importStatus.getRequestStatus().equals(RequestStatus.SUCCESS)) {
                        importStatus.getFailedIds().forEach(jsonFailedIds::put);
                        importStatus.getSuccessfulIds().forEach(jsonSuccessfulIds::put);

                        responseData.put(ProjectImportConstants.RESPONSE__FAILED_IDS, jsonFailedIds);
                        responseData.put(ProjectImportConstants.RESPONSE__SUCCESSFUL_IDS, jsonSuccessfulIds);
                }
                if (isImportSuccessful(importStatus)) {
                        responseData.put(ProjectImportConstants.RESPONSE__STATUS, ProjectImportConstants.RESPONSE__SUCCESS);
                } else if (importStatus.isSetRequestStatus() && importStatus.getRequestStatus().equals(RequestStatus.SUCCESS)) {
                        responseData.put(ProjectImportConstants.RESPONSE__STATUS, ProjectImportConstants.RESPONSE__FAILURE);
                } else {
                        responseData.put(ProjectImportConstants.RESPONSE__STATUS, ProjectImportConstants.RESPONSE__GENERAL_FAILURE);
                }
        }

        private ImportStatus importBDHubProjectVersionsData(List<String> toImportHrefs, User user, BDHubCredentials credentials) {
                ImportStatus importStatus = new ImportStatus();
                try {
                        importStatus = projectImportClient.importBDHubData(toImportHrefs, user, credentials);
                        if (!isImportSuccessful(importStatus)) {
                                if (importStatus.getRequestStatus().equals(RequestStatus.FAILURE)) {
                                        LOGGER.error("Importing of data sources failed.");
                                } else {
                                        LOGGER.error("Importing has not succeeded for the following IDs: " + importStatus.getFailedIds().toString());
                                }
                        }
                } catch (TException e) {
                        LOGGER.error("ImportDatasources failed", e);
                }
                return importStatus;
        }


}
