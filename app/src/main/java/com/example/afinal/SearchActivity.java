// completed by Siddharth harish raikar on 3/30/2020 2:07 AM (Corona)
package com.example.afinal;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.couchbase.lite.ArrayExpression;
import com.couchbase.lite.ArrayFunction;
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
import com.couchbase.lite.VariableExpression;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SearchActivity extends AppCompatActivity {

   ImageButton query;
    ListView list;
    EditText search;
    Database database;
    DatabaseConfiguration config;
    ArrayAdapter<String> adapter;
    Context context;
    String[] id;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search);

        query=(ImageButton) findViewById(R.id.query);
        search=(EditText)findViewById((R.id.search));
        list=(ListView)findViewById(R.id.list);
        context=getApplicationContext();
        CouchbaseLite.init(context);
        config = new DatabaseConfiguration();
        database = null;

        try {
            database = new Database("error-app", config);
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }


        eventlistners();


    }

    private void eventlistners() {




        query.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    database = new Database("error-app", config);
                } catch (CouchbaseLiteException e) {
                    e.printStackTrace();
                }
                // Create a query to fetch documents of type SDK.

                Expression[] values = new Expression[] {
                        Expression.property("name"),
                       Expression.property("location"),
                       Expression.property("language")
                };
                VariableExpression test = ArrayExpression.variable("likedby");
                Query query = QueryBuilder
                        .select(SelectResult.property("name"),
                                SelectResult.property("location"),
                                SelectResult.property("service"),
                                SelectResult.expression(Meta.id)

                        )
                        .from(DataSource.database(database))
                        .where(Expression.property("name").regex(Expression.string(String.valueOf(search.getText()))));


                Query query1 = QueryBuilder
                        .select(SelectResult.property("name"),
                                SelectResult.property("location"),
                                SelectResult.property("service"),
                                SelectResult.expression(Meta.id)
                        )
                        .from(DataSource.database(database))
                        .where(ArrayExpression.any( test).in(Expression.property("service"))
                                .satisfies(test.regex(Expression.string(String.valueOf(search.getText())))));

                Query query2 = QueryBuilder
                        .select(SelectResult.property("name"),
                                SelectResult.property("location"),
                                SelectResult.property("service"),
                                SelectResult.expression(Meta.id)
                        )
                        .from(DataSource.database(database))
                        .where(ArrayExpression.any( test).in(Expression.property("location"))
                                .satisfies(test.regex(Expression.string(String.valueOf(search.getText())))));

                try {


                    ResultSet result = query.execute();
                    ResultSet result1 = query1.execute();
                    ResultSet result2 = query2.execute();



                    id=new String[100];
                    final ArrayList<String> strings = new ArrayList<String>();
                    int index=0;
                    for (Result exp : result) {

                        strings.add(exp.getString("name")+"    "+exp.getArray("service").toList().toString());
                        id[index]=exp.getString("id");
                        index++;


                    }
                    for (Result exp : result1) {

                        strings.add(exp.getString("name")+"    "+exp.getArray("service").toList().toString());
                        id[index]=exp.getString("id");
                        index++;


                    }
                    for (Result exp : result2) {

                        strings.add(exp.getString("name")+"    "+exp.getArray("service").toList().toString());
                        id[index]=exp.getString("id");
                        index++;


                    }


                    adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, strings);
                    list.setAdapter(adapter);
                } catch (CouchbaseLiteException e) {
                    e.printStackTrace();
                }





            }
        });

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long temp) {
                change(position);

            }
        });
    }

    public  void change(int position){
        Intent intent = new Intent(getBaseContext(), transition.class);
        intent.putExtra("id",id[position]);

        startActivity(intent);
    }
}
