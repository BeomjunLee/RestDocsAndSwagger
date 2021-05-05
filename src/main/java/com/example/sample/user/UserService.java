package com.example.sample.user;

import com.example.sample.user.exception.ResultCode;
import com.example.sample.user.request.RequestUser;
import com.example.sample.user.response.Response;
import com.example.sample.user.response.ResponseFollowList;
import com.example.sample.user.response.ResponseSignup;
import com.example.sample.user.response.ResponseUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final FollowRepository followRepository;

    /**
     * 회원가입
     */
    public ResponseSignup createUser(RequestUser dto) {
        User user = User.createUser(dto);
        User createdUser = userRepository.save(user);

        return ResponseSignup.builder()
                .resultCode(ResultCode.OK)
                .status(HttpStatus.CREATED.value())
                .message("회원가입 성공")
                .data(ResponseUser.builder()
                        .id(createdUser.getId())
                        .username(createdUser.getUsername())
                        .build())
                .build();
    }

    /**
     * 팔로잉 및 언팔로우 하기
     */
    public Response addFollowing(Long userId, String followUsername) {
        User user = userRepository.findById(userId).orElseThrow();
        User followingUser = userRepository.findByUsername(followUsername).orElseThrow();
        Optional<Follow> follow = followRepository.findByFollowerUser_IdAndFollowingUser_Id(user.getId(), followingUser.getId());

        if (follow.isEmpty()) {
            followRepository.save(Follow.addFollowing(user, followingUser));
            return Response.builder()
                    .resultCode(ResultCode.OK)
                    .status(HttpStatus.OK.value())
                    .message("팔로우 되었습니다")
                    .build();
        }

        followRepository.deleteById(follow.get().getId());
        return Response.builder()
                .resultCode(ResultCode.OK)
                .status(HttpStatus.OK.value())
                .message("언팔로우 되었습니다")
                .build();
    }

    /**
     * 팔로잉 목록 보기
     */
    public ResponseFollowList getFollowingList(Long userId) {
        List<Follow> follow = followRepository.findAllByFollowerUser_Id(userId);
        List<String> followingList = follow.stream().map(f -> f.getFollowingUser().getUsername()).collect(Collectors.toList());

        return ResponseFollowList.builder()
                            .resultCode(ResultCode.OK)
                            .status(HttpStatus.OK.value())
                            .message("팔로잉 목록 조회 성공")
                            .data(followingList)
                            .build();
    }

    /**
     * 팔로워 목록 보기
     */
    public ResponseFollowList getFollowerList(Long userId) {
        List<Follow> follow = followRepository.findAllByFollowingUser_Id(userId);
        List<String> followerList = follow.stream().map(f -> f.getFollowerUser().getUsername()).collect(Collectors.toList());

        return ResponseFollowList.builder()
                .resultCode(ResultCode.OK)
                .status(HttpStatus.OK.value())
                .message("팔로워 목록 조회 성공")
                .data(followerList)
                .build();
    }

    public User getUser(String username) {
        return userRepository.findByUsername(username).orElseThrow();
    }
}
