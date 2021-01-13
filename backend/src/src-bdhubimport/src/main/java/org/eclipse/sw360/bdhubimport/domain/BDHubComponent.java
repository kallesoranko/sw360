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
public class BDHubComponent {

    private String componentName;
    private String componentVersionName;
    private String component;
    private String componentVersion;
    private int totalFileMatchCount;
    private BDHubComponentLicense[] licenses;

    public String getComponentName() {
        return componentName;
    }

    public void setComponentName(String componentName) {
        this.componentName = componentName;
    }

    public String getComponentVersionName() {
        return componentVersionName;
    }

    public void setComponentVersionName(String componentVersionName) {
        this.componentVersionName = componentVersionName;
    }

    public String getComponent() {
        return component;
    }

    public void setComponent(String component) {
        this.component = component;
    }

    public String getComponentVersion() {
        return componentVersion;
    }

    public void setComponentVersion(String componentVersion) {
        this.componentVersion = componentVersion;
    }

    public int getTotalFileMatchCount() {
        return totalFileMatchCount;
    }

    public void setTotalFileMatchCount(int totalFileMatchCount) {
        this.totalFileMatchCount = totalFileMatchCount;
    }

    public BDHubComponentLicense[] getLicenses() {
        return licenses;
    }

    public void setLicenses(BDHubComponentLicense[] licenses) {
        this.licenses = licenses;
    }
}

/*
  "componentName": "Apache Commons Codec",
  "componentVersionName": "1.3",
  "component": "https://20.73.223.44/api/components/dc3dee66-4939-4dea-b22f-ead288b4f117",
  "componentVersion": "https://20.73.223.44/api/components/dc3dee66-4939-4dea-b22f-ead288b4f117/versions/3d941ac5-cd4d-44b8-92cb-bd383ee1c301",
  "totalFileMatchCount": 1,
  "licenses": [
    {
      "licenseDisplay": "Apache License 2.0",
      "license": "https://20.73.223.44/api/projects/a9ab6c4e-3f48-43c2-82fa-2e1470a8e5f6/versions/a51eb5c6-8b72-46f1-8c5b-97baa388186c/components/dc3dee66-4939-4dea-b22f-ead288b4f117/versions/3d941ac5-cd4d-44b8-92cb-bd383ee1c301/licenses/7cae335f-1193-421e-92f1-8802b4243e93",
      "spdxId": "Apache-2.0",
      "licenses": []
    }
  ]
*/