package me.vukas;

import me.vukas.game.*;
import me.vukas.game.Card.CardType;

import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

public class MinMaxApp {
    public static void main(String[] args) {
        Set<Card> ourCards = Set.of(
                new Card("ARS8", CardType.MIDFIELDER, 62,88,77),
                new Card("LIV13", CardType.MIDFIELDER, 72,86,76),
                new Card("LAZ2", CardType.DEFENDER, 63,73,82),
                new Card("BEN16", CardType.FORWARD, 82,74,59),
                new Card("NEW1", CardType.GOALKEEPER, 66,65,82),
                new Card("EIN2", CardType.DEFENDER, 65,65,77)
                //new Card("MAC6", CardType.MIDFIELDER, 80,88,70)
                //new Card("LAZ5", CardType.DEFENDER, 61,76,85)
                //new Card("RAF", CardType.FORWARD, 98,96,96)
        );

        Set<Card> theirCards = Set.of(
                new Card("PSG17", CardType.FORWARD, 81,69,65),
                new Card("INT9", CardType.MIDFIELDER, 60,82,72),
                new Card("BAR10", CardType.MIDFIELDER, 71,88,68),
                new Card("NEW2", CardType.DEFENDER, 69,65,83),
                new Card("MIL6", CardType.FORWARD, 86,76,72),
                new Card("RIV17", CardType.FORWARD, 76,67,63)
                //new Card("RMA6", CardType.FORWARD, 89,79,69)
                //new Card("ATM18", CardType.FORWARD, 83,68,66)
                //new Card("GOL35", CardType.FORWARD, 100,97,98)
        );

        Scanner scanner = new Scanner(System.in);
        var coordinator = new Coordinator(new TreeNode(ourCards, theirCards));

        int whoPlays;
        do {
            System.out.print("Who plays first move? [1] us or [2] they > ");
        } while (!Set.of(1, 2).contains(whoPlays = scanner.nextInt()));

        do {
            System.out.printf("Current min-max is %.2f%n", coordinator.getMinMax());

            UnknownMove theirUnknownMove = null;

            if (!isOurTurn(whoPlays)) {
                String cardType;
                String valueType;

                do {
                    System.out.print("Their card type? [F]orward, [M]idfielder, [D]efender, [G]oalkeeper, [I]nvincible > ");
                    cardType = scanner.next().toUpperCase();
                } while (!coordinator.getTheirCards().stream().map(c -> c.cardType().getShortName().toUpperCase())
                        .collect(Collectors.toSet()).contains(cardType));

                do {
                    System.out.print("Their card value type? [A]ttack, [C]ontrol, [D]efense > ");
                    valueType = scanner.next().toUpperCase();
                } while (!Set.of("A", "C", "D").contains(valueType));

                theirUnknownMove = new UnknownMove(Card.CardType.ofShortName(cardType), Card.ValueType.ofShortName(valueType));
            }

            Move bestMove = coordinator.findBestMove(theirUnknownMove);

            System.out.printf("You should play %s with card %s.%n", bestMove.valueType(), bestMove.card().id());

            String theirCardId;
            do {
                System.out.print("What was the card id that they played with? > ");
                theirCardId = scanner.next();
            } while (!coordinator.getTheirCards().stream().map(c -> c.id().toUpperCase())
                    .collect(Collectors.toSet()).contains(theirCardId.toUpperCase()));

            final String theirCardIdFinal = theirCardId.toUpperCase();
            Card theirCard = coordinator.getTheirCards().stream().filter(c -> c.id().equals(theirCardIdFinal)).findFirst().get();

            Move theirKnownMove = new Move(theirCard, bestMove.valueType().response());

            whoPlays = coordinator.transitionToNextState(bestMove, theirKnownMove);

            Score currentScore = coordinator.getScore();
            System.out.printf("Current score is %d : %d%n%n", currentScore.diff(), currentScore.diff());
        } while (coordinator.isNotGameOver());
    }

    private static boolean isOurTurn(int whoPlays) {
        return whoPlays == 1;
    }
}
