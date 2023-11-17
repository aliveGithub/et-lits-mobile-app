package org.moa.etlits.api.response;

import java.util.ArrayList;
import java.util.List;

public class ConfigResponse {
    private final List<CatalogType> catalogs;

    private final List<TypeObjectUnmovable> objectUnmovable;
    private final List<TypeObjectUnmovableCoordinates> objectUnmovableCoordinates;

    private final List<TypeObjectDetail> objectDetail;



    public ConfigResponse(List<CatalogType> catalogs, List<TypeObjectUnmovable> objectUnmovable,
                          List<TypeObjectUnmovableCoordinates> objectUnmovableCoordinates,
                          List<TypeObjectDetail> objectDetail) {
        this.catalogs = catalogs;
        this.objectUnmovable = objectUnmovable;
        this.objectUnmovableCoordinates = objectUnmovableCoordinates;
        this.objectDetail = objectDetail;
    }

    public List<CatalogType> getCatalogs() {
        return catalogs;
    }

    public List<TypeObjectUnmovable> getObjectUnmovable() {
        return objectUnmovable;
    }

    public List<TypeObjectDetail> getObjectDetail() {
        return objectDetail;
    }

    public List<TypeObjectUnmovableCoordinates> getObjectUnmovableCoordinates() {
        return objectUnmovableCoordinates;
    }
}
