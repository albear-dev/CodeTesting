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
import kakao.api.money.app.dto.SearchReqDto;
import kakao.api.money.app.dto.SearchResDto;
import kakao.api.money.app.service.SearchService;
import reactor.core.publisher.Mono;

@RestController
public class SearchController {
//	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	SearchService searchService;

	@PostMapping(path = "/v1/money/search", produces = MediaType.APPLICATION_JSON_VALUE)
	public Mono<ResponseEntity<CommonResDto>> sprinkle(
			  @Validated @RequestBody SearchReqDto reqDto
			 ,@RequestHeader("X-ROOM-ID") @NotEmpty String xRoomId
			 ,@RequestHeader("X-USER-ID") @NotEmpty String xUserId) throws Exception {
		
		// 조회 서비스
		SearchResDto resDto = searchService.doSearchSprinkleInfo(xRoomId, xUserId, reqDto.getToken());
		
		return Mono.just(ResponseEntity.status(HttpStatus.OK)
//			      .header("X-Reason", "user-invalid")
			      .body(CommonResDto.of(ResultType.SUCCESS).setData(resDto)));
	}

}
