package org.moa.etlits.jobs;

public class SyncStoppedException extends Throwable {
    public SyncStoppedException() {
        super();
    }

    public SyncStoppedException(String message) {
        super(message);
    }
}
