package com.example.pball.micspot;

import org.json.JSONObject;

import java.util.Date;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by pball on 2/13/2017.
 */
/*
 *
 */
public class Mic {
    private int id;//0
    private String producer;//TODO: parse number to person 1
    private String name;//2
    private String venueName;//3
    private String address;//4
    private double latitude;//5
    private double longitude;//6
    private Date startDate;//7
    private int duration;//8
    private String basis;//9
    private int setLength;//10
    private int numSlots;//11


    public Mic(JSONObject jsonMic){
        Iterator<String> micInfo = jsonMic.keys();
        while(micInfo.hasNext()){
            String keyStr = micInfo.next();

        }
    }

    public Mic(String micName){
        this.name = micName;
    }


}
