package com.stake.mercariapplicationserver.repository;

import com.stake.mercariapplicationserver.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @Query(value = "SELECT * FROM user WHERE email = ?1", nativeQuery = true)
    User findAllByEmail(String email);

    @Query(value = "SELECT * FROM user WHERE user_name = ?1", nativeQuery = true)
    User findAllByUsername(String username);
}
