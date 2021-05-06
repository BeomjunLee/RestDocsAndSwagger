package com.example.sample.user;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
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
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
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
    UserRepository userRepository;
    @Autowired
    FollowRepository followRepository;
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
                .andExpect(jsonPath("data.id").exists())
                .andExpect(jsonPath("data.username").value(requestUser.getUsername()))
        //restDocs
                .andDo(document("user-signUp",
                        requestFields(
                                fieldWithPath("username").description("유저 아이디")
                        ),
                        responseFields(
                                fieldWithPath("resultCode").description("응답코드"),
                                fieldWithPath("status").description("Http 상태코드"),
                                fieldWithPath("message").description("응답 메세지"),
                                fieldWithPath("data.id").description("id"),
                                fieldWithPath("data.username").description("유저 아이디")
                        )
                ));
    }

    @Test
    @DisplayName("팔로잉 테스트")
    public void following() throws Exception{
        //given
        RequestUser requestUser = RequestUser.builder()
                .username("test")
                .build();
        userService.createUser(requestUser);
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
                        pathParameters(
                            parameterWithName("id").description("사용자 고유 id")
                        ),
                        requestFields(
                                fieldWithPath("username").description("유저 아이디")
                        ),
                        responseFields(
                                fieldWithPath("resultCode").description("응답코드"),
                                fieldWithPath("status").description("Http 상태코드"),
                                fieldWithPath("message").description("응답 메세지")
                        )
                ))
                //Swagger 만 쓰면 pathParameters 와 request,response fields 가 restdocs 생성 안됨
                .andDo(document("user-following",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(
                                ResourceSnippetParameters.builder()
                                        .description("다른 사용자를 팔로잉 할 수 있습니다")
                                        .summary("팔로잉 하기")
                                        .pathParameters(
                                                parameterWithName("id").description("사용자 고유 id")
                                        ).
                                        requestFields(
                                                fieldWithPath("username").description("유저 아이디")
                                        ).
                                        responseFields(
                                                fieldWithPath("resultCode").description("응답코드"),
                                                fieldWithPath("status").description("Http 상태코드"),
                                                fieldWithPath("message").description("응답 메세지")
                                        ).build()
                        )
                ));

    }

    @Test
    @DisplayName("팔로잉 보기 테스트")
    public void getFollowing() throws Exception{
        //given
        for (int i = 1; i <= 5; i++) {
            RequestUser requestUser = RequestUser.builder()
                    .username("test" + i)
                    .build();
            userService.createUser(requestUser);
        }

        User user = userService.getUser("test1");
        userService.addFollowing(user.getId(), "test2");
        userService.addFollowing(user.getId(), "test3");
        userService.addFollowing(user.getId(), "test4");
        userService.addFollowing(user.getId(), "test5");

        //when
        mockMvc.perform(RestDocumentationRequestBuilders.get("/users/{id}/following", user.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                //then
                .andExpect(status().isOk())
                .andExpect(jsonPath("resultCode").value(ResultCode.OK.toString()))
                .andExpect(jsonPath("status").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("message").value("팔로잉 목록 조회 성공"))
                .andExpect(jsonPath("data.[0]").value("test2"))
                .andExpect(jsonPath("data.[1]").value("test3"))
                .andExpect(jsonPath("data.[2]").value("test4"))
                .andExpect(jsonPath("data.[3]").value("test5"))
                //restDocs
                .andDo(document("user-getFollowing",
                        pathParameters(
                                parameterWithName("id").description("사용자 고유 id")
                        ),
                        responseFields(
                                fieldWithPath("resultCode").description("응답코드"),
                                fieldWithPath("status").description("Http 상태코드"),
                                fieldWithPath("message").description("응답 메세지"),
                                fieldWithPath("data").description("팔로잉 아이디")
                        )
                ));
    }

    @Test
    @DisplayName("팔로워 보기 테스트")
    public void getFollower() throws Exception{
        //given
        for (int i = 1; i <= 5; i++) {
            RequestUser requestUser = RequestUser.builder()
                    .username("test" + i)
                    .build();
            userService.createUser(requestUser);
        }

        User user1 = userService.getUser("test1");
        User user2 = userService.getUser("test2");
        User user3 = userService.getUser("test3");
        User user4 = userService.getUser("test4");
        User user5 = userService.getUser("test5");
        userService.addFollowing(user2.getId(), "test1");
        userService.addFollowing(user3.getId(), "test1");
        userService.addFollowing(user4.getId(), "test1");
        userService.addFollowing(user5.getId(), "test1");

        //when
        mockMvc.perform(RestDocumentationRequestBuilders.get("/users/{id}/follower", user1.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                //then
                .andExpect(status().isOk())
                .andExpect(jsonPath("resultCode").value(ResultCode.OK.toString()))
                .andExpect(jsonPath("status").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("message").value("팔로워 목록 조회 성공"))
                .andExpect(jsonPath("data.[0]").value("test2"))
                .andExpect(jsonPath("data.[1]").value("test3"))
                .andExpect(jsonPath("data.[2]").value("test4"))
                .andExpect(jsonPath("data.[3]").value("test5"))
                //restDocs
                .andDo(document("user-getFollower",
                        pathParameters(
                                parameterWithName("id").description("사용자 고유 id")
                        ),
                        responseFields(
                                fieldWithPath("resultCode").description("응답코드"),
                                fieldWithPath("status").description("Http 상태코드"),
                                fieldWithPath("message").description("응답 메세지"),
                                fieldWithPath("data").description("팔로워 아이디")
                        )
                ));
    }
}