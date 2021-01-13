package org.eclipse.sw360.bdhubimport.domain;

public class BDHubComponentsResponse {

    private int totalCount;
    private BDHubComponent[] items;
    private BDHubMeta _meta;

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public BDHubComponent[] getItems() {
        return items;
    }

    public void setItems(BDHubComponent[] items) {
        this.items = items;
    }

    public BDHubMeta get_meta() {
        return _meta;
    }

    public void set_meta(BDHubMeta _meta) {
        this._meta = _meta;
    }
}
