package com.smartcampus.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.HashMap;
import java.util.Map;


@Path("/")
public class ApiResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, Object> getApiInfo() {

        Map<String, Object> response = new HashMap<>();

        
        response.put("name", "Smart Campus Sensor & Room Management API");
        response.put("version", "v1");
        response.put("description", "RESTful API for managing campus rooms and IoT sensors.");

        
        Map<String, String> contact = new HashMap<>();
        contact.put("module", "5COSC022C");
        contact.put("owner", "Lakindu Chehan Rathugamage");
        contact.put("email", "w2120616@westminster.ac.uk");
        response.put("contact", contact);

        
        Map<String, String> links = new HashMap<>();
        links.put("self",    "/api/v1");
        links.put("rooms",   "/api/v1/rooms");
        links.put("sensors", "/api/v1/sensors");
        response.put("_links", links);

        return response;
    }
}
