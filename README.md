# Proiect-Whist

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

1. - In onCreate, se extrage valoarea pentru intrarea "Players", se extrag numele
    jucatorilor, se adauga in playerList si se actualizeaza arrayAdapter, pentru a se afisa jucatorii deja conectati
2. - In onCreate se seteaza listener pe intrarea cu cheia "Players", astfel incat la orice modificare a valorii intrarii sa se modifice si playerList (astfel lista de jucatori afisata pe ecran se actualizeaza in timp real).
3. - Am adaugat un TextView in care se specifica cati jucatori s-au conectat.
