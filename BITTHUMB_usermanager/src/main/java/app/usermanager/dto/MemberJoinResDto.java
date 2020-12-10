package app.usermanager.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@JsonInclude(Include.NON_NULL)
public class MemberJoinResDto extends CommonResDto implements Serializable {
	private static final long serialVersionUID = 6630792847213451247L;

}