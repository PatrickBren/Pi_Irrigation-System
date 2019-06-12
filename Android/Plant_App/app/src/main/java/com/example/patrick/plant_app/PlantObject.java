package com.example.patrick.plant_app;

public class PlantObject {

    private String id;
    private String plant;
    private Integer moisture;
    private String time_stamp;

    public PlantObject(String id, String plant, Integer moisture, String time_stamp){
        this.id = id;
        this.plant = plant;
        this.moisture = moisture;
        this.time_stamp = time_stamp;
    }

    public String getId() {
        return id;
    }

    public String getPlant() {
        return plant;
    }

    public Integer getMoisture() {
        return moisture;
    }


    public String getTime_stamp() {
        return time_stamp;
    }
}
