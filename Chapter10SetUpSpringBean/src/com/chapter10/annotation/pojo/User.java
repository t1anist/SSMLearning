package com.chapter10.annotation.pojo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component(value = "user")
public class User {
    @Value("1")
    private Long id;
    @Value("user_name_1")
    private String userName;
}
