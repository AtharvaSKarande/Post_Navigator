package com.project.postnav;


public class Packet {

    public String type;
    public String primary_key;
    public String pincode;
    public String weight;
    public String status;

    public Packet(String type, String primary_key, String pincode, String weight, String status){
        this.type = type;
        this.primary_key = primary_key;
        this.pincode = pincode;
        this.weight = weight;
        this.status = status;
    }

    public String getType(){
        return this.type;
    }

    public String getPrimaryKey(){
        return this.primary_key;
    }

    public String getPincode(){
        return this.pincode;
    }

    public String getWeight(){
        return this.weight;
    }

    public String getStatus(){
        return this.status;
    }

}
