package kakao.api.money.app;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.regex.Pattern;

import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;

import kakao.api.money.app.dto.SprinkleReqDto;
import reactor.core.publisher.Mono;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureWebTestClient
class MoneySprinkleApplicationTest {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    WebTestClient webTestClient;
    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    private final String CALL_URI = "/v1/money/sprinkle";
    
    public MoneySprinkleApplicationTest() {}
    
	@Before
	public void setup() throws Exception {
		this.webTestClient = WebTestClient
                .bindToServer()
                .baseUrl("https://localhost:8080")
                .build();
		
		/************************************************************
		 * <given>
		 * 뿌리기를 하려는 사람은 특정 채팅방에 참여하고 있다.
		 * 채팅방에는 여러 사람들이 같이 참여하고 있다.
		 ************************************************************/
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

	@Test
	void 뿌리기_기능_정상_요청_테스트() throws Exception {

		/************************************************************
		 * <given>
		 * 유저 'U001'이 10000원을 가지고 있다.
		 * 뿌리기 대상 채팅방은 '100' 번방으로 한다
		 * 총 7명이 받아갈 수 있게 한다.
		 ************************************************************/
		String sprinkleUser 	= "U001";
		String sprinkleRoomId 	= "100";
		int sprinkleAmount 		= 10000;
		int sprinkleCount 		= 7;
		
		/************************************************************
		 * <when>
		 * 뿌리기를 하려는 사람이 금액과 인원을 지정하여 뿌리기를 한다.
		 * 1. 뿌릴 금액, 뿌릴 인원을 요청값으로 받습니다.
		 ************************************************************/
		
		SprinkleReqDto sprinkleReqDto = new SprinkleReqDto();
		
		/** when Requirements No.1 */
		sprinkleReqDto.setSprinkleAmount(sprinkleAmount);
		sprinkleReqDto.setSprinkleCount(sprinkleCount);
		
        webTestClient
        	.post()
	        	.uri(CALL_URI)
	        	.accept(MediaType.APPLICATION_JSON)
	        	.header("X-USER-ID", sprinkleUser)
	        	.header("X-ROOM-ID", sprinkleRoomId)
	        	.body(Mono.just(sprinkleReqDto), SprinkleReqDto.class)
	        .exchange()
	        	/************************************************************
	    		 * <then>
	    		 * 1. 뿌리기 요청건에 대한 고유 token을 발급하고 응답값으로 내려줍니다.
	    		 * 2. token은 3자리 문자열로 구성되며 예측이 불가능해야 합니다
	    		 ************************************************************/
	        .expectStatus().isOk()
	        .expectBody()
		        .consumeWith(response -> {
		        	DocumentContext json = JsonPath.parse(new String(response.getResponseBody()));
		        	logger.info("[뿌리기_기능_예외_요청값_누락_테스트] headers {} ", response.getRequestHeaders());
		        	logger.info("[뿌리기_기능_예외_요청값_누락_테스트] json {}", json.jsonString());
		        	
		        	/** then Requirements No.1 */
		        	assertThat(json.read("$.code"		,String.class)).isEqualTo("200");
		        	assertThat(json.read("$.message"	,String.class)).isNotEmpty();
		        	/** then Requirements No.2 */
		        	assertThat(json.read("$.data.token"	,String.class)).matches(Pattern.compile("[0-9A-Z]{3}"));
		        });
	        ;
	}
	
	@Test
	void 뿌리기_기능_예외_요청값_누락_테스트() throws Exception {
		/************************************************************
		 * <given>
		 * 유저 'U001'이 10000원을 가지고 있다고 했지만 사실은 가지고 있지 않았다.
		 * 뿌리기 대상 채팅방은 '100' 번방으로 한다
		 * 총 7명이 받아갈 수 있게 한다.
		 ************************************************************/
		String sprinkleUser 	= "U001";
		String sprinkleRoomId 	= "100";
//		int sprinkleAmount 		= 10000;
		int sprinkleCount 		= 7;
		
		
		/************************************************************
		 * <when>
		 * 1. 요청 데이터 중 '금액' 필드가 누락
		 ************************************************************/
		SprinkleReqDto sprinkleReqDto = new SprinkleReqDto();
		
		/** when Requirements No.1 */
//		sprinkleReqDto.setSprinkleAmount(sprinkleAmount);
		sprinkleReqDto.setSprinkleCount(sprinkleCount);
		
        webTestClient
        	.post()
	        	.uri(CALL_URI)
	        	.accept(MediaType.APPLICATION_JSON)
	        	.header("X-USER-ID", sprinkleUser)
	        	.header("X-ROOM-ID", sprinkleRoomId)
	        	.body(Mono.just(sprinkleReqDto), SprinkleReqDto.class)
	        .exchange()
	        	/************************************************************
	    		 * <then>
	    		 * 1. 400 Bad Request 응답
	    		 * 2. errors 필드에 응답 사유(필드,코드,메세지) 출력
	    		 ************************************************************/
	        .expectStatus().is4xxClientError()
	        .expectBody()
		        .consumeWith(response -> {
		        	DocumentContext json = JsonPath.parse(new String(response.getResponseBody()));
		        	logger.info("[뿌리기_기능_예외_요청값_누락_테스트] headers {} ", response.getRequestHeaders());
		        	logger.info("[뿌리기_기능_예외_요청값_누락_테스트] json {}", json.jsonString());
		        	
		        	/** then Requirements No.1 */
		        	assertThat(json.read("$.code"				,String.class)).isEqualTo("400");
		        	assertThat(json.read("$.message"			,String.class)).isNotEmpty();
		        	/** then Requirements No.2 */
		        	assertThat(json.read("$.errors[0].field"	,String.class)).isEqualTo("sprinkleAmount");
		        	assertThat(json.read("$.errors[0].errorCode",String.class)).isEqualTo("common.E002");
		        	assertThat(json.read("$.errors[0].errorMsg"	,String.class)).isNotEmpty();
		        });
	        ;
	}
	
	
	@Test
	void 뿌리기_기능_예외_뿌리는유저_채팅방_미참여_오류_테스트() throws Exception {
		/************************************************************
		 * <given>
		 * 유저 'U001'이 10000원을 가지고 있다고 했지만 사실은 가지고 있지 않았다.
		 * 뿌리기 대상 채팅방은 '999' 번방으로 하기로 했는데 사실 존재하지 않는 방이다.
		 * 총 7명이 받아갈 수 있게 한다.
		 ************************************************************/
		String sprinkleUser 	= "U001";
		String sprinkleRoomId 	= "999";
		int sprinkleAmount 		= 10000;
		int sprinkleCount 		= 7;
		
		/************************************************************
		 * <when>
		 * 1. 뿌리기 요청중 채팅방 (X-ROOM-ID) 필드 의 값이 개설되지 않은 채팅방일때
		 ************************************************************/
		SprinkleReqDto sprinkleReqDto = new SprinkleReqDto();
		sprinkleReqDto.setSprinkleAmount(sprinkleAmount);
		sprinkleReqDto.setSprinkleCount(sprinkleCount);
		
        webTestClient
        	.post()
	        	.uri(CALL_URI)
	        	.accept(MediaType.APPLICATION_JSON)
	        	.header("X-USER-ID", sprinkleUser)
	        	/** when Requirements No.1 */
	        	.header("X-ROOM-ID", sprinkleRoomId)
	        	.body(Mono.just(sprinkleReqDto), SprinkleReqDto.class)
	        .exchange()
	        	/************************************************************
	    		 * <then>
	    		 * 1. 200 OK 응답 (비즈니스 오류는 200 OK)
	    		 * 2. errors 필드에 응답 사유(필드,코드,메세지) 출력
	    		 ************************************************************/
	        .expectStatus().isOk()
	        .expectBody()
		        .consumeWith(response -> {
		        	DocumentContext json = JsonPath.parse(new String(response.getResponseBody()));
		        	logger.info("[뿌리기_기능_예외_뿌리는유저_채팅방_미참여_오류_테스트] headers {} ", response.getRequestHeaders());
		        	logger.info("[뿌리기_기능_예외_뿌리는유저_채팅방_미참여_오류_테스트] json {}", json.jsonString());
		        	
		        	/** then Requirements No.1 */
		        	assertThat(json.read("$.code"				, String.class)).isEqualTo("200");
		        	assertThat(json.read("$.message"			, String.class)).isNotEmpty();
		        	/** then Requirements No.2 */
		        	assertThat(json.read("$.errors[0].errorCode",String.class)).isEqualTo("sprinkle.E001");
		        	assertThat(json.read("$.errors[0].errorMsg"	,String.class)).isNotEmpty();
		        });
	        ;
	}
	
	
	@Test
	void 뿌리기_기능_예외_분배금보다_배분인원_많음_테스트() throws Exception {
		/************************************************************
		 * <given>
		 * 유저 'U001'은 10원을 가지고 있다
		 * 뿌리기 대상 채팅방은 '100' 번방으로 하기로 했다.
		 * 총 20명이 받아갈수 있게 한다고 했는데 과연 가능할까...?
		 ************************************************************/
		String sprinkleUser 	= "U001";
		String sprinkleRoomId 	= "100";
		int sprinkleAmount 		= 10;
		int sprinkleCount 		= 20;
		
		/************************************************************
		 * <when>
		 * 1. 입력값중 분배금액이 분배인원보다 작을때
		 ************************************************************/
		SprinkleReqDto sprinkleReqDto = new SprinkleReqDto();
		/** when Requirements No.1 */
		sprinkleReqDto.setSprinkleAmount(sprinkleAmount);
		sprinkleReqDto.setSprinkleCount(sprinkleCount);
		
        webTestClient
        	.post()
	        	.uri(CALL_URI)
	        	.accept(MediaType.APPLICATION_JSON)
	        	.header("X-USER-ID", sprinkleUser)
	        	.header("X-ROOM-ID", sprinkleRoomId)
	        	.body(Mono.just(sprinkleReqDto), SprinkleReqDto.class)
	        .exchange()
	        	/************************************************************
	    		 * <then>
	    		 * 1. 200 OK 응답 (비즈니스 오류는 200 OK)
	    		 * 2. errors 필드에 응답 사유(필드,코드,메세지) 출력
	    		 ************************************************************/
	        .expectStatus().isOk()
	        .expectBody()
		        .consumeWith(response -> {
		        	DocumentContext json = JsonPath.parse(new String(response.getResponseBody()));
		        	logger.info("[뿌리기_기능_예외_분배금보다_배분인원_많음_테스트] headers {} ", response.getRequestHeaders());
		        	logger.info("[뿌리기_기능_예외_분배금보다_배분인원_많음_테스트] json {}", json.jsonString());
		        	
		        	/** then Requirements No.1 */
		        	assertThat(json.read("$.code"				, String.class)).isEqualTo("200");
		        	assertThat(json.read("$.message"			, String.class)).isNotEmpty();
		        	/** then Requirements No.2 */
		        	assertThat(json.read("$.errors[0].errorCode",String.class)).isEqualTo("sprinkle.E002");
		        	assertThat(json.read("$.errors[0].errorMsg"	,String.class)).isNotEmpty();
		        });
	        ;
	}
	

}
