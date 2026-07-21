# 🎬 Reely
<img width="879" height="340" alt="ReelyBanner" src="https://github.com/user-attachments/assets/05917a97-2223-4874-9b81-f8b50ecab071" />
<p align="center">
  <b>Nowoczesna aplikacja na Androida dla dwóch osób, które wspólnie oglądają filmy.</b>
  <br>
  Twórzcie wspólną watchlistę, oceniajcie filmy niezależnie, odkrywajcie nowe produkcje i analizujcie szczegółowe statystyki. Wszystko w jednym miejscu.
</p>

<p align="center">
    <img src="https://img.shields.io/badge/Android-API%2026+-3DDC84?logo=android" />
    <img src="https://img.shields.io/badge/Kotlin-2.x-7F52FF?logo=kotlin" />
    <img src="https://img.shields.io/badge/Jetpack%20Compose-Material%203-4285F4" />
    <img src="https://img.shields.io/badge/Architecture-Clean%20Architecture-orange" />
</p>

---

## ✨ Funkcje

* 👥 Dwa profile użytkowników
* 🎞️ Wspólna watchlista
* ⭐ Niezależne oceny filmów
* 📚 Historia obejrzanych filmów
* 🎲 Losowanie filmu z watchlisty
* 📊 Rozbudowane statystyki
* 🔍 Wyszukiwanie filmów z wykorzystaniem TMDB

---

# 📸 Zrzuty ekranu

<p align="center">
  <img src="https://github.com/user-attachments/assets/a3661e69-79a1-4f8a-89bb-cd637272ddda" width="31%" />
  <img src="https://github.com/user-attachments/assets/78e2fd7a-c16b-438b-b75d-df3d75b436fd" width="31%" />
  <img src="https://github.com/user-attachments/assets/6c963774-2d78-40fd-b5a0-1c315194de88"  width="31%"/>
</p>

<p align="center">
<b>Ekran główny</b>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
<b>Wyszukiwnaie</b>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
<b>Lista</b>
</p>

<br>

<p align="center">
  <img src="https://github.com/user-attachments/assets/0aa975b0-0ba1-4535-8fd9-cfb168ff74e3" width="31%" />
  <img src="https://github.com/user-attachments/assets/f92683df-49a6-4af4-b367-b847f6b74830" width="31%" />
  <img src="https://github.com/user-attachments/assets/d712356c-d6cc-4737-adc8-39cdea4fd8a7"  width="31%"/>
</p>

<p align="center">
<b>Szczegóły filmu</b>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
<b>Losowanie filmu</b>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
<b>Profile</b>
</p>

---

## 📱 Aktualne ograniczenia

* 🇵🇱 Aplikacja dostępna jest wyłącznie w języku polskim.
* 🇵🇱 Projekt skierowany jest obecnie do użytkowników z Polski.
* 📤 Eksport oraz import danych są obecnie w trakcie implementacji i pojawią się w jednej z najbliższych aktualizacji.

---

## 🛠️ Technologie

* Kotlin
* Jetpack Compose
* Material 3
* Clean Architecture
* MVVM
* Hilt
* Room
* Kotlin Coroutines & Flow
* Navigation Compose
* Retrofit
* Coil
* Kotlin Serialization
* TMDB API

---

## 🚀 Uruchomienie projektu

### 1. Sklonuj repozytorium

```bash
git clone https://github.com/TWOJ_LOGIN/Reely.git
```

### 2. Pobierz token TMDB

Załóż darmowe konto na:

https://www.themoviedb.org/

Następnie wygeneruj **Read Access Token**.

### 3. Dodaj token do `local.properties`

W katalogu głównym projektu utwórz lub edytuj plik `local.properties` i dodaj:

```properties
TMDB_ACCESS_TOKEN="TWÓJ_TMDB_ACCESS_TOKEN"
```

> Plik `local.properties` nie jest wersjonowany i nie powinien zostać dodany do repozytorium.

### 4. Uruchom aplikację

Otwórz projekt w **Android Studio**, zsynchronizuj Gradle i uruchom aplikację na emulatorze lub fizycznym urządzeniu z Androidem.

---

## 📂 Struktura projektu

```text
app
├── data
│   ├── local
│   ├── remote
│   ├── mapper
│   └── repository
│
├── domain
│   ├── model
│   ├── repository
│   └── usecase
│
└── presentation
    ├── components
    ├── navigation
    ├── screens
    ├── theme
    └── viewmodel
```

Projekt oparty jest o **Clean Architecture** oraz wzorzec **MVVM**, dzięki czemu logika biznesowa, warstwa danych oraz interfejs użytkownika są od siebie wyraźnie oddzielone.

---

## 🗺️ Roadmap

Najbliższe planowane funkcjonalności:

* 📤 Eksport danych
* 📥 Import kopii zapasowych
* 📈 Jeszcze bardziej rozbudowane statystyki
* 🎨 Dalsze ulepszanie interfejsu
* ⚡ Optymalizacja wydajności

---

## 🙏 Podziękowania

Ogromne podziękowania dla zespołu **The Movie Database (TMDB)** za udostępnienie fantastycznego API oraz ogromnej bazy filmów.

To właśnie dzięki TMDB możliwe było stworzenie Reely. Dokumentacja, jakość danych i możliwości API stoją na naprawdę wysokim poziomie — jeśli tworzysz projekt związany z filmami, zdecydowanie warto z niego skorzystać.

> This product uses the TMDB API but is not endorsed or certified by TMDB.

---

## 📄 Licencja

© 2026 Reely. Wszelkie prawa zastrzeżone.

Kod źródłowy został udostępniony wyłącznie w celach edukacyjnych, demonstracyjnych oraz do własnego użytku.

Nie wyrażam zgody na:

* sprzedaż aplikacji lub jej fragmentów,
* publikowanie zmodyfikowanych wersji jako własnego projektu,
* wykorzystywanie kodu lub zasobów projektu w celach komercyjnych bez mojej pisemnej zgody.

Jeżeli chcesz wykorzystać Reely w projekcie komercyjnym lub masz pomysł na współpracę, zapraszam do kontaktu.

Jeżeli projekt Ci się spodobał, zostaw ⭐ na GitHubie — będzie mi bardzo miło!
