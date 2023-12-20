package me.vukas.game;

public class UnknownMove {
    private final Card.CardType cardType;
    private final Card.ValueType valueType;

    public UnknownMove(Card.CardType cardType, Card.ValueType valueType) {
        this.cardType = cardType;
        this.valueType = valueType;
    }

    public Card.CardType getCardType() {
        return cardType;
    }

    public Card.ValueType getValueType() {
        return valueType;
    }
}
