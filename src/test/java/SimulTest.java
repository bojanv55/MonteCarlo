
import me.vukas.game.Card;
import me.vukas.game.Coordinator;
import me.vukas.game.Move;
import me.vukas.game.TreeNode;
import org.junit.jupiter.api.Test;

import java.util.Set;


public class SimulTest {



    @Test
    public void testSim(){
        Set<Card> ourCards = Set.of(
                new Card("A", Card.CardType.MIDFIELDER, 0,0,100),
                new Card("B", Card.CardType.MIDFIELDER, 0,0,100),
                new Card("C", Card.CardType.DEFENDER, 0,0,100)
        );

        Set<Card> theirCards = Set.of(
                new Card("E", Card.CardType.FORWARD, 0,100,0),
                new Card("F", Card.CardType.MIDFIELDER, 0,100,0),
                new Card("G", Card.CardType.MIDFIELDER, 100,100,0)
        );

        var coordinator = new Coordinator(new TreeNode(ourCards, theirCards));

        Move bestMove = coordinator.findBestMove(null);
        coordinator.transitionToNextState(bestMove, new Move(theirCards.stream().filter(x -> x.id().equals("E")).findFirst().get(), bestMove.valueType().response()));

        System.out.println(coordinator.isNotGameOver());

        bestMove = coordinator.findBestMove(null);
        coordinator.transitionToNextState(bestMove, new Move(theirCards.stream().filter(x -> x.id().equals("F")).findFirst().get(), bestMove.valueType().response()));

        System.out.println(coordinator.isNotGameOver());

        bestMove = coordinator.findBestMove(null);
        coordinator.transitionToNextState(bestMove, new Move(theirCards.stream().filter(x -> x.id().equals("G")).findFirst().get(), bestMove.valueType().response()));

        System.out.println(coordinator.isNotGameOver());
    }

}
