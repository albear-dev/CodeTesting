package kakao.api.money.app.controller;

import javax.validation.constraints.NotEmpty;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import kakao.api.money.app.common.exception.ResultType;
import kakao.api.money.app.dto.CommonResDto;
import kakao.api.money.app.dto.ReceiveReqDto;
import kakao.api.money.app.dto.ReceiveResDto;
import kakao.api.money.app.service.ReceiveService;
import reactor.core.publisher.Mono;

@RestController
public class ReceiveController {

//	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	ReceiveService receiveService;

	@PostMapping(path = "/v1/money/receive", produces = MediaType.APPLICATION_JSON_VALUE)
	public Mono<ResponseEntity<CommonResDto>> receive(
			 @Validated @RequestBody ReceiveReqDto reqDto
			 ,@RequestHeader("X-ROOM-ID") @NotEmpty String xRoomId
			 ,@RequestHeader("X-USER-ID") @NotEmpty String xUserId) throws Exception {
		
		// 뿌린 금액 받기 서비스
		int receiveAmount = receiveService.doReceiveMoney(xRoomId, xUserId, reqDto.getToken());
		
		// 받은 금액 설정
		ReceiveResDto resDto = new ReceiveResDto();
		resDto.setAmount(receiveAmount);
		
		return Mono.just(ResponseEntity.status(HttpStatus.OK)
//			      .header("X-Reason", "user-invalid")
			      .body(CommonResDto.of(ResultType.SUCCESS).setData(resDto)));
	}
}
