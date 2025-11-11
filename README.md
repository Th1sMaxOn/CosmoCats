# Cosmo Cats — Intergalactic Marketplace  
**Лабораторна робота 1.2 — Unit Tests + CI Pipeline + Code Coverage**

---

## Опис проєкту
Cosmo Cats — це навчальний веб-проєкт, реалізований на **Java + Spring Boot**, що імітує роботу інтергалактичного ринку.  
Тут можна створювати, оновлювати, переглядати та видаляти космічні товари.

---

## Мета лабораторної роботи

написати unit-тести для сервісів та контролерів  
налаштувати перевірку покриття коду (JaCoCo)  
додати GitHub Actions pipeline, який запускає тести при кожному push/PR  
забезпечити **coverage ≥ 50%**

---

##  Що було зроблено

| Завдання | Статус |
|----------|--------|
| Написано unit-тести для controller + mapper + service
| Покриття коду за допомогою **JaCoCo**
| Покриття коду ≥ 50% (факт: **57%**)
| Інтегровано GitHub Actions CI (mvn clean test)
| Додано upload артефакту звіту Jacoco
| Створено Pull Request `lab1.2 → main`

---

##  Покриття тестами

 **Code Coverage (Jacoco):** `57%`  
Повний HTML-звіт доступний у GitHub Actions → Artifacts → `jacoco-report`.

Приклад (звіт згенерований автоматично):

