package com.Birthday_reminder;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
@MapperScan("com.Birthday_reminder.mapper")
@SpringBootApplication
public class Birthday_reminder {
    public  static void main(String[] args) {
        SpringApplication.run(Birthday_reminder.class,args);
    }
}
