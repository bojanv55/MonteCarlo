package me.vukas.game;

import java.util.Objects;

public class Move {
    private final Card card;
    private final Card.ValueType valueType;

    public Move(Card card, Card.ValueType valueType) {
        this.card = card;
        this.valueType = valueType;
    }

    public Card getCard() {
        return card;
    }

    public Card.ValueType getValueType() {
        return valueType;
    }

    //-1 if loss, 0 if draw, 1 if win
    public int compareTo(Move move){
        switch (move.getValueType()){
            case ATTACK:
                if(move.getCard().getAttack() < this.getCard().getDefense()){
                    return 1;
                }
                else if(move.getCard().getAttack() > this.getCard().getDefense()){
                    return -1;
                }
            case CONTROL:
                if(move.getCard().getControl() < this.getCard().getControl()){
                    return 1;
                }
                else if(move.getCard().getControl() > this.getCard().getControl()){
                    return -1;
                }
            case DEFENSE:
                if(move.getCard().getDefense() < this.getCard().getAttack()){
                    return 1;
                }
                else if(move.getCard().getDefense() > this.getCard().getAttack()){
                    return -1;
                }
        }
        return Integer.compare(this.getCard().sumOfValues(), move.getCard().sumOfValues());
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
