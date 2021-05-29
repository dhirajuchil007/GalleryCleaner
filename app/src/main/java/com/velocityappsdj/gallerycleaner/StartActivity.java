package com.velocityappsdj.gallerycleaner;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;
import com.google.android.play.core.review.model.ReviewErrorCode;
import com.google.android.play.core.review.testing.FakeReviewManager;
import com.google.android.play.core.tasks.Task;
import com.velocityappsdj.gallerycleaner.db.AppDatabase;
import com.velocityappsdj.gallerycleaner.db.Data;
import com.velocityappsdj.gallerycleaner.viewmodels.StartViewModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class StartActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private RelativeLayout mainContainer, processContainer;
    private TextView progressText;
    private AdView mAdView;
    private AppDatabase mDb;
    StartViewModel startViewModel;
    private List<Data> toBeDeleted;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDb = AppDatabase.getInstance(this);
        setContentView(R.layout.activity_start);
        mainContainer = findViewById(R.id.mainContainer);
        processContainer = findViewById(R.id.processContainer);
        progressText = findViewById(R.id.progressText);
        toBeDeleted = new ArrayList<>();
        setUpAds();
        Button startButton = (Button) findViewById(R.id.start_button);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showProcessing(true);
                LiveData<List<Data>> imagesLiveData = mDb.dataDao().loadAllData();

                imagesLiveData.observe(StartActivity.this, data -> {
                    imagesLiveData.removeObservers(StartActivity.this);
                    toBeDeleted = data;
                    checkPermission();
                });
            }
        });

        ImageView settingButton = (ImageView) findViewById(R.id.setting_button);
        settingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                checkPermission();
            }
        });

        startViewModel = new ViewModelProvider(this).get(StartViewModel.class);
        startViewModel.isDataLoaded.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean dataLoaded) {
                if (dataLoaded) {
                    gotoMainActivity();
                    showProcessing(false);
                }

            }
        });
        startViewModel.isProcessing.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean) {
                    showProcessing(true);
                } else
                    showProcessing(false);

            }
        });

        startViewModel.noOfItemLoaded.observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer processedItems) {
                Integer totalItems = startViewModel.totalItems.getValue();
                if (totalItems != null && processedItems != null) {
                    progressText.setText(String.format("Loaded %d out of %d", processedItems, totalItems));
                }
            }
        });
        showProcessing(false);
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

    private void showProcessing(boolean isProcessing) {
        mainContainer.setVisibility(isProcessing ? View.GONE : View.VISIBLE);
        processContainer.setVisibility(isProcessing ? View.VISIBLE : View.GONE);
    }

    private void checkPermission() {
        Log.d(TAG, "checkPermission() called");
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "checkPermission: permission found");

            startViewModel.getImagesList(toBeDeleted);

        } else {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {


                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                        1);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
    }


    private void gotoMainActivity() {
        startActivity(new Intent(this, MainActivity.class));
    }

    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    startViewModel.getImagesList(toBeDeleted);


                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    public void onBackPressed() {

        SharedPrefUtil sharedPrefUtil = new SharedPrefUtil(this);
        if (sharedPrefUtil.getCleaningDone()
                && getDiffenerceInDays(new Date().getTime(), sharedPrefUtil.getLastReviewASledTime()) > 2) {
            showReviewDialog();
        } else {
            super.onBackPressed();
        }
    }


    private void showReviewDialog() {
        new SharedPrefUtil(StartActivity.this).setLastReviewAskedTime(new Date().getTime());
        ReviewManager manager = ReviewManagerFactory.create(this);
        Task<ReviewInfo> request = manager.requestReviewFlow();
        request.addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // We can get the ReviewInfo object
                Log.d(TAG, "showReviewDialog: reviewinfo found");
                ReviewInfo reviewInfo = task.getResult();
                Task<Void> flow = manager.launchReviewFlow(StartActivity.this, reviewInfo);
                flow.addOnCompleteListener(taskStatus -> {
                    if (taskStatus.isSuccessful()) {
                        Log.d(TAG, "showReviewDialog: review dialog shown ");
                    } else
                        super.onBackPressed();
                    // The flow has finished. The API does not indicate whether the user
                    // reviewed or not, or even whether the review dialog was shown. Thus, no
                    // matter the result, we continue our app flow.
                });
            } else {
                // There was some problem, log or handle the error code.
//                @ReviewErrorCode int reviewErrorCode = ((TaskException) task.getException()).getErrorCode();
                Log.e(TAG, "showReviewDialog");
                super.onBackPressed();
            }
        });
    }

    private long getDiffenerceInDays(Long date1, Long date2) {
        long difference_In_Time = date1 - date2;
        long diff = (difference_In_Time
                / (1000 * 60 * 60 * 24)) % 365;
        Log.d(TAG, "getDiffenerceInDays() returned: " + diff);
        return diff;
    }
}
