package org.example.game;

import java.util.Objects;

public class Move {
    private Card card;
    private Card.Move move;

    public Move(Card card, Card.Move move) {
        this.card = card;
        this.move = move;
    }

    public Card getCard() {
        return card;
    }

    public Card.Move getMove() {
        return move;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Move move1 = (Move) o;
        return Objects.equals(card, move1.card) && move == move1.move;
    }

    @Override
    public int hashCode() {
        return Objects.hash(card, move);
    }
}
