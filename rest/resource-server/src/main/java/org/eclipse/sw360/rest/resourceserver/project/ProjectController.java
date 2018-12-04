/*
 * Copyright Siemens AG, 2017-2018.
 * Copyright Bosch Software Innovations GmbH, 2017.
 * Part of the SW360 Portal Project.
 *
 * SPDX-License-Identifier: EPL-1.0
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.sw360.rest.resourceserver.project;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.log4j.Logger;
import org.apache.thrift.TException;
import org.eclipse.sw360.datahandler.common.SW360Utils;
import org.eclipse.sw360.datahandler.thrift.MainlineState;
import org.eclipse.sw360.datahandler.thrift.ProjectReleaseRelationship;
import org.eclipse.sw360.datahandler.thrift.ReleaseRelationship;
import org.eclipse.sw360.datahandler.thrift.attachments.Attachment;
import org.eclipse.sw360.datahandler.thrift.attachments.AttachmentContent;
import org.eclipse.sw360.datahandler.thrift.attachments.AttachmentType;
import org.eclipse.sw360.datahandler.thrift.components.Release;
import org.eclipse.sw360.datahandler.thrift.licenseinfo.LicenseInfoFile;
import org.eclipse.sw360.datahandler.thrift.licenseinfo.LicenseNameWithText;
import org.eclipse.sw360.datahandler.thrift.licenseinfo.OutputFormatInfo;
import org.eclipse.sw360.datahandler.thrift.licenses.License;
import org.eclipse.sw360.datahandler.thrift.projects.Project;
import org.eclipse.sw360.datahandler.thrift.projects.ProjectRelationship;
import org.eclipse.sw360.datahandler.thrift.users.User;
import org.eclipse.sw360.datahandler.thrift.vulnerabilities.VulnerabilityDTO;
import org.eclipse.sw360.rest.resourceserver.attachment.Sw360AttachmentService;
import org.eclipse.sw360.rest.resourceserver.core.HalResource;
import org.eclipse.sw360.rest.resourceserver.core.RestControllerHelper;
import org.eclipse.sw360.rest.resourceserver.license.Sw360LicenseService;
import org.eclipse.sw360.rest.resourceserver.licenseinfo.Sw360LicenseInfoService;
import org.eclipse.sw360.rest.resourceserver.release.Sw360ReleaseService;
import org.eclipse.sw360.rest.resourceserver.vulnerability.Sw360VulnerabilityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.BasePathAwareController;
import org.springframework.data.rest.webmvc.RepositoryLinksResource;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceProcessor;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

@BasePathAwareController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ProjectController implements ResourceProcessor<RepositoryLinksResource> {
    public static final String PROJECTS_URL = "/projects";
    private static final Logger log = Logger.getLogger(ProjectController.class);

    @NonNull
    private final Sw360ProjectService projectService;

    @NonNull
    private final Sw360ReleaseService releaseService;

    @NonNull
    private final Sw360LicenseService licenseService;

    @NonNull
    private final Sw360VulnerabilityService vulnerabilityService;

    @NonNull
    private final Sw360AttachmentService attachmentService;

    @NonNull
    private final Sw360LicenseInfoService licenseInfoService;

    @NonNull
    private final RestControllerHelper restControllerHelper;

    @RequestMapping(value = PROJECTS_URL, method = RequestMethod.GET)
    public ResponseEntity<Resources<Resource<Project>>> getProjectsForUser(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "type", required = false) String projectType) throws TException {
        User sw360User = restControllerHelper.getSw360UserFromAuthentication();
        List<Project> sw360Projects = new ArrayList<>();
        if (name != null && !name.isEmpty()) {
            sw360Projects.addAll(projectService.searchProjectByName(name, sw360User));
        } else {
            sw360Projects.addAll(projectService.getProjectsForUser(sw360User));
        }

        List<Resource<Project>> projectResources = new ArrayList<>();
        sw360Projects.stream()
                .filter(project -> projectType == null || projectType.equals(project.projectType.name()))
                .forEach(p -> {
                    Project embeddedProject = restControllerHelper.convertToEmbeddedProject(p);
                    projectResources.add(new Resource<>(embeddedProject));
                });

        Resources<Resource<Project>> resources = new Resources<>(projectResources);
        return new ResponseEntity<>(resources, HttpStatus.OK);
    }

    @RequestMapping(value = PROJECTS_URL + "/{id}", method = RequestMethod.GET)
    public ResponseEntity<Resource<Project>> getProject(
            @PathVariable("id") String id) throws TException {
        User sw360User = restControllerHelper.getSw360UserFromAuthentication();
        Project sw360Project = projectService.getProjectForUserById(id, sw360User);
        HalResource<Project> userHalResource = createHalProject(sw360Project, sw360User);
        return new ResponseEntity<>(userHalResource, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('WRITE')")
    @RequestMapping(value = PROJECTS_URL, method = RequestMethod.POST)
    public ResponseEntity createProject(
            @RequestBody Project project) throws URISyntaxException, TException {
        if (project.getReleaseIdToUsage() != null) {

            Map<String, ProjectReleaseRelationship> releaseIdToUsage = new HashMap<>();
            Map<String, ProjectReleaseRelationship> oriReleaseIdToUsage = project.getReleaseIdToUsage();
            for (String releaseURIString : oriReleaseIdToUsage.keySet()) {
                URI releaseURI = new URI(releaseURIString);
                String path = releaseURI.getPath();
                String releaseId = path.substring(path.lastIndexOf('/') + 1);
                releaseIdToUsage.put(releaseId, oriReleaseIdToUsage.get(releaseURIString));
            }
            project.setReleaseIdToUsage(releaseIdToUsage);
        }

        User sw360User = restControllerHelper.getSw360UserFromAuthentication();
        project = projectService.createProject(project, sw360User);
        HalResource<Project> halResource = createHalProject(project, sw360User);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest().path("/{id}")
                .buildAndExpand(project.getId()).toUri();

        return ResponseEntity.created(location).body(halResource);
    }

    @PreAuthorize("hasAuthority('WRITE')")
    @RequestMapping(value = PROJECTS_URL + "/{id}/releases", method = RequestMethod.POST)
    public ResponseEntity createReleases(
            @PathVariable("id") String id,
            @RequestBody List<String> releaseURIs) throws URISyntaxException, TException {
        User sw360User = restControllerHelper.getSw360UserFromAuthentication();
        Project project = projectService.getProjectForUserById(id, sw360User);
        Map<String, ProjectReleaseRelationship> releaseIdToUsage = new HashMap<>();
        for (String releaseURIString : releaseURIs) {
            URI releaseURI = new URI(releaseURIString);
            String path = releaseURI.getPath();
            String releaseId = path.substring(path.lastIndexOf('/') + 1);
            releaseIdToUsage.put(releaseId,
                    new ProjectReleaseRelationship(ReleaseRelationship.CONTAINED, MainlineState.MAINLINE));
        }
        project.setReleaseIdToUsage(releaseIdToUsage);
        projectService.updateProject(project, sw360User);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @RequestMapping(value = PROJECTS_URL + "/{id}/releases", method = RequestMethod.GET)
    public ResponseEntity<Resources<Resource<Release>>> getProjectReleases(
            @PathVariable("id") String id,
            @RequestParam(value = "transitive", required = false) String transitive) throws TException {

        final User sw360User = restControllerHelper.getSw360UserFromAuthentication();
        final Set<String> releaseIds = projectService.getReleaseIds(id, sw360User, transitive);

        final List<Resource<Release>> releaseResources = new ArrayList<>();
        for (final String releaseId : releaseIds) {
            final Release sw360Release = releaseService.getReleaseForUserById(releaseId, sw360User);
            final Release embeddedRelease = restControllerHelper.convertToEmbeddedRelease(sw360Release);
            final Resource<Release> releaseResource = new Resource<>(embeddedRelease);
            releaseResources.add(releaseResource);
        }

        final Resources<Resource<Release>> resources = new Resources<>(releaseResources);
        return new ResponseEntity<>(resources, HttpStatus.OK);
    }

    @RequestMapping(value = PROJECTS_URL + "/{id}/releases/ecc", method = RequestMethod.GET)
    public ResponseEntity<Resources<Resource<Release>>> getECCsOfReleases(
            @PathVariable("id") String id,
            @RequestParam(value = "transitive", required = false) String transitive) throws TException {

        final User sw360User = restControllerHelper.getSw360UserFromAuthentication();
        final Set<String> releaseIds = projectService.getReleaseIds(id, sw360User, transitive);

        final List<Resource<Release>> releaseResources = new ArrayList<>();
        for (final String releaseId : releaseIds) {
            final Release sw360Release = releaseService.getReleaseForUserById(releaseId, sw360User);
            Release embeddedRelease = restControllerHelper.convertToEmbeddedRelease(sw360Release);
            embeddedRelease.setEccInformation(sw360Release.getEccInformation());
            final Resource<Release> releaseResource = new Resource<>(embeddedRelease);
            releaseResources.add(releaseResource);
        }

        final Resources<Resource<Release>> resources = new Resources<>(releaseResources);
        return new ResponseEntity<>(resources, HttpStatus.OK);
    }

    @RequestMapping(value = PROJECTS_URL + "/{id}/vulnerabilities", method = RequestMethod.GET)
    public ResponseEntity<Resources<Resource<VulnerabilityDTO>>> getVulnerabilitiesOfReleases(@PathVariable("id") String id) {
        final User sw360User = restControllerHelper.getSw360UserFromAuthentication();
        final List<VulnerabilityDTO> allVulnerabilityDTOs = vulnerabilityService.getVulnerabilitiesByProjectId(id, sw360User);

        final List<Resource<VulnerabilityDTO>> vulnerabilityResources = new ArrayList<>();
        for (final VulnerabilityDTO vulnerabilityDTO : allVulnerabilityDTOs) {
            final Resource<VulnerabilityDTO> vulnerabilityDTOResource = new Resource<>(vulnerabilityDTO);
            vulnerabilityResources.add(vulnerabilityDTOResource);
        }

        final Resources<Resource<VulnerabilityDTO>> resources = new Resources<>(vulnerabilityResources);
        return new ResponseEntity<>(resources, HttpStatus.OK);
    }

    @RequestMapping(value = PROJECTS_URL + "/{id}/licenses", method = RequestMethod.GET)
    public ResponseEntity<Resources<Resource<License>>> getLicensesOfReleases(@PathVariable("id") String id) throws TException {
        final User sw360User = restControllerHelper.getSw360UserFromAuthentication();
        final Project project = projectService.getProjectForUserById(id, sw360User);
        final List<Resource<License>> licenseResources = new ArrayList<>();
        final Set<String> allLicenseIds = new HashSet<>();

        final Set<String> releaseIdToUsage = project.getReleaseIdToUsage().keySet();
        for (final String releaseId : releaseIdToUsage) {
            final Release sw360Release = releaseService.getReleaseForUserById(releaseId, sw360User);
            final Set<String> licenseIds = sw360Release.getMainLicenseIds();
            if (licenseIds != null && !licenseIds.isEmpty()) {
                allLicenseIds.addAll(licenseIds);
            }
        }
        for (final String licenseId : allLicenseIds) {
            final License sw360License = licenseService.getLicenseById(licenseId);
            final License embeddedLicense = restControllerHelper.convertToEmbeddedLicense(sw360License);
            final Resource<License> licenseResource = new Resource<>(embeddedLicense);
            licenseResources.add(licenseResource);
        }

        final Resources<Resource<License>> resources = new Resources<>(licenseResources);
        return new ResponseEntity<>(resources, HttpStatus.OK);
    }

    @RequestMapping(value = PROJECTS_URL + "/{id}/licenseinfo", method = RequestMethod.GET)
    public void downloadLicenseInfo(@PathVariable("id") String id,
                                    @RequestParam("generatorClassName") String generatorClassName,
                                    HttpServletResponse response) throws TException, IOException {
        final User sw360User = restControllerHelper.getSw360UserFromAuthentication();
        final Project sw360Project = projectService.getProjectForUserById(id, sw360User);

        final Map<String, Set<String>> selectedReleaseAndAttachmentIds = new HashMap<>();
        for (final String releaseId : sw360Project.getReleaseIdToUsage().keySet()) {
            final Release release = releaseService.getReleaseForUserById(releaseId, sw360User);
            if (release.isSetAttachments()) {
                if (!selectedReleaseAndAttachmentIds.containsKey(releaseId)) {
                    selectedReleaseAndAttachmentIds.put(releaseId, new HashSet<>());
                }
                final Set<Attachment> attachments = release.getAttachments();
                for (final Attachment attachment : attachments) {
                    selectedReleaseAndAttachmentIds.get(releaseId)
                            .add(attachment.getAttachmentContentId());
                }
            }
        }

        final Map<String, Set<LicenseNameWithText>> excludedLicenses = new HashMap<>(); // TODO: implement method to determine excluded licenses

        final String projectName = sw360Project.getName();
        final String timestamp = SW360Utils.getCreatedOn();
        final OutputFormatInfo outputFormatInfo = licenseInfoService.getOutputFormatInfoForGeneratorClass(generatorClassName);
        final String filename = String.format("LicenseInfo-%s-%s.%s", projectName, timestamp, outputFormatInfo.getFileExtension());

        final LicenseInfoFile licenseInfoFile = licenseInfoService.getLicenseInfoFile(sw360Project, sw360User, generatorClassName, selectedReleaseAndAttachmentIds, excludedLicenses);
        byte[] byteContent = licenseInfoFile.bufferForGeneratedOutput().array();
        response.setContentType(outputFormatInfo.getMimeType());
        response.setHeader("Content-Disposition", String.format("attachment; filename=\"%s\"", filename));
        FileCopyUtils.copy(byteContent, response.getOutputStream());
    }

    @RequestMapping(value = PROJECTS_URL + "/{id}/attachments", method = RequestMethod.GET)
    public ResponseEntity<Resources<Resource<Attachment>>> getProjectAttachments(
            @PathVariable("id") String id) throws TException {
        final User sw360User = restControllerHelper.getSw360UserFromAuthentication();
        final Project sw360Project = projectService.getProjectForUserById(id, sw360User);
        final Resources<Resource<Attachment>> resources = attachmentService.getResourcesFromList(sw360Project.getAttachments());
        return new ResponseEntity<>(resources, HttpStatus.OK);
    }

    @RequestMapping(value = PROJECTS_URL + "/{projectId}/attachments/{attachmentId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public void downloadAttachmentFromProject(
            @PathVariable("projectId") String projectId,
            @PathVariable("attachmentId") String attachmentId,
            HttpServletResponse response) throws TException {
        final User sw360User = restControllerHelper.getSw360UserFromAuthentication();
        final Project project = projectService.getProjectForUserById(projectId, sw360User);
        this.attachmentService.downloadAttachmentWithContext(project, attachmentId, response, sw360User);
    }

    @RequestMapping(value = PROJECTS_URL + "/{projectId}/attachments/clearingReports", method = RequestMethod.GET, produces = "application/zip")
    public void downloadClearingReports(
            @PathVariable("projectId") String projectId,
            HttpServletResponse response) throws TException {
        final User sw360User = restControllerHelper.getSw360UserFromAuthentication();
        final Project project = projectService.getProjectForUserById(projectId, sw360User);
        final String filename = "Clearing-Reports-" + project.getName() + ".zip";


        final Set<Attachment> attachments = project.getAttachments();
        final Set<AttachmentContent> clearingAttachments = new HashSet<>();
        for (final Attachment attachment : attachments) {
            if (attachment.getAttachmentType().equals(AttachmentType.CLEARING_REPORT)) {
                clearingAttachments.add(attachmentService.getAttachmentContent(attachment.getAttachmentContentId()));
            }
        }

        try (InputStream attachmentStream = attachmentService.getStreamToAttachments(clearingAttachments, sw360User, project)) {
            response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
            response.setHeader("Content-Disposition", String.format("attachment; filename=\"%s\"", filename));
            FileCopyUtils.copy(attachmentStream, response.getOutputStream());
        } catch (final TException | IOException e) {
            log.error(e.getMessage());
        }
    }

    @RequestMapping(value = PROJECTS_URL + "/{projectId}/attachments", method = RequestMethod.POST, consumes = {"multipart/mixed", "multipart/form-data"})
    public ResponseEntity<HalResource> addAttachmentToProject(@PathVariable("projectId") String projectId,
                                                              @RequestPart("file") MultipartFile file,
                                                              @RequestPart("attachment") Attachment newAttachment) throws TException {
        final User sw360User = restControllerHelper.getSw360UserFromAuthentication();

        Attachment attachment;
        try {
            attachment = attachmentService.uploadAttachment(file, newAttachment, sw360User);
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }

        final Project project = projectService.getProjectForUserById(projectId, sw360User);
        project.addToAttachments(attachment);
        projectService.updateProject(project, sw360User);

        final HalResource<Project> halResource = createHalProject(project, sw360User);
        return new ResponseEntity<>(halResource, HttpStatus.OK);
    }

    @RequestMapping(value = PROJECTS_URL + "/searchByExternalIds", method = RequestMethod.GET)
    public ResponseEntity searchByExternalIds(@RequestParam MultiValueMap<String, String> externalIdsMultiMap) throws TException {
        final User sw360User = restControllerHelper.getSw360UserFromAuthentication();
        return restControllerHelper.searchByExternalIds(externalIdsMultiMap, projectService, sw360User);
    }

    @Override
    public RepositoryLinksResource process(RepositoryLinksResource resource) {
        resource.add(linkTo(ProjectController.class).slash("api" + PROJECTS_URL).withRel("projects"));
        return resource;
    }

    private HalResource<Project> createHalProject(Project sw360Project, User sw360User) throws TException {
        HalResource<Project> halProject = new HalResource<>(sw360Project);
        restControllerHelper.addEmbeddedUser(halProject, sw360User, "createdBy");

        Map<String, ProjectReleaseRelationship> releaseIdToUsage = sw360Project.getReleaseIdToUsage();
        if (releaseIdToUsage != null) {
            restControllerHelper.addEmbeddedReleases(halProject, releaseIdToUsage.keySet(), releaseService, sw360User);
        }

        Map<String, ProjectRelationship> linkedProjects = sw360Project.getLinkedProjects();
        if (linkedProjects != null) {
            restControllerHelper.addEmbeddedProject(halProject, linkedProjects.keySet(), projectService, sw360User);
        }

        if (sw360Project.getModerators() != null) {
            Set<String> moderators = sw360Project.getModerators();
            restControllerHelper.addEmbeddedModerators(halProject, moderators);
        }

        if (sw360Project.getAttachments() != null) {
            restControllerHelper.addEmbeddedAttachments(halProject, sw360Project.getAttachments());
        }

        return halProject;
    }
}
