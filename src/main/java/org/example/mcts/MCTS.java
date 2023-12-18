package org.example.mcts;

import org.example.game.BoardState;
import org.example.game.Move;

public class MCTS {
    private static final double EXPLORATION_PARAMETER = Math.sqrt(2);
    private static final int NUMBER_OF_SIMULATIONS = 1_000_000;


    private static TreeNode select(TreeNode node){
        while (!node.getChildren().isEmpty()){  //go to children below in the tree
            node = uctSelect(node);
        }
        return node;
    }

    private static TreeNode uctSelect(TreeNode node) {
        double maxUCT = Double.MIN_VALUE;
        TreeNode selectedChild = null;

        for(TreeNode childNode : node.getChildren()){
            //if never visited, UCT value is MAX_VALUE
            double uctValue = (childNode.getVisits() == 0) ? Double.MAX_VALUE : ((double)childNode.getWins()/childNode.getVisits()
                    + EXPLORATION_PARAMETER * Math.sqrt(Math.log(node.getVisits()) / childNode.getVisits()));
            if(uctValue > maxUCT){
                maxUCT = uctValue;
                selectedChild = childNode;
            }
        }

        return selectedChild;
    }

    public static TreeNode.MoveWithStats findBestMove(BoardState boardState, Move awayMove){
        TreeNode root = new TreeNode(boardState, null);

        Move firstExpandMove = awayMove;
        for (int i = 0; i < NUMBER_OF_SIMULATIONS; i++) {
            TreeNode selectedNode = select(root);

            if(!selectedNode.isGameOver()){
                selectedNode.expand(firstExpandMove);
                firstExpandMove = null;
            }

            TreeNode nodeToExplore = selectedNode;
            if(!selectedNode.getChildren().isEmpty()){
                nodeToExplore = selectedNode.getRandomChildNode();
            }

            //here we actually got to the nodeToExplore which is node in the tree. we do not create new children for
            //this node, but just play one possible game out of many from this node downwards, and only for that game we update
            //the score of that game at this node (backpropagate) so that we know that some game that we played actually
            //resulted in that. but there may be many more games that we can play from this node downwards.
            int result = simulate(nodeToExplore);

            backpropagate(nodeToExplore, result);
        }

        return root.getMoveWithMaxScore();
    }

    private static void backpropagate(TreeNode selectedNode, int result) {
        while (selectedNode != null) {
            selectedNode.incrementVisits();
            selectedNode.updateResult(result);
            selectedNode = selectedNode.getParent();
        }
    }

    private static int simulate(TreeNode selectedNode) {
        BoardState boardState = selectedNode.getBoardState();
        while(!boardState.isGameOver()){
            boardState = boardState.randomPlay();
        }
        return boardState.result();    //one possible simulation is over, so return whatever is the result
    }
}
