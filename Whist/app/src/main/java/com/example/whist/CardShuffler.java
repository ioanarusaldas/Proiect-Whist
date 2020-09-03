package com.example.whist;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CardShuffler {
    // Cartile de joc
    private static final String[] cards = {
            "A_HEARTS", "A_SPADES", "A_CLUBS", "A_DIAMONDS",
            "K_HEARTS", "K_SPADES", "K_CLUBS", "K_DIAMONDS",
            "Q_HEARTS", "Q_SPADES", "Q_CLUBS", "Q_DIAMONDS",
            "J_HEARTS", "J_SPADES", "J_CLUBS", "J_DIAMONDS",
            "10_HEARTS", "10_SPADES", "10_CLUBS", "10_DIAMONDS",
            "9_HEARTS", "9_SPADES", "9_CLUBS", "9_DIAMONDS",
            "8_HEARTS", "8_SPADES", "8_CLUBS", "8_DIAMONDS",
            "7_HEARTS", "7_SPADES", "7_CLUBS", "7_DIAMONDS",
            "6_HEARTS", "6_SPADES", "6_CLUBS", "6_DIAMONDS",
            "5_HEARTS", "5_SPADES", "5_CLUBS", "5_DIAMONDS",
            "4_HEARTS", "4_SPADES", "4_CLUBS", "4_DIAMONDS",
            "3_HEARTS", "3_SPADES", "3_CLUBS", "3_DIAMONDS",
            "2_HEARTS", "2_SPADES", "2_CLUBS", "2_DIAMONDS",
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
