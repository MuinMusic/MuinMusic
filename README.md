# MuinMusic

## 프로젝트 목표

음악 콘서트 티켓 주문 서비스 백엔드 API 서버 구축 프로젝트입니다. 

- Redis를 활용하여 동시성 문제 해결
- JMeter를 이용해 Redis RedissonLock과 JPA PessimisticLock 성능 테스트
- 데이터 설계에 대한 고민
- 컨벤션, 예외 상황, 객체지향, 읽기 쉬운 코드에 대한 고민

## 사용 기술
- Java 17, Spring Framwork, Spring Boot, MYSQL, H2DB, JPA, Gradle , Redis, Docker, AWS EC2
  
## 기술적 issue 해결 과정 블로그 정리

- [주문 서비스 동시성 문제 발생 시 Redis를 활용한 해결](https://bocho-developer.tistory.com/4)

- [데이터 설계에 대한 고민](https://bocho-developer.tistory.com/5)

- [AWS EC2 배포 과정](https://bocho-developer.tistory.com/9)


## DB ERD
외래키 미사용으로 설계한 DB
![스크린샷 2023-09-09 오전 4 40 21](https://github.com/MuinMusic/MuinMusic/assets/112970256/ff1d3fbb-6fac-4891-8e06-30692975cfc8?raw=true&s=130)



## AWS EC2 배포
```shell
http://ec2-43-202-56-183.ap-northeast-2.compute.amazonaws.com:8080/
```

### 개발 환경 설정
필수 프로그램 설치
```shell
brew bundle
```

JDK17 환경변수 설정
```shell
# JDK17 경로 확인
$ /usr/libexec/java_home -V

# JDK17 환경변수 설정
echo 'export JDK17="Your_JDK17_Path"' >> ~/.bashrc
```

```shell
# docker-compose 실행
$ docker-compose up -d
```
