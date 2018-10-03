/*
 * Copyright Siemens AG, 2013-2018. Part of the SW360 Portal Project.
 *
 * SPDX-License-Identifier: EPL-1.0
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.sw360.portal.portlets.homepage;

import org.apache.log4j.Logger;
import org.apache.thrift.TException;
import org.eclipse.sw360.datahandler.common.CommonUtils;
import org.eclipse.sw360.datahandler.thrift.ThriftClients;
import org.eclipse.sw360.datahandler.thrift.components.ReleaseClearingStatusData;
import org.eclipse.sw360.datahandler.thrift.projects.Project;
import org.eclipse.sw360.datahandler.thrift.projects.ProjectService;
import org.eclipse.sw360.datahandler.thrift.users.User;
import org.eclipse.sw360.portal.portlets.Sw360Portlet;
import org.eclipse.sw360.portal.users.LifeRayUserSession;

import javax.portlet.*;
import java.io.IOException;
import java.util.List;
import java.util.Optional;


/**
 * @author ksoranko@verifa.io
 */
public class LicenseDebtPortlet extends Sw360Portlet {

    private static final Logger LOGGER = Logger.getLogger(LicenseDebtPortlet.class);
    private static ProjectService.Iface projectClient = new ThriftClients().makeProjectClient();
    private static Optional<Project> first = Optional.empty();

    @Override
    public void doView(RenderRequest request, RenderResponse response) throws IOException, PortletException {
        /*
        List<ReleaseClearingStatusData> clearingStatusData = null;
        try {
            String email = LifeRayUserSession.getEmailFromRequest(request);
            User user = thriftClients.makeUserClient().getByEmail(email);
            List<Project> projects = projectClient.getMyProjects(email);

            first = projects.stream().findFirst();
            clearingStatusData = projectClient.getReleaseClearingStatuses(getFirstOwnProject().id, user);
        } catch (TException e) {
            LOGGER.error("Could not fetch your projects from backend", e);
        }

        request.setAttribute("clearingStatusData",  CommonUtils.nullToEmptyList(clearingStatusData));

        super.doView(request, response);
        */

        List<Project> projects=null;

        try {
            String email = LifeRayUserSession.getEmailFromRequest(request);
            projects = thriftClients.makeProjectClient().getMyProjects(email);
        } catch (TException e) {
            LOGGER.error("Could not fetch your projects from backend", e);
        }

        request.setAttribute("projects",  CommonUtils.nullToEmptyList(projects));

        super.doView(request, response);
    }

    private Project getFirstOwnProject() {
        return first.orElse(null);
    }

}
