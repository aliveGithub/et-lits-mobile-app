package org.moa.etlits.api.response;

import java.util.Date;

public class TypeObjectUnmovable {
    private String category;
    private String key;
    private String sysUser;
    private Date sysFrom;
    private Establishment establishment;
    private Location postalLocation;
    private Location physicalLocation;
    private TypeContactDetails contactDetails;


    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getSysUser() {
        return sysUser;
    }

    public void setSysUser(String sysUser) {
        this.sysUser = sysUser;
    }

    public Date getSysFrom() {
        return sysFrom;
    }

    public void setSysFrom(Date sysFrom) {
        this.sysFrom = sysFrom;
    }

    public Establishment getEstablishment() {
        return establishment;
    }

    public void setEstablishment(Establishment establishment) {
        this.establishment = establishment;
    }

    public Location getPostalLocation() {
        return postalLocation;
    }

    public void setPostalLocation(Location postalLocation) {
        this.postalLocation = postalLocation;
    }

    public Location getPhysicalLocation() {
        return physicalLocation;
    }

    public void setPhysicalLocation(Location physicalLocation) {
        this.physicalLocation = physicalLocation;
    }

    public TypeContactDetails getContactDetails() {
        return contactDetails;
    }

    public void setContactDetails(TypeContactDetails contactDetails) {
        this.contactDetails = contactDetails;
    }
}
