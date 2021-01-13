package org.eclipse.sw360.bdhubimport.domain;

public class BDHubProjectVersionsResponse {

    private int totalCount;
    private BDHubProjectVersion[] items;
    private BDHubMeta _meta;

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public BDHubProjectVersion[] getItems() {
        return items;
    }

    public void setItems(BDHubProjectVersion[] items) {
        this.items = items;
    }

    public BDHubMeta get_meta() {
        return _meta;
    }

    public void set_meta(BDHubMeta _meta) {
        this._meta = _meta;
    }
}


/*
{
  "totalCount": 10,
  "items": [...]
  "appliedFilters": [],
  "_meta":
}
*/