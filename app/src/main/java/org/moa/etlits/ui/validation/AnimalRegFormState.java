package org.moa.etlits.ui.validation;

import org.moa.etlits.R;
import org.moa.etlits.data.models.AnimalRegistration;
import org.moa.etlits.utils.ValidationUtils;

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

    public void validateMoveEvents(AnimalRegistration animalRegistration) {
        if (animalRegistration == null) {
            return;
        }
        if (ValidationUtils.isEmpty(animalRegistration.getDateIdentification())) {
            this.setDateIdentificationError(R.string.animal_reg_field_required);
        } else {
            if (ValidationUtils.dateInFuture(animalRegistration.getDateIdentification())) {
                this.setDateIdentificationError(R.string.animal_reg_date_in_future);
            }
        }

        if (ValidationUtils.isEmpty(animalRegistration.getDateMoveOff())) {
            this.setDateMoveOffError(R.string.animal_reg_field_required);
        } else {
            if (ValidationUtils.dateInFuture(animalRegistration.getDateMoveOff())) {
                this.setDateMoveOffError(R.string.animal_reg_date_in_future);
            }
            if (ValidationUtils.dateIsAfter(animalRegistration.getDateIdentification(), animalRegistration.getDateMoveOff())) {
                this.setDateMoveOffError(R.string.animal_reg_identification_date_after_move_off);
            }
        }

        if (ValidationUtils.isEmpty(animalRegistration.getHoldingGroundEid())) {
            this.setHoldingGroundEidError(R.string.animal_reg_field_required);
        }

        if (ValidationUtils.isEmpty(animalRegistration.getDateMoveOn())) {
            this.setDateMoveOnError(R.string.animal_reg_field_required);
        } else {
            if (ValidationUtils.dateInFuture(animalRegistration.getDateMoveOn())) {
                this.setDateMoveOnError(R.string.animal_reg_date_in_future);
            }
            if (ValidationUtils.dateIsAfter(animalRegistration.getDateMoveOff(), animalRegistration.getDateMoveOn())) {
                this.setDateMoveOnError(R.string.animal_reg_move_off_date_after_move_on);
            }
        }

        if (ValidationUtils.isEmpty(animalRegistration.getEstablishmentEid())) {
            this.setEstablishmentEidError(R.string.animal_reg_field_required);
        }
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