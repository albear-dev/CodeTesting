package app.usermanager.controller;

import java.util.Locale;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import app.common.exception.BizException;
import app.common.exception.ResultType;
import app.usermanager.dto.CommonResDto;
import app.usermanager.dto.MemberInfoReqDto;
import app.usermanager.dto.MemberInfoResDto;
import app.usermanager.entity.MemberEntity;
import app.usermanager.repository.MemberRepository;

@EnableAutoConfiguration
@RestController
@RequestMapping(path = "/v1/member/info", produces = MediaType.APPLICATION_JSON_VALUE)
public class MemberInfoController {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	MessageSource messageSource;
	@Autowired
	ModelMapper modelMapper;
	@Autowired
	MemberRepository memberRepository;
	
	@PostMapping
	public ResponseEntity<CommonResDto> info(@Validated @RequestBody MemberInfoReqDto memberInfoReqDto, Locale locale) throws Exception {
		try {
			// 1.1 유저 정보 조회
			MemberEntity memberEntity = memberRepository.findByUserId(memberInfoReqDto.getUserId());
			if(memberEntity == null) {
				// ERROR. 유저 정보를 찾을 수 없습니다.
				throw new BizException("info.E001");
			}

			MemberInfoResDto resDto = new MemberInfoResDto();
			resDto.setUserName(memberEntity.getUserName());
			resDto.setUserEmail(memberEntity.getUserId());
			resDto.setLastLoginDate(memberEntity.getLastLoginDate());
			
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
