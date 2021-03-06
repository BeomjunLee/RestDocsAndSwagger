package com.example.sample.user.response;

import com.example.sample.user.exception.ResultCode;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Response {

    private ResultCode resultCode;
    private int status;
    private String message;

    @Builder
    public Response(ResultCode resultCode, int status, String message) {
        this.resultCode = resultCode;
        this.status = status;
        this.message = message;
    }
}
