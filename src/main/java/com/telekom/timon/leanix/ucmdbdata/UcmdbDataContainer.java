package com.telekom.timon.leanix.ucmdbdata;

import java.util.List;

public class UcmdbDataContainer {
    public List<Cis> cis;
    public List<Relations> relations;

    public List<Cis> getCis() {
        return cis;
    }

    public UcmdbDataContainer setCis(final List<Cis> cis) {
        this.cis = cis;
        return this;
    }

    public List<Relations> getRelations() {
        return relations;
    }

    public UcmdbDataContainer setRelations(final List<Relations> relations) {
        this.relations = relations;
        return this;
    }
}
