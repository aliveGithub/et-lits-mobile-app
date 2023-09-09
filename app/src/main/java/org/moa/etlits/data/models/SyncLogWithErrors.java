package org.moa.etlits.data.models;

import java.util.List;

import androidx.room.Embedded;
import androidx.room.Relation;

public class SyncLogWithErrors {
    @Embedded
    public SyncLog syncLog;
    @Relation(
            parentColumn = "id",
            entityColumn = "sync_log_id"
    )
    public List<SyncError> errors;
}
