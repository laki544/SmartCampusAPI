package com.smartcampus.resources;

import com.smartcampus.exceptions.RoomNotEmptyException;
import com.smartcampus.model.Room;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


@Path("/rooms")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RoomResource {

    
    @GET
    public Collection<Room> getAllRooms() {
        return DataStore.rooms.values();
    }

    
    @POST
    public Response createRoom(Room room) {

        
        if (room.getId() == null || room.getId().isBlank()) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "BAD_REQUEST");
            error.put("message", "Room 'id' field is required.");
            return Response.status(Response.Status.BAD_REQUEST).entity(error).build();
        }

        
        if (DataStore.rooms.containsKey(room.getId())) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "CONFLICT");
            error.put("message", "A room with ID '" + room.getId() + "' already exists.");
            return Response.status(Response.Status.CONFLICT).entity(error).build();
        }

        DataStore.rooms.put(room.getId(), room);

        
        return Response.status(Response.Status.CREATED).entity(room).build();
    }

    
    @GET
    @Path("/{id}")
    public Response getRoom(@PathParam("id") String id) {

        Room room = DataStore.rooms.get(id);

        if (room == null) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "NOT_FOUND");
            error.put("message", "Room with ID '" + id + "' does not exist.");
            return Response.status(Response.Status.NOT_FOUND).entity(error).build();
        }

        return Response.ok(room).build();
    }

    
    @DELETE
    @Path("/{id}")
    public Response deleteRoom(@PathParam("id") String id) {

        Room room = DataStore.rooms.get(id);

        
        if (room == null) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "NOT_FOUND");
            error.put("message", "Room with ID '" + id + "' does not exist.");
            return Response.status(Response.Status.NOT_FOUND).entity(error).build();
        }

        
        if (!room.getSensorIds().isEmpty()) {
            throw new RoomNotEmptyException(id);
        }

        DataStore.rooms.remove(id);

        Map<String, String> success = new HashMap<>();
        success.put("message", "Room '" + id + "' has been successfully deleted.");
        return Response.ok(success).build();
    }
}
