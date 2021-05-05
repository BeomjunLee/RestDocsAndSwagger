package com.example.sample;
import com.example.sample.user.UserService;
import com.example.sample.user.request.RequestUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;


@SpringBootApplication
public class SampleApplication {

    public static void main(String[] args) {
        SpringApplication.run(SampleApplication.class, args);
    }

    @Autowired
    UserService userService;

    @PostConstruct
    public void createUsers() {
        for(int i = 1; i <= 5; i++) {
            RequestUser user = RequestUser.builder()
                    .username("user" + i)
                    .build();
            userService.createUser(user);
        }
    }


}
