package org.moa.etlits.api.request;



import com.google.gson.annotations.SerializedName;

import org.moa.etlits.data.models.Animal;
import org.moa.etlits.data.models.AnimalRegistration;
import org.moa.etlits.data.models.Treatment;
import org.moa.etlits.utils.DateUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AnimalRegRequest {
    private String method;
    @SerializedName("date_identification")
    private String dateIdentification;
    @SerializedName("date_move_off")
    private String dateMoveOff;
    @SerializedName("holding_ground_eid")
    private String holdingGroundEid;

    @SerializedName("date_move_on")
    private String dateMoveOn;
    @SerializedName("establishment_eid")
    private String establishmentEid;

    private List<HashMap<String, Object>> animals;
    @SerializedName("treatment_applied")
    private List<String> treatmentApplied;

    public AnimalRegRequest(AnimalRegistration animalRegistration, List<Animal> animals, List<Treatment> treatmentApplied) {
        this.method = method;
        this.animals = new ArrayList<>();
        this.treatmentApplied = new ArrayList<>();
        this.method = animalRegistration.getMethod();
        for (Treatment treatment : treatmentApplied) {
            this.treatmentApplied.add(treatment.getTreatmentApplied());
        }

        this.dateIdentification = DateUtils.formatDateIso(animalRegistration.getDateIdentification());
        this.dateMoveOff = DateUtils.formatDateIso(animalRegistration.getDateMoveOff());
        this.holdingGroundEid = animalRegistration.getHoldingGroundEid();
        this.dateMoveOn = DateUtils.formatDateIso(animalRegistration.getDateMoveOn());
        this.establishmentEid = animalRegistration.getEstablishmentEid();

        for (Animal a : animals) {
            HashMap<String, Object> animal = new HashMap<>();
            animal.put("animal_id", a.getAnimalId());
            animal.put("breed", a.getBreed());
            animal.put("date_birth", DateUtils.formatDateIso(DateUtils.getDateOfBirth(a.getAge())));
            animal.put("dead", a.isDead());
            animal.put("sex", a.getSex());
            animal.put("seller", a.getSeller());
            this.animals.add(animal);
        }
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getDateIdentification() {
        return dateIdentification;
    }

    public void setDateIdentification(String dateIdentification) {
        this.dateIdentification = dateIdentification;
    }

    public String getDateMoveOff() {
        return dateMoveOff;
    }

    public void setDateMoveOff(String dateMoveOff) {
        this.dateMoveOff = dateMoveOff;
    }

    public String getHoldingGroundEid() {
        return holdingGroundEid;
    }

    public void setHoldingGroundEid(String holdingGroundEid) {
        this.holdingGroundEid = holdingGroundEid;
    }

    public String getDateMoveOn() {
        return dateMoveOn;
    }

    public void setDateMoveOn(String dateMoveOn) {
        this.dateMoveOn = dateMoveOn;
    }

    public String getEstablishmentEid() {
        return establishmentEid;
    }

    public void setEstablishmentEid(String establishmentEid) {
        this.establishmentEid = establishmentEid;
    }

    public List<String> getTreatmentApplied() {
        return treatmentApplied;
    }

    public void setTreatmentApplied(List<String> treatmentApplied) {
        this.treatmentApplied = treatmentApplied;
    }
}
