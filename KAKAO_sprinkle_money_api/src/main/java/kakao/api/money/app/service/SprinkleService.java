package kakao.api.money.app.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;

import kakao.api.money.app.common.exception.ExpectedException;
import kakao.api.money.app.dao.SprinkleDao;
import kakao.api.money.app.vo.SprinkleInfoVo;

@Service
public class SprinkleService {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
    private final SprinkleDao sprinkleDao;
	
    @Autowired
	public SprinkleService(SprinkleDao sprinkleDao) {
		this.sprinkleDao = sprinkleDao;
	}
	
	/**
	 * 자신이 속한 채빙방에 금액 뿌리기를 합니다.
	 *   - 뿌릴 금액, 뿌릴 인원을 정하여 요청값으로 받으면 분배합니다. (분배로직은 공평하게, 나머지는 임의의 한명에게 덤)
	 *   - 뿌리고 나면 고유 TOKEN을 발급합니다.
	 *   
	 * @param xRoomId
	 * @param xUserId
	 * @param sprinkleAmount
	 * @param sprinkleCount
	 * @return
	 * @throws ExpectedException
	 * @throws JsonProcessingException
	 */
	public String doSprinkle(String xRoomId, String xUserId, int sprinkleAmount, int sprinkleCount) throws ExpectedException, JsonProcessingException {

		// 입력값 출력
		logger.debug("Input value xRoomId[{}]", xRoomId);
		logger.debug("Input value xUserId[{}]", xUserId);
		logger.debug("Input value sprinkleAmount[{}]", sprinkleAmount);
		logger.debug("Input value sprinkleCount [{}]", sprinkleCount);

		// 유저가 해당 채팅방에 참여하고 있는지 확인
		if(!sprinkleDao.hasUserFromChatRoom(xRoomId, xUserId)) {
			throw new ExpectedException("sprinkle.E001");	// 뿌리기 요청자는 해당 채팅방에 참여하고 있지 않습니다.
		}
		
		// 금액 배분값 검증
		if(sprinkleAmount < sprinkleCount) {
			throw new ExpectedException("sprinkle.E002");	// 분배금액보다 배분 인원이 많습니다.
		}
		
		// 요청 토큰 생성
		String xRequestToken = generateRendomToken();
		logger.debug("RequestToken [{}]", xRequestToken);

		// 금액 분배
		List<Integer> distributedList = distributeMoney(sprinkleAmount, sprinkleCount);
		logger.debug("DistributedList {}", distributedList);
				
		// 금액 분배 목록 저장
		sprinkleDao.insertSplinkleAmount(xRoomId, xRequestToken, distributedList);
		
		// 분배 정보 저장
		SprinkleInfoVo sprinkleInfoVo = new SprinkleInfoVo();
		sprinkleInfoVo.setSprinkleUser(xUserId);
		sprinkleInfoVo.setSprinkleAmount(sprinkleAmount);
		sprinkleInfoVo.setSprinkleCount(sprinkleCount);
		sprinkleInfoVo.setSprinkleTime(System.currentTimeMillis());
		
		sprinkleDao.insertSplinkleInfo(xRoomId, xUserId, xRequestToken, sprinkleInfoVo);
		
		return xRequestToken;
	}
	
	
	/**
	 * 숫자 OR 영문대문자의 3자리 랜덤 문자를 생성한다.
	 * 
	 * @return
	 */
	private String generateRendomToken() {
		Random rnd = new Random();
		StringBuilder buf = new StringBuilder();
		IntStream.range(0, 3).forEach(i -> {
			if(rnd.nextBoolean()){
		        buf.append((char)((int)(rnd.nextInt(26))+65));
		    }else{
		        buf.append((rnd.nextInt(10)));
		    }
		});
		
		return buf.toString();
	}
	
	
	/**
	 * 머니 인원수에 따라 분배
	 * 
	 * @param amount
	 * @param userCount
	 * @return
	 */
	private List<Integer> distributeMoney(int amount, int userCount){
		Random rnd = new Random();
		List<Integer> amountList = new ArrayList<Integer>();
		
		// 공정분배 후 나머지 일부는 마지막에 합산
		int distributedAmount	= amount / userCount;
		int remainAmount 		= amount % userCount;
		logger.debug("distributedAmount [{}]", distributedAmount);
		logger.debug("remainAmount      [{}]", remainAmount);
		
		int bonusIndex = rnd.nextInt(userCount);
		for(int i=0; i<userCount; i++) {
			if(i == bonusIndex) {
				amountList.add(distributedAmount + remainAmount);
			}else {
				amountList.add(distributedAmount);
			}
		}
		
		return amountList;
	}
}
