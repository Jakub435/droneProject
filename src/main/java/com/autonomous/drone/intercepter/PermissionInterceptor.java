package com.autonomous.drone.intercepter;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.autonomous.drone.customException.NoPermissionException;
import com.autonomous.drone.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.List;

@Component
public class PermissionInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    TokenService tokenService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws NoPermissionException, IOException {
        String token = request.getHeader("Authorization");

        if(token == null) response.sendError(401,"No Authentication");
        if(!tokenService.validate(token)) response.sendError(401,"No Authentication");

        DecodedJWT decodedJWT = tokenService.getDecodedJwtFromToken(token);
        List<String> userRoleNames = decodedJWT.getClaim("role").asList(String.class);
        List<String> userPermissionNames = decodedJWT.getClaim("permission").asList(String.class);

        if(handler instanceof HandlerMethod){
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            if (roleAndPermissionControl(handlerMethod, userRoleNames, userPermissionNames)){
                return true;
            }
        }
        throw new NoPermissionException();
    }

    private boolean roleAndPermissionControl(
            HandlerMethod handlerMethod,
            List<String> userRoleNames,
            List<String> userPermissionNames){
        Class<?> clazz = handlerMethod.getBeanType();
        Method method = handlerMethod.getMethod();

        if (clazz == null || method == null) return false;

        boolean isClassAnnotation = clazz.isAnnotationPresent(PermissionSecure.class);
        boolean isMethodAnnotation = method.isAnnotationPresent(PermissionSecure.class);
        PermissionSecure permissionSecure = null;

        if(isMethodAnnotation)
            permissionSecure = method.getAnnotation(PermissionSecure.class);
        else if (isClassAnnotation)
            permissionSecure = clazz.getAnnotation(PermissionSecure.class);

        if (permissionSecure == null) return false;

        return isUserAllowed(userRoleNames, userPermissionNames, permissionSecure);
    }

    private boolean isUserAllowed(
            List<String> userRoleNames,
            List<String> userPermissionNames,
            PermissionSecure permissionSecure){
        String allowedRole = permissionSecure.toString();
        String allowedPermission = permissionSecure.toString();

        if(!allowedRole.isEmpty()) {
            for (String role : userRoleNames)
                if (allowedRole.contains(role)) return true;
        }

        if (!allowedPermission.isEmpty()) {
            for (String permission : userPermissionNames)
                if (allowedPermission.contains(permission)) return true;
        }

        return false;
    }
}