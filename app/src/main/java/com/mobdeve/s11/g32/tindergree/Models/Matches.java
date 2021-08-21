package com.mobdeve.s11.g32.tindergree.Models;

import java.util.ArrayList;

public class Matches {

    public String uid;
    public ArrayList<String> uidMatches;

    public Matches(String uid, ArrayList<String> uidMatches) {
        this.uid = uid;
        this.uidMatches = uidMatches;
    }

    public Matches(String uid) {
        this.uid = uid;
    }

    /**
     * For newly registered accounts, this initializes the Matches in the database, which is empty for now.
     */
    public void initializeMatches() {
        this.uidMatches = new ArrayList<>();
    }

}
