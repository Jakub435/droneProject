package com.autonomous.drone.customException;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Enter valid username and password")
public class InvalidUsernameOrPasswordException extends Exception{
}
