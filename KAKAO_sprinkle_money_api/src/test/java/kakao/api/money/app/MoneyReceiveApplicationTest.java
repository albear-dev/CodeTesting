package kakao.api.money.app;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.regex.Pattern;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.jupiter.api.Order;
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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;

import kakao.api.money.app.config.TestRedisConfiguration;
import kakao.api.money.app.dao.SprinkleDao;
import kakao.api.money.app.dto.ReceiveReqDto;
import kakao.api.money.app.service.ReceiveService;
import kakao.api.money.app.service.SprinkleService;
import kakao.api.money.app.vo.SprinkleInfoVo;
import reactor.core.publisher.Mono;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestRedisConfiguration.class)
@AutoConfigureWebTestClient
class MoneyReceiveApplicationTest {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
    WebTestClient webTestClient;
    @Autowired
    RedisTemplate<String, Object> redisTemplate;
    @Autowired
    SprinkleService sprinkleService;
    @Autowired
    ReceiveService receiveService;
    @Autowired
    SprinkleDao sprinkleDao;

	private static ObjectMapper objectMapper = new ObjectMapper();
	private final String CALL_URI = "/v1/money/receive";
	private String receiveToken;
	
	public MoneyReceiveApplicationTest() {}
	
	@BeforeClass
	public void setupOnce() throws Exception {
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
	@Before
	public void setup() throws Exception {
		// 매번 초기실행
	}
	
	@Test
	@Order(1)
	void 받기_기능_정상_요청_테스트() throws Exception {
		
		/************************************************************
		 * <given>
		 * 유저 'U002'이 금액 뿌리기를 정상적으로 수행하였다.
		 * 금액은 10000원을 5사람에게 분배하는 것으로 하였다.
		 ************************************************************/
		String sprinkleUser 	= "U001";
		String receiveUser 		= "U002";
		String sprinkleRoomId 	= "100";
		String token = sprinkleService.doSprinkle(sprinkleRoomId, sprinkleUser, 10000, 5);
		
		/************************************************************
		 * <when>
		 * 1. 뿌리기 시 발급된 token을 요청값으로 받습니다.
		 ************************************************************/
		ReceiveReqDto receiveReqDto = new ReceiveReqDto();
		
		/** when Requirements No.1 */
		receiveReqDto.setToken(token);
		String param = objectMapper.writeValueAsString(receiveReqDto);
		
        webTestClient
        	.post()
	        	.uri(CALL_URI)
	        	.accept(MediaType.APPLICATION_JSON)
	        	.header("X-USER-ID", receiveUser)
	        	.header("X-ROOM-ID", sprinkleRoomId)
	        	.body(Mono.just(receiveReqDto), ReceiveReqDto.class)
	        .exchange()
	        	/************************************************************
	    		 * <then>
	    		 * 1. token에 해당하는 뿌리기 건 중 아직 누구에게도 할당되지 않은 분배건 하나를
				 *	  API를 호출한 사용자에게 할당하고, 그 금액을 응답값으로 내려줍니다.
	    		 ************************************************************/
	        .expectStatus().isOk()
	        .expectBody()
		        .consumeWith(response -> {
		        	DocumentContext json = JsonPath.parse(new String(response.getResponseBody()));
		        	logger.info("[받기_기능_정상_요청_테스트] headers {} ", response.getRequestHeaders());
		        	logger.info("[받기_기능_정상_요청_테스트] json {}", json.jsonString());
		        	
		        	/** then Requirements No.1 */
		        	assertThat(json.read("$.code"		,String.class)).isEqualTo("200");
		        	assertThat(json.read("$.message"	,String.class)).isNotEmpty();
		        	/** then Requirements No.2 */
		        	assertThat(json.read("$.data.amount",String.class)).matches(Pattern.compile("[0-9]+"));
		        });
	        ;
	}
	
	
	@Test
	@Order(2)
	void 받기_기능_예외_요청값_누락_테스트() throws Exception {

		/************************************************************
		 * <given>
		 * Nothing
		 ************************************************************/
		String sprinkleUser 	= "U001";
		String sprinkleRoomId 	= "100";
		
		/************************************************************
		 * <when>
		 * 1. 요청 데이터 중 '토큰값' 필드가 누락
		 ************************************************************/
		ReceiveReqDto receiveReqDto = new ReceiveReqDto();
		
		/** when Requirements No.1 */
//		searchReqDto.setToken(token);
		String param = objectMapper.writeValueAsString(receiveReqDto);
		
        webTestClient
        	.post()
	        	.uri(CALL_URI)
	        	.accept(MediaType.APPLICATION_JSON)
	        	.header("X-USER-ID", sprinkleUser)
	        	.header("X-ROOM-ID", sprinkleRoomId)
	        	.body(Mono.just(receiveReqDto), ReceiveReqDto.class)
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
		        	logger.info("[받기_기능_예외_요청값_누락_테스트] headers {} ", response.getRequestHeaders());
		        	logger.info("[받기_기능_예외_요청값_누락_테스트] json {}", json.jsonString());
		        	
		        	/** then Requirements No.1 */
		        	assertThat(json.read("$.code"				,String.class)).isEqualTo("400");
		        	assertThat(json.read("$.message"			,String.class)).isNotEmpty();
		        	/** then Requirements No.2 */
		        	assertThat(json.read("$.errors[0].field"	,String.class)).isEqualTo("token");
		        	assertThat(json.read("$.errors[0].errorCode",String.class)).isEqualTo("common.E002");
		        	assertThat(json.read("$.errors[0].errorMsg"	,String.class)).isNotEmpty();
		        });
	        ;
	}
	
	
	@Test
	@Order(3)
	void 받기_기능_예외_한번만받기_테스트() throws Exception {

		/************************************************************
		 * <given>
		 * 이전에 'U002' 유저는 한번 동일한 요청 token으로 금액을 수령하였다.
		 ************************************************************/
		String sprinkleUser 	= "U001";
		String receiveUser 		= "U002";
		String sprinkleRoomId 	= "100";
		String token = sprinkleService.doSprinkle(sprinkleRoomId, sprinkleUser, 10000, 5);
		int amount 	 = receiveService.doReceiveMoney(sprinkleRoomId, receiveUser, token);

		/************************************************************
		 * <when>
		 * 1. 한번 받았던 token 값으로 다시 금액 수령 시도함
		 ************************************************************/
		ReceiveReqDto receiveReqDto = new ReceiveReqDto();

		/** when Requirements No.1 */
		receiveReqDto.setToken(token);
		String param = objectMapper.writeValueAsString(receiveReqDto);

        webTestClient
        	.post()
	        	.uri(CALL_URI)
	        	.accept(MediaType.APPLICATION_JSON)
	        	.header("X-USER-ID", "U002")
	        	.header("X-ROOM-ID", "100")
	        	.body(Mono.just(receiveReqDto), ReceiveReqDto.class)
	        .exchange()
	        	/************************************************************
	    		 * <then>
	    		 * 1. 200 OK 응답
	    		 * 2. errors 필드에 응답 사유(필드,코드,메세지) 출력
	    		 ************************************************************/
	        .expectStatus().isOk()
	        .expectBody()
		        .consumeWith(response -> {
		        	DocumentContext json = JsonPath.parse(new String(response.getResponseBody()));
		        	logger.info("[받기_기능_예외_한번만받기_테스트] headers {} ", response.getRequestHeaders());
		        	logger.info("[받기_기능_예외_한번만받기_테스트] json {}", json.jsonString());
		        	
		        	/** then Requirements No.1 */
		        	assertThat(json.read("$.code"		,String.class)).isEqualTo("200");
		        	assertThat(json.read("$.message"	,String.class)).isNotEmpty();
		        	/** then Requirements No.2 */
		        	assertThat(json.read("$.errors[0].errorCode",String.class)).isEqualTo("receive.E003");
		        	assertThat(json.read("$.errors[0].errorMsg"	,String.class)).isNotEmpty();
		        });
	        ;
	}
	
	@Test
	@Order(4)
	void 받기_기능_예외_뿌리기_당사자_제외_테스트() throws Exception {

		/************************************************************
		 * <given>
		 * 'U001' 사용자는 '100' 채팅방에 금액 뿌리기를 하였다.
		 ************************************************************/
		String sprinkleUser 	= "U001";
		String sprinkleRoomId 	= "100";
		String token = sprinkleService.doSprinkle(sprinkleRoomId, sprinkleUser, 10000, 5);

		/************************************************************
		 * <when>
		 * 1. 뿌리기 당사자인 'U001' 사용자가 금액 수령 요청을 하였다.
		 ************************************************************/
		ReceiveReqDto receiveReqDto = new ReceiveReqDto();

		/** when Requirements No.1 */
		receiveReqDto.setToken(token);
		String param = objectMapper.writeValueAsString(receiveReqDto);

        webTestClient
        	.post()
	        	.uri(CALL_URI)
	        	.accept(MediaType.APPLICATION_JSON)
	        	.header("X-USER-ID", sprinkleUser)
	        	.header("X-ROOM-ID", sprinkleRoomId)
	        	.body(Mono.just(receiveReqDto), ReceiveReqDto.class)
	        .exchange()
	        	/************************************************************
	    		 * <then>
	    		 * 1. 200 OK 응답
	    		 * 2. errors 필드에 응답 사유(필드,코드,메세지) 출력
	    		 ************************************************************/
	        .expectStatus().isOk()
	        .expectBody()
		        .consumeWith(response -> {
		        	DocumentContext json = JsonPath.parse(new String(response.getResponseBody()));
		        	logger.info("[받기_기능_예외_한번만받기_테스트] headers {} ", response.getRequestHeaders());
		        	logger.info("[받기_기능_예외_한번만받기_테스트] json {}", json.jsonString());
		        	
		        	/** then Requirements No.1 */
		        	assertThat(json.read("$.code"		,String.class)).isEqualTo("200");
		        	assertThat(json.read("$.message"	,String.class)).isNotEmpty();
		        	/** then Requirements No.2 */
		        	assertThat(json.read("$.errors[0].errorCode",String.class)).isEqualTo("receive.E002");
		        	assertThat(json.read("$.errors[0].errorMsg"	,String.class)).isNotEmpty();
		        });
	        ;
	}
	
	
	@Test
	@Order(5)
	void 받기_기능_예외_뿌리기_호출된_동일한_방에서만_수령가능_테스트() throws Exception {

		/************************************************************
		 * <given>
		 * 특정인이 금액 뿌리기를 정상적으로 수행하였다.
		 * 금액은 10000원을 5사람에게 분배하는 것으로 하였다.
		 ************************************************************/
		String sprinkleUser 	= "U001";
		String sprinkleRoomId 	= "100";
		String wrongRoomId 		= "200";
		String token = sprinkleService.doSprinkle(sprinkleRoomId, sprinkleUser, 10000, 5);
		
		/************************************************************
		 * <when>
		 * 1. 뿌리기 시 발급된 token을 요청값으로 받습니다.
		 ************************************************************/
		ReceiveReqDto receiveReqDto = new ReceiveReqDto();

		/** when Requirements No.1 */
		receiveReqDto.setToken(token);
		String param = objectMapper.writeValueAsString(receiveReqDto);
		
        webTestClient
        	.post()
	        	.uri(CALL_URI)
	        	.accept(MediaType.APPLICATION_JSON)
	        	.header("X-USER-ID", sprinkleUser)
	        	.header("X-ROOM-ID", wrongRoomId)
	        	.body(Mono.just(receiveReqDto), ReceiveReqDto.class)
	        .exchange()
		        /************************************************************
	    		 * <then>
	    		 * 1. 200 OK 응답
	    		 * 2. errors 필드에 응답 사유(필드,코드,메세지) 출력
	    		 *    잘못된 방으로 요청시 뿌리기 정보가 없다는 응답 수신
	    		 ************************************************************/
	        .expectStatus().isOk()
	        .expectBody()
		        .consumeWith(response -> {
		        	DocumentContext json = JsonPath.parse(new String(response.getResponseBody()));
		        	logger.info("[받기_기능_예외_뿌리기_호출된_동일한_방에서만_수령가능_테스트] headers {} ", response.getRequestHeaders());
		        	logger.info("[받기_기능_예외_뿌리기_호출된_동일한_방에서만_수령가능_테스트] json {}", json.jsonString());
		        	
		        	/** then Requirements No.1 */
		        	assertThat(json.read("$.code"		,String.class)).isEqualTo("200");
		        	assertThat(json.read("$.message"	,String.class)).isNotEmpty();
		        	/** then Requirements No.2 */
		        	assertThat(json.read("$.errors[0].errorCode",String.class)).isEqualTo("receive.E001");
		        	assertThat(json.read("$.errors[0].errorMsg"	,String.class)).isNotEmpty();
		        });
	        ;
	}
	
	@Test
	@Order(6)
	void 받기_기능_예외_10분간유효_테스트() throws Exception {

		/************************************************************
		 * <given>
		 * 특정인이 금액 뿌리기를 정상적으로 수행하였다.
		 * 금액은 10000원을 5사람에게 분배하는 것으로 하였다.
		 * 
		 * 1. 뿌리기 후 강제로 30분전 시간으로 변경
		 ************************************************************/
		String sprinkleUser 	= "U001";
		String receiveUser 		= "U002";
		String sprinkleRoomId 	= "100";
		
		
		String token = sprinkleService.doSprinkle(sprinkleRoomId, sprinkleUser, 10000, 5);
		SprinkleInfoVo sprinkleInfoVo = sprinkleDao.getSplinkleInfo(sprinkleRoomId, token);
		/** when Requirements No.1 */
		sprinkleInfoVo.setSprinkleTime(sprinkleInfoVo.getSprinkleTime()-1000*60*30);
		String sprinkleJson = new ObjectMapper().writeValueAsString(sprinkleInfoVo);
		redisTemplate.opsForValue().set(SprinkleInfoVo.getSplinkleInfoKey(sprinkleRoomId, token), sprinkleJson);
		
		/************************************************************
		 * <when>
		 * 1. 뿌리기 시 발급된 token을 요청값으로 받습니다.
		 ************************************************************/
		ReceiveReqDto receiveReqDto = new ReceiveReqDto();
		
		/** when Requirements No.1 */
		receiveReqDto.setToken(token);
		String param = objectMapper.writeValueAsString(receiveReqDto);
		
        webTestClient
        	.post()
	        	.uri(CALL_URI)
	        	.accept(MediaType.APPLICATION_JSON)
	        	.header("X-USER-ID", receiveUser)
	        	.header("X-ROOM-ID", sprinkleRoomId)
	        	.body(Mono.just(receiveReqDto), ReceiveReqDto.class)
	        .exchange()
	        /************************************************************
    		 * <then>
    		 * 1. 200 OK 응답
    		 * 2. errors 필드에 응답 사유(필드,코드,메세지) 출력
    		 *    잘못된 방으로 요청시 뿌리기 정보가 없다는 응답 수신
    		 ************************************************************/
	        .expectStatus().isOk()
	        .expectBody()
		        .consumeWith(response -> {
		        	DocumentContext json = JsonPath.parse(new String(response.getResponseBody()));
		        	logger.info("[받기_기능_예외_10분간유효_테스트] headers {} ", response.getRequestHeaders());
		        	logger.info("[받기_기능_예외_10분간유효_테스트] json {}", json.jsonString());
		        	
		        	/** then Requirements No.1 */
		        	assertThat(json.read("$.code"				,String.class)).isEqualTo("200");
		        	assertThat(json.read("$.message"			,String.class)).isNotEmpty();
		        	/** then Requirements No.2 */
		        	assertThat(json.read("$.errors[0].errorCode",String.class)).isEqualTo("receive.E004");
		        	assertThat(json.read("$.errors[0].errorMsg"	,String.class)).isNotEmpty();
		        });
	        ;
	}

}
