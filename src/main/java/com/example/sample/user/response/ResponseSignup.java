package com.example.sample.user.response;

import com.example.sample.user.exception.ResultCode;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ResponseSignup {
    private ResultCode resultCode;
    private int status;
    private String message;
    private ResponseUser user;

    @Builder
    public ResponseSignup(ResultCode resultCode, int status, String message, ResponseUser user) {
        this.resultCode = resultCode;
        this.status = status;
        this.message = message;
        this.user = user;
    }
}
