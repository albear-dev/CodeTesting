package kakao.api.money.app.config;

import java.util.Optional;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import redis.embedded.RedisServer;

/**
 * Embedded Redis 접속 정보 (Spring Boot App 실행)
 *
 */
@Configuration
public class EmbeddedRedisConfig implements InitializingBean, DisposableBean {

	@Value("${spring.redis.port}")
	private int redisPort;

	private RedisServer redisServer;

	@Override
	public void destroy() throws Exception {
		Optional.ofNullable(redisServer).ifPresent(RedisServer::stop);
		System.out.println("redis server destroy");
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		System.out.println("redisPort >> " + redisPort);
		
		try {
			if(redisServer == null) {
				redisServer = new RedisServer(redisPort);
				redisServer.start();
			}
		}catch(Exception e) {
			System.out.println("redis server start fail");
		}
		
	}
}