package org.moa.etlits.data.models;

import java.util.Date;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "animal_registrations")
public class AnimalRegistration {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private long id;

    @ColumnInfo(name = "method")
    private String method;
    @ColumnInfo(name = "date_identification")
    private Date dateIdentification;
    @ColumnInfo(name = "date_move_off")
    private Date dateMoveOff;
    @ColumnInfo(name = "holding_ground_eid")
    private String holdingGroundEid;
    @ColumnInfo(name = "date_move_on")
    private Date dateMoveOn;

    @ColumnInfo(name = "establishment_eid")
    private String establishmentEid;

    @ColumnInfo(name = "last_synced")
    private Date lastSynced;

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

    public Date getDateMoveOff() {
        return dateMoveOff;
    }

    public void setDateMoveOff(Date dateMoveOff) {
        this.dateMoveOff = dateMoveOff;
    }

    public String getHoldingGroundEid() {
        return holdingGroundEid;
    }

    public void setHoldingGroundEid(String holdingGroundEid) {
        this.holdingGroundEid = holdingGroundEid;
    }

    public Date getDateMoveOn() {
        return dateMoveOn;
    }

    public void setDateMoveOn(Date dateMoveOn) {
        this.dateMoveOn = dateMoveOn;
    }

    public String getEstablishmentEid() {
        return establishmentEid;
    }

    public void setEstablishmentEid(String establishmentEid) {
        this.establishmentEid = establishmentEid;
    }

    public Date getLastSynced() {
        return lastSynced;
    }

    public void setLastSynced(Date lastSynced) {
        this.lastSynced = lastSynced;
    }
}
