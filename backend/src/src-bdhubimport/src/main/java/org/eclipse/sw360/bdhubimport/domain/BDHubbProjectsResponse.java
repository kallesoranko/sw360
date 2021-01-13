package org.eclipse.sw360.bdhubimport.domain;

public class BDHubbProjectsResponse {

    private int totalCount;
    private BDHubProject[] items;
    private BDHubMeta _meta;

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public BDHubProject[] getItems() {
        return items;
    }

    public void setItems(BDHubProject[] items) {
        this.items = items;
    }

    public BDHubMeta get_meta() {
        return _meta;
    }

    public void set_meta(BDHubMeta _meta) {
        this._meta = _meta;
    }
}
