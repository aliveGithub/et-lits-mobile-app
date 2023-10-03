package org.moa.etlits.data.dao;


import org.moa.etlits.data.models.CategoryValue;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface CategoryValueDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(CategoryValue categoryValue);

    @Update
    void update(CategoryValue categoryValue);

    @Query("SELECT * FROM category_values")
    LiveData<List<CategoryValue>> getAllCategoryValues();

   @Query("SELECT * FROM category_values where category_key=:categoryKey")
    LiveData<List<CategoryValue>> loadByCategoryKey(String categoryKey);

    @Query("SELECT * FROM category_values where value_id=:valueId")
    LiveData<CategoryValue> loadByValueId(String valueId);
}
