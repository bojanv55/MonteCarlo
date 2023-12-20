package me.vukas.game;

import java.util.stream.Collectors;

public class Coordinator {
    private TreeNode rootNode;

    public Coordinator(TreeNode rootNode) {
        this.rootNode = rootNode;

        this.rootNode.expand();
    }

    public Move findBestMove(UnknownMove theirMove) {
        if(theirMove != null){
            var movesMap = rootNode.getChildren().stream()
                    .filter(x -> x.getTheirMove().getCard().getCardType()==theirMove.getCardType() && x.getTheirMove().getValueType() == theirMove.getValueType())
                    .collect(
                            Collectors.groupingBy(TreeNode::getOurMove, Collectors.averagingDouble(TreeNode::getMinmax)));
            return movesMap.entrySet().stream().findFirst().get().getKey(); //TODO!!!!
        }
        else{
            var movesMap = rootNode.getChildren().stream().collect(
                    Collectors.groupingBy(TreeNode::getOurMove, Collectors.averagingDouble(TreeNode::getMinmax)));
            return movesMap.entrySet().stream().findFirst().get().getKey(); //TODO!!!!
        }
    }

    public boolean isNotGameOver() {
        return !rootNode.isGameOver();
    }

    public int transitionToNextState(Move ourMove, Move theirMove) {
        rootNode = rootNode.getChildren().stream()
                .filter(c -> c.getOurMove().equals(ourMove) && c.getTheirMove().equals(theirMove)).findFirst().get();
        return rootNode.isWePlay() ? 1 : 2;
    }
}
