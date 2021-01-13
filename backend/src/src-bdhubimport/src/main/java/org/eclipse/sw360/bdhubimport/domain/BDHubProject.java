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
public class BDHubProject {

    private String name;
    private BDHubMeta _meta;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BDHubMeta get_meta() {
        return _meta;
    }

    public void set_meta(BDHubMeta _meta) {
        this._meta = _meta;
    }
}

/*
    "name": "EXAMPLE-PROJECT-simple-gradle",
    "projectLevelAdjustments": true,
    "cloneCategories": [
        "VULN_DATA",
        "COMPONENT_DATA"
    ],
    "customSignatureEnabled": false,
    "customSignatureDepth": 5,
    "deepLicenseDataEnabled": false,
    "snippetAdjustmentApplied": false,
    "createdAt": "2020-12-08T11:53:32.117Z",
    "createdBy": "a242075",
    "createdByUser": "https://20.73.223.44/api/users/8cdaf7ae-bbb1-4354-a48b-8dfd3d718433",
    "updatedAt": "2020-12-08T11:53:32.253Z",
    "updatedBy": "a242075",
    "updatedByUser": "https://20.73.223.44/api/users/8cdaf7ae-bbb1-4354-a48b-8dfd3d718433",
    "source": "CUSTOM",
    "_meta": {

*/