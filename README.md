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
