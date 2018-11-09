package com.velocityappsdj.gallerycleaner;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.velocityappsdj.gallerycleaner.R;

import java.io.Serializable;
import java.util.ArrayList;

public class UndoActivity extends AppCompatActivity  implements Serializable{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_undo);
        ListView listView=(ListView)findViewById(R.id.undo_list);


        listView.setAdapter(new CustomAdapter(MainActivity.undoData,this));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                MainActivity.undoData.remove(i);
                MainActivity.deletion.remove(i);
                finish();
                startActivity(getIntent());
            }
        });
    }
}
