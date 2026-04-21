package com.smartcampus.resources;

import com.smartcampus.model.Room;
import com.smartcampus.model.Sensor;
import com.smartcampus.model.SensorReading;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


public class DataStore {

    
    public static final Map<String, Room> rooms = new ConcurrentHashMap<>();

    
    public static final Map<String, Sensor> sensors = new ConcurrentHashMap<>();

    
    public static final Map<String, List<SensorReading>> readings = new ConcurrentHashMap<>();

    
    private DataStore() {}
}
