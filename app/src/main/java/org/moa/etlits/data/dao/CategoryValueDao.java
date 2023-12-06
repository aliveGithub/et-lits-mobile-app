package org.moa.etlits.data.dao;


import org.moa.etlits.data.models.CategoryValue;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

@Dao
public abstract class CategoryValueDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insert(CategoryValue categoryValue);

    @Update
    public abstract void update(CategoryValue categoryValue);

    @Query("SELECT * FROM category_values")
    public abstract LiveData<List<CategoryValue>> getAllCategoryValues();

   @Query("SELECT * FROM category_values where category_key=:categoryKey")
   public abstract LiveData<List<CategoryValue>> loadByCategoryKey(String categoryKey);

    @Query("SELECT * FROM category_values where value_id=:valueId")
    public abstract LiveData<CategoryValue> loadByValueId(String valueId);

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insertAll(List<CategoryValue> categoryValues) {
        for (CategoryValue categoryValue : categoryValues) {
            insert(categoryValue);
        }
    }
}
