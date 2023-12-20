package me.vukas.game;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Card {
    private final String id;
    private final CardType cardType;
    private final int attack;
    private final int control;
    private final int defense;

    public Card(String id, CardType cardType, int attack, int control, int defense) {
        this.id = id;
        this.cardType = cardType;
        this.attack = attack;
        this.control = control;
        this.defense = defense;
    }

    public String getId() {
        return id;
    }

    public CardType getCardType() {
        return cardType;
    }

    public int getAttack() {
        return attack;
    }

    public int getControl() {
        return control;
    }

    public int getDefense() {
        return defense;
    }

    public int sumOfValues(){
        return getAttack() + getControl() + getDefense();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Card card = (Card) o;
        return Objects.equals(id, card.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public enum CardType {
        GOALKEEPER("G"),
        DEFENDER("D"),
        MIDFIELDER("M"),
        FORWARD("F"),
        INVINCIBLE("I");

        private static final Map<String, CardType> shortNameMap;

        static {
            shortNameMap = Arrays.stream(CardType.values())
                    .collect(Collectors.toMap(x -> x.shortName, Function.identity()));
        }

        private final String shortName;

        public String getShortName() {
            return shortName;
        }

        CardType(String shortName) {
            this.shortName = shortName;
        }

        public static CardType ofShortName(String shortName){
            return shortNameMap.get(shortName);
        }
    }

    public enum ValueType {
        ATTACK("A"),
        CONTROL("C"),
        DEFENSE("D");

        private static final Map<ValueType, ValueType> reponseMap = new EnumMap<>(ValueType.class);
        private static final Map<String, ValueType> shortNameMap;

        static {
            reponseMap.put(ATTACK, DEFENSE);
            reponseMap.put(CONTROL, CONTROL);
            reponseMap.put(DEFENSE, ATTACK);

            shortNameMap = Arrays.stream(ValueType.values())
                    .collect(Collectors.toMap(x -> x.shortName, Function.identity()));
        }

        private final String shortName;

        ValueType(String shortName) {
            this.shortName = shortName;
        }

        public ValueType response() {
            return reponseMap.get(this);
        }

        public static ValueType ofShortName(String shortName){
            return shortNameMap.get(shortName);
        }
    }
}
