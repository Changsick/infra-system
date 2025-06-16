Feature: 회원가입 하기
  
  Scenario: 회원가입 성공
    Given 이메일 "test@ab180.co", 비밀번호 "abcd1234", 사용자명 "testuser"인 회원이 있다
    When 회원이 "/users/signup"에 POST 요청을 보낸다
    Then 응답 상태는 201 Created여야 한다
    And 리턴된 회원 정보는 이메일 "test@ab180.co", 사용자명 "testuser"을 포함해야 한다
    And 회원 정보가 데이터베이스에 저장되어야 한다
    
  Scenario: 회원가입 실패
  	Given 가입되어있는 이메일 "test@ab180.co", 비밀번호 "abcd1234", 사용자명 "testuser" 인 회원이 있다
  	When 회원이 "/users/signup"에 POST 실패할 요청을 보낸다
  	Then 응답 상태는 400 Bad Request여야 한다