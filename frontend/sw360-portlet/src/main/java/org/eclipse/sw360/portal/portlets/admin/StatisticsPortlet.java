/*
 * Copyright Siemens Healthcare Diagnostics Inc, 2018.
 * Part of the SW360 Portal Project.
 *
 * SPDX-License-Identifier: EPL-1.0
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.sw360.portal.portlets.admin;

import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import org.apache.log4j.Logger;
import org.apache.thrift.TException;
import org.eclipse.sw360.datahandler.common.CommonUtils;
import org.eclipse.sw360.datahandler.thrift.components.*;
import org.eclipse.sw360.datahandler.thrift.projects.Project;
import org.eclipse.sw360.datahandler.thrift.projects.ProjectService;
import org.eclipse.sw360.datahandler.thrift.users.User;
import org.eclipse.sw360.portal.common.PortalConstants;
import org.eclipse.sw360.portal.portlets.Sw360Portlet;
import org.eclipse.sw360.portal.users.UserCacheHolder;

import javax.portlet.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

import static org.eclipse.sw360.portal.common.PortalConstants.RESPONSE__PROJECT_CLEARING_STATUS_DATA;
/**
 * @author ksoranko@verifa.io
 */
public class StatisticsPortlet extends Sw360Portlet {

    private static final Logger LOGGER = Logger.getLogger(StatisticsPortlet.class);

    @Override
    public void doView(RenderRequest request, RenderResponse response) throws IOException, PortletException {
        List<Project> myProjects = new ArrayList<>();
        List<String> ids = new ArrayList<>();
        User user = UserCacheHolder.getUserFromRequest(request);
        List<Project> projects;
        try {
            myProjects = thriftClients.makeProjectClient().getMyProjects(user.getEmail());
        } catch (TException e) {
            LOGGER.error("Could not fetch myProjects from backend for user, " + user.getEmail(), e);
        }
        if(!myProjects.isEmpty()) {
            myProjects.forEach(p -> ids.add(p.getId()));
        }
        try {
            projects = thriftClients.makeProjectClient().getProjectsById(ids, user);
        } catch (TException e) {
            LOGGER.error("Could not fetch projects from backend", e);
            projects = Collections.emptyList();
        }
        projects = getWithFilledClearingStateSummary(projects, user);
        request.setAttribute("projects",  CommonUtils.nullToEmptyList(projects));
        super.doView(request, response);
    }

    private List<Project> getWithFilledClearingStateSummary(List<Project> projects, User user) {
        ProjectService.Iface projectClient = thriftClients.makeProjectClient();
        try {
            return projectClient.fillClearingStateSummary(projects, user);
        } catch (TException e) {
            LOGGER.error("Could not get summary of release clearing states for projects and their subprojects!", e);
            return projects;
        }
    }

    @Override
    public void serveResource(ResourceRequest request, ResourceResponse response) throws IOException, PortletException {
        JSONObject responseData = handlePieChartUpdate(request);
        PrintWriter writer = response.getWriter();
        writer.write(responseData.toString());
    }

    private JSONObject handlePieChartUpdate(ResourceRequest request) throws IOException, PortletException {
        String[] projectId = request.getParameterValues(PortalConstants.PROJECT_ID);
        User user = UserCacheHolder.getUserFromRequest(request);
        JSONObject responseData = JSONFactoryUtil.createJSONObject();
        JSONArray jsonClearingStatusData = JSONFactoryUtil.createJSONArray();
        if(projectId != null && projectId.length == 1) {
            List<ClearingState> clearingInformationList = getClearingStatusDataForProject(projectId[0], user);
            clearingInformationList.forEach( e -> jsonClearingStatusData.put(e.toString()));
        }
        responseData.put(RESPONSE__PROJECT_CLEARING_STATUS_DATA, jsonClearingStatusData);
        return responseData;
    }

    private List<ClearingState> getClearingStatusDataForProject(String projectId, User user) {
        ProjectService.Iface projectClient = thriftClients.makeProjectClient();
        List<ReleaseClearingStatusData> data = null;
        try {
            data = projectClient.getReleaseClearingStatuses(projectId, user);
        } catch (TException e) {
            LOGGER.error("Could not fetch project releases' clearing status data from backend", e);
        }
        List<ClearingState> clearingStates = new ArrayList<>();
        if (data != null) {
            for (ReleaseClearingStatusData e : data) {
                Release rel = e.getRelease();
                ClearingState clearingState = rel.getClearingState();
                clearingStates.add(clearingState);
            }
        }
        return clearingStates;
    }


}