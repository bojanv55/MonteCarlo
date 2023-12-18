package org.example.mcts;

import org.example.game.BoardState;
import org.example.game.Move;

import java.util.*;
import java.util.stream.Collectors;

public class TreeNode {
    private BoardState boardState;
    private int visits;
    private int wins;
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

    public void updateResult(int result) {
        switch (result){
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

    public record MoveWithStats(Move move, int wins, int draws, int losses){
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
        return new MoveWithStats(mv, mapWin.get(mv), mapDraw.get(mv), mapLoss.get(mv));
    }
}
