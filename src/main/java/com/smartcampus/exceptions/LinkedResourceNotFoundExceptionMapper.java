package com.smartcampus.exceptions;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.HashMap;
import java.util.Map;


@Provider
public class LinkedResourceNotFoundExceptionMapper implements ExceptionMapper<LinkedResourceNotFoundException> {

    @Override
    public Response toResponse(LinkedResourceNotFoundException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", "UNPROCESSABLE_ENTITY");
        error.put("status", "422");
        error.put("message", ex.getMessage());
        error.put("invalidField", "roomId");
        error.put("invalidValue", ex.getResourceId());

        return Response
                .status(422)   
                .entity(error)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}
