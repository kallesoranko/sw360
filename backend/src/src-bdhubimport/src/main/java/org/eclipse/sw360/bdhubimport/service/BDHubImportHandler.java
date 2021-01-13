/*
 * Copyright (c) Verifa Oy, 2018.
 * Part of the SW360 Portal Project.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.sw360.bdhubimport.service;

import com.google.gson.JsonSyntaxException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.sw360.bdhubimport.domain.BDHubComponent;
import org.eclipse.sw360.bdhubimport.domain.BDHubProjectVersion;
import org.eclipse.sw360.bdhubimport.rest.BDHubImportService;
import org.eclipse.sw360.bdhubimport.utility.TranslationConstants;
import org.eclipse.sw360.datahandler.thrift.projectimport.BDHubCredentials;
import org.eclipse.sw360.bdhubimport.thrift.ThriftUploader;
import org.eclipse.sw360.bdhubimport.utility.TranslationConstants;
import org.apache.thrift.TException;
import org.eclipse.sw360.datahandler.thrift.importstatus.ImportStatus;
import org.eclipse.sw360.datahandler.thrift.projectimport.ProjectImportService;
import org.eclipse.sw360.datahandler.thrift.projectimport.RemoteCredentials;
import org.eclipse.sw360.datahandler.thrift.projectimport.TokenCredentials;
import org.eclipse.sw360.datahandler.thrift.projects.Project;
import org.eclipse.sw360.datahandler.thrift.users.User;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author ksoranko@verifa.io
 */
public class BDHubImportHandler implements ProjectImportService.Iface {

    private static final Logger LOGGER = LogManager.getLogger(BDHubImportHandler.class);

    @Override
    public synchronized ImportStatus importData(List<String> projectVersionHrefs, User user, BDHubCredentials credentials) throws TException, JsonSyntaxException {
        List<BDHubProjectVersion> toImport = projectVersionHrefs
                .stream()
                .map(href -> new BDHubImportService().getBDHubProjectVersion(href, credentials))
                .collect(Collectors.toList());

        return new ThriftUploader().importBDHubProjectVersions(toImport, user, credentials);
    }

    @Override
    public String getIdName(){
        return TranslationConstants.BDHUB_ID;
    }

    @Override
    public ImportStatus importBDHubData(List<String> projectHrefs, User user, BDHubCredentials credentials) throws TException {
        return null;
    }

    @Override
    public boolean validateCredentials(RemoteCredentials credentials) { return false; }
    @Override
    public List<Project> loadImportables(RemoteCredentials reCred) { return null; }
    @Override
    public List<Project> suggestImportables(RemoteCredentials reCred, String projectName) { return null; }
    @Override
    public ImportStatus importDatasources(List<String> projectIds, User user, RemoteCredentials reCred) { return null; }

}
