package com.example.sample.user.response;

import com.example.sample.user.exception.ResultCode;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ResponseFollowList {
    private ResultCode resultCode;
    private int status;
    private String message;
    private List<String> data;

    @Builder
    public ResponseFollowList(ResultCode resultCode, int status, String message, List<String> data) {
        this.resultCode = resultCode;
        this.status = status;
        this.message = message;
        this.data = data;
    }
}
