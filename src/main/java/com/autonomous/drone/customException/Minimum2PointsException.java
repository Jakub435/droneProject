package com.autonomous.drone.customException;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.xml.ws.ResponseWrapper;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Minumum of 2 Points required")
public class Minimum2PointsException extends IllegalArgumentException{
}