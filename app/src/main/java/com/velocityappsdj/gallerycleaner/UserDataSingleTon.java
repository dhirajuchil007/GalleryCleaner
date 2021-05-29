package com.velocityappsdj.gallerycleaner;

import com.velocityappsdj.gallerycleaner.db.Data;
import com.velocityappsdj.gallerycleaner.models.FolderItemModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class UserDataSingleTon {
    private static UserDataSingleTon instance;
    public List<Data> allImages;
    public HashMap<String, FolderItemModel> folderMap;
    public String selectedFolder;

    private UserDataSingleTon() {
        allImages = new ArrayList<>();
        folderMap = new HashMap<>();
        selectedFolder = ApplicationConstants.ALL;
    }

    public static UserDataSingleTon getInstance() {
        if (instance == null)
            instance = new UserDataSingleTon();

        return instance;
    }

    public void clearData() {
        instance = new UserDataSingleTon();
    }
}
