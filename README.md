README  
======

http://localhost:8080/login -> logowanie. Obsługuję TYLKO requesty POST i przyjmuje TYLKO pliki JSON z loginem i haslem  
  
Przyklad:  
  
{  
"login": "def",  
"password": "def"  
}  
  
http://localhost:8080/register -> rejestracja. TYLKO POST i TYLKO JSON (login, password, matchingPassword, email, userName) (POST)  
  
  Przyklad:  
    
  {    
  "emailAddress": "string",    
  "login": "string",    
  "matchingPassword": "string",    
  "password": "string",    
  "userName": "string@string.com"    
}      
    
  
http://localhost:8080/api/users/all -> zwraca liste zarejestrowanych uzytkownikow (GET)  
  
http://localhost:8080/swagger-ui.html#/ -> swagger  
  
http://localhost:8080/api/users/{page} -> zwraca liste zarejestrowanych uzytkownikow podzielona na strony (argumenty: pageNr, size) (GET)  
  
http://localhost:8080/api/users/user/{id} -> zwraca uzytkownika po id (GET)    
  
http://localhost:8080/api/users/resetpassword -> przyjmuje JSON z emailem, zwraca wyjątek jeśli konto o podanym emailu nie istnieje. Jeśli istnieje, wysyła email z linkiem potwierdzającym na podany email (POST)
  
http://localhost:8080/api/users/requestpasswordvalidate?token={token} -> przyjmuje token z linku potwierdzającego jako parametr. Sprawdza czy token jest ważny (tokeny do resetu haseł mają ważność 15 minut), jeśli nie - wyrzuca wyjątek, jeśli tak - zwraca HttpStatus "OK" (POST)
  
http://localhost:8080/api/users/setnewpassword -> przyjmuje JSON:
  
  {    
    "matchingPassword": "string",    
    "password": "string",    
    "token": "string"    
  }    
    
-> ustawia nowe hasło dla użytkownika. Wyrzuca wyjątek jeśli hasła są różne lub jeśli hasło jest niepoprawne (hasło musi mieć co najmniej 6 znaków i nie może zawierać spacji) (POST)
  
http://localhost:8080/api/users/user/{id} (PUT) -> edycja użytkownika
  
    
      
Habits
======
    
http://localhost:8080/api/habits/new-habit -> 
  
  
  {    
    "category": "None",    
    "frequency": "Daily",    
    "members": [    
      {    
        "doneDates": [    
          {}    
        ],    
        "habits": [    
          {    
            "category": "None",    
            "frequency": "Daily",    
            "habitStartDate": "2019-04-10T19:27:25.278Z",    
            "habitTitle": "string",    
            "id": 0,    
            "members": [    
              {}    
            ],    
            "status": "Public"    
          }    
        ],    
        "id": 0,    
        "memberID": 0    
      }    
    ],    
    "startDate": "2019-04-10T19:27:25.279Z",    
    "status": "Public",    
    "title": "string"    
  }    
        
   
http://localhost:8080/api/habits/all -> zwraca wszystkie habity
  
  
----------------------------------------------------------------------------------------------
UWAGA! Po pierwszym uruchomieniu serwera i utworzeniu bazy danych warto wejsc w plik application.properties (znajdujący się w katalogu resources) i zmienic wartosc :  
  
spring.jpa.hibernate.ddl-auto (create -> update)  
  
Inaczej przy kazdym uruchomieniu baza będzie usuwać tabele i tworzyć je od nowa!
  
    
WSZYSTKIE ARGUMENTY I SZKIELETY METOD SPRAWDZAJCIE W SWAGGERZE!!!    
