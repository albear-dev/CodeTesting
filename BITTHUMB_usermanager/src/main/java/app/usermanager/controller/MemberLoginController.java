package app.usermanager.controller;

import java.util.Date;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import app.common.exception.BizException;
import app.common.exception.ResultType;
import app.common.security.JwtProvider;
import app.usermanager.dto.CommonResDto;
import app.usermanager.dto.MemberLoginReqDto;
import app.usermanager.dto.MemberLoginResDto;
import app.usermanager.entity.MemberEntity;
import app.usermanager.repository.MemberRepository;

@EnableAutoConfiguration
@RestController
@RequestMapping(path = "/v1/member/login", produces = MediaType.APPLICATION_JSON_VALUE)
public class MemberLoginController {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private MessageSource messageSource;
//	@Autowired
//	private ModelMapper modelMapper;
	@Autowired
	private MemberRepository memberRepository;
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	@Autowired
	private JwtProvider jwtUtil;
	
	@PostMapping
	public ResponseEntity<CommonResDto> login(@Validated @RequestBody MemberLoginReqDto memberLoginReqDto, Locale locale) throws Exception {
		
		
		try {
			// TODO 나중에 시간나면 ElseThrow 변경해 보기

			// 1.1 유저 정보 조회
			MemberEntity memberEntity = memberRepository.findByUserId(memberLoginReqDto.getUserId());
			if(memberEntity == null) {
				// ERROR. 유저 정보를 찾을 수 없습니다.
				throw new BizException("login.E001");
			}
			
			// 1.2 비밀번호 검증
			if(!passwordEncoder.matches(memberLoginReqDto.getUserPw(), memberEntity.getUserPw())) {
				// ERROR. 올바르지 않은 비밀번호 입니다.
				throw new BizException("login.E002");
			}
			
			// 2 .로그인 기록 업데이트
			memberRepository.updateLastLoginDate(memberLoginReqDto.getUserId(), new Date());
			
			// 3.1 토큰 발급
			String newToken = jwtUtil.createToken(memberLoginReqDto.getUserId(), memberEntity.getUserName(), new String[] {"USER"});
			
			MemberLoginResDto resDto = new MemberLoginResDto();
			resDto.setToken(newToken);
			
			return new ResponseEntity<>(
				CommonResDto.of(ResultType.SUCCESS).setData(resDto),
				HttpStatus.OK
			);
			
		}catch(BizException e) {
			return new ResponseEntity<>(
				CommonResDto.of(
					ResultType.BIZ_ERROR,
					e.getCode(),
					messageSource.getMessage(e.getCode(), null, locale)
				),
				HttpStatus.OK
			);
			
		}catch(Exception e) {
			e.printStackTrace();
			if(logger.isErrorEnabled()) { logger.error("Error Message [{}]", e.getMessage()); }
			
			return new ResponseEntity<>(
				CommonResDto.of(
					ResultType.BIZ_ERROR,
					BizException.BIZ_ETC_ERROR_CODE,
					messageSource.getMessage(BizException.BIZ_ETC_ERROR_CODE, null, locale)
				),
				HttpStatus.OK
			);
		}
		
		
	}
	
}
