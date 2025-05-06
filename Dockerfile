# OpenJDK 17 slim 이미지를 베이스로 사용 (경량화된 JDK 환경 제공)
FROM openjdk:17-jdk-slim

# JAR 파일 경로를 build 시점에 ARG로 받아옴 (기본값: build/libs/server-0.0.1-SNAPSHOT.jar)
ARG JAR_FILE=build/libs/server-0.0.1-SNAPSHOT.jar

# Spring 프로파일 이름을 build 시점에 ARG로 받아옴 (예: dev, prod 등)
ARG PROFILES

# 추가적인 환경 설정용 ARG (예: dev, staging, prod 등을 구분할 수 있음)
ARG ENV

# 위에서 지정한 JAR 파일을 컨테이너 내부에 app.jar라는 이름으로 복사
COPY ${JAR_FILE} app.jar

# 컨테이너 실행 시 Java 애플리케이션을 실행
# -Dspring.profiles.active : Spring 실행 프로파일 지정
# -Dserver.env : 커스텀 환경 설정 값 지정
ENTRYPOINT ["java", "-Dspring.profiles.active=${PROFILES}", "-Dserver.env=${ENV}", "-jar", "app.jar"]
