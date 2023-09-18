package org.moa.etlits.utils;

public class Constants {
    public static final String SYNC_LOG_ID = "syncLogId";
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";

    //sync error codes
    public static String UNKNOWN_SYNC_ERROR = "UNKNOWN_SYNC_ERROR";


    public enum SyncType {
        CONFIG_DATA,
        ALL_DATA
    }

    public enum SyncStatus {
        INITIALIZING,
        IN_PROGRESS,
        STOPPING,
        STOPPED,
        SUCCESSFUL,
        FAILED
    }
}

