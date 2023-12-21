package me.vukas.game;

import java.util.Objects;

public record Score(int diff) {
    public Score calculateNewScore(Move ourMove, Move theirMove) {
        int compResult = ourMove.compareTo(theirMove);
        if(compResult==1){
            return new Score(diff+1);
        }
        else if(compResult==-1){
            return new Score(diff-1);
        }
        else{
            return this;
        }
    }

    public int diff(){
        return diff;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Score score = (Score) o;
        return diff == score.diff;
    }

    @Override
    public int hashCode() {
        return Objects.hash(diff);
    }
}
