package org.moa.etlits.data.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(foreignKeys = @ForeignKey(
        entity = SyncLog.class,
        parentColumns = "id",
        childColumns = "sync_log_id",
        onDelete = ForeignKey.CASCADE))
public class SyncError {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private long id;
    @ColumnInfo(name = "error_key")
    private String errorKey; // code or key - type of error
    @ColumnInfo(name = "message")
    private String message;
    @ColumnInfo(name = "sync_log_id")
    private String syncLogId;

    public SyncError() {
    }
    public SyncError(String syncLogId, String errorKey, String message) {
        this.syncLogId = syncLogId;
        this.errorKey = errorKey;
        this.message = message;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }


    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSyncLogId() {
        return syncLogId;
    }

    public void setSyncLogId(String syncLogId) {
        this.syncLogId = syncLogId;
    }


    public String getErrorKey() {
        return errorKey;
    }

    public void setErrorKey(String errorKey) {
        this.errorKey = errorKey;
    }
}
