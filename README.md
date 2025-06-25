# Tidy Habit

Tidy Habit is an offline gamified mobile application that helps users build sustainable household habits through daily tasks, rewards, and streaks.

---

## Features

* Local user registration (name, age)
* Create and manage household tasks
* Mark tasks as completed and track progress
* Streak tracker
* Daily reminders and streak loss warnings
* Localization support: English and Ukrainian
* Encrypted SharedPreferences for secure local data storage

---

## Motivation

Designed for young individuals to gamify their chores and improve self-organization in a fun, engaging, and offline way.

---

## Tech Stack

* **Kotlin + Jetpack Compose**
* **Room** (for offline task/user data)
* **EncryptedSharedPreferences** (AES256 encryption)
* **Hilt** for DI
* **DataStore / SharedPreferences** for persistent user state
* **BroadcastReceiver + AlarmManager** for background daily notifications

---

## Getting Started

1. Clone the repo
2. Open in Android Studio
3. Run the app on Android 8+ device or emulator

---

---

# Tidy Habit

Tidy Habit — це гейміфікований офлайн-застосунок, який допомагає формувати корисні побутові звички через щоденні завдання, рівні, streak-систему та винагороди.

---

## Основні функції

* Реєстрація користувача (ім’я, вік)
* Створення та керування побутовими завданнями
* Позначення виконаних завдань і перегляд історії
* Система рівнів
* Лічильник "стрік" за щоденну активність
* Push-сповіщення:

    * про завдання на день
    * про ризик втрати streak
* Локалізація: 🇺🇦 Українська та 🇬🇧 Англійська мови
* Захист даних через шифрування `EncryptedSharedPreferences`

---

## Для кого?

Застосунок орієнтований на молодь, студентів та всіх, хто хоче покращити організованість у побуті без складних трекерів і з мінімальними ресурсами.

---

## Технології

* Kotlin, Jetpack Compose
* Room (локальна БД)
* Jetpack Security (`EncryptedSharedPreferences`)
* Dagger Hilt (DI)
* DataStore / SharedPreferences
* BroadcastReceiver + AlarmManager (для сповіщень)

---

## Запуск

1. Клонувати репозиторій
2. Відкрити в Android Studio
3. Запустити на Android 8+ пристрої або емуляторі

---