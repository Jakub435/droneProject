package com.autonomous.drone.customException;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Coordinates must be an array of Points")
public class CoordinateArrayException extends NullPointerException {
}
