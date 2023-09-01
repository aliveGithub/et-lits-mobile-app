package org.moa.etlits.data.repositories;

import android.app.Application;

import org.moa.etlits.data.dao.AppDatabase;
import org.moa.etlits.data.dao.CategoryValueDao;
import org.moa.etlits.data.models.CategoryValue;

import java.util.List;

import androidx.lifecycle.LiveData;

public class CategoryValueRepository {
    private CategoryValueDao categoryValueDao;

    private LiveData<List<CategoryValue>> categoryValues;

    public CategoryValueRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        categoryValueDao = db.categoryValueDao();
        categoryValues = categoryValueDao.getAllCategoryValues();
    }

    public LiveData<List<CategoryValue>> getAllCategoryValues() {
        return categoryValues;
    }

    public void insert(CategoryValue categoryValue) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            categoryValueDao.insert(categoryValue);
        });
    }

    public void update(CategoryValue categoryValue) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            categoryValueDao.update(categoryValue);
        });
    }

    public LiveData<CategoryValue>loadByType(String categoryKey){
        return categoryValueDao.loadByCategoryKey(categoryKey);
    }

    public LiveData<CategoryValue>loadByValueId(String valueId){
        return categoryValueDao.loadByValueId(valueId);
    }
}
