package org.example.game;

import org.example.mcts.MCTS;
import org.example.mcts.TreeNode;

import java.util.HashSet;
import java.util.Set;

public class GameCoordinator {

    private BoardState boardState;
    private int nextPlay;

    public GameCoordinator(BoardState boardState, int nextPlay) {
        this.boardState = boardState;
        this.nextPlay = nextPlay;
    }

    public TreeNode.MoveWithStats findBestMove(Move awayMove){
        return MCTS.findBestMove(boardState, awayMove);
    }

    public int roundsHomeWon(){
        return boardState.getScore().roundsHomeWon();
    }

    public int roundsAwayWon(){
        return boardState.getScore().roundsAwayWon();
    }

    public int transitionToNextState(Move homeMove, Move awayMove){
        //new state
        boardState = new BoardState(boardState, homeMove, awayMove, boardState.getAwayCards(), boardState.getScore());

        if(nextPlay==1 && boardState.getWhichPlayerWonRound()==-1){
            nextPlay = 2;
        }
        else if(nextPlay==2 && boardState.getWhichPlayerWonRound()==1){
            nextPlay = 1;
        }

        return nextPlay;
    }

    public boolean isNotGameOver(){
        return !boardState.isGameOver();
    }
}
