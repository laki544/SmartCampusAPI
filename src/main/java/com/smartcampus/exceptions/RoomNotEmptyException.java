package com.smartcampus.exceptions;


public class RoomNotEmptyException extends RuntimeException {

    private final String roomId;

    public RoomNotEmptyException(String roomId) {
        super("Room '" + roomId + "' still has active sensors and cannot be deleted.");
        this.roomId = roomId;
    }

    public String getRoomId() {
        return roomId;
    }
}
