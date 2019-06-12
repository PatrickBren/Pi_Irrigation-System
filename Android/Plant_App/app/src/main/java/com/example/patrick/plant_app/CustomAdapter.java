package com.example.patrick.plant_app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class CustomAdapter extends ArrayAdapter<PlantObject> {

   private Context mContext;
   private ArrayList<PlantObject> plantObjects = new ArrayList<>();

    int mResource;

   public CustomAdapter(Context context, int resource, ArrayList<PlantObject> objects){
       super(context,resource,objects);
       mContext = context;
       mResource = resource;
   }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {



        String plant = getItem(position).getPlant();
        String moisture = getItem(position).getMoisture().toString();
        String time_stamp = getItem(position).getTime_stamp();

        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource, parent, false);

        final TextView tv_plant  = (TextView) convertView.findViewById(R.id.tv_plant);
        final TextView tv_moisture = (TextView) convertView.findViewById(R.id.tv_moisture);
        final TextView tv_time_stamp = (TextView) convertView.findViewById(R.id.tv_timestamp);

        tv_plant.setText("Plant: "+plant);
        tv_moisture.setText("Moisture: "+moisture);
        tv_time_stamp.setText("Time Stamp: "+time_stamp);

        return convertView;
    }


}
