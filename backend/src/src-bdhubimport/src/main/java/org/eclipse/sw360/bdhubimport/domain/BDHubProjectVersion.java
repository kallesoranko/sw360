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
public class BDHubProjectVersion {

    private String versionName;
    private String phase;
    private String distribution;
    private BDHubMeta _meta;

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public String getPhase() {
        return phase;
    }

    public void setPhase(String phase) {
        this.phase = phase;
    }

    public String getDistribution() {
        return distribution;
    }

    public void setDistribution(String distribution) {
        this.distribution = distribution;
    }

    public BDHubMeta get_meta() {
        return _meta;
    }

    public void set_meta(BDHubMeta _meta) {
        this._meta = _meta;
    }
}
/*
"versionName": "exclude-all",
"phase": "DEVELOPMENT",
"distribution": "EXTERNAL",
"license": {
    "type": "DISJUNCTIVE",
    "licenses": [
        {
        "license": "https://20.73.223.44/api/licenses/00000000-0010-0000-0000-000000000000",
        "licenses": [],
        "name": "Unknown License",
        "ownership": "UNKNOWN",
        "licenseDisplay": "Unknown License",
        "licenseFamilySummary": {
            "name": "Unknown",
            "href": "https://20.73.223.44/api/license-families/5"
        }
        }
    ],
    "licenseDisplay": "Unknown License"
},
"createdAt": "2020-12-08T11:54:44.108Z",
"createdBy": "a242075",
"createdByUser": "https://20.73.223.44/api/users/8cdaf7ae-bbb1-4354-a48b-8dfd3d718433",
"settingUpdatedAt": "2020-12-08T11:54:57.416Z",
"settingUpdatedBy": "a242075",
"settingUpdatedByUser": "https://20.73.223.44/api/users/8cdaf7ae-bbb1-4354-a48b-8dfd3d718433",
"source": "CUSTOM",
*/