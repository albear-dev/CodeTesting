package kakao.api.money.app;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;

import kakao.api.money.app.config.TestRedisConfiguration;
import kakao.api.money.app.dao.SprinkleDao;
import kakao.api.money.app.dto.SearchReqDto;
import kakao.api.money.app.service.ReceiveService;
import kakao.api.money.app.service.SprinkleService;
import kakao.api.money.app.vo.SprinkleInfoVo;
import reactor.core.publisher.Mono;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestRedisConfiguration.class)
@AutoConfigureWebTestClient
class MoneySearchApplicationTest {
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
	private final String CALL_URI = "/v1/money/search";
	private String receiveToken;

	public MoneySearchApplicationTest() {}
	
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
	void 조회_기능_정상_요청_테스트() throws Exception {
		
		/************************************************************
		 * <given>
		 * 'U001' 사용자는 금액 뿌리기를 정상적으로 수행하였다.
		 * 금액은 10000원을 5사람에게 분배하는 것으로 하였다.
		 * 사용자 'U002' 가 금액을 수령하였다.
		 * 사용자 'U003' 가 금액을 수령하였다.
		 ************************************************************/
		String sprinkleUser 	= "U001";
		String receiveUser1 	= "U002";
		String receiveUser2 	= "U003";
		String sprinkleRoomId 	= "100";
		String token = sprinkleService.doSprinkle(sprinkleRoomId, sprinkleUser, 10000, 5);
		receiveService.doReceiveMoney(sprinkleRoomId, receiveUser1, token);
		receiveService.doReceiveMoney(sprinkleRoomId, receiveUser2, token);
		
		/************************************************************
		 * <when>
		 * 1. 뿌리기 확인 토큰으로 뿌린 당사자 'U001' 이 수령 내역을 조회할때
		 ************************************************************/
		SearchReqDto searchReqDto = new SearchReqDto();
		
		/** when Requirements No.1 */
		searchReqDto.setToken(token);
		
        webTestClient
        	.post()
	        	.uri(CALL_URI)
	        	.accept(MediaType.APPLICATION_JSON)
	        	.header("X-USER-ID", sprinkleUser)
	        	.header("X-ROOM-ID", sprinkleRoomId)
	        	.body(Mono.just(searchReqDto), SearchReqDto.class)
	        .exchange()
	        
	        	/************************************************************
	    		 * <then>
	    		 * 1. 200 OK 응답
	    		 * 2. 수령 정보 2건이 확인 가능하다.
	    		 *    각 건은 아래 내용이 포함되어 있다.
	    		 *    뿌린 시각, 뿌린 금액, 받기 완료된 금액, 받기 완료된 정보 ([받은 금액, 받은사용자 아이디] 리스트)
	    		 ************************************************************/
	        .expectStatus().isOk()
	        .expectBody()
		        .consumeWith(response -> {
		        	DocumentContext json = JsonPath.parse(new String(response.getResponseBody()));
		        	logger.info("[조회_기능_정상_요청_테스트] headers {} ", response.getRequestHeaders());
		        	logger.info("[조회_기능_정상_요청_테스트] json {}", json.jsonString());
		        	
		        	/** then Requirements No.1 */
		        	assertThat(json.read("$.code"		,String.class)).isEqualTo("200");
		        	assertThat(json.read("$.message"	,String.class)).isNotEmpty();
		        	/** then Requirements No.2 */
		        	assertThat(json.read("$.data.sprinkleTime",String.class)).isNotEmpty();
		        	assertThat(json.read("$.data.sprinkleAmount",String.class)).matches(Pattern.compile("[0-9]+"));
		        	assertThat(json.read("$.data.totalReceiveAmout",String.class)).matches(Pattern.compile("[0-9]+"));
		        	assertThat(json.read("$.data.receiveInfo", ArrayList.class)).hasSize(2);
		        	assertThat(json.read("$.data.receiveInfo[0].userId", String.class)).isEqualTo("U002");
		        	assertThat(json.read("$.data.receiveInfo[0].amount", String.class)).matches(Pattern.compile("[0-9]+"));
		        	assertThat(json.read("$.data.receiveInfo[0].time", String.class)).isNotEmpty();
		        });
	        ;
	}
	
	
	
	@Test
	void 조회기능_예외_뿌린사람_외_조회시_오류_테스트() throws Exception {
		
		/************************************************************
		 * <given>
		 * 'U001' 사용자는 금액 뿌리기를 정상적으로 수행하였다.
		 * 금액은 10000원을 5사람에게 분배하는 것으로 하였다.
		 * 사용자 'U002' 가 금액을 수령하였다.
		 * 사용자 'U003' 가 금액을 수령하였다.
		 ************************************************************/
		String sprinkleUser 	= "U001";
		String receiveUser1 	= "U002";
		String receiveUser2 	= "U003";
		String sprinkleRoomId 	= "100";
		String token = sprinkleService.doSprinkle(sprinkleRoomId, sprinkleUser, 10000, 5);
		receiveService.doReceiveMoney(sprinkleRoomId, receiveUser1, token);
		receiveService.doReceiveMoney(sprinkleRoomId, receiveUser2, token);
		
		/************************************************************
		 * <when>
		 * 1. 뿌리기 확인 토큰으로 뿌린 당사자가 아닌 유저 'U002' 이 수령 내역을 조회할때
		 ************************************************************/
		SearchReqDto searchReqDto = new SearchReqDto();
		
		/** when Requirements No.1 */
		searchReqDto.setToken(token);
		
        webTestClient
        	.post()
	        	.uri(CALL_URI)
	        	.accept(MediaType.APPLICATION_JSON)
	        	.header("X-USER-ID", receiveUser1)
	        	.header("X-ROOM-ID", sprinkleRoomId)
	        	.body(Mono.just(searchReqDto), SearchReqDto.class)
	        .exchange()

	        	/************************************************************
	    		 * <then>
	    		 * 1. 200 OK 응답
	    		 * 2. 
	    		 ************************************************************/
	        .expectStatus().isOk()
	        .expectBody()
		        .consumeWith(response -> {
		        	DocumentContext json = JsonPath.parse(new String(response.getResponseBody()));
		        	logger.info("[조회기능_예외_뿌린사람_외_조회시_오류_테스트] headers {} ", response.getRequestHeaders());
		        	logger.info("[조회기능_예외_뿌린사람_외_조회시_오류_테스트] json {}", json.jsonString());

		        	/** then Requirements No.1 */
		        	assertThat(json.read("$.code"				,String.class)).isEqualTo("200");
		        	assertThat(json.read("$.message"			,String.class)).isNotEmpty();
		        	/** then Requirements No.2 */
		        	assertThat(json.read("$.errors[0].errorCode",String.class)).isEqualTo("search.E002");
		        	assertThat(json.read("$.errors[0].errorMsg"	,String.class)).isNotEmpty();

		        });
	        ;
	}
	
	
	
	
	@Test
	void 조회기능_예외_뿌린건_7일이내_조회_오류_테스트() throws Exception {
		
		/************************************************************
		 * <given>
		 * 'U001' 사용자는 금액 뿌리기를 정상적으로 수행하였다.
		 * 금액은 10000원을 5사람에게 분배하는 것으로 하였다.
		 * 사용자 'U002' 가 금액을 수령하였다.
		 * 사용자 'U003' 가 금액을 수령하였다.
		 * 
		 * 1. 뿌리기 후 강제로 10일후 시간으로 변경
		 ************************************************************/
		String sprinkleUser 	= "U001";
		String receiveUser1 	= "U002";
		String receiveUser2 	= "U003";
		String sprinkleRoomId 	= "100";
		String token = sprinkleService.doSprinkle(sprinkleRoomId, sprinkleUser, 10000, 5);
		receiveService.doReceiveMoney(sprinkleRoomId, receiveUser1, token);
		receiveService.doReceiveMoney(sprinkleRoomId, receiveUser2, token);
		
		SprinkleInfoVo sprinkleInfoVo = sprinkleDao.getSplinkleInfo(sprinkleRoomId, token);
		/** when Requirements No.1 */
		sprinkleInfoVo.setSprinkleTime(sprinkleInfoVo.getSprinkleTime()-1000*60*60*24*10);
		String sprinkleJson = new ObjectMapper().writeValueAsString(sprinkleInfoVo);
		redisTemplate.opsForValue().set(SprinkleInfoVo.getSplinkleInfoKey(sprinkleRoomId, token), sprinkleJson);
		
		/************************************************************
		 * <when>
		 * 1. 뿌리기 확인 토큰으로 뿌린 당사자가 10일 후 수령 내역을 조회할때
		 ************************************************************/
		SearchReqDto searchReqDto = new SearchReqDto();
		
		/** when Requirements No.1 */
		searchReqDto.setToken(token);
		
        webTestClient
        	.post()
	        	.uri(CALL_URI)
	        	.accept(MediaType.APPLICATION_JSON)
	        	.header("X-USER-ID", sprinkleUser)
	        	.header("X-ROOM-ID", sprinkleRoomId)
	        	.body(Mono.just(searchReqDto), SearchReqDto.class)
	        .exchange()

	        	/************************************************************
	    		 * <then>
	    		 * 1. 200 OK 응답
	    		 * 2. 
	    		 ************************************************************/
	        .expectStatus().isOk()
	        .expectBody()
		        .consumeWith(response -> {
		        	DocumentContext json = JsonPath.parse(new String(response.getResponseBody()));
		        	logger.info("[조회기능_예외_뿌린건_7일이내_조회_오류_테스트] headers {} ", response.getRequestHeaders());
		        	logger.info("[조회기능_예외_뿌린건_7일이내_조회_오류_테스트] json {}", json.jsonString());

		        	/** then Requirements No.1 */
		        	assertThat(json.read("$.code"				,String.class)).isEqualTo("200");
		        	assertThat(json.read("$.message"			,String.class)).isNotEmpty();
		        	/** then Requirements No.2 */
		        	assertThat(json.read("$.errors[0].errorCode",String.class)).isEqualTo("search.E003");
		        	assertThat(json.read("$.errors[0].errorMsg"	,String.class)).isNotEmpty();


		        });
	        ;
	}
	
	
}
