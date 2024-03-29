package kakao.api.money.app.config;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.test.context.TestConfiguration;

import redis.embedded.RedisServer;

@TestConfiguration
public class TestRedisConfiguration {
	private RedisServer redisServer;
	 
    public TestRedisConfiguration(RedisProperties redisProperties) {
        this.redisServer = new RedisServer(9010);
    }
 
    @PostConstruct
    public void postConstruct() {
        redisServer.start();
    }
 
    @PreDestroy
    public void preDestroy() {
        redisServer.stop();
    }
}
