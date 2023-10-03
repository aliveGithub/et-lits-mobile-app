package org.moa.etlits.ui.validation;

import androidx.annotation.Nullable;

public class AnimalFormState {

    @Nullable
    private Integer animalIdError;
    @Nullable
    private Integer breedError;
    @Nullable
    private Integer sexError;

    @Nullable
    private Integer ageError;

    @Nullable
    private Integer sellerError;



    @Nullable
    public Integer getAnimalIdError() {
        return animalIdError;
    }

    public void setAnimalIdError(@Nullable Integer animalIdError) {
        this.animalIdError = animalIdError;
    }

    @Nullable
    public Integer getBreedError() {
        return breedError;
    }

    public void setBreedError(@Nullable Integer breedError) {
        this.breedError = breedError;
    }

    @Nullable
    public Integer getSexError() {
        return sexError;
    }

    public void setSexError(@Nullable Integer sexError) {
        this.sexError = sexError;
    }

    @Nullable
    public Integer getAgeError() {
        return ageError;
    }

    public void setAgeError(@Nullable Integer ageError) {
        this.ageError = ageError;
    }

    @Nullable
    public Integer getSellerError() {
        return sellerError;
    }

    public void setSellerError(@Nullable Integer sellerError) {
        this.sellerError = sellerError;
    }


    public AnimalFormState() {
    }
    public AnimalFormState(@Nullable Integer animalIdError, @Nullable Integer breedError, @Nullable Integer sexError, @Nullable Integer ageError, @Nullable Integer sellerError) {
        this.animalIdError = animalIdError;
        this.breedError = breedError;
        this.sexError = sexError;
        this.ageError = ageError;
        this.sellerError = sellerError;
    }

    public boolean isDataValid() {
        return animalIdError == null && breedError == null && sexError == null && ageError == null && sellerError == null;
    }
}