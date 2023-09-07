package org.moa.etlits.data.models;

import java.util.Date;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "sync_logs")
public class SyncLog {
    public SyncLog(@NonNull String id, Date lastSync, String type, String status) {
        this.id = id;
        this.lastSync = lastSync;
        this.type = type;
        this.status = status;
    }

    public SyncLog(){
    }

    @PrimaryKey
    @ColumnInfo(name = "id")
    @NonNull
    private String id;
    @ColumnInfo(name = "last_sync")
    private Date lastSync;
    @ColumnInfo(name = "type")
    private String type;
    @ColumnInfo(name = "status")
    private String status;

    @ColumnInfo(name = "records_to_send")
    private int recordsToSend;
    @ColumnInfo(name = "records_sent")
    private int recordsSent;
    @ColumnInfo(name = "records_received")
    private int recordsReceived;
    private int recordsNotSent;
    @ColumnInfo(name = "synced_by")
    private String syncedBy;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getLastSync() {
        return lastSync;
    }

    public void setLastSync(Date lastSync) {
        this.lastSync = lastSync;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getRecordsSent() {
        return recordsSent;
    }

    public void setRecordsSent(int recordsSent) {
        this.recordsSent = recordsSent;
    }

    public int getRecordsReceived() {
        return recordsReceived;
    }

    public void setRecordsReceived(int recordsReceived) {
        this.recordsReceived = recordsReceived;
    }

    public int getRecordsNotSent() {
        return recordsNotSent;
    }

    public void setRecordsNotSent(int recordsNoSent) {
        this.recordsNotSent = recordsNoSent;
    }

    public String getSyncedBy() {
        return syncedBy;
    }

    public void setSyncedBy(String syncedBy) {
        this.syncedBy = syncedBy;
    }

    public int getRecordsToSend() {
        return recordsToSend;
    }

    public void setRecordsToSend(int recordsToSend) {
        this.recordsToSend = recordsToSend;
    }

}

