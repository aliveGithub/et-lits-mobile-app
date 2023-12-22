package org.moa.etlits.data.models;

import java.util.ArrayList;
import java.util.List;

public class AnimalListData {
    private List<AnimalSearchResult> animalsList = new ArrayList<>();

    private List<CategoryValue> categoryValueList = new ArrayList<>();
    public List<AnimalSearchResult> getAnimalsList() {
        return animalsList;
    }
    public void setAnimalsList(List<AnimalSearchResult> animalsList) {
        this.animalsList = animalsList;
    }

    public List<CategoryValue> getCategoryValueList() {
        return categoryValueList;
    }

    public void setCategoryValueList(List<CategoryValue> categoryValueList) {
        this.categoryValueList = categoryValueList;
    }

}
