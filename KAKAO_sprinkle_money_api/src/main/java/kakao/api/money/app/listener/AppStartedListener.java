package kakao.api.money.app.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.stereotype.Component;

@Component
public class AppStartedListener implements ApplicationListener<ApplicationStartedEvent> {
	@Autowired
    RedisTemplate<String, Object> redisTemplate;
	
    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {
        System.out.println("=====================");
        System.out.println("Application started");
        System.out.println("=====================");
        
        // 테스트를 위한 초기 데이터 세팅
        SetOperations<String,Object> setOperations = redisTemplate.opsForSet();
        setOperations.add("ChatRoomList", "100");
        setOperations.add("ChatRoomList", "200");
        setOperations.add("ChatRoomList", "300");
        
        setOperations.add("ChatUserList:100", "U001");
        setOperations.add("ChatUserList:100", "U002");
        setOperations.add("ChatUserList:100", "U003");
        
        setOperations.add("ChatUserList:200", "U001");
        setOperations.add("ChatUserList:200", "U004");
        setOperations.add("ChatUserList:200", "U005");
        setOperations.add("ChatUserList:200", "U006");
        setOperations.add("ChatUserList:200", "U007");
        
        setOperations.add("ChatUserList:300", "U001");
        setOperations.add("ChatUserList:300", "U002");
        setOperations.add("ChatUserList:300", "U004");
        setOperations.add("ChatUserList:300", "U005");
        setOperations.add("ChatUserList:300", "U008");
        setOperations.add("ChatUserList:300", "U009");
        setOperations.add("ChatUserList:300", "U010");
        
    }
}