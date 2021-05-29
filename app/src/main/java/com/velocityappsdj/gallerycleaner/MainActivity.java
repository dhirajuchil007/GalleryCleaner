package com.velocityappsdj.gallerycleaner;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.lorentzos.flingswipe.SwipeFlingAdapterView;
import com.velocityappsdj.gallerycleaner.db.AppDatabase;
import com.velocityappsdj.gallerycleaner.db.Data;
import com.velocityappsdj.gallerycleaner.models.FolderItemModel;
import com.velocityappsdj.gallerycleaner.util.AppExecutors;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import smartdevelop.ir.eram.showcaseviewlib.GuideView;
import smartdevelop.ir.eram.showcaseviewlib.config.DismissType;
import smartdevelop.ir.eram.showcaseviewlib.listener.GuideListener;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private AdView mAdView;
    String path;
    private FloatingActionButton fab;
    Intent restartIntent;
    private ImageView imgFolder, imgFilter;
    TextView warnTextView;
    public static MyAppAdapter myAppAdapter;
    public static ViewHolder viewHolder;
    public List<Data> currentArray;
    private SwipeFlingAdapterView flingContainer;
    public static ArrayList<String> deletion = new ArrayList<>();
    String cam = new String();
    private SharedPrefUtil sharedPrefUtil;
    private HashMap<String, FolderItemModel> folderHashMap;
    private AppDatabase mDb;
    private TextView fabBadge;
    private ImageView imgBack;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedPrefUtil = new SharedPrefUtil(this);
        restartIntent = getIntent();
        folderHashMap = UserDataSingleTon.getInstance().folderMap;
        currentArray = UserDataSingleTon.getInstance().allImages;
        mDb = AppDatabase.getInstance(this);
        initViews();
        setUpClickListeners();
        setUpAds();
        setupAdapter();
        setUpDb();
        checkAndStartShowCase();


    }

    private void checkAndStartShowCase() {
        if (!new SharedPrefUtil(MainActivity.this).getShowCaseDone()) {
            showCase("", "Welcome to gallery cleaner. Here's  a short tutorial", warnTextView, 1);
        }
    }

    private void showCase(String title, String text, View view, int step) {
        new GuideView.Builder(this)
                .setContentText(text)
                .setTargetView(view)
                .setContentTextSize(18)//optional
                .setTitleTextSize(20)//optional
                .setGuideListener(new GuideListener() {
                    @Override
                    public void onDismiss(View view) {
                        if (step == 1) {
                            showCase("", "Swipe right to keep the image", warnTextView, step + 1);
                        } else if (step == 2) {
                            showCase("", "Swipe left to move the image to trash", warnTextView, step + 1);
                        } else if (step == 3) {
                            showCase("", "Tap the trash icon to see and confirm/undo the images you want to delete", fab, step + 1);
                        } else if (step == 4) {
                            showCase("", "Tap on the folder icon to switch to a specific folder", imgFolder, step + 1);
                        } else if (step == 5) {
                            showCase("", "Tap on the sort icon to sort images by date, size or name", imgFilter, step + 1);
                        } else if (step == 6) {
                            showCase("", "Thats all for now, enjoy swiping.", warnTextView, step + 1);
                        }
                    }


                })
                .setDismissType(DismissType.anywhere) //optional - default dismissible by TargetView
                .build()
                .show();
        new SharedPrefUtil(MainActivity.this).setShowCaseDone(true);
    }

    private void setUpDb() {
        mDb.dataDao().loadAllData().observe(MainActivity.this, data -> {
            if (data == null || data.size() == 0)
                fabBadge.setVisibility(View.GONE);
            else {
                fabBadge.setVisibility(View.VISIBLE);
                fabBadge.setText(String.valueOf(data.size()));
            }
        });
    }

    private void setupAdapter() {
        Log.d(TAG, "setupAdapter() called");
        flingContainer = (SwipeFlingAdapterView) findViewById(R.id.frame);
        myAppAdapter = new MyAppAdapter(currentArray, MainActivity.this);
        flingContainer.setAdapter(myAppAdapter);
        flingContainer.removeAllViewsInLayout();
        myAppAdapter.notifyDataSetChanged();
        flingContainer.setMaxVisible(1);
        flingContainer.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {
            @Override
            public void removeFirstObjectInAdapter() {

            }

            @Override
            public void onLeftCardExit(Object dataObject) {


                /*deleteImage(currentArray.get(0));*/
                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        mDb.dataDao().insertData(currentArray.get(0));
                        currentArray.remove(0);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                myAppAdapter.notifyDataSetChanged();
                                checkIfArrayEmpty();
                            }
                        });


                    }
                });


                //Do something on the left!
                //You also have access to the original object.
                //If you want to use it just cast it (String) dataObject
            }

            @Override
            public void onRightCardExit(Object dataObject) {

             /*   cur.moveToNext();

                int bitColumn = cur.getColumnIndex(MediaStore.Images.Media.DATA);
                path = cur.getString(bitColumn);
                Log.d("imagepath", path);

                array.add(new Data(path, "lol"));*/

                currentArray.remove(0);
                myAppAdapter.notifyDataSetChanged();
                checkIfArrayEmpty();
            }

            @Override
            public void onAdapterAboutToEmpty(int itemsInAdapter) {

            }


            @Override
            public void onScroll(float scrollProgressPercent) {

                View view = flingContainer.getSelectedView();
                view.findViewById(R.id.background).setAlpha(0);
                //view.findViewById(R.id.item_swipe_right_indicator).setAlpha(scrollProgressPercent < 0 ? -scrollProgressPercent : 0);
                //view.findViewById(R.id.item_swipe_left_indicator).setAlpha(scrollProgressPercent > 0 ? scrollProgressPercent : 0);
                view.findViewById(R.id.delete_panel).setAlpha(scrollProgressPercent < 0 ? -scrollProgressPercent : 0);
                view.findViewById(R.id.keep_panel).setAlpha(scrollProgressPercent > 0 ? scrollProgressPercent : 0);
            }

        });


        // Optionally add an OnItemClickListener
        flingContainer.setOnItemClickListener(new SwipeFlingAdapterView.OnItemClickListener() {
            @Override
            public void onItemClicked(int itemPosition, Object dataObject) {

                View view = flingContainer.getSelectedView();
                view.findViewById(R.id.background).setAlpha(0);

                myAppAdapter.notifyDataSetChanged();
            }
        });
    }

    private void checkIfArrayEmpty() {
        if (currentArray.size() == 0) {
            warnTextView.setVisibility(View.VISIBLE);
            warnTextView.setText("All images int this folder viewed. Please select another folder.");
        } else
            warnTextView.setVisibility(View.GONE);
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


    private void setUpClickListeners() {
        imgFilter.setOnClickListener(v -> {
            SortByBottomSheet bottomSheetFragment = new SortByBottomSheet(new SortByBottomSheet.IOnSortSelected() {
                @Override
                public void OnSelected(String pref) {
                    Log.d(TAG, "OnSelected() called with: pref = [" + pref + "]");
                    sharedPrefUtil.saveSortPref(ApplicationConstants.sort_order.get(pref));

                    sortCurrentList();
                }
            }, new SharedPrefUtil(MainActivity.this).getSortPref());
            bottomSheetFragment.show(getSupportFragmentManager(), bottomSheetFragment.getTag());
        });
        imgFolder.setOnClickListener(v -> {

            FolderListBottomSheet bottomSheet = new FolderListBottomSheet(onFolderSelected, MainActivity.this, folderHashMap, currentArray);
            bottomSheet.show(getSupportFragmentManager(), bottomSheet.getTag());

        });
        fab.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, UndoActivity.class)));
        imgBack.setOnClickListener(v -> onBackPressed());
    }

    FolderListBottomSheet.OnFolderSelected onFolderSelected = new FolderListBottomSheet.OnFolderSelected() {
        @Override
        public void onSelected(int position, FolderItemModel folderItemModel) {
            Log.d(TAG, "onSelected() called with: position = [" + position + "], folderItemModel = [" + folderItemModel + "]");
            List<Data> dataList = folderItemModel.getImagesData();
            UserDataSingleTon.getInstance().selectedFolder = folderItemModel.getName();
            currentArray = dataList;
            sortCurrentList();
            myAppAdapter = new MyAppAdapter(currentArray, MainActivity.this);
            flingContainer.setAdapter(myAppAdapter);
            flingContainer.removeAllViewsInLayout();
            myAppAdapter.notifyDataSetChanged();

            checkIfArrayEmpty();

        }
    };

    private void sortCurrentList() {
        String pref = sharedPrefUtil.getSortPref();
        switch (pref) {
            case "name": {
                Log.d(TAG, "getImagesList: by name");
                Collections.sort(currentArray, (a, b) -> {
                    return a.getDisplayName().compareTo(b.getDisplayName());
                });

                break;
            }

            case "size": {
                Log.d(TAG, "getImagesList: by size");
                Collections.sort(currentArray, (a, b) -> a.getSize() - b.getSize());
                break;
            }
            default: {
                Log.d(TAG, "getImagesList: by default" + cam);
                Collections.sort(currentArray, (a, b) -> b.getDateTaken().compareTo(a.getDateTaken()));
                break;
            }
        }
        flingContainer.removeAllViewsInLayout();
        myAppAdapter.notifyDataSetChanged();
        checkIfArrayEmpty();
    }

    private void initViews() {
        imgFolder = findViewById(R.id.imgFolder);
        imgFilter = findViewById(R.id.imgFilter);
        warnTextView = findViewById(R.id.warning_text);
        fab = findViewById(R.id.fab);
        fabBadge = findViewById(R.id.txtFabBadge);
        imgBack = findViewById(R.id.imgBack);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }

    public void deleteImage(Data data) {
        Log.d(TAG, "deleteImage() called");

        deletion.add(data.getImagePath());
        File file = new File(deletion.get(0));
        file.getAbsoluteFile().delete();
        getContentResolver().delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, MediaStore.Images.Media.DATA + "=?", new String[]{deletion.get(0)});
        deletion.remove(0);
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    Thread.sleep(5000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                if(deletion.size()!=0) {
//                    File file = new File(deletion.get(0));
//                    file.getAbsoluteFile().delete();
//                    getContentResolver().delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, MediaStore.Images.Media.DATA + "=?", new String[]{deletion.get(0)});
//                   runOnUiThread(new Runnable() {
//                       @Override
//                       public void run() {
//                           deletion.remove(0);
//                       }
//                   });
//                }
//            }
//        }).start();
//        Snackbar.with(this).text("1 item deleted").actionLabel("Undo").actionListener(new ActionClickListener() {
//            @Override
//            public void onActionClicked(Snackbar snackbar) {
//                deletion.remove(0);
//                Toast.makeText(MainActivity.this,"Undo Successful",Toast.LENGTH_SHORT).show();
//
//
//
//            }
//        }).show(MainActivity.this);
//        File file;
//        int nameind=cur.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME);
//        String name=cur.getString(nameind);
//
//        BitmapFactory.Options options = new BitmapFactory.Options();
//        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
//        Bitmap bitmap = BitmapFactory.decodeFile(path, options);
//        DataModel undoObject= new DataModel(name,path,bitmap);
//        undoData.add(undoObject);


//        int x=deletion.size();
//        Log.d("deletion",Integer.toString(x));
//        Log.d("add to deleltion",deletion.get(x-1));
//        if(x>10) {
//
//            file = new File(deletion.get(0));
//            file.getAbsoluteFile().delete();
//            int nameIndex = cur.getColumnIndex(MediaStore.Images.Media.DATA);
//            Toast toast=Toast.makeText(this,path,Toast.LENGTH_SHORT);
//            Log.d("deleted",deletion.get(0));
//            getContentResolver().delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, MediaStore.Images.Media.DATA + "=?", new String[]{deletion.get(0)});
//            deletion.remove(0);
//            undoData.remove(0);
//        }


    }


    public static class ViewHolder {
        public static FrameLayout background;
        public TextView DataText;
        public ImageView cardImage;


    }


    public class MyAppAdapter extends BaseAdapter {


        public List<Data> parkingList;
        public Context context;

        public MyAppAdapter(List<Data> apps, Context context) {
            this.parkingList = apps;
            this.context = context;
        }

        @Override
        public int getCount() {
            return parkingList.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            View rowView = convertView;


            if (rowView == null) {

                LayoutInflater inflater = getLayoutInflater();
                rowView = inflater.inflate(R.layout.item, parent, false);
                // configure view holder
                viewHolder = new MainActivity.ViewHolder();
                //viewHolder.DataText = (TextView) rowView.findViewById(R.id.bookText);
                viewHolder.background = (FrameLayout) rowView.findViewById(R.id.background);
                viewHolder.cardImage = (ImageView) rowView.findViewById(R.id.cardImage);
                rowView.setTag(viewHolder);

            } else {
                viewHolder = (MainActivity.ViewHolder) convertView.getTag();
            }
            //viewHolder.DataText.setText(parkingList.get(position).getDescription() + "");

            Glide.with(MainActivity.this).load(parkingList.get(position).getImagePath())
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            Log.e(TAG, "onLoadFailed: ", e);
                            if (currentArray.size() > 0) {
                                currentArray.remove(0);
                                flingContainer.removeAllViewsInLayout();
                                myAppAdapter.notifyDataSetChanged();
                            }
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            return false;
                        }
                    })
                    .into(viewHolder.cardImage);

            return rowView;
        }
    }
}


