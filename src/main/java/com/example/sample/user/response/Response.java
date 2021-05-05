package com.example.sample.user.response;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Response {

    private String status;
    private int code;
    private String message;

    @Builder
    public Response(String status, int code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}
