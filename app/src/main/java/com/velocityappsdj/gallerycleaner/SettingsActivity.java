package com.velocityappsdj.gallerycleaner;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;

public class SettingsActivity extends AppCompatActivity {
    SharedPreferences sharedPref ;
    SharedPreferences.Editor editor;
    SharedPreferences.Editor editorFolder;
    SharedPreferences.Editor editorFolderName;
    EditText foldername;
    RadioButton custom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        SharedPreferences sharedPref = this.getSharedPreferences("MyPreferences",Context.MODE_PRIVATE);
        String sortOrder=sharedPref.getString(getString(R.string.sort_preference),"recent");
        RadioButton recent=(RadioButton)findViewById(R.id.sort_by_recent);
        RadioButton alphabet=(RadioButton)findViewById(R.id.sort_by_alphabet);
        RadioButton allFolder=(RadioButton)findViewById(R.id.load_all) ;
        RadioButton whatsapp=(RadioButton)findViewById(R.id.load_whatsapp) ;
        RadioButton camera=(RadioButton) findViewById(R.id.load_camera);
        custom=(RadioButton) findViewById(R.id.load_custom);
        foldername=(EditText)findViewById(R.id.folder_name);
        SharedPreferences ss=this.getSharedPreferences("FolderName",Context.MODE_PRIVATE);
        String folderis=ss.getString("Folderss","");
        editorFolderName=ss.edit();

        SharedPreferences sf=this.getSharedPreferences("FolderSelect",Context.MODE_PRIVATE);
        String folderselect=sf.getString("FolderSelect","All");

        if(sortOrder.equals("recent"))
        {
            recent.setChecked(true);
        }
        else
        {
            alphabet.setChecked(true);
        }
        if(folderselect.equals("All"))
        {
            allFolder.setChecked(true);
            foldername.setEnabled(false);



        }
        else if (folderselect.equals("Camera"))
        {
            camera.setChecked(true);
            foldername.setEnabled(false);
        }
        else if (folderselect.equals("WhatsApp")){

            whatsapp.setChecked(true);
            foldername.setEnabled(false);
        }
        else {
            custom.setChecked(true);
            foldername.setEnabled(true);
            foldername.setText(folderis);
        }

        sharedPref = this.getSharedPreferences("MyPreferences",Context.MODE_PRIVATE);
         editor = sharedPref.edit();
        sf=this.getSharedPreferences("FolderSelect",Context.MODE_PRIVATE);
        editorFolder=sf.edit();




    }
    public void onSortOptionSelected(View view){
        boolean checked = ((RadioButton) view).isChecked();
    switch (view.getId())
    {
        case R.id.sort_by_recent:
            if(checked)


            editor.putString(getString(R.string.sort_preference),"recent");
            editor.commit();
                break;
        case R.id.sort_by_alphabet:
            if(checked)
                editor.putString(getString(R.string.sort_preference),"alphabet");
            editor.commit();
                break;

    }


    }
  public  void onFolderSelected(View view){
      boolean checked = ((RadioButton) view).isChecked();
      switch (view.getId())
      {
          case R.id.load_all:
              if(checked)

                  foldername.setEnabled(false);
                  editorFolder.putString("FolderSelect","All");
              editorFolder.commit();
              editorFolderName.putString("Folderss","");
              editorFolderName.commit();
              break;
          case R.id.load_camera:
              if(checked)
                  foldername.setEnabled(false);
                  editorFolder.putString("FolderSelect","Camera");
              editorFolder.commit();
              editorFolderName.putString("Folderss","Camera");
              editorFolderName.commit();
              break;
          case R.id.load_whatsapp:
              if(checked)
                  foldername.setEnabled(false);
              editorFolder.putString("FolderSelect","WhatsApp");
              editorFolder.commit();
              editorFolderName.putString("Folderss","WhatsApp");
              editorFolderName.commit();
              break;

          case R.id.load_custom:
              if(checked)
                  editorFolder.putString("FolderSelect","Custom");
              editorFolder.commit();
              foldername.setEnabled(true);
              break;

      }
    }
    @Override
    public void onBackPressed() {

        if(custom.isChecked())
        {
           String frd= foldername.getText().toString();
            editorFolderName.putString("Folderss",frd);
            editorFolderName.commit();
            editorFolderName.commit();

        }
        SettingsActivity.this.finish();
    }
}
