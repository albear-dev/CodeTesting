package app.usermanager.controller;

import java.util.Date;
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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import app.common.exception.BizException;
import app.common.exception.ResultType;
import app.usermanager.dto.CommonResDto;
import app.usermanager.dto.MemberInfoResDto;
import app.usermanager.dto.MemberJoinReqDto;
import app.usermanager.dto.MemberJoinResDto;
import app.usermanager.entity.MemberEntity;
import app.usermanager.repository.MemberRepository;

@EnableAutoConfiguration
@RestController
@RequestMapping(path = "/v1/member/join", produces = MediaType.APPLICATION_JSON_VALUE)
public class MemberJoinController {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	MessageSource messageSource;
	@Autowired
	ModelMapper modelMapper;
	@Autowired
	MemberRepository memberRepository;
	@Autowired
	BCryptPasswordEncoder passwordEncoder;

	@PostMapping
	public ResponseEntity<CommonResDto> join(@Validated @RequestBody MemberJoinReqDto memberJoinReqDto, Locale locale) throws Exception {
		
		try {
			// 1. Do post process check
			// 이미 등록된 아이디가 있는지 확인
			if(memberRepository.existsById(memberJoinReqDto.getUserId())) {
				throw new BizException("join.E001");
			}
			
			// 2. Mapping DTO -> Entity
//			MemberEntity map = modelMapper.map(memberJoinReqDto, MemberEntity.class);
			MemberEntity memberEntity = new MemberEntity();
			memberEntity.setUserId(memberJoinReqDto.getUserId());
			memberEntity.setUserPw(passwordEncoder.encode(memberJoinReqDto.getUserPw()));
			memberEntity.setUserName(memberJoinReqDto.getUserName());
			memberEntity.setRegDate(new Date());
			
			// 3. Do business process
			MemberEntity memberEntityResult = memberRepository.save(memberEntity);
			if (memberEntityResult == null) {
				// 유저 정보 등록에 실패하였습니다.
				throw new BizException("join.E002");
			}
			
			// 4. Return data
			return new ResponseEntity<>(CommonResDto.of(ResultType.SUCCESS), HttpStatus.OK);
			
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
