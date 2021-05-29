package com.velocityappsdj.gallerycleaner.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface DataDao {

    @Query("SELECT * FROM DeleteData")
    LiveData<List<Data>> loadAllData();

    @Insert
    void insertData(Data data);

    @Delete
    void deleteData(Data data);
}
