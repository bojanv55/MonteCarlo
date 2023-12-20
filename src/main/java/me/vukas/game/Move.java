package me.vukas.game;

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
            case CONTROL:
                if (move.card().control() < this.card().control()) {
                    return 1;
                } else if (move.card().control() > this.card().control()) {
                    return -1;
                }
            case DEFENSE:
                if (move.card().defense() < this.card().attack()) {
                    return 1;
                } else if (move.card().defense() > this.card().attack()) {
                    return -1;
                }
        }
        return Integer.compare(this.card().sumOfValues(), move.card().sumOfValues());
    }
}
