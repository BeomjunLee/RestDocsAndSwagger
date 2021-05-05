package com.example.sample.user;
import com.example.sample.user.request.RequestUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import javax.persistence.EntityManager;
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
    @Autowired
    EntityManager entityManager;

    @BeforeEach
    public void createUsers() {
        for(int i = 1; i <= 30; i++) {
            RequestUser user = RequestUser.builder()
                    .username("user" + i)
                    .build();
            userService.createUser(user);
        }
    }

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
        userService.addFollowing(1L, "user2");
        userService.addFollowing(1L, "user3");
        userService.addFollowing(1L, "user3");
        //when
        List<Follow> followingList = followRepository.findAllByFollowerUser_Id(1L);
        List<Follow> followerList = followRepository.findAllByFollowingUser_Id(3L);
        User user3 = userRepository.findById(3L).get();
        User user1 = userRepository.findById(1L).get();
        //then
        //user1 의 팔로잉 목록은 user2 1개 만 보유해야됨
        assertThat(followingList.stream().count()).isEqualTo(1);
        //user1 의 팔로잉 목록에 user3 이 없어야 됨
        assertThat(!followingList.contains(user3)).isEqualTo(true);
        //user3 의 팔로워 목록에 user1 이 없어야 됨
        assertThat(!followerList.contains(user1)).isEqualTo(true);

    }
}