package com.example.sample.user;

import lombok.Builder;
import lombok.Getter;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
public class Follow {

    @Id
    @GeneratedValue
    @Column(name = "friend_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "following_id")
    private User followingUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "follower_id")
    private User followerUser;

    @Builder
    public Follow(User followingUser, User followerUser) {
        this.followingUser = followingUser;
        this.followerUser = followerUser;
    }

    public static Follow addFollowing(User loginUser, User followingUser) {
        Follow follow = Follow.builder()
                            .followingUser(followingUser)
                            .followerUser(loginUser)
                            .build();
        return follow;
    }

}
