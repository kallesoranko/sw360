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
package org.eclipse.sw360.bdhubimport.entitytranslation;

import org.eclipse.sw360.bdhubimport.domain.BDHubComponent;
import org.eclipse.sw360.bdhubimport.utility.TranslationConstants;
import org.eclipse.sw360.datahandler.thrift.components.ClearingState;
import org.eclipse.sw360.datahandler.thrift.components.Release;

import java.util.HashMap;
import java.util.HashSet;

import static com.google.common.base.Strings.isNullOrEmpty;
import static org.eclipse.sw360.bdhubimport.utility.TranslationConstants.*;

/**
 * @author ksoranko@verifa.io
 */
public class BDHubComponentToSw360ReleaseTranslator implements EntityTranslator<BDHubComponent, Release> {

    @Override
    public Release apply(BDHubComponent component) {

        Release sw360Release = new Release();
        sw360Release.setExternalIds(new HashMap<>());
        sw360Release.setName(component.getName());
        sw360Release.getExternalIds().put(TranslationConstants.WS_ID, Integer.toString(component.getKeyId()));
        sw360Release.getExternalIds().put(FILENAME, component.getFilename());

        if (component.getReferences() != null) {
            sw360Release.setSourceCodeDownloadurl(component.getReferences().getUrl());
            if (!isNullOrEmpty(component.getReferences().getPomUrl())) {
                sw360Release.getExternalIds().put(POM_FILE_URL, component.getReferences().getPomUrl());
            }
            if (!isNullOrEmpty(component.getReferences().getScmUrl())) {
                sw360Release.getExternalIds().put(SCM_URL, component.getReferences().getScmUrl());
            }
        }

        if (!isNullOrEmpty(component.getVersion())) {
            String version = component.getVersion().replaceFirst("^(?i)v","");
            sw360Release.setVersion(version.replaceAll(VERSION_SUFFIX_REGEX,"").trim());
        } else {
            sw360Release.setVersion(UNKNOWN);
        }

        sw360Release.setClearingState(ClearingState.NEW_CLEARING);

        return sw360Release;
    }

}
