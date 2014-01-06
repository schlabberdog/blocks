package com.github.users.schlabberdog.blocks.solver;

import java.util.HashMap;

public class LeveledSteps {
    private HashMap<String,Integer> steps = new HashMap<String, Integer>();

    public void clear() {
        steps.clear();
    }

    /**
     * Fügt einen Step mit einem Level hinzu
     * @param hash
     * @param level
     */
    public void pushOnLevel(String hash, int level) {
        if(containsOnBetterLevel(hash,level))
            throw new RuntimeException("Hash <"+hash+"> @ "+level+" is already at level <"+steps.get(hash)+">!");
        steps.put(hash,level);
    }

    /**
     * Prüft ob es den Step bereits gegeben hat UND ob er auf einem höheren Level passiert ist
     * @param nextHash
     * @param myLevel
     * @return
     */
    public boolean containsOnBetterLevel(String nextHash, int myLevel) {
        Integer oldLevel = steps.get(nextHash);
        if(oldLevel != null) {
            if(oldLevel < myLevel)
                return true;
        }
        return false;
    }
}
