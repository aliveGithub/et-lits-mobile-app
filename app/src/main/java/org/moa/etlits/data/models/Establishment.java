package org.moa.etlits.data.models;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;

@Entity(tableName = "establishments", primaryKeys = {"code","name"})
public class Establishment implements Comparable<Establishment> {
    @NonNull
    @ColumnInfo(name = "code")
    private String code;
    @ColumnInfo(name = "type")
    private String type;
    @ColumnInfo(name = "category")
    private String category;
    @NonNull
    @ColumnInfo(name = "name")
    private String name;
    @ColumnInfo(name = "latitude")
    private String latitude;
    @ColumnInfo(name = "longitude")
    private String longitude;

    @ColumnInfo(name = "city")
    private String city;
    @ColumnInfo(name = "region")
    private String region;
    @ColumnInfo(name = "woreda")
    private String woreda;

    @ColumnInfo(name = "zone")
    private String zone;

    @ColumnInfo(name = "alternative_postal_address")
    private String alternativePostalAddress;

    @ColumnInfo(name = "telephone_number")
    private String telephoneNumber;

    @ColumnInfo(name = "mobile_number")
    private String mobileNumber;

    @ColumnInfo(name = "email")
    private String email;

   public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLatitude() {
        if (latitude == null) {
            return "";
        }

       return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
       if (longitude == null) {
            return "";
       }
       return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getCity() {
        if (city == null) {
            return "";
        }

        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getRegion() {
        if (region == null) {
            return "";
        }

        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getWoreda() {
        if (woreda == null) {
            return "";
        }
       return woreda;
    }

    public void setWoreda(String woreda) {
        this.woreda = woreda;
    }

    public String getZone() {
        if (zone == null) {
            return "";
        }

        return zone;
    }

    public void setZone(String zone) {
        this.zone = zone;
    }

    public String getAlternativePostalAddress() {
        return alternativePostalAddress;
    }

    public void setAlternativePostalAddress(String alternativePostalAddress) {
        this.alternativePostalAddress = alternativePostalAddress;
    }

    public String getTelephoneNumber() {
        return telephoneNumber;
    }

    public void setTelephoneNumber(String telephoneNumber) {
        this.telephoneNumber = telephoneNumber;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String toString() {
       return code + " - " + name.substring(0, Math.min(name.length(), 20)) + " ...";
    }

    public String getCodeAndName() {
        return code + " - " + name;
    }

    public String getPhysicalAddress() {
      return (getCity() + " " + getWoreda() + " " + getZone() + " " + getRegion()).trim();
    }

    public String getLatLng() {
        return (getLatitude() + " " + getLongitude()).trim();
    }

    @Override
    public int compareTo(Establishment est2) {
        return this.name.compareTo(est2.name);
    }
}
