package com.smartcampus.resources;

import com.smartcampus.exceptions.LinkedResourceNotFoundException;
import com.smartcampus.model.Sensor;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Path("/sensors")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SensorResource {

    
    @GET
    public List<Sensor> getSensors(@QueryParam("type") String type) {

        Collection<Sensor> all = DataStore.sensors.values();

        
        if (type == null || type.isBlank()) {
            return new ArrayList<>(all);
        }

        
        List<Sensor> filtered = new ArrayList<>();
        for (Sensor s : all) {
            if (s.getType() != null && s.getType().equalsIgnoreCase(type)) {
                filtered.add(s);
            }
        }
        return filtered;
    }

    
    @POST
    public Response addSensor(Sensor sensor) {

        
        if (sensor.getId() == null || sensor.getId().isBlank()) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "BAD_REQUEST");
            error.put("message", "Sensor 'id' field is required.");
            return Response.status(Response.Status.BAD_REQUEST).entity(error).build();
        }

       
        if (DataStore.sensors.containsKey(sensor.getId())) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "CONFLICT");
            error.put("message", "A sensor with ID '" + sensor.getId() + "' already exists.");
            return Response.status(Response.Status.CONFLICT).entity(error).build();
        }

        
        if (sensor.getRoomId() == null || !DataStore.rooms.containsKey(sensor.getRoomId())) {
            throw new LinkedResourceNotFoundException("Room", sensor.getRoomId());
        }

        
        DataStore.sensors.put(sensor.getId(), sensor);

        
        DataStore.rooms.get(sensor.getRoomId())
                .getSensorIds()
                .add(sensor.getId());

        return Response.status(Response.Status.CREATED).entity(sensor).build();
    }

    
    @GET
    @Path("/{id}")
    public Response getSensor(@PathParam("id") String id) {

        Sensor sensor = DataStore.sensors.get(id);

        if (sensor == null) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "NOT_FOUND");
            error.put("message", "Sensor with ID '" + id + "' does not exist.");
            return Response.status(Response.Status.NOT_FOUND).entity(error).build();
        }

        return Response.ok(sensor).build();
    }

   
    @DELETE
    @Path("/{id}")
    public Response deleteSensor(@PathParam("id") String id) {

        Sensor sensor = DataStore.sensors.get(id);

        if (sensor == null) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "NOT_FOUND");
            error.put("message", "Sensor with ID '" + id + "' does not exist.");
            return Response.status(Response.Status.NOT_FOUND).entity(error).build();
        }

        
        DataStore.sensors.remove(id);

        
        if (sensor.getRoomId() != null && DataStore.rooms.containsKey(sensor.getRoomId())) {
            DataStore.rooms.get(sensor.getRoomId()).getSensorIds().remove(id);
        }

                DataStore.readings.remove(id);

        Map<String, String> success = new HashMap<>();
        success.put("message", "Sensor '" + id + "' has been successfully deleted.");
        return Response.ok(success).build();
    }

    
    @Path("/{sensorId}/readings")
    public SensorReadingResource getReadingResource(@PathParam("sensorId") String sensorId) {
        
        return new SensorReadingResource(sensorId);
    }
}
