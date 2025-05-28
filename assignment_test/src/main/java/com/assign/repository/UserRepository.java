package com.assign.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.assign.model.UserVO;

@Repository
public interface UserRepository extends JpaRepository<UserVO, Long> {

	Optional<UserVO> findByEmail(String email);

}
