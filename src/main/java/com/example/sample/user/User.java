package com.example.sample.user;

import com.example.sample.user.request.RequestUser;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

    @Id
    @GeneratedValue
    @Column(name = "user_id")
    private Long id;

    private String username;

//    @OneToMany(mappedBy = "followerUser")
//    private List<Follow> followingList = new ArrayList<>();
//
//    @OneToMany(mappedBy = "followingUser")
//    private List<Follow> followerList = new ArrayList<>();

    @Builder
    public User(String username) {
        this.username = username;
    }

    public static User createUser(RequestUser dto) {
        User user = User.builder()
                .username(dto.getUsername())
                .build();
        return user;
    }


}
