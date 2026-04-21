package com.smartcampus.exceptions;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;


@Provider
public class GlobalExceptionMapper implements ExceptionMapper<Throwable> {

    private static final Logger LOGGER = Logger.getLogger(GlobalExceptionMapper.class.getName());

    @Override
    public Response toResponse(Throwable ex) {
        
        LOGGER.log(Level.SEVERE, "Unexpected server error: " + ex.getMessage(), ex);

        Map<String, String> error = new HashMap<>();
        error.put("error", "INTERNAL_SERVER_ERROR");
        error.put("status", "500");
        error.put("message", "An unexpected error occurred. Please contact the system administrator.");

        return Response
                .status(Response.Status.INTERNAL_SERVER_ERROR)   // 500
                .entity(error)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}
