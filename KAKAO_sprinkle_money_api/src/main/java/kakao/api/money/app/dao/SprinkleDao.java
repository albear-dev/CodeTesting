package kakao.api.money.app.dao;

import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import kakao.api.money.app.vo.SprinkleInfoVo;

@Repository
public class SprinkleDao {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private final ListOperations<String, Object>  listOperations;
	private final SetOperations<String,Object> setOperations;
	private final ValueOperations<String, Object> valueOperations;

	@Autowired
	public SprinkleDao(RedisTemplate<String, Object> redisTemplate){
		listOperations 		= redisTemplate.opsForList();
		setOperations 		= redisTemplate.opsForSet();
		valueOperations 	= redisTemplate.opsForValue();
	}
	
	/**
	 * 해당 유저 채팅방 참여 여부 확인
	 * 
	 * @param xRoomId
	 * @param xUserId
	 * @return
	 */
	public boolean hasUserFromChatRoom(String xRoomId, String xUserId) {
		// 유저가 해당 채팅방에 참여하고 있는지 확인
		Set<Object> userSet = setOperations.members("ChatUserList:"+xRoomId);
		return userSet==null?false:userSet.contains(xUserId);
	}
	
	/**
	 * 분배 금액 저장
	 * 
	 * @param xRoomId
	 * @param xRequestToken
	 * @param distributedList
	 */
	public void insertSplinkleAmount(String xRoomId, String xRequestToken, List<Integer> distributedList) {
		String key = "DistributedList:"+xRoomId+":"+xRequestToken;
		listOperations.rightPushAll(key, distributedList.toArray());
	}
	
	/**
	 * 금액 분배 정보 저장
	 * 
	 * @param xRoomId
	 * @param xUserId
	 * @param xRequestToken
	 * @param sprinkleReqDto
	 * @throws JsonProcessingException 
	 */
	public void insertSplinkleInfo(String xRoomId, String xUserId, String xRequestToken, SprinkleInfoVo sprinkleInfoVo) throws JsonProcessingException {
		String json = new ObjectMapper().writeValueAsString(sprinkleInfoVo);
		valueOperations.set(SprinkleInfoVo.getSplinkleInfoKey(xRoomId, xRequestToken), json);

		logger.debug("insertSplinkleInfo Key >> {}", SprinkleInfoVo.getSplinkleInfoKey(xRoomId, xRequestToken));
		logger.debug("insertSplinkleInfo Data >> {}", json);
		logger.debug("insertSplinkleInfo re Data >> {}", valueOperations.get(SprinkleInfoVo.getSplinkleInfoKey(xRoomId, xRequestToken)));
	}
	
	
	/**
	 * 금액 분배 정보 조회
	 * 
	 * @param xRoomId
	 * @param xRequestToken
	 * @return
	 * @throws JsonMappingException
	 * @throws JsonProcessingException
	 */
	public SprinkleInfoVo getSplinkleInfo(String xRoomId, String xRequestToken) throws JsonMappingException, JsonProcessingException {
		String json = (String)valueOperations.get(SprinkleInfoVo.getSplinkleInfoKey(xRoomId, xRequestToken));
		
		logger.debug("getSplinkleInfo Data >> {}", json);
		
		return new SprinkleInfoVo().deSerialize(json);
	}
}
