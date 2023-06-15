package com.android.hre.api;

import org.json.JSONObject;

public class Convertion {

    public JSONObject convertStringToJSON(String value){

        JSONObject jsonObject = null;

        try{
            jsonObject = new JSONObject(value);
        }catch (Exception e){
            e.printStackTrace();
        }
        return jsonObject;
    }
}
