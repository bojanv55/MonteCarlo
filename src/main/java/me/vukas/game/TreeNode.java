package me.vukas.game;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class TreeNode {
    private final Set<Card> ourCards;
    private final Set<Card> theirCards;
    private Move ourMove;
    private Move theirMove;
    private boolean wePlay = true;
    private final Score score;
    private double minmax;
    private int resultInThisNode; //-1 theirWin, 0 draw, 1 ourWin
    private TreeNode parent;
    private Set<TreeNode> children = new HashSet<>();

    public TreeNode(Set<Card> ourCards, Set<Card> theirCards) {
        this.ourCards = ourCards;
        this.theirCards = theirCards;
        this.score = new Score(0, 0);
    }

    private TreeNode(TreeNode parent, Move ourMove, Move theirMove) {
        this.ourCards = parent.ourCards.stream().filter(c -> !c.equals(ourMove.getCard())).collect(Collectors.toSet());
        this.theirCards = parent.theirCards.stream().filter(c -> !c.equals(theirMove.getCard())).collect(Collectors.toSet());
        this.resultInThisNode = ourMove.compareTo(theirMove);
        this.wePlay = (resultInThisNode==0) ? parent.wePlay : (resultInThisNode == 1);
        this.score = parent.score.calculateNewScore(ourMove, theirMove);

        this.parent = parent;
        this.ourMove = ourMove;
        this.theirMove = theirMove;
    }

    public Set<Card> getTheirCards() {
        return theirCards;
    }

    public Set<TreeNode> getChildren() {
        return children;
    }

    public boolean isWePlay() {
        return wePlay;
    }

    public double getMinMax() {
        return minmax;
    }

    public Move getOurMove() {
        return ourMove;
    }

    public Move getTheirMove() {
        return theirMove;
    }

    public int remainingCardsPerPlayer() {
        return ourCards.size(); //can use theirCards.size also
    }

    public boolean isGameOver() {
        return (Math.abs(score.theirScore() - score.ourScore()) > remainingCardsPerPlayer()) || remainingCardsPerPlayer() == 0;
    }

    public void expand() {
        if (this.isGameOver()) {
            backpropagation(this);
            return;
        }
        this.children = generateAllPossibleChildren();
        children.forEach(TreeNode::expand);
    }

    private Set<TreeNode> generateAllPossibleChildren() {
        Set<TreeNode> allPossibleChildren = new HashSet<>();
        for (Card ourCard : ourCards) {
            for (Card theirCard : theirCards) {
                for (Card.ValueType cardValueType : Card.ValueType.values()) {
                    allPossibleChildren.add(new TreeNode(this, new Move(ourCard, cardValueType), new Move(theirCard, cardValueType.response())));
                }
            }
        }
        return allPossibleChildren;
    }

    private static void backpropagation(TreeNode treeNode) {
        if (treeNode == null) {
            return; //we reached root
        }

        if (treeNode.children.isEmpty()) {
            treeNode.minmax = treeNode.resultInThisNode;
        } else {
            if (treeNode.wePlay) {
                treeNode.minmax = treeNode.children.stream().collect(
                                Collectors.groupingBy(x -> x.theirMove, Collectors.averagingDouble(x -> x.minmax)))
                        .values().stream().min(Comparator.comparingDouble(x -> x)).get();
            } else {
                treeNode.minmax = treeNode.children.stream().collect(
                                Collectors.groupingBy(x -> x.ourMove, Collectors.averagingDouble(x -> x.minmax)))
                        .values().stream().max(Comparator.comparingDouble(x -> x)).get();
            }
        }

        backpropagation(treeNode.parent);
    }

    public Score getScore() {
        return this.score;
    }
}
