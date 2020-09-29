package com.example.whist;

import java.util.ArrayList;

public class ScoreSingleton {

    private ArrayList<Integer> bids;
    private ArrayList<Integer> handsWon;

    private static ScoreSingleton instance = null;

    protected ScoreSingleton() {
    }

    public static ScoreSingleton getInstance() {
        if (instance == null) {
            instance = new ScoreSingleton();
        }
        return instance;
    }

    public void setBids(ArrayList<Integer> bids) {
        this.bids = bids;
    }

    public ArrayList<Integer> getBids() {
        return bids;
    }

    public void setHandsWon(ArrayList<Integer> handsWon) {
        this.handsWon = handsWon;
    }

    public ArrayList<Integer> getHandsWon() {
        return handsWon;
    }
}