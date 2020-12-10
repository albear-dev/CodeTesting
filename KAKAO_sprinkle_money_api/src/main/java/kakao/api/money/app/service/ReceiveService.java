package kakao.api.money.app.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;

import kakao.api.money.app.common.exception.ExpectedException;
import kakao.api.money.app.dao.ReceiveDao;
import kakao.api.money.app.dao.SprinkleDao;
import kakao.api.money.app.vo.ReceiveInfoVo;
import kakao.api.money.app.vo.SprinkleInfoVo;

@Service
public class ReceiveService {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	ReceiveDao receiveDao;
	SprinkleDao sprinkleDao;
	private final long MAX_PAST_TIME = 1000*60*10;
	
	@Autowired
	private RedisTemplate<String, Object> redisTemplate;
	
	@Autowired
	public ReceiveService(ReceiveDao receiveDao, SprinkleDao sprinkleDao) {
		this.receiveDao = receiveDao;
		this.sprinkleDao = sprinkleDao;
	}
	
	/**
	 * 뿌린 금액을 수령합니다.
	 * 	- 뿌린 당사자는 받을수 없습니다.
	 *  - 두번 중복으로 받을수 없습니다.
	 *  - 뿌리기가 호출된 대화방과 동일한 대화방에 속한 사용자만이 받을 수 있습니다
	 *  - 뿌리고 난 후 10분안에 받을수 있습니다.
	 * 
	 * @param xRoomId
	 * @param xUserId
	 * @param xRequestToken
	 * @return
	 * @throws ExpectedException
	 * @throws JsonProcessingException
	 */
	public int doReceiveMoney(String xRoomId, String xUserId, String xRequestToken) throws ExpectedException, JsonProcessingException {
		logger.debug("Input value xRoomId[{}]", xRoomId);
		logger.debug("Input value xUserId[{}]", xUserId);
		logger.debug("Input value xRequestToken[{}]", xRequestToken);
		String json = (String)redisTemplate.opsForValue().get("SprinkleInfo:"+xRoomId+":"+xRequestToken);
		logger.debug("json22>>> {}", json);
		
		// 뿌리기 정보 존재 여부 확인
		// 동일 채팅방 소속 여부 확인도 동시에 가능함.
		if(!receiveDao.existSplinkleInfo(xRoomId, xRequestToken)) {
			// 뿌리기 정보가 존재하지 않습니다.
			throw new ExpectedException("receive.E001");
		}
		
		// 뿌리기 정보 가져오기
		SprinkleInfoVo splinkleInfo = sprinkleDao.getSplinkleInfo(xRoomId, xRequestToken);
		logger.debug("splinkleInfo [{}]", splinkleInfo.toString());
		
		// 수령자 확인
		if(xUserId.contentEquals(splinkleInfo.getSprinkleUser())) {
			// 뿌리기 당자사는 수령이 불가능합니다.
			throw new ExpectedException("receive.E002");
		}
		
		// 이미 수령 하였는지 확인
		if(receiveDao.isReceived(xRoomId, xRequestToken, xUserId)) {
			// 이미 금액을 수령하였습니다.
			throw new ExpectedException("receive.E003");
		}
		
		// 수령 가능 일자 확인 (10분내)
		long pastTime = System.currentTimeMillis() - splinkleInfo.getSprinkleTime();
		logger.debug("pastTime [{}]", pastTime);
		if(pastTime > MAX_PAST_TIME) {
			// 수령 가능 시간이 지났습니다.
			throw new ExpectedException("receive.E004");
		}
		
		// 수령 가능 금액
		int receiveMoneyAmount = receiveDao.getReceiveAmount(xRoomId, xRequestToken);
		logger.debug("receiveAmount [{}]", receiveMoneyAmount);
		
		// 수령 정보 저장
		ReceiveInfoVo receiveInfoVo = new ReceiveInfoVo();
		receiveInfoVo.setUserId(xUserId);
		receiveInfoVo.setAmount(receiveMoneyAmount);
		receiveInfoVo.setTime(System.currentTimeMillis());
		receiveDao.insertReceiveInfo(xRoomId, xRequestToken, receiveInfoVo);
		
		return receiveMoneyAmount;
	}
	
}
