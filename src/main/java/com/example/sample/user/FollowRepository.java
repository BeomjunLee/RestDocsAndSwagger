package com.example.sample.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FollowRepository extends JpaRepository<Follow, Long> {

    List<Follow> findAllByFollowerUser_Id(Long id);
    List<Follow> findAllByFollowingUser_Id(Long id);

    Optional<Follow> findByFollowerUser_IdAndFollowingUser_Id(Long loginUserId, Long followingUserId);

}
