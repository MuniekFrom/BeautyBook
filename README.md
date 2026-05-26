# Jak działa aplikacja Pracownia Urody Hola

Pracownia Urody Hola to aplikacja webowa do obsługi rezerwacji wizyt w salonie beauty. System umożliwia klientom umawianie wizyt, pracownikom zarządzanie swoimi terminami, a administratorowi zarządzanie całą ofertą, kategoriami usług, pracownikami oraz kontami użytkowników.

Aplikacja została podzielona na trzy główne role:

- CLIENT — klient salonu,
- EMPLOYEE — pracownik salonu,
- ADMIN — administrator systemu.

Każda rola ma dostęp tylko do tych funkcji, które są jej potrzebne. Dostęp do paneli i endpointów jest zabezpieczony przy pomocy JWT oraz Spring Security.

---

## 1. Logowanie i bezpieczeństwo

Użytkownik loguje się przez formularz logowania, podając login oraz hasło. Backend sprawdza, czy konto istnieje oraz czy hasło jest poprawne.

Hasła nie są przechowywane w bazie danych w zwykłej postaci. Podczas rejestracji lub tworzenia konta hasło jest hashowane za pomocą BCrypt. Dzięki temu w bazie znajduje się zaszyfrowany hash hasła, a nie prawdziwe hasło użytkownika.

Po poprawnym logowaniu backend generuje token JWT. Token zawiera informacje pozwalające rozpoznać zalogowanego użytkownika. Frontend zapisuje token w localStorage i później wysyła go w nagłówku każdego chronionego zapytania:

Authorization: Bearer token

Dzięki temu backend wie, kto wykonuje daną akcję i jaką ma rolę.

---

## 2. Rola administratora

Administrator ma największe uprawnienia w systemie. Po zalogowaniu trafia do panelu admina, gdzie może zarządzać aplikacją.

Administrator może:

- dodawać nowe kategorie usług,
- usuwać kategorie, jeśli nie są używane,
- dodawać usługi,
- edytować usługi,
- usuwać usługi,
- dodawać pracowników,
- przeglądać listę kont użytkowników,
- dezaktywować i aktywować konta,
- usuwać nieaktywne konta,
- przeglądać listę pracowników,
- przeglądać wszystkie wizyty,
- sprawdzać statystyki systemu.

Kategorie usług są przechowywane w bazie danych, a nie w enumie w kodzie. Dzięki temu admin może dodawać nowe kategorie bez zmiany kodu aplikacji, np. „Depilacja”, „Makijaż”, „Masaż” albo „Stylizacja paznokci”.

Przy dodawaniu pracownika admin podaje login, hasło, imię, nazwisko, specjalizację, opis oraz kategorię usług, którą dany pracownik wykonuje. Backend automatycznie tworzy konto użytkownika z rolą EMPLOYEE oraz powiązany z nim profil pracownika.

---

## 3. Rola pracownika

Pracownik loguje się na swoje konto i trafia do panelu pracownika.

Pracownik może:

- dodawać swoje wolne terminy,
- przeglądać swoje terminy,
- przeglądać wizyty przypisane do siebie,
- oznaczać wizyty jako wykonane,
- zmienić swoje hasło.

Pracownik nie wybiera ręcznie, do kogo należy termin. Backend rozpoznaje pracownika po tokenie JWT i automatycznie przypisuje tworzony termin do jego profilu.

Jeśli pracownik doda wolny termin, klient będzie mógł go zobaczyć tylko wtedy, gdy wybierze usługę z tej samej kategorii, którą wykonuje pracownik.

Przykład:

- pracownik Iwona ma kategorię „Paznokcie”,
- usługa „Manicure hybrydowy” też ma kategorię „Paznokcie”,
- klient wybiera usługę „Manicure hybrydowy”,
- system pokazuje tylko terminy pracowników z kategorią „Paznokcie”.

Dzięki temu klient nie może przypadkowo umówić usługi paznokciowej do osoby, która wykonuje np. laminację brwi.

---

## 4. Rola klienta

Klient może zarejestrować konto, zalogować się i korzystać z panelu klienta.

Klient może:

- przeglądać dostępne usługi,
- wybrać usługę,
- zobaczyć tylko pasujące terminy,
- zarezerwować wizytę,
- dodać notatkę do wizyty,
- przeglądać swoje wizyty,
- anulować swoją wizytę.

Proces rezerwacji wygląda tak:

1. Klient wybiera usługę.
2. Frontend pobiera wolne terminy pasujące do kategorii tej usługi.
3. Klient wybiera termin.
4. Klient wysyła formularz rezerwacji.
5. Backend tworzy wizytę i przypisuje ją do klienta.
6. Termin zmienia status z AVAILABLE na BOOKED.

Dzięki temu jeden termin nie może zostać zarezerwowany kilka razy.

---

## 5. Kategorie usług

Kategorie usług są przechowywane w osobnej tabeli w bazie danych.

Przykładowe kategorie:

- Brwi,
- Rzęsy,
- Paznokcie,
- Zabiegi estetyczne,
- Konsultacje,
- Depilacja.

Każda usługa ma przypisaną kategorię. Każdy pracownik również ma przypisaną kategorię. To pozwala filtrować terminy po wybranej usłudze.

Przykład działania:

- usługa „Depilacja woskiem” ma kategorię „Depilacja”,
- pracownik Anna ma kategorię „Depilacja”,
- pracownik Iwona ma kategorię „Paznokcie”,
- klient wybiera „Depilacja woskiem”,
- system pokazuje tylko terminy Anny, a nie Iwony.

---

## 6. Usługi

Usługa zawiera:

- nazwę,
- opis,
- cenę,
- czas trwania,
- kategorię.

Administrator może dodawać, edytować i usuwać usługi. Klient widzi usługi w panelu klienta i może na ich podstawie wybrać wizytę.

Usługa jest powiązana z kategorią, dzięki czemu system wie, którzy pracownicy mogą ją wykonywać.

---

## 7. Terminy

Terminy są tworzone przez pracowników. Każdy termin ma:

- datę i godzinę rozpoczęcia,
- datę i godzinę zakończenia,
- status,
- przypisanego pracownika.

Status terminu może oznaczać, czy termin jest wolny lub zajęty.

Przykładowy status:

- AVAILABLE — termin wolny,
- BOOKED — termin zarezerwowany,
- CANCELLED — termin anulowany.

Podczas dodawania terminu backend sprawdza, czy nowy termin nie nachodzi na inny termin tego samego pracownika. Dzięki temu pracownik nie może przypadkowo dodać dwóch wizyt w tym samym czasie.

---

## 8. Wizyty

Wizyta powstaje wtedy, gdy klient zarezerwuje wybrany termin.

Wizyta zawiera:

- klienta,
- usługę,
- termin,
- pracownika,
- status,
- opcjonalną notatkę od klienta.

Przykładowe statusy wizyty:

- BOOKED — wizyta zarezerwowana,
- COMPLETED — wizyta wykonana,
- CANCELLED — wizyta anulowana.

Klient może anulować swoją wizytę. Pracownik może oznaczyć wizytę jako wykonaną. Admin widzi wszystkie wizyty w systemie.

---

## 9. Dezaktywacja i usuwanie kont

Administrator może dezaktywować konto użytkownika. Dezaktywowane konto nie może się zalogować.

Usuwanie konta działa bezpiecznie. Konto najpierw musi zostać dezaktywowane. Dopiero wtedy administrator może je usunąć z widocznej listy. W praktyce system nie musi usuwać wszystkich danych historycznych, tylko może oznaczyć konto jako usunięte i zmienić jego login techniczny. Dzięki temu ten sam email można wykorzystać ponownie, a historia wizyt nie zostaje przypadkowo zniszczona.

---

## 10. Statystyki administratora

Panel admina pokazuje podstawowe statystyki systemu:

- liczba usług,
- liczba pracowników,
- liczba wszystkich wizyt,
- liczba wizyt zarezerwowanych,
- liczba wizyt wykonanych,
- liczba wizyt anulowanych.

Dzięki temu administrator ma szybki podgląd aktualnego stanu systemu.

---

## 11. Frontend

Frontend został wykonany w HTML, CSS i JavaScript. Pliki znajdują się w katalogu:

src/main/resources/static

Najważniejsze strony:

- index.html — strona główna,
- login.html — logowanie,
- register.html — rejestracja klienta,
- client-dashboard.html — panel klienta,
- employee-dashboard.html — panel pracownika,
- admin-dashboard.html — panel administratora.

Frontend komunikuje się z backendem za pomocą funkcji fetch. Przy chronionych zapytaniach wysyła token JWT w nagłówku Authorization.

---

## 12. Backend

Backend został wykonany w Java Spring Boot.

Najważniejsze warstwy aplikacji:

- controller — obsługuje endpointy REST,
- service — zawiera logikę biznesową,
- repository — odpowiada za komunikację z bazą danych,
- model — encje bazodanowe,
- dto — obiekty do przesyłania danych między frontendem a backendem,
- exception — obsługa błędów,
- security — konfiguracja JWT i Spring Security.

Aplikacja korzysta ze Spring Data JPA i Hibernate do obsługi bazy danych.

---

## 13. Baza danych

Aplikacja korzysta z relacyjnej bazy danych MySQL.

Najważniejsze tabele:

- users — konta użytkowników,
- beauty_categories — kategorie usług,
- beauty_services — usługi,
- employee_profiles — profile pracowników,
- availability_slots — wolne terminy pracowników,
- appointments — wizyty klientów.

Relacje między tabelami pozwalają powiązać klienta, pracownika, usługę i termin w jedną wizytę.

---

## 14. Ogólny przepływ działania aplikacji

Przykładowy pełny scenariusz działania:

1. Administrator loguje się do panelu.
2. Administrator dodaje kategorię, np. „Paznokcie”.
3. Administrator dodaje usługę „Manicure hybrydowy” i przypisuje ją do kategorii „Paznokcie”.
4. Administrator dodaje pracownika Iwonę i przypisuje ją do kategorii „Paznokcie”.
5. Pracownik Iwona loguje się do panelu pracownika.
6. Iwona dodaje wolny termin.
7. Klient loguje się do panelu klienta.
8. Klient wybiera usługę „Manicure hybrydowy”.
9. System pokazuje tylko terminy pracowników, którzy wykonują kategorię „Paznokcie”.
10. Klient wybiera termin i rezerwuje wizytę.
11. Termin zmienia status na zajęty.
12. Pracownik widzi wizytę w swoim panelu.
13. Po wykonaniu usługi pracownik oznacza wizytę jako wykonaną.
14. Administrator widzi wizytę i statystyki w panelu admina.
