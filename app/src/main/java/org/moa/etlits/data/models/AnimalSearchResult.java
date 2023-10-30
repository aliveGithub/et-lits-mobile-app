package org.moa.etlits.data.models;

import java.util.Date;

import javax.annotation.Nullable;

public class AnimalSearchResult implements Comparable<AnimalSearchResult> {

    private long id = 0;

    @Nullable
    private String animalId = "";

    private String species = "Cattle";

    private String breed;

    private String sex;
    @Nullable
    private Integer age = 0;
    @Nullable
    private String eid;

    private String event = "Move On";

    @Nullable
    private Date eventDate;
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
    public String getAnimalId() {
        return animalId;
    }

    public void setAnimalId(String animalId) {
        this.animalId = animalId;
    }

    public String getEid() {
        return eid;
    }

    public void setEid(String eid) {
        this.eid = eid;
    }

    public String getSpecies() {
        return species;
    }

    public void setSpecies(String species) {
        this.species = species;
    }

    public String getBreed() {
        return breed;
    }

    public void setBreed(String breed) {
        this.breed = breed;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public Date getEventDate() {
        return eventDate;
    }

    public void setEventDate(Date eventDate) {
        this.eventDate = eventDate;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    @Override
    public int compareTo(AnimalSearchResult o) {
        return this.getAnimalId().compareTo(o.getAnimalId());
    }


    @Override
    public String toString() {
        return animalId + " " + species + " - EID " + eid;
    }
}
