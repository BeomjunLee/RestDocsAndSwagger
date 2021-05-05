package com.example.sample.user;

import com.example.sample.user.request.RequestUser;
import com.example.sample.user.response.Response;
import com.example.sample.user.response.ResponseUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final FollowRepository followRepository;


    public ResponseUser createUser(RequestUser dto) {
        User user = User.createUser(dto);
        User createdUser = userRepository.save(user);

        return ResponseUser.builder()
                    .id(createdUser.getId())
                    .username(createdUser.getUsername())
                    .build();
    }

    public Response addFollowing(Long userId, String followUsername) {
        User user = userRepository.findById(userId).orElseThrow();
        User followingUser = userRepository.findByUsername(followUsername).orElseThrow();
        Optional<Follow> follow = followRepository.findByFollowerUser_IdAndFollowingUser_Id(user.getId(), followingUser.getId());

        if (follow.isEmpty()) {
            followRepository.save(Follow.addFollowing(user, followingUser));
            return Response.builder()
                    .status("success")
                    .code(200)
                    .message("팔로우 되었습니다")
                    .build();
        }

        followRepository.deleteById(follow.get().getId());
        return Response.builder()
                .status("success")
                .code(200)
                .message("언팔로우 되었습니다")
                .build();
    }
}
