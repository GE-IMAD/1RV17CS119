// completed by Siddharth harish raikar on 3/30/2020 2:07 AM (Corona)
package com.example.afinal;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.couchbase.lite.CouchbaseLite;
import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.DataSource;
import com.couchbase.lite.Database;
import com.couchbase.lite.DatabaseConfiguration;
import com.couchbase.lite.Expression;
import com.couchbase.lite.Meta;
import com.couchbase.lite.MutableArray;
import com.couchbase.lite.MutableDocument;
import com.couchbase.lite.Query;
import com.couchbase.lite.QueryBuilder;
import com.couchbase.lite.Result;
import com.couchbase.lite.ResultSet;
import com.couchbase.lite.SelectResult;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class profileActivity extends AppCompatActivity {
    Button location,save,attach,service,capture;
    Database database;
    DatabaseConfiguration config;
    EditText name,address,phone,email,buisness,landline;
    Query query;
    ResultSet result;
    String id="";
    String number="";
    int num=0;
    Location currentLocation;
    private static final int REQUEST_CODE = 101;



@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);
        location=(Button)findViewById(R.id.attachtitle);
        save=(Button)findViewById(R.id.save);
        capture=(Button)findViewById(R.id.loc);
        CouchbaseLite.init(getApplicationContext());
        config = new DatabaseConfiguration();
       name=(EditText)findViewById(R.id.nameset);
       email=(EditText)findViewById(R.id.emailset);
       phone=(EditText)findViewById(R.id.phoneset);
       landline=(EditText)findViewById(R.id.landline);
        address=(EditText)findViewById(R.id.addressset);
       buisness=(EditText)findViewById(R.id.buisnesset);
       attach=(Button)findViewById(R.id.attach);
       service=(Button)findViewById(R.id.service);

        database = null;

    if (ActivityCompat.checkSelfPermission(
            getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
            getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);

        return;
    }

        try {
            database = new Database("error-app", config);
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }

        query = QueryBuilder.select(SelectResult.property("self"),
                SelectResult.property("name"),
                SelectResult.property("buisnessname"),
                SelectResult.property("address"),
                SelectResult.property("phone"),
                SelectResult.property("email"),
                SelectResult.property("landline"),
                SelectResult.expression(Meta.id))
                .from(DataSource.database(database))
                .where(Expression.property("self").equalTo(Expression.string("true")));

    try {
        result = query.execute();
      num=result.allResults().size();
        Log.w("register","self id"+num);
    } catch (CouchbaseLiteException e) {
        e.printStackTrace();
    }
    try {
        result = query.execute();
    } catch (CouchbaseLiteException e) {
        e.printStackTrace();
    }

    for (Result exp:result) {
        id=exp.getString("id");
        number=exp.getString("phone");
        Log.w("register","self id"+id);
        Log.w("register","same id"+exp.getString("name"));
    }


    try {
        dataloading();
    } catch (CouchbaseLiteException e) {
        e.printStackTrace();
    }
       eventlistner();

    }







    private void dataloading() throws CouchbaseLiteException {

        if(num!=0){

            result=query.execute();
            for (Result fill:result) {
                Log.w("loading","name"+fill.getString("name"));
               name.setText(fill.getString("name"));
                buisness.setText(fill.getString("buisnessname"));
                email.setText(fill.getString("email"));
                phone.setText(fill.getString("phone"));
               address.setText(fill.getString("address"));
               landline.setText(fill.getString("landline"));

            }

        }

    }

    private void eventlistner() {

        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditDialog();

            }
        });

        capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.w("debug","test");



                FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getApplicationContext());
                Task<Location> task = fusedLocationProviderClient.getLastLocation();
                task.addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            currentLocation = location;

                            MutableDocument mutableDoc = new MutableDocument();
                            mutableDoc = database.getDocument(id).toMutable();

                            mutableDoc.setString("latitude", String.valueOf(currentLocation.getLatitude()))
                                    .setString("longitude", String.valueOf(currentLocation.getLongitude()));
                            Toast.makeText(getApplicationContext(), "Mylocation "+currentLocation.getLatitude() + "    " + currentLocation.getLongitude(), Toast.LENGTH_SHORT).show();

                            try {
                                database.save(mutableDoc);
                            } catch (CouchbaseLiteException e) {
                                e.printStackTrace();
                            }

//                                .setString("address", String.valueOf(address.getText()));



                       ;
                        }
                    }
                });
            }
        });

        service.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditDialogservice();

            }
        });

        attach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),Attach.class);
                intent. putExtra("dir",number);
                startActivity(intent);

            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {


                    if(num==0){
                        Log.w("register","DEBUGGING");
                        MutableDocument mutableDoc = new MutableDocument();
                                mutableDoc.setString("name", String.valueOf(name.getText()));
                        mutableDoc.setString("buisnessname", String.valueOf(buisness.getText()));
                        mutableDoc.setString("email", String.valueOf(email.getText()));
                        mutableDoc.setString("self","true");
                        mutableDoc.setString("phone", String.valueOf(phone.getText()));

                        mutableDoc.setString("address",String.valueOf(address.getText()));
                        mutableDoc.setString("landline",String.valueOf(landline.getText()));
                        MutableArray array=new MutableArray();
                        List<Object> rand=new ArrayList<>();
                        rand.add("goa");
                        rand.add("bagalore");
                        array.setData(rand);
                        mutableDoc.setArray("location",array);
                        mutableDoc.setArray("service",array);


// Save it to the database.
                        database.save(mutableDoc);
                        Log.w("register","initial saving done");
                    }
                    else{
                        Log.w("register","updation");
                        MutableDocument mutableDoc = new MutableDocument();
                        mutableDoc = database.getDocument(id).toMutable();

                        mutableDoc.setString("name", String.valueOf(name.getText()))
                                .setString("buisnessname", String.valueOf(buisness.getText()))
                                .setString("email", String.valueOf(email.getText()))
                                .setString("self","true")
                                .setString("phone", String.valueOf(phone.getText()))
                                .setString("address", String.valueOf(address.getText()));
                        mutableDoc.setString("landline",String.valueOf(landline.getText()));
                        Log.w("register","modification done");

// Save it to the database.
                        database.save(mutableDoc);




                    }
                } catch (CouchbaseLiteException e) {
                    e.printStackTrace();
                }

            }
        });

    }

    private void showEditDialog() {
        FragmentManager fm = getSupportFragmentManager();
        Locationdialogue locationdialogue = Locationdialogue.newInstance("Some Title");
         locationdialogue.show(fm, "fragment_edit_name");
    }

    private void showEditDialogservice() {
        FragmentManager fm = getSupportFragmentManager();
        Servicedialogue servicedialogue = Servicedialogue.newInstance("Some Title");
        servicedialogue.show(fm, "fragment_edit_name");
    }


}
