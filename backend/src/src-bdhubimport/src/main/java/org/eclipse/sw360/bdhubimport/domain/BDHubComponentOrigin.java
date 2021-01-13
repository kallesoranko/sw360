package org.eclipse.sw360.bdhubimport.domain;

public class BDHubComponentOrigin {

    private String name;
    private String origin;
    private String externalNamespace;
    private String externalId;
    private BDHubMeta _meta;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getExternalNamespace() {
        return externalNamespace;
    }

    public void setExternalNamespace(String externalNamespace) {
        this.externalNamespace = externalNamespace;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public BDHubMeta get_meta() {
        return _meta;
    }

    public void set_meta(BDHubMeta _meta) {
        this._meta = _meta;
    }
}

/*
  "name": "1.3",
  "origin": "https://20.73.223.44/api/components/dc3dee66-4939-4dea-b22f-ead288b4f117/versions/3d941ac5-cd4d-44b8-92cb-bd383ee1c301/origins/2899ce11-cb6d-4057-91e2-423eada58f5f",
  "externalNamespace": "maven",
  "externalId": "commons-codec:commons-codec:1.3",
  "externalNamespaceDistribution": false,
  "_meta": {
    "allow": [],
    "links": [
      {
        "rel": "origin",
        "href": "https://20.73.223.44/api/components/dc3dee66-4939-4dea-b22f-ead288b4f117/versions/3d941ac5-cd4d-44b8-92cb-bd383ee1c301/origins/2899ce11-cb6d-4057-91e2-423eada58f5f"
      },
      {
        "rel": "matched-files",
        "href": "https://20.73.223.44/api/projects/a9ab6c4e-3f48-43c2-82fa-2e1470a8e5f6/versions/a51eb5c6-8b72-46f1-8c5b-97baa388186c/components/dc3dee66-4939-4dea-b22f-ead288b4f117/versions/3d941ac5-cd4d-44b8-92cb-bd383ee1c301/origins/2899ce11-cb6d-4057-91e2-423eada58f5f/matched-files"
      },
      {
        "rel": "upgrade-guidance",
        "href": "https://20.73.223.44/api/components/dc3dee66-4939-4dea-b22f-ead288b4f117/versions/3d941ac5-cd4d-44b8-92cb-bd383ee1c301/origins/2899ce11-cb6d-4057-91e2-423eada58f5f/upgrade-guidance"
      },
      {
        "rel": "component-origin-copyrights",
        "href": "https://20.73.223.44/api/components/dc3dee66-4939-4dea-b22f-ead288b4f117/versions/3d941ac5-cd4d-44b8-92cb-bd383ee1c301/origins/2899ce11-cb6d-4057-91e2-423eada58f5f/copyrights"
      }
    ]
  }
*/