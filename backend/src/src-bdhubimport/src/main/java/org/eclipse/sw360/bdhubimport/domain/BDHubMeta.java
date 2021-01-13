package org.eclipse.sw360.bdhubimport.domain;

public class BDHubMeta {

    private String[] allow;
    private String href;
    private BDHubLink[] links;

    public String[] getAllow() {
        return allow;
    }

    public void setAllow(String[] allow) {
        this.allow = allow;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public BDHubLink[] getLinks() {
        return links;
    }

    public void setLinks(BDHubLink[] links) {
        this.links = links;
    }
}

/*
    "allow": [
      "GET",
      "PUT"
    ],
    "href": "https://20.73.223.44/api/projects/a9ab6c4e-3f48-43c2-82fa-2e1470a8e5f6/versions/a51eb5c6-8b72-46f1-8c5b-97baa388186c/components/9c0920e4-104a-448e-8192-866544b75890/versions/ae8deda0-886f-4423-9c60-f07442016409",
    "links": [
      {
        "rel": "comments",
        "href": "https://20.73.223.44/api/projects/a9ab6c4e-3f48-43c2-82fa-2e1470a8e5f6/versions/a51eb5c6-8b72-46f1-8c5b-97baa388186c/components/9c0920e4-104a-448e-8192-866544b75890/component-versions/ae8deda0-886f-4423-9c60-f07442016409/comments"
      },
      {
        "rel": "component-issues",
        "href": "https://20.73.223.44/api/projects/a9ab6c4e-3f48-43c2-82fa-2e1470a8e5f6/versions/a51eb5c6-8b72-46f1-8c5b-97baa388186c/components/9c0920e4-104a-448e-8192-866544b75890/component-versions/ae8deda0-886f-4423-9c60-f07442016409/issues"
      },
      {
        "rel": "policy-rules",
        "href": "https://20.73.223.44/api/projects/a9ab6c4e-3f48-43c2-82fa-2e1470a8e5f6/versions/a51eb5c6-8b72-46f1-8c5b-97baa388186c/components/9c0920e4-104a-448e-8192-866544b75890/versions/ae8deda0-886f-4423-9c60-f07442016409/policy-rules"
      },
      {
        "rel": "vulnerabilities",
        "href": "https://20.73.223.44/api/components/9c0920e4-104a-448e-8192-866544b75890/versions/ae8deda0-886f-4423-9c60-f07442016409/vulnerabilities"
      },
      {
        "rel": "matched-files",
        "href": "https://20.73.223.44/api/projects/a9ab6c4e-3f48-43c2-82fa-2e1470a8e5f6/versions/a51eb5c6-8b72-46f1-8c5b-97baa388186c/components/9c0920e4-104a-448e-8192-866544b75890/versions/ae8deda0-886f-4423-9c60-f07442016409/origins/6e8797ee-53f1-46ed-9132-a195dce4c1a8/matched-files"
      },
      {
        "rel": "origins",
        "href": "https://20.73.223.44/api/components/9c0920e4-104a-448e-8192-866544b75890/versions/ae8deda0-886f-4423-9c60-f07442016409/origins"
      },
      {
        "rel": "policy-status",
        "href": "https://20.73.223.44/api/projects/a9ab6c4e-3f48-43c2-82fa-2e1470a8e5f6/versions/a51eb5c6-8b72-46f1-8c5b-97baa388186c/components/9c0920e4-104a-448e-8192-866544b75890/versions/ae8deda0-886f-4423-9c60-f07442016409/policy-status"
      },
      {
        "rel": "upgrade-guidance",
        "href": "https://20.73.223.44/api/components/9c0920e4-104a-448e-8192-866544b75890/versions/ae8deda0-886f-4423-9c60-f07442016409/upgrade-guidance"
      }
    ]
*/