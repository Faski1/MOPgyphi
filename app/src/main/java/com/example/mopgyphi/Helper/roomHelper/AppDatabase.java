package com.example.mopgyphi.Helper.roomHelper;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {GifEntity.class}, version = 2)
public abstract class AppDatabase extends RoomDatabase {
    public abstract GifDao gifDao();
}