package org.moa.etlits.data.models;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "treatments")
public class Treatment implements java.io.Serializable {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private long id;
    @NonNull
    @ColumnInfo(name = "treatment_applied")
    private String treatmentApplied;
    @NonNull
    @ColumnInfo(name = "animal_registration_id")
    private long animalRegistrationId;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getAnimalRegistrationId() {
        return animalRegistrationId;
    }

    public void setAnimalRegistrationId(long animalRegistrationId) {
        this.animalRegistrationId = animalRegistrationId;
    }

    @NonNull
    public String getTreatmentApplied() {
        return treatmentApplied;
    }

    public void setTreatmentApplied(@NonNull String treatmentApplied) {
        this.treatmentApplied = treatmentApplied;
    }
}
