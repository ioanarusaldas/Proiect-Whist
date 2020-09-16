package com.example.whist;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CardShuffler {

    // ID-uri pentru cartile de joc
    private static final String[] cards = {
            "hearts_a", "spades_a", "clubs_a", "diamonds_a",
            "hearts_k", "spades_k", "clubs_k", "diamonds_k",
            "hearts_q", "spades_q", "clubs_q", "diamonds_q",
            "hearts_j", "spades_j", "clubs_j", "diamonds_j",
            "hearts_10", "spades_10", "clubs_10", "diamonds_10",
            "hearts_9", "spades_9", "clubs_9", "diamonds_9",
            "hearts_8", "spades_8", "clubs_8", "diamonds_8",
            "hearts_7", "spades_7", "clubs_7", "diamonds_7",
            "hearts_6", "spades_6", "clubs_6", "diamonds_6",
            "hearts_5", "spades_5", "clubs_5", "diamonds_5",
            "hearts_4", "spades_4", "clubs_4", "diamonds_4",
            "hearts_3", "spades_3", "clubs_3", "diamonds_3",
            "hearts_2", "spades_2", "clubs_2", "diamonds_2",
    };

    /*
        Intoarce un String Array cu cartile amestecate
        Parametru: numarul de jucatori
            3 jucatori - cartile de la 9 in sus
            4 jucatori - cartile de la 7 in sus
            5 jucatori - cartile de la 5 in sus
            6 jucatori - cartile de la 3 in sus
    */
    public static ArrayList<String> shuffleCards(int playerCount) {

        // Extrage cartile necesare in functie de numarul de jucatori
        String[] cardsArray = Arrays.copyOfRange(cards, 0, 4 * 2 * playerCount);

        List<String> cardsList = Arrays.asList(cardsArray);

        // Amesteca lista de carti
        Collections.shuffle(cardsList);
        return new ArrayList<> (cardsList);
    }
}
