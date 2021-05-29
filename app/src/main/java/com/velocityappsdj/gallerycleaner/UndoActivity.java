package com.velocityappsdj.gallerycleaner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.velocityappsdj.gallerycleaner.db.AppDatabase;
import com.velocityappsdj.gallerycleaner.db.Data;
import com.velocityappsdj.gallerycleaner.util.AppExecutors;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UndoActivity extends AppCompatActivity {
    private static final String TAG = "UndoActivity";
    private AppDatabase mDb;
    private RecyclerView undoRecycler;
    private Context context;
    private List<Data> dataList;
    private UndoListAdapter undoListAdapter;
    private Button deleteAll;
    private AdView mAdView;
    private ImageView imgBack;
    private TextView nothingText;
    private View mainContainer, processingContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_undo);
        context = this;
        initViews();
        dataList = new ArrayList<>();
        mDb = AppDatabase.getInstance(this);

        mDb.dataDao().loadAllData().observe(UndoActivity.this, data -> {
            dataList = data;
            setUpList(dataList);
        });
        deleteAll.setOnClickListener(v -> {
            new AlertDialog.Builder(context).setMessage("Images below will be deleted from your device")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            new SharedPrefUtil(context).setCleaningDone(true);
                            deleteImages(dataList);
                        }
                    })
                    .setNegativeButton("cancel", ((dialog, which) -> dialog.dismiss()))
                    .show();

        });
        imgBack.setOnClickListener(v -> onBackPressed());
        setUpAds();

    }

    private void initViews() {
        undoRecycler = findViewById(R.id.undoRecycler);
        imgBack = findViewById(R.id.imgBack);
        deleteAll = findViewById(R.id.deleteAll);
        nothingText = findViewById(R.id.nothingText);
        mainContainer = findViewById(R.id.mainContainer);
        processingContainer = findViewById(R.id.processContainer);
    }

    private void setUpAds() {
        RequestConfiguration requestConfiguration = new RequestConfiguration.Builder().setTestDeviceIds(Arrays.asList("7F79B920BD3C90A7F8DE397779A69B4A")).build();
        MobileAds.setRequestConfiguration(requestConfiguration);
        MobileAds.initialize(getApplicationContext());
        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .build();
        mAdView.loadAd(adRequest);
    }

    private void setUpList(List<Data> data) {

        nothingText.setVisibility(View.GONE);
        undoListAdapter = new UndoListAdapter(data, context, new UndoListAdapter.OnImageClicked() {
            @Override
            public void OnTap(Data imageData) {
                data.remove(imageData);
                undoListAdapter.notifyDataSetChanged();
                removeFromDb(imageData);
            }
        });
        undoRecycler.setLayoutManager(new GridLayoutManager(context, calculateNoOfColumns(context, 130)));
        undoRecycler.setAdapter(undoListAdapter);
        if (data.size() == 0)
            nothingText.setVisibility(View.VISIBLE);
        else
            nothingText.setVisibility(View.GONE);
    }

    private void removeFromDb(Data imageData) {
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                mDb.dataDao().deleteData(imageData);
            }
        });
    }

    public int calculateNoOfColumns(Context context, float columnWidthDp) { // For example columnWidthdp=180
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float screenWidthDp = displayMetrics.widthPixels / displayMetrics.density;
        int noOfColumns = (int) (screenWidthDp / columnWidthDp + 0.5); // +0.5 for correct rounding to int.
        return noOfColumns;
    }

    public void deleteImages(List<Data> dataList) {
        toggleProcessingView(true);
        Log.d(TAG, "deleteImages() called with: dataList = [" + dataList.size() + "]");
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                for (Data data : dataList) {
                    File file = new File(data.getImagePath());
                    getContentResolver().delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, MediaStore.Images.Media.DATA + "=?", new String[]{file.getAbsolutePath()});
                    boolean isDeleted = file.getAbsoluteFile().delete();
                    if (file.exists()) {
                        getContentResolver().delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, MediaStore.Images.Media.DATA + "=?", new String[]{file.getAbsolutePath()});
                    }
                    Log.d(TAG, "run: isfiledeleted " + isDeleted);


                    removeFromDb(data);
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        toggleProcessingView(false);
                        Toast.makeText(context, "Images deleted", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });


    }

    private void toggleProcessingView(boolean isProcessing) {
        mainContainer.setVisibility(isProcessing ? View.GONE : View.VISIBLE);
        processingContainer.setVisibility(isProcessing ? View.VISIBLE : View.GONE);
    }


}
