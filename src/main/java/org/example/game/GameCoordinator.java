package org.example.game;

import org.example.mcts.MCTS;
import org.example.mcts.TreeNode;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class GameCoordinator {

    private BoardState boardState;
    private int nextPlay;

    public GameCoordinator(BoardState boardState, int nextPlay) {
        this.boardState = boardState;
        this.nextPlay = nextPlay;
    }

    private TreeNode rootNode;

    public TreeNode.MoveWithStats findBestMinMaxMove(Move awayMove){
        if(rootNode==null) {
            rootNode = MCTS.findBestMinMaxMove(boardState, awayMove);
        }

        if(awayMove!=null){
            //away move, so minimize the effect
            var play = rootNode.getChildren().stream()
                    .filter(x -> x.getBoardState().getAwayMove().getCard().getType()==awayMove.getCard().getType() && x.getBoardState().getAwayMove().getMove()== awayMove.getMove())
                    .collect(
                    Collectors.groupingBy(x -> x.getBoardState().getHomeMove(), Collectors.averagingDouble(x -> x.getMinmaxD())));
            Move homeplay = TreeNode.getKeyWithMaxValue(play);

            return new TreeNode.MoveWithStats(homeplay, 0,0,0);
        }
        else{
            var play = rootNode.getChildren().stream().collect(
                    Collectors.groupingBy(x -> x.getBoardState().getHomeMove(), Collectors.averagingDouble(x -> x.getMinmaxD())));
            Move homeplay = TreeNode.getKeyWithMaxValue(play);

            return new TreeNode.MoveWithStats(homeplay, 0,0,0);
        }
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

    public int transitionToNextMinMaxState(Move bestMove, Move p2Move) {
        rootNode = rootNode.getChildren().stream().filter(x -> x.getBoardState().getHomeMove().equals(bestMove) && x.getBoardState().getAwayMove().equals(p2Move))
                .findFirst().get();

        boardState = rootNode.getBoardState();
        return rootNode.getBoardState().getWhichPlayerPlaysRound();
    }
}
