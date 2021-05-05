package com.example.sample.user;

import com.example.sample.user.request.RequestUser;
import com.example.sample.user.response.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @PostMapping("/{id}/following")
    public Response following(@PathVariable Long id, @RequestBody RequestUser requestUser) {
        return userService.addFollowing(id , requestUser.getUsername());
    }
}
