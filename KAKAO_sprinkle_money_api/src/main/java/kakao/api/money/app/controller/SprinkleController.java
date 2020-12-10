package kakao.api.money.app.controller;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import kakao.api.money.app.common.exception.ResultType;
import kakao.api.money.app.dto.CommonResDto;
import kakao.api.money.app.dto.SprinkleReqDto;
import kakao.api.money.app.dto.SprinkleResDto;
import kakao.api.money.app.service.SprinkleService;
import reactor.core.publisher.Mono;

/**
 * 뿌리기 API
 * 
 * @author albear
 *
 */
@RestController
public class SprinkleController {
//	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	SprinkleService sprinkleService;

	@PostMapping(path = "/v1/money/sprinkle", produces = MediaType.APPLICATION_JSON_VALUE)
	public Mono<ResponseEntity<CommonResDto>> sprinkle(
			 @Valid @RequestBody SprinkleReqDto reqDto
			,@RequestHeader("X-ROOM-ID") @NotEmpty String xRoomId
			,@RequestHeader("X-USER-ID") @NotEmpty String xUserId) throws Exception {
		
		// 금액 분배 및 저장 서비스
		String token = sprinkleService.doSprinkle(xRoomId, xUserId, reqDto.getSprinkleAmount(), reqDto.getSprinkleCount());
		
		// 분배 토큰 섯팅
		SprinkleResDto resDto = new SprinkleResDto();
		resDto.setToken(token);
		return Mono.just(ResponseEntity.status(HttpStatus.OK)
//			      .header("X-Reason", "user-invalid")
			      .body(CommonResDto.of(ResultType.SUCCESS).setData(resDto)));
	}
}