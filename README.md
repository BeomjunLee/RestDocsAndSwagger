## RestDocs + Swagger (With OpenAPI-Spec)
:clipboard:Spring RestDocs + Swagger 조합 한번에 사용하기(OpenApi Spec):clipboard:

자세한 내용은 포스팅 하였으니 여기서 확인해주세요
https://blog.naver.com/qjawnswkd/222340413113<br>

### Gradle Settings

```java
plugins {
    ...
    id "org.asciidoctor.convert" version "1.5.9.2"
    id 'com.epages.restdocs-api-spec' version '0.11.3' //swagger 설정 1
}

dependencies {
    ...
    testCompile('com.epages:restdocs-api-spec-mockmvc:0.11.3') //swagger 설정2
    asciidoctor 'org.springframework.restdocs:spring-restdocs-asciidoctor'
    testImplementation 'org.springframework.restdocs:spring-restdocs-mockmvc'
    ...
}

ext {
    snippetsDir = file('build/generated-snippets')
}

test {
    outputs.dir snippetsDir
    useJUnitPlatform()
}

asciidoctor {
    inputs.dir snippetsDir
    dependsOn test
}

bootJar {
    dependsOn asciidoctor

    copy{
        from "build/asciidoc/html5"
        into "src/main/resources/static/docs/"
    }
}

//swagger 설정 3
openapi3 {
    server = 'http://localhost:8080'
    title = 'My API'
    description = 'My API description'
    version = '0.1.0'
    format = 'yaml'

    copy{
        from "build/api-spec"
        into "src/main/resources/static/docs"
    }
}

```
<br>

### Sample Test Code (OpenAPI-Spec Swagger)

```java
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
```

<br>

### Install & Run Swagger-UI with Docker

```java
docker pull swaggerapi/swagger-ui

docker run -p 80:8080 swaggerapi/swagger-ui
```

### Result

주소창에 서버상의 openapi3.yaml 파일 경로 입력
<img src="https://user-images.githubusercontent.com/69130921/117283847-2d6cba00-aea1-11eb-99ae-85816745751f.png">

