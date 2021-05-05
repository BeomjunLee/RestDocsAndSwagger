package com.example.sample.user.request;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RequestUser {
    private String username;

    @Builder
    public RequestUser(String username) {
        this.username = username;
    }
}
