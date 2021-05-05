package com.example.sample.user;

import com.example.sample.user.request.RequestUser;
import com.example.sample.user.response.Response;
import com.example.sample.user.response.ResponseUser;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    /**
     * 팔로잉 및 언팔로잉 하기
     */
    @PostMapping("/{id}/following")
    public Response following(@PathVariable Long id, @RequestBody RequestUser requestUser) {
        return userService.addFollowing(id , requestUser.getUsername());
    }

    /**
     * 팔로잉 목록보기
     */
    @GetMapping("/{id}/following")
    public List<ResponseUser> getFollowingList(@PathVariable Long id) {
        return userService.getFollowingList(id);
    }

    /**
     * 팔로워 목록 보기
     */
    @GetMapping("/{id}/follower")
    public List<ResponseUser> getFollowerList(@PathVariable Long id) {
        return userService.getFollowerList(id);
    }
}
