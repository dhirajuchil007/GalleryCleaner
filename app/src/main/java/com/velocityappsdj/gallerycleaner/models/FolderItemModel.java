package com.velocityappsdj.gallerycleaner.models;

import com.velocityappsdj.gallerycleaner.db.Data;

import java.util.List;

public class FolderItemModel {
    private String name;
    private List<Data> imagesData;

    public String getName() {
        return name;
    }

    public FolderItemModel setName(String name) {
        this.name = name;
        return this;
    }

    public List<Data> getImagesData() {
        return imagesData;
    }

    public FolderItemModel setImagesData(List<Data> imagesData) {
        this.imagesData = imagesData;
        return this;
    }


}
