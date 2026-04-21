package com.smartcampus.exceptions;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.HashMap;
import java.util.Map;


@Provider
public class RoomNotEmptyExceptionMapper implements ExceptionMapper<RoomNotEmptyException> {

    @Override
    public Response toResponse(RoomNotEmptyException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", "CONFLICT");
        error.put("status", "409");
        error.put("message", ex.getMessage());
        error.put("hint", "Remove all sensors from room '" + ex.getRoomId() + "' before deleting it.");

        return Response
                .status(Response.Status.CONFLICT)       
                .entity(error)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}
