package com.example.mopgyphi.Helper.roomHelper;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface GifDao {

        @Query("SELECT * FROM GifEntity")
        List<GifEntity> getAll();

        @Insert
        void insertAll(GifEntity... gifEntities);

        @Insert
        void insert(GifEntity gifEntity);

        @Query("DELETE FROM GifEntity")
        void deleteAll();
}
