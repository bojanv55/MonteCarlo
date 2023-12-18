package org.example;

import org.example.game.BoardState;
import org.example.game.Card;
import org.example.game.Move;
import org.example.mcts.MCTS;
import org.example.mcts.TreeNode;

import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

public class GameRunner {
    public static void main(String[] args) {


        List<Card> homeCards = List.of(
                new Card("ARS8", Card.Type.MIDFIELDER, 62,88,77),
                new Card("LIV13", Card.Type.MIDFIELDER, 72,86,76),
                new Card("LAZ2", Card.Type.DEFENDER, 63,73,82),
                new Card("BEN16", Card.Type.FORWARD, 82,74,59),
                new Card("NEW1", Card.Type.GOALKEEPER, 66,65,82)
        );

        List<Card> awayCards = List.of(
                new Card("PSG17", Card.Type.FORWARD, 81,69,65),
                new Card("INT9", Card.Type.MIDFIELDER, 60,82,72),
                new Card("BAR10", Card.Type.MIDFIELDER, 71,88,68),
                new Card("NEW2", Card.Type.DEFENDER, 69,65,83),
                new Card("MIL6", Card.Type.FORWARD, 86,76,72)
        );


        /*
        List<Card> homeCards = List.of(
                new Card("LAZ2", Card.Type.DEFENDER, 63,73,82)
        );

        List<Card> awayCards = List.of(
                new Card("INT9", Card.Type.MIDFIELDER, 60,82,72)
        );

        List<Card> homeCards = List.of(
                new Card("LIV13", Card.Type.MIDFIELDER, 72,86,76),
                new Card("BEN16", Card.Type.FORWARD, 82,74,59)
        );

        List<Card> awayCards = List.of(
                new Card("INT9", Card.Type.MIDFIELDER, 60,82,72),
                new Card("BAR10", Card.Type.MIDFIELDER, 71,88,68)
        );*/

        Scanner s = new Scanner(System.in);

        Set<Card> p1Played = new HashSet<>();
        Set<Card> p2Played = new HashSet<>();

        var boardState = new BoardState(homeCards, awayCards);
        TreeNode.MoveWithStats bestMoveWithStats;


        int nextPlay;
        do{
            System.out.println("WHO STARTS? (1 or 2)? > ");
        }while (!Set.of(1,2).contains(nextPlay = s.nextInt()));


        /*while ((bestMoveWithStats = MCTS.findBestMove(boardState)) != null){

        }*/

        do{
            //System.out.println("POSSIBLE WIN ");
            //System.out.println((double)bestMoveWithStats.wins()/bestMoveWithStats.visits());
            //System.out.println("SCORE");
            //System.out.printf("%s : %s%n", boardState.getRoundsHomeWon(), boardState.getRoundsAwayWon());

            if(nextPlay==1){
                //first player plays
                bestMoveWithStats = MCTS.findBestMove(boardState, null);

            }
            else{
                //second player plays
                String cardTy = null;
                String moveTy = null;
                do{
                    System.out.println("P2 card type? [F]orward, [M]idfielder, [D]efender, [G]oalkeeper, [I]nvincible: ");
                    cardTy = s.next();
                }while (!Set.of("F","M","D","G","I").contains(cardTy));
                do{
                    System.out.println("P2 move type? [A]ttack, [C]ontrol, [D]efense: ");
                    moveTy = s.next();
                }while (!Set.of("A","C","D").contains(moveTy));

                Move awayMove = new Move(Card.getOfType(cardTy), Card.Move.getOfType(moveTy));

                bestMoveWithStats = MCTS.findBestMove(boardState, awayMove);
            }


            Move bestMove = bestMoveWithStats.move();

            System.out.println("PLAY ");
            System.out.println(bestMove.getCard().getId());
            System.out.println(bestMove.getMove());
            System.out.printf("Win prob: %.5f, Draw prob: %.5f, Loss prob: %.5f%n", bestMoveWithStats.winProb(), bestMoveWithStats.drawProb(), bestMoveWithStats.lossProb());

            String p2Id;

            do{
                System.out.println("Enter the p2 card id: ");
                p2Id = s.next();
            }while (!awayCards.stream().map(Card::getId).collect(Collectors.toSet()).contains(p2Id));

            final String p2IdSel = p2Id;
            p1Played.add(bestMove.getCard());
            Card p2Card = awayCards.stream().filter(c -> c.getId().equals(p2IdSel)).findFirst().get();
            p2Played.add(p2Card);

            Move p2Move = new Move(p2Card, bestMove.getMove().response());

            //new state
            boardState = new BoardState(boardState, bestMove, p2Move, boardState.getAwayCards(), boardState.getScore());

            if(nextPlay==1 && boardState.getWhichPlayerWonRound()==-1){
                nextPlay = 2;
            }
            else if(nextPlay==2 && boardState.getWhichPlayerWonRound()==1){
                nextPlay = 1;
            }

            System.out.printf("Score = %s : %s%n", boardState.getScore().roundsHomeWon(), boardState.getScore().roundsAwayWon());

        }while (!boardState.isGameOver());

    }
}
