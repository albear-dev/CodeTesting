package app.usermanager.dto;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import app.common.exception.ResultType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Value;

@Setter
@Getter
@NoArgsConstructor
@JsonInclude(Include.NON_NULL)
public class MemberLoginResDto implements Serializable {
	private static final long serialVersionUID = 6630792847213451247L;
	private String token;
}