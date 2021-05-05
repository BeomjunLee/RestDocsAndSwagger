package com.example.sample.user;

import com.example.sample.user.exception.ResultCode;
import com.example.sample.user.request.RequestUser;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@AutoConfigureRestDocs
class UserControllerTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    UserService userService;
    @Autowired
    ObjectMapper objectMapper;
    
    @Test
    @DisplayName("회원가입 성공 테스트")
    public void signUp() throws Exception{
        //given
        RequestUser requestUser = RequestUser.builder()
                .username("test")
                .build();
        //when
        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestUser)))
                .andDo(print())
        //then
                .andExpect(status().isCreated())
                .andExpect(jsonPath("resultCode").value(ResultCode.OK.toString()))
                .andExpect(jsonPath("status").value(HttpStatus.CREATED.value()))
                .andExpect(jsonPath("message").value("회원가입 성공"))
                .andExpect(jsonPath("user.id").exists())
                .andExpect(jsonPath("user.username").value(requestUser.getUsername()))
        //restDocs
                .andDo(document("user-signUp",
                        requestFields(
                                fieldWithPath("username").description("유저 아이디")
                        ),
                        responseFields(
                                fieldWithPath("resultCode").description("응답코드"),
                                fieldWithPath("status").description("Http 상태코드"),
                                fieldWithPath("message").description("응답 메세지"),
                                fieldWithPath("user.id").description("id"),
                                fieldWithPath("user.username").description("유저 아이디")
                        )
                ));
    }

    @Test
    @DisplayName("팔로우 테스트")
    public void follow() throws Exception{
        //given
        RequestUser requestUser = RequestUser.builder()
                .username("test")
                .build();
        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestUser)))
                .andDo(print());

        User user = userService.getUser(requestUser.getUsername());

        //when
        mockMvc.perform(post("/users/{id}/following", user.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestUser)))
                .andDo(print())
        //then
                .andExpect(status().isOk())
                .andExpect(jsonPath("resultCode").value(ResultCode.OK.toString()))
                .andExpect(jsonPath("status").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("message").value("팔로우 되었습니다"))
        //restDocs
                .andDo(document("user-following",
                        requestFields(
                                fieldWithPath("username").description("유저 아이디")
                        ),
                        responseFields(
                                fieldWithPath("resultCode").description("응답코드"),
                                fieldWithPath("status").description("Http 상태코드"),
                                fieldWithPath("message").description("응답 메세지")
                        )
                ));
    }
}