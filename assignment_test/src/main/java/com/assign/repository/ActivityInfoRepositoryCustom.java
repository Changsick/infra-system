package com.assign.repository;

import java.util.Date;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.assign.constant.UserConstant.ResultEnum;
import com.assign.constant.UserConstant.UserActivityInfoEnum;
import com.assign.dto.ActivityInfoDTO;

public interface ActivityInfoRepositoryCustom {

	Page<ActivityInfoDTO> findInfoData (
			Long userId
			, UserActivityInfoEnum infoType
			, ResultEnum resultType
			, Date startDate
			, Date endDate
			, Pageable pageable);
}
