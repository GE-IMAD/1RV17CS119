// completed by Siddharth harish raikar on 3/30/2020 2:07 AM (Corona)
package com.example.afinal;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

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

import java.util.ArrayList;
import java.util.List;

public class Locationdialogue extends DialogFragment {

    Button addbutton,deletebutton;
    ListView listView;
    ArrayAdapter<String> adapter;
    Query query;
    ResultSet result;
    String id="";
    int num=0;
    Database database;
    DatabaseConfiguration config;
    ArrayList<String> strings;
    EditText editlocation;
    int index=0;
    public Locationdialogue() {

    }

    public static Locationdialogue newInstance(String title) {
        Locationdialogue frag = new Locationdialogue();
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.locationprofile, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Get field from view
        addbutton = (Button) view.findViewById(R.id.attach);
        deletebutton=(Button)view.findViewById(R.id.deletebutton);
        listView=(ListView)view.findViewById(R.id.attachlist);
        editlocation=(EditText)view.findViewById(R.id.editlocation);
        database = null;
        CouchbaseLite.init(getContext());
        config = new DatabaseConfiguration();
        strings = new ArrayList<String>();
        try {
            database = new Database("error-app", config);
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }







        query = QueryBuilder.select(SelectResult.property("self"),
               SelectResult.property("location"),

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
            Log.w("register","self id"+id);

        }

        try {
            dataloading();
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }

      addbutton.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {

              strings.add(editlocation.getText().toString());
              editlocation.setText("");
              MutableDocument mutableDoc = new MutableDocument();
              mutableDoc = database.getDocument(id).toMutable();
              MutableArray array=new MutableArray();
              List<Object> rand=new ArrayList<>();
              for (String place:strings) {
                  rand.add(place);
              }
              array.setData(rand);
              mutableDoc.setArray("location",array);
              try {
                  database.save(mutableDoc);
              } catch (CouchbaseLiteException e) {
                  e.printStackTrace();
              }
              adapter.notifyDataSetChanged();
          }
      });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                index=position;
            }
        });

        deletebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                strings.remove(index);
                MutableDocument mutableDoc = new MutableDocument();
                mutableDoc = database.getDocument(id).toMutable();
                MutableArray array=new MutableArray();
                List<Object> rand=new ArrayList<>();
                for (String place:strings) {
                    rand.add(place);
                }
                array.setData(rand);
                mutableDoc.setArray("location",array);
                try {
                    database.save(mutableDoc);
                } catch (CouchbaseLiteException e) {
                    e.printStackTrace();
                }
                adapter.notifyDataSetChanged();
            }
        });
    }


    private void dataloading() throws CouchbaseLiteException {

        if(num!=0){

            result=query.execute();

            for (Result exp:result) {

                //Log.w("location",exp.getArray("location").toList().toString());


                for (Object loc:exp.getArray("location").toList()) {
                    strings.add(String.valueOf(loc));

                }


                //Log.w("test",strings[0]);

                adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, strings);
                listView.setAdapter(adapter);

            }


        }

    }
}