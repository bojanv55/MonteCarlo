package org.example.mcts;

import org.example.game.BoardState;
import org.example.game.Move;

import java.util.*;
import java.util.stream.Collectors;

public class TreeNode {
    private BoardState boardState;
    private int visits;
    private int wins;
    private double winsRatio;
    private double homePlaysRatio;
    private double homeWinCert;
    private int minmax;

    private double minmaxD;

    private double winprob;
    private double looseprob;
    private double drawprob;

    private int draws;
    private int loses;
    private List<TreeNode> children = new ArrayList<>();
    private TreeNode parent;
    private final Random random = new Random();

    public TreeNode(BoardState boardState, TreeNode parent) {
        this.boardState = boardState;
        this.parent = parent;
    }

    public List<TreeNode> getChildren() {
        return children;
    }

    public int getVisits() {
        return visits;
    }

    public int getWins() {
        return wins;
    }

    public int getDraws() {
        return draws;
    }

    public int getLoses() {
        return loses;
    }

    public void incrementVisits(){
        this.visits++;
    }

    public void updateResult(MCTS.ResultState state) {
        switch (state.result()){
            case -1:
                this.loses++;
                break;
            case 0:
                this.draws++;
                break;
            case 1:
                this.wins++;
                break;
        }

        if(!children.isEmpty()){
            this.winsRatio = children.stream().mapToDouble(x -> x.visits==0 ? 0 : (double)x.wins/x.visits).average().getAsDouble();
            this.homePlaysRatio = children.stream().mapToDouble(x -> x.visits==0 ? 0 : x.homePlaysRatio).sum();
        }
        else{
            this.winsRatio = this.wins;
            this.homePlaysRatio = state.ratioOfHomePLays();
        }
    }

    public TreeNode getParent() {
        return this.parent;
    }

    public void expand(Move awayMove) {
        //populate this with all possible children
        List<BoardState> possibleStates = boardState.getAllPossibleNextStates(awayMove, true);

        //copy this to children here
        possibleStates.forEach(state -> children.add(new TreeNode(state, this)));
    }

    public TreeNode getRandomChildNode() {
        return children.get(random.nextInt(children.size()));
    }

    public boolean isGameOver() {
        return boardState.isGameOver();
    }

    public BoardState getBoardState() {
        return boardState;
    }

    public double getHomePlaysRatio() {
        return homePlaysRatio;
    }

    public static <K, V extends Comparable<V>> K getKeyWithMaxValue(Map<K, V> map) {
        // Check if the map is not empty
        if (map.isEmpty()) {
            return null; // or throw an exception, depending on your requirements
        }

        // Initialize variables to track the key and max value
        K keyWithMaxValue = null;
        V maxValue = null;

        // Iterate through the entries of the map
        for (Map.Entry<K, V> entry : map.entrySet()) {
            // Get the value of the current entry
            V value = entry.getValue();

            // Check if the current value is greater than the max value
            if (maxValue == null || value.compareTo(maxValue) > 0) {
                // Update the key and max value
                keyWithMaxValue = entry.getKey();
                maxValue = value;
            }
        }

        // Return the key with the maximum value
        return keyWithMaxValue;
    }

    public static <K, V extends Comparable<V>> K getKeyWithMinValue(Map<K, V> map) {
        // Check if the map is not empty
        if (map.isEmpty()) {
            return null; // or throw an exception, depending on your requirements
        }

        // Initialize variables to track the key and max value
        K keyWithMaxValue = null;
        V maxValue = null;

        // Iterate through the entries of the map
        for (Map.Entry<K, V> entry : map.entrySet()) {
            // Get the value of the current entry
            V value = entry.getValue();

            // Check if the current value is greater than the max value
            if (maxValue == null || value.compareTo(maxValue) < 0) {
                // Update the key and max value
                keyWithMaxValue = entry.getKey();
                maxValue = value;
            }
        }

        // Return the key with the maximum value
        return keyWithMaxValue;
    }

    public double getMinmaxD() {
        return minmaxD;
    }

    public double getWinsRatio() {
        return this.winsRatio;
    }

    public static void backPropagateMinMax(TreeNode treeNode){
        if(treeNode==null){
            return;
        }

        if(treeNode.children.isEmpty()){
            //leaf node
            //treeNode.minmax = treeNode.getBoardState().getWhichPlayerWonRound();

            treeNode.minmaxD = treeNode.getBoardState().getWhichPlayerWonRound();

            if(treeNode.getBoardState().getWhichPlayerWonRound()==-1){
                treeNode.looseprob = 1;
            }
            else if(treeNode.getBoardState().getWhichPlayerWonRound()==0){
                treeNode.drawprob = 1;
            }
            else{
                treeNode.winprob = 1;
            }
        }
        else{
            //any parent
            if(treeNode.getBoardState().isAwayMove()){
                //if any child has -1 we lose the game, otherwise 0 or 1.
                //treeNode.minmax = treeNode.getChildren().stream().mapToInt(x -> x.minmax).min().getAsInt();

                treeNode.minmaxD = treeNode.getChildren().stream().collect(
                        Collectors.groupingBy(x -> x.getBoardState().getAwayMove(), Collectors.averagingDouble(x -> x.minmaxD))).values().stream().min(Comparator.comparingDouble(x -> x)).get();
            }
            else{
                //away move
                //treeNode.minmax = treeNode.getChildren().stream().mapToInt(x -> x.minmax).max().getAsInt();

                treeNode.minmaxD = treeNode.getChildren().stream().collect(
                        Collectors.groupingBy(x -> x.getBoardState().getHomeMove(), Collectors.averagingDouble(x -> x.minmaxD))).values().stream().max(Comparator.comparingDouble(x -> x)).get();
            }
        }

        backPropagateMinMax(treeNode.parent);
    }

    public void expandMinMax(Move awayMove) {

        if(boardState.isGameOver()){
            //no need to expand anymore

            backPropagateMinMax(this);

            return;
        }

        //populate this with all possible children
        List<BoardState> possibleStates = boardState.getAllPossibleNextMinMaxStates(awayMove);

        //copy this to children here
        possibleStates.forEach(state -> children.add(new TreeNode(state, this)));

        for(TreeNode child : children){
            child.expandMinMax(null);   //
        }
    }


    public record MoveWithStats(Move move, int wins, int draws, int losses, double winProbD){
        public double winProb(){
            return (double)wins / (wins+draws+losses);
        }

        public double drawProb(){
            return (double)draws / (wins+draws+losses);
        }

        public double lossProb(){
            return (double)losses / (wins+draws+losses);
        }
    }

    public MoveWithStats getMoveWithMaxScore() {
        if(this.children.isEmpty()){
            return null;
        }
        Map<Move, Integer> mapVis = this.children.stream()
                .collect(Collectors.groupingBy(c -> c.getBoardState().getFirstHome(), Collectors.summingInt(TreeNode::getVisits)));
        Map<Move, Integer> mapWin = this.children.stream()
                .collect(Collectors.groupingBy(c -> c.getBoardState().getFirstHome(), Collectors.summingInt(TreeNode::getWins)));
        Map<Move, Integer> mapDraw = this.children.stream()
                .collect(Collectors.groupingBy(c -> c.getBoardState().getFirstHome(), Collectors.summingInt(TreeNode::getDraws)));
        Map<Move, Integer> mapLoss = this.children.stream()
                .collect(Collectors.groupingBy(c -> c.getBoardState().getFirstHome(), Collectors.summingInt(TreeNode::getLoses)));
        Move mv = getKeyWithMaxValue(mapVis);
        return new MoveWithStats(mv, mapWin.get(mv), mapDraw.get(mv), mapLoss.get(mv), 0.0);
    }
}
