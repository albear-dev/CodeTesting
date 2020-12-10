package kakao.api.money.app.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import kakao.api.money.app.common.exception.ExpectedException;
import kakao.api.money.app.dao.ReceiveDao;
import kakao.api.money.app.dao.SearchDao;
import kakao.api.money.app.dao.SprinkleDao;
import kakao.api.money.app.dto.SearchResDto;
import kakao.api.money.app.vo.ReceiveInfoVo;
import kakao.api.money.app.vo.SprinkleInfoVo;

@Service
public class SearchService {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	private final SearchDao searchDao;
	private final SprinkleDao sprinkleDao;
	private final ReceiveDao receiveDao;
	private final long MAX_PAST_TIME = 1000*60*60*24*7;
	
	@Autowired
	public SearchService(SearchDao searchDao, SprinkleDao sprinkleDao, ReceiveDao receiveDao) {
		this.searchDao = searchDao;
		this.sprinkleDao = sprinkleDao;
		this.receiveDao = receiveDao;
	}

	/**
	 * 뿌린 금액을 조회합니다.
	 *   - 뿌린 당사자만 조회 가능
	 *   - 10일지나면 조회 불가
	 *   - 뿌린 시각, 뿌린 금액, 받기 완료된 금액, 받기 완료된 정보 ([받은 금액, 받은사용자 아이디] 리스트) 데이터 응답
	 * 
	 * @param xRoomId
	 * @param xUserId
	 * @param xRequestToken
	 * @return
	 * @throws ExpectedException
	 * @throws JsonMappingException
	 * @throws JsonProcessingException
	 */
	public SearchResDto doSearchSprinkleInfo(String xRoomId, String xUserId, String xRequestToken) throws ExpectedException, JsonMappingException, JsonProcessingException {
		
		logger.debug("Input value xRoomId[{}]"     , xRoomId);
		logger.debug("Input value xUserId[{}]"     , xUserId);
		logger.debug("Input value xRequestToken[{}]", xRequestToken);
		
		// 뿌리기 정보 확인
		if(!searchDao.hasDistributedList(xRoomId, xRequestToken)) {
			// 뿌리기 정보가 없습니다.
			throw new ExpectedException("search.E001");
		}
		
		// 조회 요청이 뿌린 사람 당사자인지 확인
		SprinkleInfoVo sprinkleInfoVo = sprinkleDao.getSplinkleInfo(xRoomId, xRequestToken);
		logger.debug("SprinkleUser [{}]", sprinkleInfoVo.getSprinkleUser());
		// 수령자 확인
		if(!xUserId.contentEquals(sprinkleInfoVo.getSprinkleUser())) {
			// 조회 요청은 뿌리기 당사자만 가능합니다.
			throw new ExpectedException("search.E002");
		}
		
		// 뿌린 건에 대해서는 7일 이내에서만 조회 가능
		long pastTime = System.currentTimeMillis() - sprinkleInfoVo.getSprinkleTime();
		logger.debug("pastTime [{}]", pastTime);
		if(pastTime > MAX_PAST_TIME) {
			// 수령 가능 시간이 지났습니다.
			throw new ExpectedException("search.E003");
		}
		
		// 뿌린 시각, 뿌린 금액, 받기 완료된 금액, 받기 완료된 정보 ([받은 금액, 받은사용자 아이디] 리스트)
		SearchResDto searchResDto = new SearchResDto();
		searchResDto.setSprinkleTime(new SimpleDateFormat("yyyy-mm-dd hh:mm:ss").format(new Date(sprinkleInfoVo.getSprinkleTime())));
		searchResDto.setSprinkleAmount(sprinkleInfoVo.getSprinkleAmount());
		List<ReceiveInfoVo> receiveInfoVoList = receiveDao.getReceiveInfoList(xRoomId, xRequestToken);
		int totalReceivedAmount = receiveInfoVoList.stream().mapToInt(vo -> vo.getAmount()).sum();
		searchResDto.setTotalReceiveAmout(totalReceivedAmount);
		searchResDto.setReceiveInfo(receiveInfoVoList);
		
		return searchResDto;
	}
}
