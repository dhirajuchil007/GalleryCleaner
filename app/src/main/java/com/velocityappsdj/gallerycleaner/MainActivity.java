package com.velocityappsdj.gallerycleaner;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.lorentzos.flingswipe.SwipeFlingAdapterView;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.listeners.ActionClickListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private AdView mAdView;
    String path;
    Cursor cur;
    Intent restartIntent;
    Button confirm;
        static ArrayList<DataModel> undoData=new ArrayList<>();

    TextView warnTextView;



    public static MyAppAdapter myAppAdapter;
    public static ViewHolder viewHolder;
    public ArrayList<Data> array=new ArrayList<>();
    private SwipeFlingAdapterView flingContainer;
    public static ArrayList<String> deletion=new ArrayList<>();
    String cam=new String();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MobileAds.initialize(getApplicationContext(), "ca-app-pub-9540086841520699~4547636763");
        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .addTestDevice("4E0FA1397589AB27")
                .build();
        mAdView.loadAd(adRequest);
        restartIntent=getIntent();

        SharedPreferences ss=this.getSharedPreferences("FolderName",Context.MODE_PRIVATE);
        cam="%"+ss.getString("Folderss","")+"%";
        Log.e("lol",cam);
        confirm=(Button)findViewById(R.id.confirm_button);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmDeletions("confirm");
            }
        });
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED)
            getImagesList();
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        1);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }


        flingContainer = (SwipeFlingAdapterView) findViewById(R.id.frame);

        //array = new ArrayList<>();
        /*


        array.add(new Data("https://www.androidtutorialpoint.com/wp-content/uploads/2016/11/Katrina-Kaif.jpg", "Hi I am Katrina Kaif. Wanna chat with me ?. \n" +
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum."));
        array.add(new Data("https://www.androidtutorialpoint.com/wp-content/uploads/2016/11/Emma-Watson.jpg", "Hi I am Emma Watson. Wanna chat with me ? \n" +
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum."));
        array.add(new Data("https://www.androidtutorialpoint.com/wp-content/uploads/2016/11/Scarlett-Johansson.jpg", "Hi I am Scarlett Johansson. Wanna chat with me ? \n" +
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum."));
        array.add(new Data("https://www.androidtutorialpoint.com/wp-content/uploads/2016/11/Priyanka-Chopra.jpg", "Hi I am Priyanka Chopra. Wanna chat with me ? \n" +
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum."));
        array.add(new Data("https://www.androidtutorialpoint.com/wp-content/uploads/2016/11/Deepika-Padukone.jpg", "Hi I am Deepika Padukone. Wanna chat with me ? \n" +
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum."));
        array.add(new Data("https://www.androidtutorialpoint.com/wp-content/uploads/2016/11/Anjelina-Jolie.jpg", "Hi I am Anjelina Jolie. Wanna chat with me ? \n" +
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum."));
        array.add(new Data("https://www.androidtutorialpoint.com/wp-content/uploads/2016/11/Aishwarya-Rai.jpg", "Hi I am Aishwarya Rai. Wanna chat with me ? \n" +
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum."));
            */
        myAppAdapter = new MyAppAdapter(array, MainActivity.this);
        flingContainer.setAdapter(myAppAdapter);
        flingContainer.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {
            @Override
            public void removeFirstObjectInAdapter() {

            }

            @Override
            public void onLeftCardExit(Object dataObject) {
                deleteImage();
                cur.moveToNext();

                int bitColumn = cur.getColumnIndex(MediaStore.Images.Media.DATA);
                path = cur.getString(bitColumn);
                Log.d("imagepath",path);


                array.add(new Data(path, ""));
                array.remove(0);
                myAppAdapter.notifyDataSetChanged();

                //Do something on the left!
                //You also have access to the original object.
                //If you want to use it just cast it (String) dataObject
            }

            @Override
            public void onRightCardExit(Object dataObject) {

                cur.moveToNext();

                int bitColumn = cur.getColumnIndex(MediaStore.Images.Media.DATA);
                path = cur.getString(bitColumn);
                Log.d("imagepath",path);

                array.add(new Data(path, "lol"));

                array.remove(0);
                myAppAdapter.notifyDataSetChanged();
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

        final Button undo =(Button)findViewById(R.id.undo_button);
        undo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent inter=new Intent(MainActivity.this,UndoActivity.class);

                startActivity(inter);
            }
        });


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
    @Override
    public void onBackPressed() {
        if(deletion.size()>0)
        confirmDeletions("back");
        else
        MainActivity.this.finish();

    }

    public void deleteImage(){
        deletion.add(path);
        File file =new File(deletion.get(0));
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
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[],int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    getImagesList();
                    finish();
                    startActivity(restartIntent);


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


    public static class ViewHolder {
        public static FrameLayout background;
        public TextView DataText;
        public ImageView cardImage;


    }

    public void getImagesList() {

        String[] projection = new String[]{
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                MediaStore.Images.Media.DATE_TAKEN,
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.DISPLAY_NAME,



        };

        //String cam="%Camera%";
// content:// style URI for the "primary" external storage volume
        Uri images = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        SharedPreferences sharedPref = this.getSharedPreferences("MyPreferences",Context.MODE_PRIVATE);
        String sortOrder=sharedPref.getString(getString(R.string.sort_preference),"recent");
        if(sortOrder.equals("recent")) {

// Make the query.
            cur = getContentResolver().query(images,
                    projection, // Which columns to return
                    MediaStore.Images.Media.DATA +" like ?",       // Which rows to return (all rows)
                    new String[]{cam},       // Selection arguments (none)
                    MediaStore.Images.Media.DATE_TAKEN + " DESC"       // Ordering
            );
        }
        else
        {
            cur = getContentResolver().query(images,
                    projection, // Which columns to return
                    MediaStore.Images.Media.DATA +" like ?",       // Which rows to return (all rows)
                    new String[]{cam},       // Selection arguments (none)
                    MediaStore.Images.Media.DISPLAY_NAME      // Ordering
            );

        }
        /*
        if (cur.moveToFirst()) {
            String bucket;
            String date;
            Bitmap bit;
            int bucketColumn = cur.getColumnIndex(
                    MediaStore.Images.Media.DISPLAY_NAME);
            int bitColumn=cur.getColumnIndex(MediaStore.Images.Media.DATA);
            int dateColumn = cur.getColumnIndex(
                    MediaStore.Images.Media.DATE_TAKEN);
            String format = "MM-dd-yyyy HH:mm:ss";
            path=cur.getString(bitColumn);
            SimpleDateFormat formatter = new SimpleDateFormat(format, Locale.ENGLISH);
            */

        setImageView();

/*
            do {
                // Get the field values
                bucket = cur.getString(bucketColumn);
                date = cur.getString(dateColumn);
                String dateTime = formatter.format(new Date(Long.parseLong(date)));


                // Do something with the values.
                Log.i("ListingImages", " bucket=" + bucket
                        + "  date_taken=" + dateTime);
            } while (cur.moveToNext());
*/
    }


    public void setImageView() {


        cur.moveToFirst();
        if(cur.getCount()==0)
        {
            warnTextView=(TextView)findViewById(R.id.warning_text);
            warnTextView.setText(R.string.warning);

        }
        else {

            int bitColumn = cur.getColumnIndex(MediaStore.Images.Media.DATA);
            path = cur.getString(bitColumn);
            Log.d("imagepath", path);

            array.add(new Data(path, "lol"));
        }
           // cur.moveToNext();


    }
    public void confirmDeletions(String action){
        if(action.equals("confirm")) {
            new AlertDialog.Builder(this)
                    .setMessage(R.string.confirm_deletion)
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            undoData.clear();
                            int x = deletion.size();
                            for (int i = 0; i < x; i++) {
                                File file = new File(deletion.get(i));
                                file.getAbsoluteFile().delete();
                                int nameIndex = cur.getColumnIndex(MediaStore.Images.Media.DATA);

                                //Log.d("deleted",deletion.get(0));
                                getContentResolver().delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, MediaStore.Images.Media.DATA + "=?", new String[]{deletion.get(i)});

                            }
                            deletion.clear();
                            //  MainActivity.this.finish();
                        }
                    })
                    .setNegativeButton("No", null)
                    .show();
        }
        else if(action.equals("back"))
        {
            new AlertDialog.Builder(this)
                    .setMessage(R.string.confirm_deletion)
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            undoData.clear();
                            int x=deletion.size();
                            for(int i=0;i<x;i++)
                            {
                                File file = new File(deletion.get(i));
                                file.getAbsoluteFile().delete();
                                int nameIndex = cur.getColumnIndex(MediaStore.Images.Media.DATA);

                                //Log.d("deleted",deletion.get(0));
                                getContentResolver().delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, MediaStore.Images.Media.DATA + "=?", new String[]{deletion.get(i)});

                            }
                            deletion.clear();
                              MainActivity.this.finish();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            MainActivity.this.finish();
                        }
                    })
                    .show();
        }



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

            Glide.with(MainActivity.this).load(parkingList.get(position).getImagePath()).into(viewHolder.cardImage);

            return rowView;
        }
    }
}


