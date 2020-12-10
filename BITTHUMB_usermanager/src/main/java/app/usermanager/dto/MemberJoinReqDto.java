package app.usermanager.dto;

import java.io.Serializable;
import java.util.regex.Matcher;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

import app.common.validator.PasswordCheck;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class MemberJoinReqDto implements Serializable{
	private static final long serialVersionUID = -5447464762567997494L;

	@NotBlank(message = "{VALID.MEMBER_JOIN_REQ_DTO.USER_ID.NOT_EMPTY}")
	@Email(message = "{VALID.MEMBER_JOIN_REQ_DTO.EMAIL.NOT_VALID}")
	String userId;
	
	@NotBlank(message = "{VALID.MEMBER_JOIN_REQ_DTO.USER_PW.NOT_EMPTY}")
	@PasswordCheck(message = "{VALID.MEMBER_JOIN_REQ_DTO.USER_PW.NEED_MIX}")
	String userPw;

	@NotBlank(message = "{VALID.MEMBER_JOIN_REQ_DTO.USER_NAME.NOT_EMPTY}")
	String userName;
	
}
