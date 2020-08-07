package com.telekom.timon.leanix.ucmdbdata;

public class Cis {
    public String ucmdbId;
    public String globalId;
    public String type;
    public Properties properties;
    public String identifier;
    public String label;

    public String getUcmdbId() {
        return ucmdbId;
    }

    public Cis setUcmdbId(final String ucmdbId) {
        this.ucmdbId = ucmdbId;
        return this;
    }

    public String getGlobalId() {
        return globalId;
    }

    public Cis setGlobalId(final String globalId) {
        this.globalId = globalId;
        return this;
    }

    public String getType() {
        return type;
    }

    public Cis setType(final String type) {
        this.type = type;
        return this;
    }

    public Properties getProperties() {
        return properties;
    }

    public Cis setProperties(final Properties properties) {
        this.properties = properties;
        return this;
    }

    public String getIdentifier() {
        return identifier;
    }

    public Cis setIdentifier(final String identifier) {
        this.identifier = identifier;
        return this;
    }

    public String getLabel() {
        return label;
    }

    public Cis setLabel(final String label) {
        this.label = label;
        return this;
    }
}
