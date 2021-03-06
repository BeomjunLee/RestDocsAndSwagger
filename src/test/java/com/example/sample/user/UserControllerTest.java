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
    @DisplayName("???????????? ?????? ?????????")
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
                .andExpect(jsonPath("message").value("???????????? ??????"))
                .andExpect(jsonPath("data.id").exists())
                .andExpect(jsonPath("data.username").value(requestUser.getUsername()))
        //restDocs
                .andDo(document("user-signUp",
                        requestFields(
                                fieldWithPath("username").description("?????? ?????????")
                        ),
                        responseFields(
                                fieldWithPath("resultCode").description("????????????"),
                                fieldWithPath("status").description("Http ????????????"),
                                fieldWithPath("message").description("?????? ?????????"),
                                fieldWithPath("data.id").description("id"),
                                fieldWithPath("data.username").description("?????? ?????????")
                        )
                ));
    }

    @Test
    @DisplayName("????????? ?????????")
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
                .andExpect(jsonPath("message").value("????????? ???????????????"))
        //restDocs
                .andDo(document("user-following",
                        pathParameters(
                            parameterWithName("id").description("????????? ?????? id")
                        ),
                        requestFields(
                                fieldWithPath("username").description("?????? ?????????")
                        ),
                        responseFields(
                                fieldWithPath("resultCode").description("????????????"),
                                fieldWithPath("status").description("Http ????????????"),
                                fieldWithPath("message").description("?????? ?????????")
                        )
                ))
                //Swagger ??? ?????? pathParameters ??? request,response fields ??? restdocs ?????? ??????
                .andDo(document("user-following",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(
                                ResourceSnippetParameters.builder()
                                        .description("?????? ???????????? ????????? ??? ??? ????????????")
                                        .summary("????????? ??????")
                                        .pathParameters(
                                                parameterWithName("id").description("????????? ?????? id")
                                        ).
                                        requestFields(
                                                fieldWithPath("username").description("?????? ?????????")
                                        ).
                                        responseFields(
                                                fieldWithPath("resultCode").description("????????????"),
                                                fieldWithPath("status").description("Http ????????????"),
                                                fieldWithPath("message").description("?????? ?????????")
                                        ).build()
                        )
                ));

    }

    @Test
    @DisplayName("????????? ?????? ?????????")
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
                .andExpect(jsonPath("message").value("????????? ?????? ?????? ??????"))
                .andExpect(jsonPath("data.[0]").value("test2"))
                .andExpect(jsonPath("data.[1]").value("test3"))
                .andExpect(jsonPath("data.[2]").value("test4"))
                .andExpect(jsonPath("data.[3]").value("test5"))
                //restDocs
                .andDo(document("user-getFollowing",
                        pathParameters(
                                parameterWithName("id").description("????????? ?????? id")
                        ),
                        responseFields(
                                fieldWithPath("resultCode").description("????????????"),
                                fieldWithPath("status").description("Http ????????????"),
                                fieldWithPath("message").description("?????? ?????????"),
                                fieldWithPath("data").description("????????? ?????????")
                        )
                ));
    }

    @Test
    @DisplayName("????????? ?????? ?????????")
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
                .andExpect(jsonPath("message").value("????????? ?????? ?????? ??????"))
                .andExpect(jsonPath("data.[0]").value("test2"))
                .andExpect(jsonPath("data.[1]").value("test3"))
                .andExpect(jsonPath("data.[2]").value("test4"))
                .andExpect(jsonPath("data.[3]").value("test5"))
                //restDocs
                .andDo(document("user-getFollower",
                        pathParameters(
                                parameterWithName("id").description("????????? ?????? id")
                        ),
                        responseFields(
                                fieldWithPath("resultCode").description("????????????"),
                                fieldWithPath("status").description("Http ????????????"),
                                fieldWithPath("message").description("?????? ?????????"),
                                fieldWithPath("data").description("????????? ?????????")
                        )
                ));
    }
}