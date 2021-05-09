package com.example.sample.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

//    @Query("select u from User u join fetch u.followingList join fetch u.followerList")
//    List<User> findAllByFetchJoin();
}
