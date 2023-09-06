# MuinMusic

AWS EC2 배포작업 
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
