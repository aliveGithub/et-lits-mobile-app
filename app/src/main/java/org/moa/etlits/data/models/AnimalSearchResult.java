package org.moa.etlits.data.models;

public class AnimalSearchResult implements Comparable<AnimalSearchResult> {

    private long id;
    private String animalId;

    private String species = "Cattle";

    private String eid;
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

    @Override
    public int compareTo(AnimalSearchResult o) {
        return this.getAnimalId().compareTo(o.getAnimalId());
    }


    @Override
    public String toString() {
        return animalId + " " + species + " - " + eid;
    }
}
