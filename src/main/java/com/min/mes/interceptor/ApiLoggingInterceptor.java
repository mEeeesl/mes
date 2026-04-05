package com.min.mes.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

public class ApiLoggingInterceptor implements HandlerInterceptor {

    private static final String START_TIME = "startTime";

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) {

        request.setAttribute(START_TIME, System.currentTimeMillis());

        String uri = request.getRequestURI();
        String method = request.getMethod();
        long threadId = Thread.currentThread().getId();

        String user = getUser();

        if(handler instanceof HandlerMethod handlerMethod){

            String controller =
                    handlerMethod.getBeanType().getName();

            String methodName =
                    handlerMethod.getMethod().getName();

            System.out.println(
                    "[TID:" + threadId + "] "
                            + "user:" + user + " | "
                            + method + " "
                            + uri + " -> "
                            + controller + "."
                            + methodName
            );
        }

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request,
                                HttpServletResponse response,
                                Object handler,
                                Exception ex) {

        Long start = (Long) request.getAttribute(START_TIME);

        if(start != null){

            long duration =
                    System.currentTimeMillis() - start;

            System.out.println(
                    " -> completed in "
                            + duration + "ms"
            );
        }
    }

    private String getUser(){

        Authentication auth =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if(auth == null || !auth.isAuthenticated()){
            return "anonymous";
        }

        return auth.getName();
    }
}