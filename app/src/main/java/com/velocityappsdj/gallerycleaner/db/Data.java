package com.velocityappsdj.gallerycleaner.db;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "DeleteData")
public class Data {
    @PrimaryKey(autoGenerate = true)
    private int id;
    @ColumnInfo(name = "_ID")
    private int _ID;
    @ColumnInfo(name = "description")
    private String description;
    @ColumnInfo(name = "folderName")
    private String folderName;
    @ColumnInfo(name = "imagePath")
    private String imagePath;
    @ColumnInfo(name = "dateTaken")
    private Long dateTaken;
    @ColumnInfo(name = "displayName")
    private String displayName;
    @ColumnInfo(name = "size")
    private int size;

    @Ignore
    public Data(String imagePath, String folderName) {
        this.imagePath = imagePath;
        this.folderName = folderName;
    }

    public Data(int id, int _ID, String description, String folderName, String imagePath, Long dateTaken, String displayName, int size) {
        this.id = id;
        this._ID = _ID;
        this.description = description;
        this.folderName = folderName;
        this.imagePath = imagePath;
        this.dateTaken = dateTaken;
        this.displayName = displayName;
        this.size = size;
    }

    public String getDescription() {
        return description;
    }

    public String getImagePath() {
        return imagePath;
    }

    public Data setDescription(String description) {
        this.description = description;
        return this;
    }

    public String getFolderName() {
        return folderName;
    }

    public Data setFolderName(String folderName) {
        this.folderName = folderName;
        return this;
    }

    public Data setImagePath(String imagePath) {
        this.imagePath = imagePath;
        return this;
    }

    public Long getDateTaken() {
        return dateTaken;
    }

    public Data setDateTaken(Long dateTaken) {
        this.dateTaken = dateTaken;
        return this;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Data setDisplayName(String displayName) {
        this.displayName = displayName;
        return this;
    }

    public int getSize() {
        return size;
    }

    public Data setSize(int size) {
        this.size = size;
        return this;
    }

    public int getId() {
        return id;
    }

    public Data setId(int id) {
        this.id = id;
        return this;
    }

    public int get_ID() {
        return _ID;
    }

    public Data set_ID(int _ID) {
        this._ID = _ID;
        return this;
    }
}
