# JDK 17 기반의 이미지 사용 (빌드 단계)
# 이 단계에서는 Gradle 빌드를 위한 환경을 설정
FROM openjdk:17-jdk-slim as build

# 작업 디렉토리 설정
# 이 디렉토리는 모든 파일을 복사하고, 빌드 명령어를 실행할 곳
WORKDIR /app

# 애플리케이션 소스 코드 복사
# 현재 디렉토리의 모든 파일을 컨테이너 내의 /app 디렉토리에 복사
COPY . /app

# Gradle Wrapper로 빌드 실행
# Gradle Wrapper(./gradlew)를 사용하여 애플리케이션을 빌드
# 이 명령어가 실행되면 프로젝트가 빌드되고, `build/libs` 폴더에 `.jar` 파일이 생성
# -x test : 빌드 시 실행되는 테스트 생략
RUN ./gradlew clean build -x test

# 최종 이미지를 위한 설정 (실행 단계)
# 이제 빌드된 애플리케이션을 실행할 수 있는 최종 이미지를 생성
FROM openjdk:17-jdk-slim

# Gradle 빌드로 생성된 .jar 파일을 최종 이미지로 복사
# 빌드 단계에서 생성된 `.jar` 파일을 최종 이미지의 `app.jar`로 복사
# `COPY --from=build`는 이전 단계에서 만든 빌드 이미지를 참조
COPY --from=build /app/build/libs/server-0.0.1-SNAPSHOT.jar app.jar

# 애플리케이션 실행 명령어 설정
# 최종 이미지를 실행할 때 사용할 명령어
# `java -jar app.jar` 명령어로 빌드된 `.jar` 파일을 실행
ENTRYPOINT ["java", "-jar", "app.jar"]
