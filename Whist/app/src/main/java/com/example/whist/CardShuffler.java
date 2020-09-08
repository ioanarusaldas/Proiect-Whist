package com.example.whist;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CardShuffler {
    // Cartile de joc
//    private static final String[] cards = {
//            "A_HEARTS", "A_SPADES", "A_CLUBS", "A_DIAMONDS",
//            "K_HEARTS", "K_SPADES", "K_CLUBS", "K_DIAMONDS",
//            "Q_HEARTS", "Q_SPADES", "Q_CLUBS", "Q_DIAMONDS",
//            "J_HEARTS", "J_SPADES", "J_CLUBS", "J_DIAMONDS",
//            "10_HEARTS", "10_SPADES", "10_CLUBS", "10_DIAMONDS",
//            "9_HEARTS", "9_SPADES", "9_CLUBS", "9_DIAMONDS",
//            "8_HEARTS", "8_SPADES", "8_CLUBS", "8_DIAMONDS",
//            "7_HEARTS", "7_SPADES", "7_CLUBS", "7_DIAMONDS",
//            "6_HEARTS", "6_SPADES", "6_CLUBS", "6_DIAMONDS",
//            "5_HEARTS", "5_SPADES", "5_CLUBS", "5_DIAMONDS",
//            "4_HEARTS", "4_SPADES", "4_CLUBS", "4_DIAMONDS",
//            "3_HEARTS", "3_SPADES", "3_CLUBS", "3_DIAMONDS",
//            "2_HEARTS", "2_SPADES", "2_CLUBS", "2_DIAMONDS",
//    };

    private static final Integer[] cards = {
            R.drawable.hearts_a, R.drawable.spades_a, R.drawable.clubs_a, R.drawable.diamonds_a,
            R.drawable.hearts_k, R.drawable.spades_k, R.drawable.clubs_k, R.drawable.diamonds_k,
            R.drawable.hearts_q, R.drawable.spades_q, R.drawable.clubs_q, R.drawable.diamonds_q,
            R.drawable.hearts_j, R.drawable.spades_j, R.drawable.clubs_j, R.drawable.diamonds_j,
            R.drawable.hearts_10, R.drawable.spades_10, R.drawable.clubs_10, R.drawable.diamonds_10,
            R.drawable.hearts_9, R.drawable.spades_9, R.drawable.clubs_9, R.drawable.diamonds_9,
            R.drawable.hearts_8, R.drawable.spades_8, R.drawable.clubs_8, R.drawable.diamonds_8,
            R.drawable.hearts_7, R.drawable.spades_7, R.drawable.clubs_7, R.drawable.diamonds_7,
            R.drawable.hearts_6, R.drawable.spades_6, R.drawable.clubs_6, R.drawable.diamonds_6,
            R.drawable.hearts_5, R.drawable.spades_5, R.drawable.clubs_5, R.drawable.diamonds_5,
            R.drawable.hearts_4, R.drawable.spades_4, R.drawable.clubs_4, R.drawable.diamonds_4,
            R.drawable.hearts_3, R.drawable.spades_3, R.drawable.clubs_3, R.drawable.diamonds_3,
            R.drawable.hearts_2, R.drawable.spades_2, R.drawable.clubs_2, R.drawable.diamonds_2
    };

    /*
        Intoarce un String Array cu cartile amestecate
        Parametru: numarul de jucatori
            3 jucatori - cartile de la 9 in sus
            4 jucatori - cartile de la 7 in sus
            5 jucatori - cartile de la 5 in sus
            6 jucatori - cartile de la 3 in sus
    */
    public static ArrayList<Integer> shuffleCards(int playerCount) {

        // Extrage cartile necesare in functie de numarul de jucatori
        Integer[] cardsArray = Arrays.copyOfRange(cards, 0, 4 * 2 * playerCount);

        List<Integer> cardsList = Arrays.asList(cardsArray);

        // Amesteca lista de carti
        Collections.shuffle(cardsList);
        return new ArrayList<> (cardsList);
    }
}
