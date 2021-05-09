package com.example.sample.user;
import com.example.sample.user.request.RequestUser;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.event.annotation.BeforeTestMethod;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback(value = false)
class UserServiceTest {

    @Autowired
    UserService userService;
    @Autowired
    UserRepository userRepository;
    @Autowired
    FollowRepository followRepository;

    @Test
    @DisplayName("팔로우 테스트")
    public void follow() throws Exception{
        //given
        userService.addFollowing(1L, "user2");
        userService.addFollowing(1L, "user3");
        userService.addFollowing(3L, "user2");
        //when
        List<Follow> followingList = followRepository.findAllByFollowerUser_Id(1L);
        List<Follow> followerList = followRepository.findAllByFollowingUser_Id(2L);
        //then
        //user1 의 팔로잉 목록 -> user2 포함
        assertThat(followingList.get(0).getFollowingUser().getUsername()).isEqualTo("user2");
        //user1 의 팔로잉 목록 -> user3 포함
        assertThat(followingList.get(1).getFollowingUser().getUsername()).isEqualTo("user3");
        //user2 의 팔로워 목록 -> user1 포함
        assertThat(followerList.get(0).getFollowerUser().getUsername()).isEqualTo("user1");
        //user2 의 팔로워 목록 -> user3 포함
        assertThat(followerList.get(1).getFollowerUser().getUsername()).isEqualTo("user3");
        //user3 의 팔로워 목록 -> user1 포함
        assertThat(followerList.get(0).getFollowerUser().getUsername()).isEqualTo("user1");
    }

    @Test
    @DisplayName("언팔로우 테스트")
    public void unFollow() throws Exception{
        //given
        userService.addFollowing(1L, "user4");
        userService.addFollowing(1L, "user3");
        userService.addFollowing(1L, "user3");
        //when
        List<Follow> followingList = followRepository.findAllByFollowerUser_Id(1L);
        List<Follow> followerList = followRepository.findAllByFollowingUser_Id(3L);
        User user3 = userRepository.findById(3L).get();
        User user4 = userRepository.findById(4L).get();
        //then
        //user1 의 팔로잉 목록은 user4 1개 만 보유해야됨
        assertThat(followingList.stream().count()).isEqualTo(1);
        //user1 의 팔로잉 목록에 user3 이 없어야 됨
        assertThat(!followingList.contains(user3)).isEqualTo(true);
        //user3 의 팔로워 목록에 user1 이 없어야 됨
        assertThat(!followerList.contains(user4)).isEqualTo(true);

    }

    @Test
    @DisplayName("배치사이즈 테스트")
    public void BatchSize() throws Exception{
        //given
        //when
        List<User> users = userRepository.findAll();
        //then
        for (int i = 0; i < users.size(); i++) {
            List<Follow> followingList = users.get(i).getFollowingList();
            List<Follow> followerList = users.get(i).getFollowerList();
            followingList.toString();
            followerList.toString();
        }
    }
}