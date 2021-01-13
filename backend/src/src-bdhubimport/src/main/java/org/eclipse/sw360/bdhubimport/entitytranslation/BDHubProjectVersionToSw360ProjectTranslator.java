/*
 * Copyright (c) Verifa Oy, 2018. Part of the SW360 Project.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.sw360.bdhubimport.entitytranslation;

import org.eclipse.sw360.bdhubimport.domain.BDHubProjectVersion;

import java.util.HashMap;

import static org.eclipse.sw360.bdhubimport.utility.TranslationConstants.*;

/**
 * @author ksoranko@verifa.io
 */
public class BDHubProjectVersionToSw360ProjectTranslator implements EntityTranslator<BDHubProjectVersion, org.eclipse.sw360.datahandler.thrift.projects.Project>{

    @Override
    public org.eclipse.sw360.datahandler.thrift.projects.Project apply(BDHubProjectVersion project) {

        org.eclipse.sw360.datahandler.thrift.projects.Project sw360Project = new org.eclipse.sw360.datahandler.thrift.projects.Project();

        sw360Project.setExternalIds(new HashMap<>());
        sw360Project.getExternalIds().put(BDHUB_ID, Integer.toString(project.getId()));
        sw360Project.setDescription(project.getProjectToken());
        sw360Project.setName(project.getProjectName());
        sw360Project.setCreatedOn(project.getCreationDate());
        sw360Project.setDescription(IMPORTED_FROM_BDHUB);

        return sw360Project;
    }

}
