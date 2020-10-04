# Proiect-Whist


===============================================================================

TODO:

- Design frumos la ScoreTab + Tabelul cu scorul
- Continuam functia turn cu dat-ul cartilor

===============================================================================

04.09 - Sergiu

- Adaugat o clasa CardShuffler care genereaza o permutare a cartilor de joc
in functie de numarul de jucatori (testata in main activity). In functie de
permutare, vom afisa pe ecran imagini cu cartile de joc


05.09 - Sergiu + Rusalda

- Am adaugat activitatea WaitingRoomActivity cu un EditText in care 
utilizatorul isi introduce numele si o lista in care apar numele jucatorilor
conectati


06.09 - Sergiu

In clasa WaitingRoomActivity:

- In onCreate, se extrage valoarea pentru intrarea "Players", se extrag numele
    jucatorilor, se adauga in playerList si se actualizeaza arrayAdapter, pentru a 
    se afisa jucatorii deja conectati
- In onCreate se seteaza listener pe intrarea cu cheia "Players", astfel incat
la orice modificare a valorii intrarii sa se modifice si playerList 
(astfel lista de jucatori afisata pe ecran se actualizeaza in timp real).
- Am adaugat un TextView in care se specifica cati jucatori s-au conectat.



07.09 - Sergiu + Rusalda

WaitingRoomActivity:

- Conectarea noastra modifica baza de date (se adauga o intrare noua)
- Inchiderea activitatii determina eliminarea noastra din baza de date si
restructurarea acesteia (jucatorii care s-au conectat dupa isi modifica intrarea)
- Am introdus un TextView care indica cati jucatori sunt conectati in prezent si
care se actualizeaza
- Numarul de jucatori care se pot conecta este limitat la 6
- Am adaugat un buton (Start Activity) vizibil doar cand sunt conectati minim 3
jucatori care porneste jocul cu jucatorii conectati atunci cand este apasat de
oricare din cei conectati.

GameActivity:

- am adaugat metodele de turn si runGame
- in turn: se foloseste clasa CardShuffler pentru a genera o permutare a
cartilor de joc
- se trimite la server aceasta permutare
- am adaugat un listener pe intrarea corespunzatoare player-ului care (momentan)
face un Log la cartile primite


08.09 Irina + Miruna + Sergiu  + Rusalda 
- iconita aplicatie
- rezolvat bug program
- incarcare drawable carti
- realizare tab-uri GAME/SCORE
- Afisare carti in aplicatie

09.09 - Sergiu + Rusalda
- asezare corecta a cartilor pe ecran

09.09 - Sergiu
- Mutare metode runGame si turn in GameTab
- adaugare avatare adversari in partea de sus a ecranului jocului
- se afiseaza numele adversarilor pentru jocurile de 4

10.09 - Sergiu + Rusalda
- fixat bug-uri la conectarea jucatorilor

11.09 - Sergiu
- adaugat flow-ul de bid al playerilor 
(ultimele modificari trebuie testate pe mai multe device-uri)!

11.09 - Rusalda + Sergiu
- Adaugat avatare pentru jucatori
- ajustare layout in functie de numarul de jucatori
- implementare layout personalizat dinamic pentru fiecare jucator (fiecare jucator
isi vede adversarii in partea de sus)

12.09 - Sergiu + Irina
- testare

14.09 - Sergiu
- Identificare probleme blocare UI Thread (getActivity() intoarce null in fragment)
- Modularizare WaitingRoomActivity si GameTab

15.09 - Irina + Miruna
- Modificare design meniu principal + Waiting room activity

15.09 - Rusalda + Sergiu
- Adaugare textview-uri cu bid-urile si setarea lor dupa ce toata lumea a licitat

16.09 - RUSALDA 
- fixat bug in care se afisau imaginile gresite ale cartilor

16.09 - Irina + Sergiu + Rusalda
- am setat ImageView pentru cartile date de adversar si date de jucator
- an setat onClick pe imaginile jucatorului prin care cartea selectata se
muta in centru si dispare din "mana"

17.09 - Sergiu + Rusalda + Miruna
- metodele de onclick pe imagini se activeaza doar cand e randul jucatorului
- se inregisteaza in baza de date cartile date de fiecare jucator
- jucatorii asculta intrarea "Hands" cu cartile date de adversari si 
actualizeaza UI-ul cu cartile corespunzatoare (pentru o singura mana,
 momentan)

18.09 - Sergiu + Rusalda
- Adaugat intrari de HandsLeft si Color in baza de date + setare listeneri pe
aceste intrari
- Adaugare reguli de setare onClick in functie de culoarea cartii date jos

20.09 - Sergiu
- (netestat) - bid-urile si dat-ul de carti nu se mai face strict incepand
de la primul jucator

24.09 - Toata lumea
- oricine poate incepe jocul, nu doar strict primul jucator conectat
- calculare castigator mana

25.09 - Toata lumea
- implementare scoreTab
- setare nume al jucatorilor in tabel

28.09 - Sergiu + Rusalda
- jucatorii dau jos toate cartile din mana
- se determina corect castigatorul mainii si se seteaza corect ordinea in care trebuie
sa dea maini jos jucatorii

29.09 - Sergiu + Rusalda
- adaugare background de culoare diferita pentru jucatorul care da jos carte
- introdus o animatie de fade pe carti dupa toata lumea a dat o carte
- am creat o clasa ScoreSingleton cu care transmitem de la GameTab la ScoreTab
bids si handsWon dupa ce toate cartile au fost date jos
- calculare score in scoreTab si actualizare tabel
- TextView-urie cu Bids apar imediat ce un player a votat, nu dupa ce toti au
votat


04.10 - Sergiu + Rusalda
- Dezactivare buton de bid pentru ultimul jucator (astfel incat suma bidurilor
sa nu dea 8)


===============================================================================


