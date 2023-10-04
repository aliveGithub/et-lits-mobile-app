package org.moa.etlits.data.models;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "category_values", primaryKeys = {"category_key","value_id","language"})
public class CategoryValue {

    @ColumnInfo(name = "category_key")
    @NonNull
    private String categoryKey;

    @ColumnInfo(name = "value")
    private String value;

    @ColumnInfo(name = "value_id")
    @NonNull
    private String valueId;

    @ColumnInfo(name = "language")
    @NonNull
    private String language;

    public String getCategoryKey() {
        return categoryKey;
    }

    public void setCategoryKey(String categoryKey) {
        this.categoryKey = categoryKey;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getValueId() {
        return valueId;
    }

    public void setValueId(String valueId) {
        this.valueId = valueId;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String toString() {
        if (getValue() != null) {
            return getValue();
        }
        return  "";
    }
}
