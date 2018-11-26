package com.autonomous.drone.intercepter;

import com.autonomous.drone.customEnum.PermissionEnum;
import com.autonomous.drone.customEnum.RoleEnum;

import java.lang.annotation.*;

@Target(value = ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface PermissionSecure {
    RoleEnum[] role() default {};
    PermissionEnum[] permission() default {};
}
