package com.example.demo.exceptions;

import org.springframework.boot.autoconfigure.web.servlet.error.BasicErrorController;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorViewResolver;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;

import javax.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.util.HashMap;

@RestControllerAdvice
public class ControllerAdvice {

    @ExceptionHandler({ConstraintViolationException.class})
    public ResponseEntity<?> invalidParamsExceptionHandler(ConstraintViolationException e, WebRequest request){
        ModelMap modelMap = new ModelMap();
        modelMap.addAttribute("timestamp", LocalDateTime.now());
        modelMap.addAttribute("status",400);
        modelMap.addAttribute("error","Bad Request");
        modelMap.addAttribute("message",e.getMessage());
        modelMap.addAttribute("path", ((ServletWebRequest)request).getRequest().getRequestURL().toString());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(modelMap);
    }


    @ExceptionHandler({MissingServletRequestParameterException.class})
    public ResponseEntity<?> missingParamsExceptionHandler(MissingServletRequestParameterException e, WebRequest request){
        ModelMap modelMap = new ModelMap();
        modelMap.addAttribute("timestamp", LocalDateTime.now());
        modelMap.addAttribute("status",400);
        modelMap.addAttribute("error","Bad Request");
        modelMap.addAttribute("message",e.getMessage());
        modelMap.addAttribute("path", ((ServletWebRequest)request).getRequest().getRequestURL().toString());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(modelMap);
    }
}
