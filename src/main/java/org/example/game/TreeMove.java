package org.example.game;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class TreeMove {
    Move homeTM;
    Move awayTM;

    public TreeMove(Move homeTM, Move awayTM) {
        this.homeTM = homeTM;
        this.awayTM = awayTM;
    }

    public Move homeTM() {
        return homeTM;
    }

    public Move awayTM() {
        return awayTM;
    }

    Map<String, Integer> cardIdToTimesVisitedH = new HashMap<>();
    Map<String, Integer> cardIdToTimesVisitedA = new HashMap<>();

    public void adjustVisits(Move home, Move away){
        cardIdToTimesVisitedH.compute(home.getCard().getId(), (k,v) -> v==null? 0 : v+1);
        cardIdToTimesVisitedA.compute(away.getCard().getId(), (k,v) -> v==null? 0 : v+1);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TreeMove treeMove = (TreeMove) o;
        return Objects.equals(homeTM, treeMove.homeTM) && Objects.equals(awayTM, treeMove.awayTM);
    }

    @Override
    public int hashCode() {
        return Objects.hash(homeTM, awayTM);
    }
}
