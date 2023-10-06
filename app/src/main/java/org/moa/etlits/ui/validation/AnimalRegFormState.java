package org.moa.etlits.ui.validation;

import androidx.annotation.Nullable;

public class AnimalRegFormState {
    @Nullable
    private Integer dateIdentificationError;
    @Nullable
    private Integer dateMoveOffError;
    @Nullable
    private Integer holdingGroundEidError;
    @Nullable
    private Integer dateMoveOnError;
    @Nullable
    private Integer establishmentEidError;

    public AnimalRegFormState() {
    }

    @Nullable
    public Integer getDateIdentificationError() {
        return dateIdentificationError;
    }

    public void setDateIdentificationError(@Nullable Integer dateIdentificationError) {
        this.dateIdentificationError = dateIdentificationError;
    }

    @Nullable
    public Integer getDateMoveOffError() {
        return dateMoveOffError;
    }

    public void setDateMoveOffError(@Nullable Integer dateMoveOffError) {
        this.dateMoveOffError = dateMoveOffError;
    }

    @Nullable
    public Integer getHoldingGroundEidError() {
        return holdingGroundEidError;
    }

    public void setHoldingGroundEidError(@Nullable Integer holdingGroundEidError) {
        this.holdingGroundEidError = holdingGroundEidError;
    }

    @Nullable
    public Integer getDateMoveOnError() {
        return dateMoveOnError;
    }

    public void setDateMoveOnError(@Nullable Integer dateMoveOnError) {
        this.dateMoveOnError = dateMoveOnError;
    }

    @Nullable
    public Integer getEstablishmentEidError() {
        return establishmentEidError;
    }

    public void setEstablishmentEidError(@Nullable Integer establishmentEidError) {
        this.establishmentEidError = establishmentEidError;
    }

    public boolean isDataValid() {
        return dateIdentificationError == null && dateMoveOffError == null && holdingGroundEidError == null && dateMoveOnError == null && establishmentEidError == null;
    }
}