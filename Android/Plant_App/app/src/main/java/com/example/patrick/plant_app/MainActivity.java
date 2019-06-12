package com.example.patrick.plant_app;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;


import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ListView lv_plants;
    private ImageView iv_plant;
    private TextView tv_timestamp;
    private TextView tv_moisture_A;
    private TextView tv_timestamp_A;
    private TextView tv_moisture_B;
    private TextView tv_timestamp_B;
    private TextView tv_temp;
    private TextView tv_humidity;

    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private DatabaseReference databaseReference_A;
    private DatabaseReference databaseReference_B;
    private StorageReference mStorageRef;


    private ArrayList<PlantObject> plant_list = new ArrayList<>();
    private CustomAdapter customAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("Plants");
        databaseReference_A = database.getReference("Plant_A");
        databaseReference_B = database.getReference("Plant_B");
        mStorageRef = FirebaseStorage.getInstance("gs://plantpi-f9f46.appspot.com").getReference();

        lv_plants = (ListView)findViewById(R.id.lv_plants);
        tv_timestamp = (TextView)findViewById(R.id.tv_timestamp);
        tv_moisture_A = (TextView)findViewById(R.id.tv_moisture_A);
        tv_timestamp_A = (TextView)findViewById(R.id.tv_timestamp_A);
        tv_temp = (TextView)findViewById(R.id.tv_temp);
        tv_humidity = (TextView)findViewById(R.id.tv_humidity);
        tv_moisture_B = (TextView)findViewById(R.id.tv_moisture_B);
        tv_timestamp_B = (TextView)findViewById(R.id.tv_timestamp_B);
        iv_plant = (ImageView)findViewById(R.id.iv_plant);

        //Custom adapter for list view
        customAdapter = new CustomAdapter(this,R.layout.plant_items,plant_list);
        lv_plants.setAdapter(customAdapter);

        //Get Plant Readings for both plants
        databaseReference.child("list").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String id = dataSnapshot.getKey();
                String plant = dataSnapshot.child("plant").getValue(String.class);
                Integer moisture = dataSnapshot.child("soil_moisture").getValue(Integer.class);
                String time_stamp = dataSnapshot.child("time_stamp").getValue(String.class);


                PlantObject plantObject = new PlantObject(id,plant,moisture,time_stamp);

                plant_list.add(plantObject);
                customAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //Get Temp and humidity status data
        databaseReference.child("status").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Integer temperature = dataSnapshot.child("temperature").getValue(Integer.class);
                Integer humidity = dataSnapshot.child("humidity").getValue(Integer.class);
                String timestamp = dataSnapshot.child("timestamp").getValue(String.class);

                tv_temp.setText("Temperature: "+Integer.toString(temperature));
                tv_humidity.setText("Humidity: "+Integer.toString(humidity));
                tv_timestamp.setText("Image taken at: "+timestamp);

                mStorageRef.child("images/plant").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        // Got the download URL for 'users/me/profile.png'
                        Picasso.get().load(uri).into(iv_plant);

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        System.out.println("and Error: "+exception);
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //Get status data for Plant B
        databaseReference_B.child("status").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Integer moisture = dataSnapshot.child("soil_moisture").getValue(Integer.class);
                String time_stamp = dataSnapshot.child("time_stamp").getValue(String.class);
                tv_moisture_B.setText("Moisture: "+Integer.toString(moisture));
                tv_timestamp_B.setText("Last update: "+time_stamp);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //Get status data for Plant A
        databaseReference_A.child("status").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Integer moisture = dataSnapshot.child("soil_moisture").getValue(Integer.class);
                String time_stamp = dataSnapshot.child("time_stamp").getValue(String.class);

                tv_moisture_A.setText("Moisture: "+Integer.toString(moisture));
                tv_timestamp_A.setText("Last update: "+time_stamp);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    //Generate menu Items
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    //Handle items that open the plant A and plant B Activity
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.itm_plant_A:
                startActivity(new Intent(this, PlantA_Activity.class));
                return true;
            case R.id.itm_plant_B:
                startActivity(new Intent(this, PlantB_Activity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
