/*
 * Copyright (c) Bosch Software Innovations GmbH 2017.
 * With modifications by Verifa Oy, 2018-2019.
 * Part of the SW360 Portal Project.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.sw360.bdhubimport.thrift;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonSyntaxException;
import org.eclipse.sw360.bdhubimport.domain.BDHubComponent;
import org.eclipse.sw360.bdhubimport.domain.BDHubComponentLicense;
import org.eclipse.sw360.bdhubimport.domain.BDHubLicense;
import org.eclipse.sw360.bdhubimport.domain.BDHubProject;
import org.eclipse.sw360.bdhubimport.domain.BDHubProjectVersion;
import org.eclipse.sw360.bdhubimport.entitytranslation.BDHubComponentLicenseToSw360LicenseTranslator;
import org.eclipse.sw360.bdhubimport.entitytranslation.BDHubComponentToSw360ComponentTranslator;
import org.eclipse.sw360.bdhubimport.entitytranslation.BDHubComponentToSw360ReleaseTranslator;
import org.eclipse.sw360.bdhubimport.entitytranslation.BDHubLicenseToSw360LicenseTranslator;
import org.eclipse.sw360.bdhubimport.entitytranslation.BDHubProjectVersionToSw360ProjectTranslator;
import org.eclipse.sw360.bdhubimport.rest.BDHubImportService;
import org.eclipse.sw360.bdhubimport.thrift.helper.ProjectImportError;
import org.eclipse.sw360.datahandler.thrift.ProjectReleaseRelationship;

import org.eclipse.sw360.bdhubimport.entitytranslation.helper.ReleaseRelation;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.thrift.TException;
import org.eclipse.sw360.datahandler.thrift.ReleaseRelationship;
import org.eclipse.sw360.datahandler.thrift.RequestStatus;
import org.eclipse.sw360.datahandler.thrift.components.Component;
import org.eclipse.sw360.datahandler.thrift.components.Release;
import org.eclipse.sw360.datahandler.thrift.importstatus.ImportStatus;
import org.eclipse.sw360.datahandler.thrift.licenses.License;
import org.eclipse.sw360.datahandler.thrift.projectimport.BDHubCredentials;
import org.eclipse.sw360.datahandler.thrift.projects.Project;
import org.eclipse.sw360.datahandler.thrift.users.User;
import org.eclipse.sw360.datahandler.thrift.projectimport.TokenCredentials;
import org.eclipse.sw360.bdhubimport.thrift.helper.ProjectImportResult;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.google.common.base.Strings.isNullOrEmpty;
import static org.eclipse.sw360.bdhubimport.utility.TranslationConstants.UNKNOWN;

/**
 * @author: ksoranko@verifa.io
 */
public class ThriftUploader {

    private static final Logger LOGGER = LogManager.getLogger(ThriftUploader.class);
    private final BDHubComponentToSw360ComponentTranslator componentToComponentTranslator = new BDHubComponentToSw360ComponentTranslator();
    private final BDHubComponentToSw360ReleaseTranslator componentToReleaseTranslator = new BDHubComponentToSw360ReleaseTranslator();
    private final BDHubComponentLicenseToSw360LicenseTranslator licenseToLicenseTranslator = new BDHubComponentLicenseToSw360LicenseTranslator();
    private final BDHubProjectVersionToSw360ProjectTranslator projectToProjectTranslator = new BDHubProjectVersionToSw360ProjectTranslator();

    private ThriftExchange thriftExchange;

    public ThriftUploader() {
        this.thriftExchange = new ThriftExchange();
    }

    private <T> Optional<String> searchExistingEntityId(Optional<List<T>> nomineesOpt, Function<T, String> idExtractor, String wsName, String sw360name) {
        return nomineesOpt.flatMap(
                nominees -> {
                    Optional<String> nomineeId = nominees.stream()
                            .findFirst()
                            .map(idExtractor);
                    if (nomineeId.isPresent()) {
                        LOGGER.info(wsName + " to import matches a " + sw360name + " with id: " + nomineeId.get());
                        nominees.stream()
                                .skip(1)
                                .forEach(n -> LOGGER.error(wsName + " to import would also match a " + sw360name + " with id: " + idExtractor.apply(n)));
                    }
                    return nomineeId;
                }
        );
    }

    protected ProjectImportResult createProject(BDHubProjectVersion projectVersion, User sw360User, BDHubCredentials credentials) throws TException, JsonSyntaxException {
        BDHubProject project = new BDHubImportService().getProjectWithProjectVersion(projectVersion, credentials);
        LOGGER.info("Try to import DBHub project: " + project.getName());
        LOGGER.info("Sw360-User: " + sw360User.email);

        if (thriftExchange.projectExists(project.getName(), sw360User)) {
            LOGGER.error("Project already in database: " + project.getName());
            return new ProjectImportResult(ProjectImportError.PROJECT_ALREADY_EXISTS);
        }

        Project sw360Project = projectToProjectTranslator.apply(projectVersion);
        Set<ReleaseRelation> releases = createReleases(projectVersion.get_meta().getHref(), sw360User, credentials);
        sw360Project.setProjectResponsible(sw360User.getEmail());

        /*
         * TODO: Improve duplicate handling
         */
        Map<String, ProjectReleaseRelationship> releaseIdToUsage =
                releases.stream()
                    .collect(Collectors.toMap(
                            ReleaseRelation::getReleaseId,
                            ReleaseRelation::getProjectReleaseRelationship,
                            (projectReleaseRelationship1, projectReleaseRelationship2) -> {
                                LOGGER.info("--- Duplicate key found!");
                                LOGGER.info("--- 1: " + projectReleaseRelationship1.getReleaseRelation());
                                LOGGER.info("--- 2: " + projectReleaseRelationship2.getReleaseRelation());
                                return projectReleaseRelationship1;
                            }
                    ));
        sw360Project.setReleaseIdToUsage(releaseIdToUsage);
        String projectId = thriftExchange.addProject(sw360Project, sw360User);

        if(isNullOrEmpty(projectId)) {
            return new ProjectImportResult(ProjectImportError.OTHER);
        } else {
            return new ProjectImportResult(projectId);
        }
    }

    public ImportStatus importWsProjects(Collection<BDHubProjectVersion> projectVersions, User sw360User, BDHubCredentials credentials) {
        List<String> successfulIds = new ArrayList<>();
        Map<String, String> failedIds = new HashMap<>();
        ImportStatus wsImportStatus = new ImportStatus().setRequestStatus(RequestStatus.SUCCESS);

        for (BDHubProjectVersion projectVersion : projectVersions) {
            ProjectImportResult projectImportResult;
            try{
                projectImportResult = createProject(projectVersion, sw360User, credentials);
            } catch (TException e){
                LOGGER.error("Error when creating the project", e);
                wsImportStatus.setRequestStatus(RequestStatus.FAILURE);
                return wsImportStatus;
            }
            if (projectImportResult.isSuccess()) {
                successfulIds.add(projectVersion.get_meta().getHref());
            } else {
                LOGGER.error("Could not import project with self link: " + projectVersion.get_meta().getHref());
                failedIds.put(projectVersion.get_meta().getHref(), projectImportResult.getError().getText());
            }
        }
        return wsImportStatus
                .setFailedIds(failedIds)
                .setSuccessfulIds(successfulIds);
    }

    protected String getOrCreateLicenseId(BDHubComponentLicense license, User sw360User) {
        LOGGER.info("Try to import whitesource License: " + license.getLicenseDisplay());

        Optional<String> potentialLicenseId = searchExistingEntityId(thriftExchange.searchLicenseByWsName(license.getSpdxId()),
                License::getId,
                "License",
                "Licence");

        if (potentialLicenseId.isPresent()) {
            return potentialLicenseId.get();
        } else {
            License sw360License = licenseToLicenseTranslator.apply(license);
            String licenseId = thriftExchange.addLicense(sw360License, sw360User);
            LOGGER.info("Imported license: " + licenseId);
            return licenseId;
        }
    }

    private String getOrCreateComponent(BDHubComponent component, User sw360User) {
        LOGGER.info("Try to import BDHub component: " + component.getComponentName() + ", version: " + component.getComponentVersionName());

        String componentVersion = isNullOrEmpty(component.getComponentVersionName()) ? UNKNOWN : component.getComponentVersionName();
        Optional<String> potentialReleaseId = searchExistingEntityId(thriftExchange.searchReleaseByNameAndVersion(component.getComponentName(), componentVersion),
                Release::getId,
                "Library",
                "Release");
        if (potentialReleaseId.isPresent()) {
            return potentialReleaseId.get();
        }

        Release sw360Release = componentToReleaseTranslator.apply(component);
        sw360Release.setModerators(new HashSet<>());
        sw360Release.getModerators().add(sw360User.getEmail());
        Optional<String> potentialComponentId = searchExistingEntityId(thriftExchange.searchComponentByName(component.getComponentName()),
                Component::getId,
                "Library",
                "Component");

        String componentId;
        if (potentialComponentId.isPresent()) {
            componentId = potentialComponentId.get();
        } else {
            Component sw360Component = componentToComponentTranslator.apply(component);
            componentId = thriftExchange.addComponent(sw360Component, sw360User);
        }
        sw360Release.setComponentId(componentId);

        if (component.getLicenses() == null) {
            sw360Release.setMainLicenseIds(Collections.singleton(UNKNOWN));
        } else {
            Set<String> mainLicenses = new HashSet<>();
            for (BDHubComponentLicense license : component.getLicenses()) {
                mainLicenses.add(getOrCreateLicenseId(license, sw360User));
            }
            sw360Release.setMainLicenseIds(mainLicenses);
        }

        return thriftExchange.addRelease(sw360Release, sw360User);
    }

    private ReleaseRelation createReleaseRelation(BDHubComponent components, User sw360User) {
        String releaseId = getOrCreateComponent(components, sw360User);
        if (releaseId == null) {
            return null;
        } else {
            ReleaseRelationship releaseRelationship = ReleaseRelationship.UNKNOWN;
            return new ReleaseRelation(releaseId, releaseRelationship);
        }
    }

    private Set<ReleaseRelation> createReleases(String projectVersionComponentsLink, User sw360User, BDHubCredentials credentials) {
        BDHubComponent[] components = null;
        try {
            components =  new BDHubImportService().getProjectVersionComponents(projectVersionComponentsLink, credentials);
        } catch (JsonSyntaxException jse) {
            LOGGER.error(jse);
        }
        List<BDHubComponent> componentList;
        if (components == null) {
            return ImmutableSet.of();
        } else {
            componentList = new ArrayList<>(Arrays.asList(components));
        }
        Set<ReleaseRelation> releases = componentList.stream()
                .map(c -> createReleaseRelation(c, sw360User))
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        if (releases.size() != componentList .size()) {
            LOGGER.warn("expected to get " + componentList.size() + " different ids of releases but got " + releases.size());
        } else {
            LOGGER.info("The expected number of releases was imported or already found in database.");
        }

        return releases;
    }

    public ImportStatus importBDHubProjectVersions(List<BDHubProjectVersion> projectVersions, User user, BDHubCredentials credentials) {
    }
}
