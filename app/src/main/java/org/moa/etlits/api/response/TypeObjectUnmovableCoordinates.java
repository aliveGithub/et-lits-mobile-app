package org.moa.etlits.api.response;

import java.util.Date;

public class TypeObjectUnmovableCoordinates {
     private String category;
     private String key;
     private String sysUser;
     private Date sysFrom;
     private String geometry;

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

    public String getGeometry() {
            return geometry;
        }
        public void setGeometry(String geometry) {
            this.geometry = geometry;
        }
}
