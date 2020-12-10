# 카카오페이 머니 뿌리기

## 목차
- [개발 환경 및 주요 라이브러리](#개발 환경 및 주요 라이브러리)
- [빌드 및 실행](#빌드 및 실행)
- [통신 프로세스 개요](#통신 프로세스 개요)
	- [API 목록](#API 목록)
		- [주요 기능](#주요 기능)
	- [API 명세](#API 명세)
		- [공통 요청 Header](#공통 요청 Header)
		- [1.1 뿌리기 필드](#1.1 뿌리기 필드)
			- [1.2 뿌리기 요청 예시](#1.2 뿌리기 요청 예시)
			- [1.3 뿌리기 응답-정상 예시](#1.3 뿌리기 응답-정상 예시)
			- [1.4 뿌리기 응답-오류 예시](#1.4 뿌리기 응답-오류 예시)
		- [2.1 받기 필드](#2.1 받기 필드)
			- [2.2 받기 요청 예시](#2.2 받기 요청 예시)
			- [2.3 받기 응답-정상 예시](#2.3 받기 응답-정상 예시)
			- [2.4 받기 응답-오류 예시](#2.4 받기 응답-오류 예시)
		- [3.2 조회 요청 예시](#3.2 조회 요청 예시)
			- [3.3 조회 응답-정상 예시](#3.3 조회 응답-정상 예시)
			- [3.4 조회 응답-오류 예시](#3.4 조회 응답-오류 예시)

## 개발 환경 및 주요 라이브러리
- Eclipse spring tool suite
- Java 8
- Spring Boot 2.3.5
- Embeded Redis
- Maven
- Junit 5

## 빌드 및 실행
```
$ git clone https://github.com/creative-albear/sprinkle_money_api.git
$ cd sprinkle_money_api
$ mvn clean package
$ java -jar target/money_sprinkle-1.0.0-RELEASE-boot.jar
```


## 통신 프로세스 개요
- Protocol : http / json
- Method : POST
- URL : http://127.0.0.1:8080/{서비스_URI}

### API 목록

|API명           |서비스 URI  |
| ------------ | ------------ |
|뿌리기         |/v1/money/sprinkle| 
|받기            |/v1/money/receive| 
|조회            |/v1/money/search| 
#### 주요 기능
- 뿌리기
	- 대화방에 뿌릴 금액 , 인원을 설정하여 요청하면 인원에 맞게 수령 가능한 금액을 고루 나눈다
	- 분배 로직은 인원수대로 공평하게 분배하며 나머지 끝전은 랜덤하게 한명에게 몰아준다.
	- 뿌리기 수행 후 랜덤한 3자리 문자열을 응답으로 받는다.
- 받기
	- 뿌리기 시 발급된 token을 요청하면 인원수에 맞게 분배된 금액을 받는다.
	- 뿌리기 한번에 사용자는 한번만 받을수 있다.
	- 자신이 뿌리기 한건은 자신이 받을수 없다.
	- 뿌리기를 수행했던 대화방에 속한 사용자만 받을수 있다.
	- 뿌린후 10분이 지나면 받을 수 없다.
- 조회
	- 뿌리기시 발급된 token을 이용하여 조회한다.
	- 뿌리기 한 사용자만 조회 가능하다.
	- 뿌린 시각, 뿌린 금액, 받기 완료된 금액, 받기 완료된 정보 ([받은 금액, 받은 사용자 아이디] 리스트) 를 결과로 받을수 있다.
	- 뿌린건에 대한 조회는 7일동안 할 수 있다.


### API 명세
#### 공통 요청 Header
|Key                  |Value                  |
| --------------- | ----------------- |
|Content-Type  |application/json|
|X-USER-ID       |{유저ID}                 |
|X-ROOM-ID     |{대화방ID}             |

#### 응답 코드 및 메세지
- common.E001 = 업무 처리중 오류가 발생하였습니다.
- common.E002 = 입력값이 잘못되었습니다.
- sprinkle.E001 = 뿌리기 요청자는 해당 채팅방에 참여하고 있지 않습니다.
- sprinkle.E002 = 분배금액보다 배분 인원이 많습니다.
- receive.E001 = 뿌리기 정보가 존재하지 않습니다.
- receive.E002 = 뿌리기 당자사는 수령이 불가능합니다.
- receive.E003 = 이미 금액을 수령하였습니다.
- receive.E004 = 수령 가능 시간이 지났습니다. (뿌린후 10분내 수령 가능)
- search.E001 = 뿌리기 정보가 없습니다.
- search.E002 = 조회 요청은 뿌리기 당사자만 가능합니다.
- search.E003 = 조회 가능 시간이 지났습니다. (뿌린 후 7일 이내 가능)

#### 1.1 뿌리기 필드
##### Request
|Field                  |Type                       |Description
| ---------------      | ----------------- |
|sprinkleAmount  |Number               |뿌릴 금액
|sprinkleCount      |Number              |뿌릴 인원 수

##### Response
|Field                  |Type                       |Description
| ---------------   | ----------------- |
|code                  |String               |응답코드(status code)
|message            |String              |응답메세지
|data                   |Map              |응답 데이터 Map

###### data 필드 하위
|Field                  |Type                       |Description
| ---------------   | ----------------- |
|token                 |String              |뿌리기 요청 토큰 값

#### 1.2 뿌리기 요청 예시
Http Header
```
Content-Type : application/json
X-USER-ID : U002
X-ROOM-ID : 100
```
Http Body
```
{
	"sprinkleAmount": 10000,
	"sprinkleCount": 6
}
```
#### 1.3 뿌리기 응답-정상 예시
Http Status 200
```
{
    "code": "200",
    "message": "성공",
    "data": {
        "token": "Z84"
    }
}
```
#### 1.4 뿌리기 응답-오류 예시
Http Status 400
```
{
    "code": "400",
    "message": "잘못된 요청입니다",
    "errors": [
        {
            "objectName": "sprinkleReqDto",
            "field": "sprinkleCount",
            "errorCode": "common.E002",
            "errorMsg": "[Min] 뿌릴 인원이 비어 있습니다!"
        }
    ]
}
```

#### 2.1 받기 필드
##### Request
|Field                  |Type                       |Description
| ---------------      | ----------------- |
|token                    |String               |뿌리기 요청 토큰값

##### Response
|Field                  |Type                       |Description
| ---------------   | ----------------- |
|code                  |String               |응답코드(status code)
|message            |String              |응답메세지
|data                   |Map              |응답 데이터 Map

###### data 필드 하위
|Field                  |Type                       |Description
| ---------------   | ----------------- |
|amount              |Number              |뿌리기 요청 토큰 값

#### 2.2 받기 요청 예시
Http Header
```
Content-Type : application/json
X-USER-ID : U002
X-ROOM-ID : 100
```
Http Body
```
{
	"token" : "XT0"
}
```
#### 2.3 받기 응답-정상 예시
Http Status 200
```
{
    "code": "200",
    "message": "성공",
    "data": {
        "amount": 1666
    }
}
```
#### 2.4 받기 응답-오류 예시
Http Status 400
```
{
    "code": "400",
    "message": "잘못된 요청입니다",
    "errors": [
        {
            "objectName": "receiveReqDto",
            "field": "token",
            "errorCode": "common.E002",
            "errorMsg": "[NotEmpty] 토큰값이 비어 있습니다!"
        }
    ]
}
```


#### 3.1 조회 필드
##### Request
|Field                  |Type                       |Description
| ---------------      | ----------------- |
|token                    |String               |뿌리기 요청 토큰값

##### Response
|Field                  |Type                       |Description
| ---------------   | ----------------- |
|code                  |String               |응답코드(status code)
|message            |String              |응답메세지
|data                   |Map              |응답 데이터 Map

###### data 필드 하위
|Field                          |Type                   |Description
| ---------------           | ----------------- |
|sprinkleTime             |String                 |뿌리기 요청 시간
|sprinkleAmount        |Number            |뿌리기 금액
|totalReceiveAmout   |Number             |수령 총 금액
|receiveInfo                |List                    |수령 정보

###### receiveInfo 필드 하위
|Field                          |Type                   |Description
| ---------------      | ----------------- |
|userId                   |String                 |유저 ID
|amount                |Number            |수령 금액
|time                     |String                |수령 시간

#### 3.2 조회 요청 예시
Http Header
```
Content-Type : application/json
X-USER-ID : U002
X-ROOM-ID : 100
```
Http Body
```
{
	"token" :"49T"
}
```
#### 3.3 조회 응답-정상 예시
Http Status 200
```
{
    "code": "200",
    "message": "성공",
    "data": {
        "sprinkleTime": "2020-48-03 12:48:58",
        "sprinkleAmount": 10000,
        "totalReceiveAmout": 1666,
        "receiveInfo": [
            {
                "userId": "U002",
                "amount": 1666,
                "time": 1604332142426
            }
        ]
    }
}
```
#### 3.4 조회 응답-오류 예시
Http Status 400
```
{
    "code": "200",
    "message": "업무 처리중 오류가 발생하였습니다.",
    "errors": [
        {
            "errorCode": "search.E001",
            "errorMsg": "뿌리기 정보가 없습니다."
        }
    ]
}
```

## Database 설계사항
- 현 예제에서 사용된 Redis는 Key-Value 방식의 DB로써 가능한 Key 조합에 대한 설계 진행

|키설명                |데이터 유형      |Key값
| ------------       | ------------      |
|대화방 리스트     |setOperations |ChatRoomList
|대화방 유저 목록 |setOperations|ChatUserList:{대화방ID}
|배분금액 리스트  |listOperations|DistributedList:{대화방ID}:{뿌리기token}
|배분정보             |valueOperations|SprinkleInfo:{대화방ID}:{뿌리기token}
|수령정보             |hashOperations|ReceiveInfo:{대화방ID}:{뿌리기token}

## Testing 정보
- Junit5를 사용하여 spring-webflux 환경의 REST API 테스팅을 수행할 수 있도록 함
- 최초 실행시 동작에 필요한 대화방 목록/대화방 참여 유저 정보를 미리 생성함
- maven 빌드시 자동으로 테스팅 수행

|테스트명 |패키지 위치|
| ------------ | ------------ 
|뿌리기 기능 테스트|kakao.api.money.app.MoneySprinkleApplicationTest
|받기 기능 테스트|kakao.api.money.app.MoneyReceiveApplicationTest
|조회 기능 테스트|kakao.api.money.app.MoneySearchApplicationTest