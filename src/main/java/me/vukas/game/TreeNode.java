package me.vukas.game;

import java.util.*;
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
    private Set<TreeNode> children = new HashSet<>();
    private Map<Move, ChildrenStats> childrenStatsByOurMove = new HashMap<>();
    private Map<Move, ChildrenStats> childrenStatsByTheirMove = new HashMap<>();

    static class ChildrenStats{
        double currentSum;
        int summedElements;
        int remainingElements;

        public ChildrenStats(int remainingElements) {
            this.remainingElements = remainingElements;
        }

        public void updateSum(double averageResultFromChild){
            summedElements++;
            remainingElements--;
            currentSum += averageResultFromChild;
        }

        public OptionalDouble getAverage(){
            if(remainingElements!=0){
                return OptionalDouble.empty();
            }
            return OptionalDouble.of(currentSum/(summedElements));
        }
    }

    public TreeNode(Set<Card> ourCards, Set<Card> theirCards) {
        this.ourCards = ourCards;
        this.theirCards = theirCards;
        this.score = new Score(0, 0);
    }

    private TreeNode(TreeNode parent, Move ourMove, Move theirMove) {
        this.ourCards = parent.ourCards.stream().filter(c -> !c.equals(ourMove.card())).collect(Collectors.toSet());
        this.theirCards = parent.theirCards.stream().filter(c -> !c.equals(theirMove.card())).collect(Collectors.toSet());
        this.resultInThisNode = ourMove.compareTo(theirMove);
        this.wePlay = (resultInThisNode==0) ? parent.wePlay : (resultInThisNode == 1);
        this.score = parent.score.calculateNewScore(ourMove, theirMove);

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

    public double expand() {
        if (this.isGameOver()) {
            this.minmax = this.resultInThisNode;
            return this.minmax;
        }

        generateAllPossibleChildren();

        for(TreeNode child : this.children){
            double averageResultFromChild = child.expand();
            ChildrenStats stats = this.childrenStatsByOurMove.get(child.ourMove);
            stats.updateSum(averageResultFromChild);
            ChildrenStats stats2 = this.childrenStatsByTheirMove.get(child.theirMove);
            stats2.updateSum(averageResultFromChild);
            if (this.wePlay) {
                //try to check if new sum update actually can produce the average and if average is actually final one
                if(stats.getAverage().isPresent() && stats.getAverage().getAsDouble()==1.0){
                    //no more need to check other children
                    this.minmax = 1.0;
                    return 1.0;
                }
            }
            else{
                if(stats2.getAverage().isPresent() && stats2.getAverage().getAsDouble()==-1.0){
                    //no more need to check other children
                    this.minmax = -1.0;
                    return -1.0;
                }
            }
        }

        if (this.wePlay) {
            this.minmax = this.childrenStatsByOurMove.values().stream().max(Comparator.comparingDouble(x -> x.getAverage().getAsDouble())).get().getAverage().getAsDouble();
            return this.minmax;
        }
        else{
            this.minmax = this.childrenStatsByTheirMove.values().stream().min(Comparator.comparingDouble(x -> x.getAverage().getAsDouble())).get().getAverage().getAsDouble();
            return this.minmax;
        }
    }

    private void generateAllPossibleChildren() {
        this.children = new HashSet<>();
        for (Card ourCard : ourCards) {
            for (Card theirCard : theirCards) {
                for (Card.ValueType cardValueType : Card.ValueType.values()) {
                    this.children.add(new TreeNode(this, new Move(ourCard, cardValueType), new Move(theirCard, cardValueType.response())));
                }
            }
        }
        this.childrenStatsByOurMove = this.children.stream().collect(
                        Collectors.groupingBy(x -> x.ourMove,
                                Collectors.collectingAndThen(Collectors.counting(), x -> new ChildrenStats(x.intValue()))));
        this.childrenStatsByTheirMove = this.children.stream().collect(
                Collectors.groupingBy(x -> x.theirMove,
                        Collectors.collectingAndThen(Collectors.counting(), x -> new ChildrenStats(x.intValue()))));
    }

    public Score getScore() {
        return this.score;
    }
}
