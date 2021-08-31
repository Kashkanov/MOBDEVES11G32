package com.mobdeve.s11.g32.tindergree.Models;

import java.util.ArrayList;

public class Matches {

    public String uid;

    public ArrayList<String> uidMatches;
    public ArrayList<String> uidMatchRequests;

    public Matches(String uid) {
        this.uid = uid;
    }

    public Matches(String uid, ArrayList<String> uidMatches, ArrayList<String> uidMatchRequests) {
        this.uid = uid;
        this.uidMatches = uidMatches;
        this.uidMatchRequests = uidMatchRequests;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    /**
     * For newly registered accounts, this initializes the Matches in the database, which is empty for now.
     */
    public void initializeMatches() {
        this.uidMatches = new ArrayList<>();
        this.uidMatchRequests = new ArrayList<>();
    }

    public void addMatchRequest(String matchRequestUid) {
        uidMatchRequests.add(matchRequestUid);
    }

    public void addMatches(String matchUid) {
        uidMatches.add(matchUid);
    }

    public void removeMatchRequests(String matchRequestUid) {
        uidMatchRequests.remove(matchRequestUid);
    }

    public void removeMatch(String matchId) {
        uidMatches.remove(matchId);
    }

}
