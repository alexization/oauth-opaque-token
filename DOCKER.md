# Docker 환경 설정 가이드

이 문서는 Docker를 사용하여 Spring Boot 애플리케이션과 MySQL 데이터베이스를 실행하는 방법을 설명합니다.

## 📋 전제 조건

- Docker 설치 (20.10.0 이상)
- Docker Compose 설치 (v2.0.0 이상)

## 🏗️ 아키텍처

```
┌─────────────────────┐         ┌─────────────────────┐
│   Spring Boot App   │◄────────┤   MySQL Database    │
│   (Port: 8080)      │         │   (Port: 3306)      │
└─────────────────────┘         └─────────────────────┘
         │                               │
         └───────────┬───────────────────┘
                     │
              oauth-network
```

## 📦 구성 요소

### 1. Dockerfile
- **Multi-stage build**: 최적화된 이미지 크기를 위한 빌드 및 런타임 단계 분리
- **보안**: 비루트 사용자로 실행
- **성능**: JVM 컨테이너 최적화 설정
- **Health check**: 애플리케이션 상태 모니터링

### 2. docker-compose.yml
- **MySQL**: 8.0, UTF-8MB4, Asia/Seoul 타임존
- **Spring Boot**: Docker 프로파일 자동 활성화
- **네트워크**: 컨테이너 간 통신을 위한 브리지 네트워크
- **볼륨**: 데이터 영속성 보장

### 3. application.yml
- **local 프로파일**: 로컬 개발 환경 (로컬 MySQL 연결)
- **docker 프로파일**: Docker 환경 (컨테이너 간 통신)

## 🚀 실행 방법

### 1. 전체 환경 실행
```bash
# 컨테이너 빌드 및 실행 (백그라운드)
docker-compose up -d

# 로그 확인
docker-compose logs -f

# 특정 서비스 로그만 확인
docker-compose logs -f app
docker-compose logs -f mysql
```

### 2. 재빌드 실행
```bash
# 이미지 재빌드 후 실행
docker-compose up -d --build

# 특정 서비스만 재빌드
docker-compose up -d --build app
```

### 3. 환경 종료
```bash
# 컨테이너 중지 및 제거
docker-compose down

# 볼륨까지 제거 (데이터베이스 데이터 삭제)
docker-compose down -v
```

## 🔧 개발 워크플로우

### 코드 변경 후 재시작
```bash
# 1. 애플리케이션 재빌드 및 재시작
docker-compose up -d --build app

# 2. 로그 확인
docker-compose logs -f app
```

### 데이터베이스 초기화
```bash
# 1. MySQL 컨테이너 및 볼륨 제거
docker-compose down -v

# 2. 다시 실행
docker-compose up -d
```

### 컨테이너 내부 접속
```bash
# Spring Boot 컨테이너 접속
docker exec -it oauth-app sh

# MySQL 컨테이너 접속
docker exec -it oauth-mysql bash

# MySQL 클라이언트 실행
docker exec -it oauth-mysql mysql -u oauth_user -p oauth_db
# Password: oauth_password
```

## 🔍 모니터링

### 컨테이너 상태 확인
```bash
# 실행 중인 컨테이너 확인
docker-compose ps

# 컨테이너 리소스 사용량
docker stats oauth-app oauth-mysql
```

### Health Check
```bash
# Spring Boot 애플리케이션 상태
curl http://localhost:8080/actuator/health

# MySQL 상태
docker exec oauth-mysql mysqladmin -u root -proot_password ping
```

## 🌐 접속 정보

### Spring Boot 애플리케이션
- **URL**: http://localhost:8080
- **프로파일**: docker
- **로그 레벨**: INFO

### MySQL 데이터베이스
- **Host**: localhost (호스트에서 접속 시) / mysql (컨테이너 내부)
- **Port**: 3306
- **Database**: oauth_db
- **Username**: oauth_user
- **Password**: oauth_password
- **Root Password**: root_password

## 🛠️ 환경 변수 커스터마이징

docker-compose.yml 파일에서 다음 환경 변수를 수정할 수 있습니다:

```yaml
app:
  environment:
    # OAuth2 설정
    INTROSPECTION_URI: http://your-auth-server:9000/oauth2/introspect
    CLIENT_ID: your-client-id
    CLIENT_SECRET: your-client-secret

    # 데이터베이스 설정 (필요시)
    SPRING_DATASOURCE_URL: jdbc:mysql://mysql:3306/oauth_db?...
    SPRING_DATASOURCE_USERNAME: oauth_user
    SPRING_DATASOURCE_PASSWORD: oauth_password
```

## ⚠️ 주의사항

1. **프로덕션 환경**:
   - `root_password`, `oauth_password` 등의 기본 비밀번호를 반드시 변경하세요
   - 환경 변수를 `.env` 파일로 분리하여 관리하세요
   - 볼륨 백업 전략을 수립하세요

2. **포트 충돌**:
   - 로컬에서 MySQL(3306)이나 애플리케이션(8080)이 실행 중이면 충돌이 발생합니다
   - docker-compose.yml에서 포트를 변경할 수 있습니다: `"8081:8080"`

3. **데이터 영속성**:
   - `docker-compose down -v` 명령은 데이터베이스 데이터를 삭제합니다
   - 데이터를 보존하려면 `-v` 옵션을 제거하세요

## 🐛 트러블슈팅

### 애플리케이션이 MySQL에 연결하지 못할 때
```bash
# MySQL 컨테이너가 준비될 때까지 대기
docker-compose logs mysql

# "ready for connections" 메시지 확인 후 애플리케이션 재시작
docker-compose restart app
```

### 빌드 캐시 문제
```bash
# Docker 빌드 캐시 무시
docker-compose build --no-cache

# Gradle 캐시 초기화 (로컬)
./gradlew clean
```

### 포트가 이미 사용 중일 때
```bash
# 포트 사용 중인 프로세스 확인 (macOS/Linux)
lsof -i :8080
lsof -i :3306

# 프로세스 종료 또는 docker-compose.yml에서 포트 변경
```

## 📚 참고 자료

- [Docker Documentation](https://docs.docker.com/)
- [Docker Compose Documentation](https://docs.docker.com/compose/)
- [Spring Boot Docker Guide](https://spring.io/guides/gs/spring-boot-docker/)
