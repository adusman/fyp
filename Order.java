package com.example.emedcare;

import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.List;

public class Order {

    @SerializedName("pharmacy_id")
    public int pharmacy_id;

    @SerializedName("customer_id")
    public int customer_id;

    @SerializedName("province")
    public String province;

    @SerializedName("city")
    public String city;

    @SerializedName("phone")
    public String phone;

    @SerializedName("address")
    public String address;

    @SerializedName("medicines")
    public List<HashMap<String, Integer>> medicines;

}
