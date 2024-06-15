Projekt Kolejka TIR

Projekt składa się z dwóch głównych części: backendu napisanego w Spring Boot oraz frontendu napisanego w Angularze.
Backend
Technologie:

    Java 17
    Spring Boot 2.6.2
    Maven
Kroki do uruchomienia projektu :
    
    git clone https://github.com/gucio123/TruckCheckSystem

Uruchomienie backendu:

    cd backend

Budowanie projektu:
    
    ./mvnw clean install

Uruchomienie aplikacji:

    ./mvnw spring-boot:run

Endpointy API:

Dodanie ciężarówki:

    POST /api/trucks/arrive

Status wszystkich ciężarówek:

    GET /api/trucks/status

Symulacja jednostki czasu:

    POST /api/trucks/step

Estymowany czas oczekiwania dla ciężarówki:

    GET /api/trucks/estimatedWaitTime/{id}

Rozpoczęcie sprawdzania ciężarówki:

        POST /api/trucks/startChecking/{id}


Frontend
Technologie:

    Angular 15
    TypeScript

Uruchomienie frontendu:

    cd frontend/frontend_app

Instalacja zależności:

    npm install

Uruchomienie aplikacji:

bash

    ng serve lub npm start

    Aplikacja będzie dostępna pod adresem http://localhost:4200.

Pliki i katalogi

    backend/: Zawiera kod źródłowy backendu.
        src/main/java/rekrutacja/backend/: Kod źródłowy aplikacji Spring Boot.
            config/: Konfiguracje aplikacji.
            controller/: Kontrolery REST.
            model/: Modele danych.
            service/: Logika biznesowa.
        src/test/java/rekrutacja/backend/: Testy jednostkowe i integracyjne.

    frontend/frontend_app/: Zawiera kod źródłowy frontendu.
        src/app/: Kod źródłowy aplikacji Angular.
            truck-status/: Komponent do wyświetlania statusu ciężarówek.
            assets/: Zasoby statyczne aplikacji.
        src/: Główne pliki aplikacji, takie jak main.ts, polyfills.ts itp.

Aby aplikacja zadziałała proszę upewnić się, że zainstalowane są wymagane wersje JDK i Node.js.
