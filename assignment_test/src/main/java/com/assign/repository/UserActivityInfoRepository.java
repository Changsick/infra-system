package com.assign.repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.assign.constant.UserConstant.UserActivityInfoEnum;
import com.assign.model.UserActivityInfoVO;

@Repository
public interface UserActivityInfoRepository extends JpaRepository<UserActivityInfoVO, Long> {

	List<UserActivityInfoVO> findByUserIdAndInfoTypeAndCreatedAtGreaterThanEqual(Long userId,
			UserActivityInfoEnum infoType, Date targetDate);

	Optional<UserActivityInfoVO> findByLoginToken(String loginToken);
}
