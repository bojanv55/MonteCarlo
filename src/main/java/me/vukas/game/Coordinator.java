package me.vukas.game;

import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class Coordinator {
    private Score score = new Score(0);
    private TreeNode rootNode;

    public Coordinator(TreeNode rootNode) {
        this.rootNode = rootNode;

        this.rootNode.expand();
    }

    public Move findBestMove(UnknownMove theirMove) {
        if(theirMove != null){
            var movesMap = rootNode.getChildren().stream()
                    .filter(x -> x.getTheirMove().card().cardType() == theirMove.cardType() && x.getTheirMove().valueType() == theirMove.valueType())
                    .collect(
                            Collectors.groupingBy(TreeNode::getOurMove, Collectors.minBy(Comparator.comparingDouble(TreeNode::getMinMax))))
                    .entrySet().stream().collect(Collectors.toMap(x -> x.getKey(), y->y.getValue().get().getMinMax()));
            return Collections.max(movesMap.entrySet(), Map.Entry.comparingByValue()).getKey();
        }
        else{
            var movesMap = rootNode.getChildren().stream().collect(
                    Collectors.groupingBy(TreeNode::getOurMove, Collectors.minBy(Comparator.comparingDouble(TreeNode::getMinMax))))
                    .entrySet().stream().collect(Collectors.toMap(x -> x.getKey(), y->y.getValue().get().getMinMax()));
            return Collections.max(movesMap.entrySet(), Map.Entry.comparingByValue()).getKey();
        }
    }

    public boolean isNotGameOver() {
        return !rootNode.isGameOver(this.score);
    }

    public Score getScore(){
        return this.score;
    }

    public double getMinMax(){
        return rootNode.getMinMax();
    }

    public Set<Card> getTheirCards() {
        return rootNode.getTheirCards();
    }

    public int transitionToNextState(Move ourMove, Move theirMove) {
        score = score.calculateNewScore(ourMove, theirMove);
        rootNode = rootNode.getChildren().stream()
                .filter(c -> c.getOurMove().equals(ourMove) && c.getTheirMove().equals(theirMove)).findFirst().get();
        return rootNode.isWePlay() ? 1 : 2;
    }
}
