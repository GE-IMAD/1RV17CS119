// completed by Siddharth harish raikar on 3/30/2020 2:07 AM (Corona)
package com.example.afinal;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.couchbase.lite.CouchbaseLite;
import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.DatabaseConfiguration;
import com.couchbase.lite.Query;
import com.couchbase.lite.ResultSet;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import static org.apache.commons.io.FileUtils.getFile;

public class AttachDialogue extends AppCompatActivity {

    ListView listView;
    ArrayAdapter<String> adapter;
    Query query;
    ResultSet result;
    String id = "";
    int num = 0;
    Database database;
    Button attach, deletebutton;
    DatabaseConfiguration config;
    ArrayList<String> strings;
    String name_file = "";
    String directory = "";
    String directorypath = "";
    private static final int PICKFILE_RESULT_CODE = 6;
    ArrayList<String> name, path;
    EditText taskEditText;

    int index = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.attachdialogue);
        CouchbaseLite.init(getApplicationContext());
        listView = (ListView) findViewById(R.id.attachlist);


        strings = new ArrayList<String>();
        directory = getIntent().getStringExtra("Dirname");

        name = new ArrayList<String>();
        path = new ArrayList<String>();
        File dir=new File(getObbDir()+"/"+directory);
        dir.mkdir();
        directorypath = getObbDir() + "/received/"+directory;
        File[] files = new File(directorypath).listFiles();
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        for (File file : files) {



            name.add(file.getName());
            path.add(file.getAbsolutePath());
            Log.w("debug",file.getAbsolutePath());


        }
        adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, name);
        listView.setAdapter(adapter);


        eventlistner();
    }

    private void eventlistner() {


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long temp) {
                MimeTypeMap myMime = MimeTypeMap.getSingleton();
                Intent newIntent = new Intent(Intent.ACTION_VIEW);
                String test=path.get(position);
                newIntent.setDataAndType(Uri.fromFile(getFile(test)),getMimeType(test));
                Log.w("open", String.valueOf(getObbDir()));
                newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                try {
                    getApplicationContext().startActivity(newIntent);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(getApplicationContext(), "No handler for this type of file.", Toast.LENGTH_LONG).show();
                }

            }
        });

    }




    public static String getMimeType(String url) {

        String type = null;
        String extension = url.substring(url.lastIndexOf(".") + 1);
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        return type;
    }

    public static String getMimeType(Context context, Uri uri) {
        String extension;

        //Check uri format to avoid null
        if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
            //If scheme is a content
            final MimeTypeMap mime = MimeTypeMap.getSingleton();
            extension = mime.getExtensionFromMimeType(context.getContentResolver().getType(uri));
        } else {
            //If scheme is a File
            //This will replace white spaces with %20 and also other special characters. This will avoid returning null values on file name with spaces and special characters.
            extension = MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(new File(uri.getPath())).toString());

        }

        return extension;
    }




}