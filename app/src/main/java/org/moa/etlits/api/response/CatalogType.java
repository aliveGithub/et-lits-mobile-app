package org.moa.etlits.api.response;


import java.util.ArrayList;
import java.util.List;

public class CatalogType {

    protected List<EntryType> entry;
    protected String key;

    public List<EntryType> getEntry() {
        if (entry == null) {
            entry = new ArrayList<EntryType>();
        }
        return this.entry;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String value) {
        this.key = value;
    }
}
