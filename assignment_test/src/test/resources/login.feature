Feature: 로그인 로그아웃 하기
  
  Scenario: 로그인 하기
    Given 사용자가 "test@ab180.co" 이메일과 "abcd1234" 비밀번호로 로그인 시도
    When 사용자가 "/users/signin" 엔드포인트에 POST 요청을 보낸다
    Then 응답 상태는 201이어야 한다
    And 응답 데이터에 "token", "iat", "exp" 필드가 포함되어 있어야 한다
    And 로그인 이력이 저장되어야 한다
    
  Scenario: 로그아웃 하기
  	Given 로그인 이후 토큰값을 준비한다
  	When jwt토큰을 헤더에 넣고 "/users/signout" 으로 요청
  	Then 로그아웃 응답 상태는 201이어야 한다
  	And 응답 데이터에 "success" 값은 "true" 로 나온다.