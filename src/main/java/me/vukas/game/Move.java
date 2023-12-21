package me.vukas.game;

import java.util.Objects;

public record Move(Card card, Card.ValueType valueType) {
    //-1 if loss, 0 if draw, 1 if win
    public int compareTo(Move move) {
        switch (move.valueType()) {
            case ATTACK:
                if (move.card().attack() < this.card().defense()) {
                    return 1;
                } else if (move.card().attack() > this.card().defense()) {
                    return -1;
                }
                break;
            case CONTROL:
                if (move.card().control() < this.card().control()) {
                    return 1;
                } else if (move.card().control() > this.card().control()) {
                    return -1;
                }
                break;
            case DEFENSE:
                if (move.card().defense() < this.card().attack()) {
                    return 1;
                } else if (move.card().defense() > this.card().attack()) {
                    return -1;
                }
                break;
        }
        return Integer.compare(this.card().sumOfValues(), move.card().sumOfValues());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Move move = (Move) o;
        return Objects.equals(card, move.card) && valueType == move.valueType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(card, valueType);
    }
}
