# CosmoCats — Feature Toggle + Spring AOP (Updated)
Автор: Пашко

## Опис
Проєкт демонструє feature toggles (environment) з використанням Spring AOP.
Додано:
- REST API endpoint GET /api/cats
- Global exception handler (returns 503 when feature disabled)
- CommandLineRunner demo that calls the service on startup
- GitHub Actions workflow to run tests on push/PR
- Gradle wrapper scripts/properties included (wrapper jar not included)

## Запуск локально
### Запуск тестів
```bash
./gradlew test
```
або на Windows PowerShell:
```powershell
.\gradlew.bat test
```

### Запуск додатка
```bash
./gradlew bootRun
```
Відкрий у браузері: http://localhost:8080/api/cats

### Зміна флагу
У `src/main/resources/application.yml` змінюй `feature.cosmoCats.enabled` на `false`/`true` або експортуй змінну середовища:
```bash
export FEATURE_COSMO_CATS_ENABLED=false
```
або Windows PowerShell:
```powershell
$env:FEATURE_COSMO_CATS_ENABLED = 'false'
```

## CI (GitHub Actions)
Файл CI знаходиться в `.github/workflows/ci.yml` — запускає `./gradlew test` на push/pull_request.

## Примітки
Gradle wrapper jar не включено у цей архів. Якщо хочеш — я додам повний wrapper (з jar) у наступному оновленні.

---

## Лабораторна робота 3 — Cosmo Cats Intergalactic Marketplace (БД + JPA + Liquibase + Testcontainers)

Ця версія проєкту доповнює попередню ЛР-2 (feature toggles) і додає:

- реляційну базу даних PostgreSQL у 3NF;
- міграції схеми через Liquibase (`db/changelog`);
- Spring Data JPA-репозиторії для `Product`, `Category`, `Order`, `Customer`, `OrderLine`;
- транзакційний сервісний шар з `@Transactional`;
- Natural ID для `Order` (`orderNumber`);
- Projections + JPQL-запит для найпопулярніших продуктів;
- інтеграційні тести з Docker Testcontainers + Jacoco звіт покриття.

### Як запустити

1. Створи БД та користувача PostgreSQL:

   ```sql
   CREATE DATABASE cosmocats;
   CREATE USER cosmocats WITH PASSWORD 'cosmocats';
   GRANT ALL PRIVILEGES ON DATABASE cosmocats TO cosmocats;
   ```

2. Запусти застосунок:

   ```bash
   ./gradlew clean bootRun
   ```

3. Перевір ендпоїнти у Postman / HTTP-клієнті:

   - `POST /api/categories`
   - `POST /api/products`
   - `POST /api/orders`
   - `GET  /api/orders/{orderNumber}`
   - `GET  /api/products/popular?limit=5`

4. Запусти тести з Testcontainers:

   ```bash
   ./gradlew clean test
   ```

   Звіт покриття Jacoco буде у `build/reports/jacoco/test/html/index.html`.
