// completed by Siddharth harish raikar on 3/30/2020 2:07 AM (Corona)
package com.example.afinal;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.couchbase.lite.CouchbaseLite;
import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.DatabaseConfiguration;
import com.couchbase.lite.Document;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class transition extends AppCompatActivity {
    TextView testv;
    Button phone,landline;
    ImageButton navigate;
    File root, fileroot, curFolder;
    int requestcode;
    Intent btnenable;
    TextView textFolder,name,email,address,buisness;
    EditText dataPath;
    static final int CUSTOM_DIALOG_ID = 0;
    ListView dialog_ListView;
    Button buttonopenDailog, buttonUp,attach;
    String id,number,latitude,longitude,optional;
    Database database;
    DatabaseConfiguration config;
    Context context;
    ListView location,service;
    String dir;
    ArrayAdapter<String> adapter,serviceadapter;
    ArrayList<String> locationlist,servicelist ;
    private List<String> fileList = new ArrayList<String>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.transition);
        attach=(Button)findViewById(R.id.attach);
        location=(ListView)findViewById(R.id.locationlist);
        service=(ListView)findViewById(R.id.service);
       buisness=(TextView) findViewById(R.id.buisness);
        id=getIntent().getStringExtra("id");

        btnenable = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        requestcode = 1;
        context=getApplicationContext();
        CouchbaseLite.init(context);
        config = new DatabaseConfiguration();
        database = null;

        try {
            database = new Database("error-app", config);
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }


      //  dataPath=(EditText)findViewById(R.id.FilePath);


        name=(TextView)findViewById(R.id.nameset);
        address=(TextView) findViewById(R.id.addressset);
        email=(TextView)findViewById(R.id.emailset) ;
        Document info=database.getDocument(id);
        info.getString("name");
       landline=(Button)findViewById(R.id.number2);
        email.setText(info.getString("email"));
        name.setText(info.getString("name"));
        address.setText(info.getString("address"));
        buisness.setText(info.getString("buisnessname"));
        latitude=info.getString("latitude");
        longitude=info.getString("longitude");
        optional=info.getString("landline");
        dir=info.getString("phone");
        navigate=(ImageButton)findViewById(R.id.navigate);
        locationlist = new ArrayList<String>();
        servicelist = new ArrayList<String>();
        phone=(Button)findViewById(R.id.call);
        number=info.getString("phone");
        phone.setText(number);
        landline.setText(info.getString("landline"));


        for (Object loc:info.getArray("location").toList()) {
            locationlist.add(String.valueOf(loc));

        }

        adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, locationlist);
        location.setAdapter(adapter);
        Log.w("debug",info.getArray("service").toList().toString());

        for (Object serv:info.getArray("service").toList()) {
            servicelist.add(String.valueOf(serv));

        }

        serviceadapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1,servicelist);
        service.setAdapter(serviceadapter);
        eventlistner();
    }

    private void eventlistner() {

        navigate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Uri gmmIntentUri = Uri.parse("google.navigation:q="+latitude+","+longitude);
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
            }
        });

        attach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(getApplicationContext(),AttachDialogue.class);
                intent.putExtra("Dirname",dir);
                startActivity(intent);
            }
        });

        phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String uri = "tel:" + number ;
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse(uri));
                startActivity(intent);
            }
        });

        landline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uri = "tel:" + optional ;
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse(uri));
                startActivity(intent);
            }
        });
    }

}
