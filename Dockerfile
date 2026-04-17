# 보안과 이미지 크기 최적화 방식) 빌드와 실행을 분리한 Multi-stage 빌드 방식

# 1단계: Build Stage
FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /app

# Gradle 래퍼와 설정 파일들을 먼저 복사 (캐시 활용)
COPY gradlew .
RUN chmod +x gradlew  # (실행 권한 부여 - 리눅스 기반 도커 환경에서는 실행 권한을 명시, 터미널에서 파일 자체 권한 설정을 바꾸고 커밋해두됨)
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

# 의존성 먼저 다운로드 (코드 변경 시에도 의존성은 캐시됨 - 빌드 속도 향상)
RUN ./gradlew dependencies --no-daemon

# 전체 소스 복사 및 빌드 ( 테스트 제외 - 메모리 절약 )
COPY src src
RUN ./gradlew clean build -x test --no-daemon

# 2단계: Run Stage (실행 전용 경량 이미지)
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# 실행에 필요한 jar 파일만 복사
COPY --from=build /app/build/libs/*.jar app.jar

# 리소스 폴더에 있는 설정 파일들을 현재 작업 디렉토리(/app)로 복사
# file:./application-kakao.yml 설정을 그대로 쓸 수 있음
# 추후 유료 클라우드 이관 시, 해당 사이트 내 대시보드에서 환경 변수 설정하자
COPY --from=build /app/src/main/resources/application-kakao.yml ./application-kakao.yml
COPY --from=build /app/src/main/resources/application-secret.yml ./application-secret.yml

# Render용 포트 설정 (기본 10000 포트 권장)
ENV PORT 10000
EXPOSE 10000

# 서비스를 위한 JVM 메모리 및 타임존 설정
# Render 무료티어(512MB)라면 아래 설정을 추천
# 실행 명령어
# Render 서버가 켜질 때 "/app/~~secret.yml , kakao.yml 파일도 읽도록 설정 - Docker로 빌드 시 app.jar라는 덩어리만 남는데, 외부 파일(file:/app/...)을 강제로 읽게 하는 설정
ENTRYPOINT ["java", \
            "-Xmx400M", \
            "-Xms400M", \
            "-Duser.timezone=Asia/Seoul", \
            "-Dspring.config.additional-location=file:./application-secret.yml,file:./application-kakao.yml", \
            "-jar", \
            "app.jar"]

# Render 연동 시 주의사항
# 1. Health Check: 상업용 서비스는 서버가 살아있는지 주기적으로 확인해야 합니다. Render 설정의 Health Check Path를 /api/auth/login이나 별도의 /api/health로 지정
# 2. Auto-Deploy: GitHub에 main 브랜치 push 시 자동 배포되도록 설정하되, 나중에 상업용으로 정식 런칭하면 develop 브랜치에서 검증 후 main으로 머지하는 전략 필요
# 3. 포트 설정: Render는 기본적으로 80 또는 10000 포트를 기대합니다. 스프링 부트 application.properties에 server.port=10000을 명시하거나, Render 환경변수에 PORT: 10000을 추가
# 배포: Render에서 Runtime을 Docker로 선택하고 배포 버튼