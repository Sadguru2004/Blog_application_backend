package com.sadguru.blogapplication.payloads;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JwtResponse {

    private String token;
    private Integer userId;
}