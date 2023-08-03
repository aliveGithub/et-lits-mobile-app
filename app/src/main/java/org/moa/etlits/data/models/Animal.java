package org.moa.etlits.data.models;

import java.util.Date;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "animals")
public class Animal {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private long id;

    @ColumnInfo(name = "animal_id")
    private String tag;

    @ColumnInfo(name = "method")
    private String method;

    @ColumnInfo(name = "date_identification")
    private Date dateIdentification;

    public Animal() {
    }

    public Animal(long id, String tag, String method, Date dateIdentification) {
        this.id = id;
        this.tag = tag;
        this.method = method;
        this.dateIdentification = dateIdentification;
    }


    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Date getDateIdentification() {
        return dateIdentification;
    }

    public void setDateIdentification(Date dateIdentification) {
        this.dateIdentification = dateIdentification;
    }
}
