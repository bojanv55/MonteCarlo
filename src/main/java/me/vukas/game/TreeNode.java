package me.vukas.game;

import java.util.*;
import java.util.stream.Collectors;

public class TreeNode {
    private final Set<Card> ourCards;
    private final Set<Card> theirCards;
    private final int remainingCardsPerPlayer;
    private Move ourMove;
    private Move theirMove;
    private boolean wePlay = true;
    private Score score;
    private double minmax;
    private int resultInThisNode; //-1 theirWin, 0 draw, 1 ourWin
    private Set<TreeNode> parents = new HashSet<>();
    private Set<TreeNode> children = new HashSet<>();
    private Map<Move, ChildrenStats> childrenStatsByOurMove = new HashMap<>();
    private Map<Move, ChildrenStats> childrenStatsByTheirMove = new HashMap<>();

    private static final Map<RemainingCards, TreeNode> alreadyCalculatedPositions = new HashMap<>();

    record RemainingCards(Set<Card> ourCards, Set<Card> theirCards, boolean wePlay, int resultInThisNode, int scoreDiff){
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            RemainingCards that = (RemainingCards) o;
            return wePlay == that.wePlay && resultInThisNode == that.resultInThisNode && scoreDiff == that.scoreDiff && Objects.equals(ourCards, that.ourCards) && Objects.equals(theirCards, that.theirCards);
        }

        @Override
        public int hashCode() {
            return Objects.hash(ourCards, theirCards, wePlay, resultInThisNode, scoreDiff);
        }
    }

    static class ChildrenStats{
        double currentExtreme;
        double currentMin = Integer.MAX_VALUE;
        double currentMax = Integer.MIN_VALUE;
        int summedElements;
        int remainingElements;

        public ChildrenStats(int remainingElements) {
            this.remainingElements = remainingElements;
        }

        public void updateExtreme(double extreme){
            summedElements++;
            remainingElements--;
            this.currentExtreme += extreme;
            this.currentMin = Math.min(extreme, this.currentMin);
            this.currentMax = Math.max(extreme, this.currentMax);
        }

        public OptionalDouble getAverage(){
            if(remainingElements!=0 /*|| !(Math.abs(currentExtreme/summedElements)==1)*/){
                return OptionalDouble.empty();
            }
            return OptionalDouble.of(currentExtreme/summedElements);
        }

        public double getCurrentExtreme() {
            return currentExtreme;
        }

        public double getCurrentMin() {
            return (double)currentMin/*/summedElements*/;
        }

        public double getCurrentMax() {
            return (double)currentMax/*/summedElements*/;
        }
    }

    public TreeNode(Set<Card> ourCards, Set<Card> theirCards) {
        this.ourCards = ourCards;
        this.remainingCardsPerPlayer = ourCards.size();
        this.theirCards = theirCards;
        this.score = new Score(0);
    }

    private TreeNode(TreeNode parent, Move ourMove, Move theirMove) {
        if(ourMove.card().id().equals("C") && ourMove.valueType().equals(Card.ValueType.ATTACK)
        && theirMove.card().id().equals("E") && theirMove.valueType().equals(Card.ValueType.DEFENSE)){
            int ss = 33;
        }
        this.ourCards = parent.ourCards.stream().filter(c -> !c.equals(ourMove.card())).collect(Collectors.toSet());
        this.remainingCardsPerPlayer = ourCards.size();
        this.theirCards = parent.theirCards.stream().filter(c -> !c.equals(theirMove.card())).collect(Collectors.toSet());
        this.resultInThisNode = ourMove.compareTo(theirMove);
        this.wePlay = (resultInThisNode==0) ? parent.wePlay : (resultInThisNode == 1);
        this.score = parent.score.calculateNewScore(ourMove, theirMove);

        this.parents.add(parent);

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

    public boolean isGameOver(Score score) {
        return (Math.abs(score.diff()) > remainingCardsPerPlayer) || remainingCardsPerPlayer == 0;
    }

    private void saveHistory(TreeNode child){
        alreadyCalculatedPositions.putIfAbsent(new RemainingCards(child.ourCards, child.theirCards, child.wePlay, child.resultInThisNode, child.score.diff()), child);
    }

    public double expand() {

        if(ourMove!=null && ourMove.card().id().equals("MAC6") && ourMove.valueType().equals(Card.ValueType.ATTACK)
                && theirMove.card().id().equals("PSG17") && theirMove.valueType().equals(Card.ValueType.DEFENSE)){
            int ss = 33;
        }

        if (this.isGameOver(this.score)) {
            this.minmax = Integer.compare(this.score.diff(), 0);
            saveHistory(this);
            return this.minmax;
        }

        var remCards = new RemainingCards(this.ourCards, this.theirCards, this.wePlay, this.resultInThisNode, this.score.diff());
        if(alreadyCalculatedPositions.containsKey(remCards)){
            //we already had these remaining cards before

            TreeNode foundNode = alreadyCalculatedPositions.get(remCards);  //for this node, the score will be wrong, since we attached incorrect children score
            this.score = foundNode.score; //foundNode.score;

            if(foundNode.children.isEmpty()){
                System.out.println("EMPTY CHILDREN!!!!");
            }

            this.children.forEach(c -> c.parents.add(this));

            this.minmax = foundNode.minmax;
            this.children = foundNode.children;
            if(this.children.isEmpty()){
                System.out.println("33q242134");
            }
            this.childrenStatsByOurMove = foundNode.childrenStatsByOurMove;
            this.childrenStatsByTheirMove = foundNode.childrenStatsByTheirMove;
            return this.minmax;
        }

        generateAllPossibleChildren();

        for(TreeNode child : this.children){
            double averageResultFromChild = child.expand();

            if (this.wePlay) {
                ChildrenStats stats = this.childrenStatsByOurMove.get(child.ourMove);
                stats.updateExtreme(averageResultFromChild);

                if(stats.getAverage().isPresent() && stats.getAverage().getAsDouble()==1.0){
                    //no more need to check other children
                    this.minmax = 1;
                    saveHistory(child);
                    return this.minmax;
                }
            }
            else{
                ChildrenStats stats2 = this.childrenStatsByTheirMove.get(child.theirMove);
                stats2.updateExtreme(averageResultFromChild);

                if(stats2.getAverage().isPresent() && stats2.getAverage().getAsDouble()==-1.0){
                    //no more need to check other children
                    this.minmax = -1;
                    saveHistory(child);
                    return this.minmax;
                }
            }
        }

        if (this.wePlay) {
            this.minmax = this.childrenStatsByOurMove.values().stream().max(Comparator.comparingDouble(x -> x.getCurrentMin())).get().getCurrentMin();
            saveHistory(this);
            return this.minmax;
        }
        else{
            this.minmax = this.childrenStatsByTheirMove.values().stream().min(Comparator.comparingDouble(x -> x.getCurrentMin())).get().getCurrentMin();
            saveHistory(this);
            return this.minmax;
        }
    }

    private void generateAllPossibleChildren() {
        if(!this.children.isEmpty()){
            System.out.println("!!!!!!!!!!!!!!!1");
        }
        this.children = new HashSet<>();
        for (Card ourCard : ourCards) {
            for (Card theirCard : theirCards) {
                for (Card.ValueType cardValueType : Card.ValueType.values()) {
                    this.children.add(new TreeNode(this, new Move(ourCard, cardValueType), new Move(theirCard, cardValueType.response())));
                }
            }
        }

        if(wePlay) {
            this.childrenStatsByOurMove = this.children.stream().collect(
                    Collectors.groupingBy(x -> x.ourMove,
                            Collectors.collectingAndThen(Collectors.counting(), x -> new ChildrenStats(x.intValue()))));
        }
        else {
            this.childrenStatsByTheirMove = this.children.stream().collect(
                    Collectors.groupingBy(x -> x.theirMove,
                            Collectors.collectingAndThen(Collectors.counting(), x -> new ChildrenStats(x.intValue()))));
        }
    }

    public Score getScore() {
        return this.score;
    }
}
