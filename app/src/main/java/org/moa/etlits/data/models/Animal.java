package org.moa.etlits.data.models;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "animals", indices = {@Index(value = {"animal_id"}, unique = true)})
public class Animal implements java.io.Serializable, Comparable<Animal> {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private long id;

    @NonNull
    @ColumnInfo(name = "animal_id")
    private String animalId;
    @ColumnInfo(name = "breed")
    private String breed;

    @ColumnInfo(name = "age")
    private Integer age;

    @ColumnInfo(name = "dead")
    private boolean dead;

    @ColumnInfo(name = "sex")
    private String sex;

    @ColumnInfo(name = "seller")
    private String seller;

    @NonNull
    @ColumnInfo(name = "animal_registration_id")
    private long animalRegistrationId;

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

    public String getBreed() {
        return breed;
    }

    public void setBreed(String breed) {
        this.breed = breed;
    }


    public boolean isDead() {
        return dead;
    }

    public void setDead(boolean dead) {
        this.dead = dead;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getSeller() {
        return seller;
    }

    public void setSeller(String seller) {
        this.seller = seller;
    }
    public long getAnimalRegistrationId() {
        return animalRegistrationId;
    }

    public void setAnimalRegistrationId(long animalRegistrationId) {
        this.animalRegistrationId = animalRegistrationId;
    }

    @Override
    public String toString() {
        return animalId + " - " + breed;
    }

    @Override
    public int compareTo(Animal o) {
        return this.animalId.compareTo(o.animalId);
    }
}
