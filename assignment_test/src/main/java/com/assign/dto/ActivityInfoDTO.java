package com.assign.dto;

import java.util.Date;

import com.assign.constant.UserConstant.ResultEnum;
import com.assign.constant.UserConstant.UserActivityInfoEnum;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ActivityInfoDTO {

	private Long infoId;
	
	private UserActivityInfoEnum infoType;
	
	private ResultEnum resultType;

	private String reason;
	
	private Long userId;
	
	private String loginToken;
	
    private Date createdAt;

}
