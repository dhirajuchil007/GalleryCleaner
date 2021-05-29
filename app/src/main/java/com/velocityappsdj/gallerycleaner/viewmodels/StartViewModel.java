package com.velocityappsdj.gallerycleaner.viewmodels;

import android.app.Application;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.velocityappsdj.gallerycleaner.db.Data;
import com.velocityappsdj.gallerycleaner.UserDataSingleTon;
import com.velocityappsdj.gallerycleaner.models.FolderItemModel;
import com.velocityappsdj.gallerycleaner.util.AppExecutors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class StartViewModel extends AndroidViewModel {
    private static final String TAG = "StartViewModel";
    public MutableLiveData<Boolean> isDataLoaded = new MutableLiveData<>();
    public MutableLiveData<Boolean> isProcessing = new MutableLiveData<>();
    public MutableLiveData<Integer> noOfItemLoaded = new MutableLiveData<>();
    public MutableLiveData<Integer> totalItems = new MutableLiveData<>();


    Cursor cur;

    public StartViewModel(@NonNull Application application) {
        super(application);
    }


    public void getImagesList(List<Data> toBeDeleted) {
        UserDataSingleTon.getInstance().clearData();
        isProcessing.postValue(true);
        isDataLoaded.postValue(false);
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                String[] projection = new String[]{
                        MediaStore.Images.Media._ID,
                        MediaStore.Images.Media.BUCKET_ID,
                        MediaStore.Images.Media.DATE_TAKEN,
                        MediaStore.Images.Media.DATA,
                        MediaStore.Images.Media.RELATIVE_PATH,
                        MediaStore.Images.Media.SIZE,
                        MediaStore.Images.Media.DISPLAY_NAME
                };

                String cam = "%%";
                Uri images = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;


                cur = getApplication().getContentResolver().query(images,
                        projection, // Which columns to return
                        MediaStore.Images.Media.DATA + " like ?",       // Which rows to return (all rows)
                        new String[]{cam},       // Selection arguments (none)
                        MediaStore.Images.Media.DATE_TAKEN + " DESC"   // Ordering
                );


                setAllImageData(toBeDeleted);
            }
        });

    }


    public void setAllImageData(List<Data> toBeDeleted) {
        Log.d(TAG, "setAllImageData() called with: toBeDeleted = [" + toBeDeleted + "]");
        HashSet<Integer> toBeDeletedPathsSet = new HashSet<>();
        for (Data data : toBeDeleted) {
            toBeDeletedPathsSet.add(data.get_ID());
        }
        Log.d(TAG, "setAllImageData() called");
        ArrayList<Data> currentArray = new ArrayList<>();
        HashMap<String, FolderItemModel> folderHashMap = UserDataSingleTon.getInstance().folderMap;
        totalItems.postValue(cur.getCount());
        int count = 0;
        while (cur.moveToNext()) {


            int idIndex = cur.getColumnIndex(MediaStore.Images.Media._ID);

            int image_ID = cur.getInt(idIndex);
            if (!toBeDeletedPathsSet.contains(image_ID)) {
                int bitColumn = cur.getColumnIndex(MediaStore.Images.Media.DATA);
                int bucketCol = cur.getColumnIndex(MediaStore.Images.Media.BUCKET_ID);
                int dateIndex = cur.getColumnIndex(MediaStore.Images.Media.DATE_TAKEN);
                int nameIndex = cur.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME);
                int sizeIndex = cur.getColumnIndex(MediaStore.Images.Media.SIZE);
                String path = cur.getString(bitColumn);
                String bucket = cur.getString(bucketCol);

                Log.d("imagepath", path + " " + bucket);
                String folders[] = path.split("/");
                String folder = folders[folders.length - 2];
                Data data = new Data(path, folder).setDateTaken(cur.getLong(dateIndex))
                        .setSize(cur.getInt(sizeIndex)).setDisplayName(cur.getString(nameIndex)).set_ID(image_ID);
                currentArray.add(data);
                if (!folderHashMap.containsKey(folder)) {
                    FolderItemModel folderItemModel = new FolderItemModel();
                    folderItemModel.setImagesData(new ArrayList<>()).setName(folder);
                    folderHashMap.put(folder, folderItemModel);
                }
                folderHashMap.get(folder).getImagesData().add(data);

                count++;
                noOfItemLoaded.postValue(count);
            }
        }


        Log.d(TAG, "setAllImageData: images list size" + currentArray.size());
        Log.d(TAG, "setAllImageData: images list size" + folderHashMap.size());
        UserDataSingleTon.getInstance().allImages = currentArray;
        UserDataSingleTon.getInstance().folderMap = folderHashMap;
        isProcessing.postValue(false);
        isDataLoaded.postValue(true);
//        myAppAdapter.notifyDataSetChanged();

        //   cur.moveToFirst();


        // cur.moveToNext();
    }
}
