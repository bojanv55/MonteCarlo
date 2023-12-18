import org.example.GameRunner;
import org.example.game.BoardState;
import org.example.game.Card;
import org.example.game.GameCoordinator;
import org.example.game.Move;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SimulTest {

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

    @Test
    public void testSim(){
        var gameCoordinator = new GameCoordinator(new BoardState(homeCards, awayCards), 1);
        gameCoordinator.transitionToNextState(new Move(homeCards.get(0), Card.Move.CONTROL)
        , new Move(awayCards.get(1), Card.Move.CONTROL));
        gameCoordinator.transitionToNextState(new Move(homeCards.get(4), Card.Move.DEFENSE)
                , new Move(awayCards.get(4), Card.Move.ATTACK));

        var bestM = gameCoordinator.findBestMove(new Move(Card.getOfType("D"), Card.Move.getOfType("D")));

        //gameCoordinator.transitionToNextState(new Move(homeCards.get(1), Card.Move.ATTACK)
        //        , new Move(awayCards.get(3), Card.Move.DEFENSE));

        //var bestM = gameCoordinator.findBestMove(new Move(Card.getOfType("M"), Card.Move.getOfType("C")));
    }

}
