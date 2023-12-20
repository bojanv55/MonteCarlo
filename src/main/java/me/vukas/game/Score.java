package me.vukas.game;

public record Score(int ourScore, int theirScore) {
    public Score calculateNewScore(Move ourMove, Move theirMove) {
        int compResult = ourMove.compareTo(theirMove);
        if(compResult==1){
            return new Score(ourScore+1, theirScore);
        }
        else if(compResult==-1){
            return new Score(ourScore, theirScore+1);
        }
        else{
            return this;
        }
    }
}
