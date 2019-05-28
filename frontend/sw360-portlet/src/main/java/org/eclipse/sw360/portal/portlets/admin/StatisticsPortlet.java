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

import com.google.common.collect.Sets;
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
import org.eclipse.sw360.portal.portlets.Sw360Portlet;
import org.eclipse.sw360.portal.users.UserCacheHolder;

import javax.portlet.*;
import java.io.IOException;
import java.util.*;

import static org.eclipse.sw360.portal.common.PortalConstants.RESPONSE__PROJECT_DETAILS_DATA;

/**
 * @author ksoranko@verifa.io
 */
public class StatisticsPortlet extends Sw360Portlet {

    private static final Logger LOGGER = Logger.getLogger(StatisticsPortlet.class);

    @Override
    public void doView(RenderRequest request, RenderResponse response) throws IOException, PortletException {
        List<Project> myProjects = new ArrayList<>();
        User user = UserCacheHolder.getUserFromRequest(request);
        try {
            myProjects = thriftClients.makeProjectClient().getMyProjects(user.getEmail());
        } catch (TException e) {
            LOGGER.error("Could not fetch myProjects from backend for user, " + user.getEmail(), e);
        }
        myProjects = getWithFilledClearingStateSummary(myProjects, user);
        request.setAttribute("projects",  CommonUtils.nullToEmptyList(myProjects));
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
        JSONObject responseData = handleProjectDetailsUpdate(request);
        response.getWriter().write(responseData.toString());
    }

    private JSONObject handleProjectDetailsUpdate(ResourceRequest request) {
        String[] ids = request.getParameterValues("ids[]");
        Set<String> idSet = Sets.newHashSet(ids);
        User user = UserCacheHolder.getUserFromRequest(request);
        JSONObject responseData = JSONFactoryUtil.createJSONObject();
        JSONArray jsonProjectDetailsData = JSONFactoryUtil.createJSONArray();
        List<Release> releaseList = null;
        ComponentService.Iface componentClient = thriftClients.makeComponentClient();
        try {
            releaseList = componentClient.getReleasesById(idSet, user);
        } catch (TException e) {
            LOGGER.error("Could not get releases from backend!", e);
        }
        if(releaseList != null) {
            for (Release release : releaseList) {
                JSONObject releaseData = JSONFactoryUtil.createJSONObject();
                releaseData
                        .put("id", release.getId())
                        .put("name", release.getName())
                        .put("clearingState", release.getClearingState().toString());
                jsonProjectDetailsData.put(releaseData);
            }
        }
        responseData.put(RESPONSE__PROJECT_DETAILS_DATA, jsonProjectDetailsData);
        return responseData;
    }

}


