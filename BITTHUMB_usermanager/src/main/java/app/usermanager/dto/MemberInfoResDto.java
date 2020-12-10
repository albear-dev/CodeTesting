package app.usermanager.dto;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@JsonInclude(Include.NON_NULL)
public class MemberInfoResDto implements Serializable {
	private static final long serialVersionUID = 6630792847213451247L;

	private String userName;
	private String userEmail;
	private Date lastLoginDate;

}