package com.Birthday_reminder.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@RestController
@RequestMapping("/hello")//这是一个测试接口
public class HelloController {
    @GetMapping("/test")
    public String test(){
        return "Hello World";
    }
}
