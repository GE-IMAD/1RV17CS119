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

public class Attach extends AppCompatActivity {

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
        setContentView(R.layout.attach);
        CouchbaseLite.init(getApplicationContext());
        listView = (ListView) findViewById(R.id.attachlist);
        attach = (Button) findViewById(R.id.attach);
        deletebutton = (Button) findViewById(R.id.deletebutton);


        strings = new ArrayList<String>();
        directory = getIntent().getStringExtra("dir");

        name = new ArrayList<String>();
        path = new ArrayList<String>();
        File dir=new File(getObbDir()+"/"+directory);
        dir.mkdir();
        directorypath = getObbDir() + "/"+directory;
        File[] files = new File(directorypath).listFiles();

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

        attach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String[] mimeTypes =
                        {"image/*", "application/pdf", "application/msword", "application/vnd.ms-powerpoint", "application/vnd.ms-excel", "text/plain", "application/zip", "audio/*", "video/*"};

                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    intent.setType(mimeTypes.length == 1 ? mimeTypes[0] : "*/*");
                    if (mimeTypes.length > 0) {
                        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
                    }
                } else {
                    String mimeTypesStr = "";
                    for (String mimeType : mimeTypes) {
                        mimeTypesStr += mimeType + "|";
                    }
                    intent.setType(mimeTypesStr.substring(0, mimeTypesStr.length() - 1));
                }
                startActivityForResult(Intent.createChooser(intent, "ChooseFile"), 0);

                startActivityForResult(intent, PICKFILE_RESULT_CODE);


            }
        });

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICKFILE_RESULT_CODE) {
            if (resultCode == RESULT_OK) {
                try {
                    Uri uri = data.getData();
                    String uriString = uri.toString();
                    File myFile = new File(uriString);
                    String extenstion = getMimeType(getApplicationContext(), uri);
                    InputStream inputStream = getContentResolver().openInputStream(data.getData());
                    showForgotDialog(Attach.this, inputStream, extenstion);

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }


            }
        }

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


    private void showForgotDialog(Context c,InputStream inputStream,String extention) {
        taskEditText = new EditText(c);
        AlertDialog dialog = new AlertDialog.Builder(c)
                .setTitle("ENTER FILE DESCRIPTION")
                .setView(taskEditText)
                .setPositiveButton("SET", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String task = String.valueOf(taskEditText.getText());
                        name_file=String.valueOf(taskEditText.getText());
                        Log.w("saving file",name_file);
                        Editable filedescription= taskEditText.getText();
                        Log.w("debug",name_file);
                        String filepath;


                        FileOutputStream outputStream= null;
                        try {
                           filepath =getObbDir()+"/"+directory+"/"+name_file+"."+extention;
                           Log.w("debug",filepath);
                            outputStream = new FileOutputStream(filepath);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                        byte[] buffer=new byte[1024];
                        try {
                            while (inputStream.read(buffer) != -1)
                                outputStream.write(buffer);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        name.add(name_file);
                        path.add(getObbDir()+"/"+directory+"/"+name_file+"."+extention);
                        adapter.notifyDataSetChanged();
                        Toast.makeText(getApplicationContext(),"file transfer complete",Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .create();
        dialog.show();
    }

}