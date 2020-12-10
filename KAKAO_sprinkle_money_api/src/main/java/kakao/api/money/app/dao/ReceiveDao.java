package kakao.api.money.app.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import kakao.api.money.app.vo.ReceiveInfoVo;
import kakao.api.money.app.vo.SprinkleInfoVo;

@Repository
public class ReceiveDao {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private final ListOperations<String, Object>  listOperations;
	private final HashOperations<String, String, String> hashOperations;
	private final ValueOperations<String, Object> valueOperations;

	@Autowired
	public ReceiveDao(RedisTemplate<String, Object> redisTemplate){
		listOperations 		= redisTemplate.opsForList();
		hashOperations 		= redisTemplate.opsForHash();
		valueOperations 	= redisTemplate.opsForValue();
	}
	
	/**
	 * 특정 채팅방에 뿌리기 토큰으로 뿌리기 이력이 있는지 확인
	 * 
	 * @param xRoomId
	 * @param xRequestToken
	 * @return
	 */
	public boolean existSplinkleInfo(String xRoomId, String xRequestToken) {
		logger.debug("existSplinkleInfo Key >> {}", SprinkleInfoVo.getSplinkleInfoKey(xRoomId, xRequestToken));
		String json = (String)valueOperations.get(SprinkleInfoVo.getSplinkleInfoKey(xRoomId, xRequestToken));
		logger.debug("existSplinkleInfo Size >> {}", valueOperations.size(SprinkleInfoVo.getSplinkleInfoKey(xRoomId, xRequestToken)));
		logger.debug("existSplinkleInfo Data >> {}", json);
		return !(json==null);
	}
	
	/**
	 * 특정 유저가 특정 채팅방에서 금액을 수령하였는지 확인
	 * 
	 * @param xRoomId
	 * @param xRequestToken
	 * @param xUserId
	 * @return
	 */
	public boolean isReceived(String xRoomId, String xRequestToken, String xUserId) {
		return hashOperations.hasKey(ReceiveInfoVo.getReceiveInfoKey(xRoomId, xRequestToken), xUserId);
	}
	
	/**
	 * 특정 방에서 뿌린 금액 특정인 수령 처리
	 * 
	 * @param xRoomId
	 * @param xRequestToken
	 * @return
	 */
	public int getReceiveAmount(String xRoomId, String xRequestToken) {
		String key = "DistributedList:"+xRoomId+":"+xRequestToken;
		return (Integer)listOperations.rightPop(key);
	}
	
	/**
	 * 특정인 금액 수령 후 수령 정보 등록
	 * 
	 * @param xRoomId
	 * @param xRequestToken
	 * @param receiveInfoVo
	 * @throws JsonProcessingException
	 */
	public void insertReceiveInfo(String xRoomId, String xRequestToken, ReceiveInfoVo receiveInfoVo) throws JsonProcessingException {
		hashOperations.put(ReceiveInfoVo.getReceiveInfoKey(xRoomId, xRequestToken), receiveInfoVo.getUserId() , receiveInfoVo.serialize());
	}
	
	/**
	 * 특정 방의 뿌리기 건에 대해 각 참가자들의 금액 수령 여부 확인
	 * 
	 * @param xRoomId
	 * @param xRequestToken
	 * @return
	 * @throws JsonMappingException
	 * @throws JsonProcessingException
	 */
	public List<ReceiveInfoVo> getReceiveInfoList(String xRoomId, String xRequestToken) throws JsonMappingException, JsonProcessingException {
		
		Map<String, String> receiveInfoMap = hashOperations.entries(ReceiveInfoVo.getReceiveInfoKey(xRoomId, xRequestToken));
		
		List<ReceiveInfoVo> receiveInfoVoList = new ArrayList<ReceiveInfoVo>();
		ReceiveInfoVo vo = new ReceiveInfoVo();
		for (Map.Entry<String, String> entry : receiveInfoMap.entrySet()) {
			receiveInfoVoList.add(vo.deSerialize(entry.getValue()));
        }
		
		return receiveInfoVoList;
	}
	
}
