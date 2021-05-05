package com.example.sample.user.response;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseUser {

    private Long id;
    private String username;

    @Builder
    public ResponseUser(Long id, String username) {
        this.id = id;
        this.username = username;
    }
}
