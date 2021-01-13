/*
 * Copyright (c) Verifa Oy, 2018-2019.
 * Part of the SW360 Portal Project.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.sw360.bdhubimport.entitytranslation;

import org.eclipse.sw360.bdhubimport.domain.BDHubComponentLicense;

import org.eclipse.sw360.bdhubimport.utility.TranslationConstants;
import org.eclipse.sw360.datahandler.thrift.licenses.License;

import java.util.HashMap;

/**
 * @author ksoranko@verifa.io
 */
public class BDHubComponentLicenseToSw360LicenseTranslator implements EntityTranslator<BDHubComponentLicense, org.eclipse.sw360.datahandler.thrift.licenses.License> {

    @Override
    public License apply(BDHubComponentLicense license) {
        String licenseName = license.getLicenseDisplay();
        String licenseShortName = license.getSpdxId();

        License sw360License = new License();
        sw360License.setId(licenseShortName);
        sw360License.setExternalIds(new HashMap<>());
        sw360License.getExternalIds().put(TranslationConstants.BDHUB_ID, license.getSpdxId());
        sw360License.setShortname(licenseShortName);
        sw360License.setFullname(licenseName);
        if (license.getLicense() != null) {
            sw360License.setExternalLicenseLink(license.getLicense());
        }
        return sw360License;
    }

}
