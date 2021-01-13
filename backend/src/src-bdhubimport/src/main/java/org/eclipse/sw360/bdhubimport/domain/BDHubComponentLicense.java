/*
 * Copyright (c) Verifa Oy, 2021. Part of the SW360 Project.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.sw360.bdhubimport.domain;

/**
 * @author: ksoranko@verifa.io
 */
public class BDHubComponentLicense {

    private String licenseDisplay;
    private String spdxId;
    private String license;

    public String getLicenseDisplay() {
        return licenseDisplay;
    }

    public void setLicenseDisplay(String licenseDisplay) {
        this.licenseDisplay = licenseDisplay;
    }

    public String getSpdxId() {
        return spdxId;
    }

    public void setSpdxId(String spdxId) {
        this.spdxId = spdxId;
    }

    public String getLicense() {
        return license;
    }

    public void setLicense(String license) {
        this.license = license;
    }
}

/*
{
    "licenseDisplay": "Apache License 2.0",
    "license": "https://20.73.223.44/api/projects/a9ab6c4e-3f48-43c2-82fa-2e1470a8e5f6/versions/a51eb5c6-8b72-46f1-8c5b-97baa388186c/components/dc3dee66-4939-4dea-b22f-ead288b4f117/versions/3d941ac5-cd4d-44b8-92cb-bd383ee1c301/licenses/7cae335f-1193-421e-92f1-8802b4243e93",
    "spdxId": "Apache-2.0",
    "licenses": []
}
*/
