##  2단계 - Proxy

## TODO

1. Dynamic Proxy 연습
  - [x] `Proxy.newProxyInstance`를 활용하여 HelloHandler에 Dynamic Proxy를 구현해본다.

2. Proxy 활용
  - [ ] InvocationHandler 를 활용해 LazyLoading을 구현한다.

### 요구 사항 1
- 요구 사항 1 - Dynamic Proxy 연습
- 요구 사항 2 - Proxy 활용

### 생각
- JDK Dynamic Proxy를 써서 반드시 인터페이스가 필요함.
- ```테스트 코드를 통해서 프록시 객체를 호출 했을 때와 하지 않았을 때를 비교해보자```  
-> 이거 근데 어떻게 테스트해야하는지?   
-> 아무리 봐도 필드 직접 접근해서 프록시 우회하는거랑, 메서드로 접근해서 프록시 사용하는것으로 비교해야할듯하다.




