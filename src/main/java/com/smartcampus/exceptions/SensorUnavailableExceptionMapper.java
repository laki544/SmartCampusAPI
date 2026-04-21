package com.smartcampus.exceptions;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.HashMap;
import java.util.Map;


@Provider
public class SensorUnavailableExceptionMapper implements ExceptionMapper<SensorUnavailableException> {

    @Override
    public Response toResponse(SensorUnavailableException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", "FORBIDDEN");
        error.put("status", "403");
        error.put("message", ex.getMessage());
        error.put("sensorId", ex.getSensorId());
        error.put("hint", "Change sensor status to ACTIVE before submitting readings.");

        return Response
                .status(Response.Status.FORBIDDEN)   
                .entity(error)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}
