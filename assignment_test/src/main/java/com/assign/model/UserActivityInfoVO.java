package com.assign.model;

import java.util.Date;

import com.assign.constant.UserConstant.ResultEnum;
import com.assign.constant.UserConstant.UserActivityInfoEnum;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_activity_info")
@Data
@NoArgsConstructor
public class UserActivityInfoVO {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "info_id")
	private Long infoId;
	
	@Column(name = "info_type")
	@Enumerated(EnumType.STRING)
	private UserActivityInfoEnum infoType;
	
	@Column(name = "result_type")
	@Enumerated(EnumType.STRING)
	private ResultEnum resultType;

	@Column(name = "reason")
	private String reason;
	
	@Column(name = "user_id")
	private Long userId;
	
	@Column(name = "login_token")
	private String loginToken;
	
	@Column(name = "created_at")
    private Date createdAt;

	@Builder
	public UserActivityInfoVO(UserActivityInfoEnum infoType, ResultEnum resultType, String reason) {
		this.infoType = infoType;
		this.resultType = resultType;
		this.reason = reason;
		this.createdAt = new Date();
	}

	
	
}
