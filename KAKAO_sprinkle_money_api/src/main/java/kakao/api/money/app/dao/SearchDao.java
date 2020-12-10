package kakao.api.money.app.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class SearchDao {
	private final ListOperations<String, Object>  listOperations;

	@Autowired
	public SearchDao(RedisTemplate<String, Object> redisTemplate){
		listOperations 		= redisTemplate.opsForList();
	}
	
	public boolean hasDistributedList(String xRoomId, String xRequestToken) {
		String key = "DistributedList:"+xRoomId+":"+xRequestToken;
		
		return listOperations.size(key)>0;
	}
	
}
