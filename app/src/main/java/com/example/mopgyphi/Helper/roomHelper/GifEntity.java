package com.example.mopgyphi.Helper.roomHelper;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
    @Entity
    public class GifEntity {

        public GifEntity(){}
        public GifEntity(String ddownsurl, String ffixedhurl)
        {
            downsurl=ddownsurl;
            fixedhurl=ffixedhurl;
        }
        @PrimaryKey(autoGenerate = true)
        @NonNull
        public int gid;

        @ColumnInfo(name = "downsurl")
        public String downsurl;

        @ColumnInfo(name = "fixedhurl")
        public String fixedhurl;
    }