package com.example.patrick.plant_app;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class PlantA_Activity extends AppCompatActivity {

    private ListView lv_plant;
    private Button btn_back;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;

    private ArrayList<PlantObject> plant_list = new ArrayList<>();
    private CustomAdapter customAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plant_a_);

        //Firebase database ref
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("Plant_A");

        lv_plant = (ListView)findViewById(R.id.lv_plant);
        btn_back = (Button)findViewById(R.id.btn_back);

        //Custom adapter for update list
        customAdapter = new CustomAdapter(this,R.layout.plant_items,plant_list);
        lv_plant.setAdapter(customAdapter);

        //Go back to main menu
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(),MainActivity.class);
                startActivity(intent);

            }
        });

        //Get plant A updates
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
    }
}
