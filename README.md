# E-Commerce 프로젝트

**좋아요** 누르고, **쿠폰** 쓰고, 포인트로 **결제**하는 **이커머스** 프로젝트 입니다.

## Getting Started
현재 프로젝트 안정성 및 유지보수성 등을 위해 아래와 같은 장치를 운용하고 있습니다. 이에 아래 명령어를 통해 프로젝트의 기반을 설치해주세요.
### Environment
`local` 프로필로 동작할 수 있도록, 필요 인프라를 `docker-compose` 로 제공합니다.
```shell
docker-compose -f ./docker/infra-compose.yml up
```
### Monitoring
`local` 환경에서 모니터링을 할 수 있도록, `docker-compose` 를 통해 `prometheus` 와 `grafana` 를 제공합니다.

애플리케이션 실행 이후, **http://localhost:3000** 로 접속해, admin/admin 계정으로 로그인하여 확인하실 수 있습니다.
```shell
docker-compose -f ./docker/monitoring-compose.yml up
```

### About Multi-Module Project
본 프로젝트는 멀티 모듈 프로젝트로 구성되어 있습니다. 각 모듈의 위계 및 역할을 분명히 하고, 아래와 같은 규칙을 적용합니다.

apps : 각 모듈은 실행가능한 SpringBootApplication 을 의미합니다.
modules : 특정 구현이나 도메인에 의존적이지 않고, reusable 한 configuration 을 원칙으로 합니다.
supports : logging, monitoring 과 같이 부가적인 기능을 지원하는 add-on 모듈입니다.
Root
├── apps ( spring-applications )
│   └── 📦 commerce-api
│   └── 📦 commerce-collector
│   └── 📦 pg-simulator
├── modules ( reusable-configurations )
│   ├── 📦 jpa
│   ├── 📦 kafka
│   └── 📦 redis
└── supports ( add-ons )
├── 📦 jackson
├── 📦 logging
└── 📦 monitoring

### 개발환경
- **Language**: Java 21
- **Framework**: Spring Boot 3.4.4
- **DB ORM**: JPA
- **Test**: JUnit 5 + AssertJ

### 설계 문서
- [요구사항 명세서](./docs/design/01-requirements.md)
- [시퀀스 다이어그램](./docs/design/02-sequence-diagrams.md)
- [클래스 다이어그램](./docs/design/03-class-diagrams.md)
- [ERD](./docs/design/04-erd.md)
