# test

FROM ubuntu:latest

LABEL version="0.0.1"
LABEL description="test for docker build and compose"
LABEL authors="keon"

# 필요한 패키지 업데이트 및 설치
RUN apt-get update && apt-get install -y \
    software-properties-common \
    python3 \
    g++ \
    wget \
    unzip

# Java 17 설치
RUN apt-get update && apt-get install -y openjdk-17-jdk && \
    export JAVA_HOME=$(dirname $(dirname $(readlink -f $(which java))))

# Gradle 설치
RUN wget https://services.gradle.org/distributions/gradle-8.5-bin.zip -P /tmp && \
    unzip -d /opt/gradle /tmp/gradle-*.zip && \
    ln -s /opt/gradle/gradle-8.5/bin/gradle /usr/bin/gradle

# 환경 변수 설정
ENV GRADLE_HOME /opt/gradle/gradle-8.5
ENV PATH $PATH:$GRADLE_HOME/bin


# 작업 디렉토리 설정
WORKDIR /app

# 애플리케이션 소스 파일 복사
COPY . .

# 스프링 부트 애플리케이션 빌드
RUN gradle build

# 빌드된 JAR 파일 실행
CMD ["java", "-jar", "build/libs/runserver-0.0.1-SNAPSHOT.jar"]