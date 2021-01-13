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
public class BDHubLink {

    private String rel;
    private String href;

    public String getRel() {
        return rel;
    }

    public void setRel(String rel) {
        this.rel = rel;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }
}

/*
{
    "rel": "versionReport",
    "href": "https://20.73.223.44/api/versions/8ce27a85-ec0c-4bc0-b642-d120589190e6/reports"
}
*/