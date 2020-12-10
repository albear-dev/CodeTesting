package analyzer.app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import analyzer.app.common.exception.ResultType;
import analyzer.app.dto.AnalyzeReqDto;
import analyzer.app.dto.AnalyzeResDto;
import analyzer.app.dto.CommonResDto;
import analyzer.app.service.AnalyzeService;

@RestController
public class AnalyzeController {
	
	AnalyzeService analyzeService;
	
	@Autowired
	public void setAnalyzeService(AnalyzeService analyzeService) {
		this.analyzeService = analyzeService;
	}

	@PostMapping(path = "/v1/analyze", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<CommonResDto> receive(@Validated @RequestBody AnalyzeReqDto reqDto) throws Exception {

		AnalyzeResDto resDto = analyzeService.doAnalyze(reqDto);

		return ResponseEntity.status(HttpStatus.OK)
//			      .header("X-Reason", "user-invalid")
			      .body(CommonResDto.of(ResultType.SUCCESS).setData(resDto));
	}
}
