package org.example.game;

import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

public class Card {
    private String id;
    private Type type;
    private int attack;
    private int control;
    private int defense;

    public Card(){}

    public boolean isUnknown(){
        return id == null;
    }

    public Card(String id, Type type, int attack, int control, int defense) {
        this.id = id;
        this.type = type;
        this.attack = attack;
        this.control = control;
        this.defense = defense;
    }

    private Card(Type type){
        this.type = type;
    }

    static Map<Type, Card> emptyCards = Map.of(
            Type.FORWARD, new Card(Type.FORWARD),
            Type.INVINCIBLE, new Card(Type.INVINCIBLE),
            Type.DEFENDER, new Card(Type.DEFENDER),
            Type.GOALKEEPER, new Card(Type.GOALKEEPER),
            Type.MIDFIELDER, new Card(Type.MIDFIELDER)
            );

    public static Card getOfType(Type type){
        return emptyCards.get(type);
    }

    public static Card getOfType(String type){
        switch (type){
            //"F","M","D","G","I"
            case "F":
                return emptyCards.get(Type.FORWARD);
            case "M":
                return emptyCards.get(Type.MIDFIELDER);
            case "D":
                return emptyCards.get(Type.DEFENDER);
            case "G":
                return emptyCards.get(Type.GOALKEEPER);
            case "I":
                return emptyCards.get(Type.INVINCIBLE);
        }
        return null;
    }

    public Type getType() {
        return type;
    }

    public String getId() {
        return id;
    }

    public int sumOfValues(){
        return getAttack() + getControl() + getDefense();
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

    public enum Type {
        GOALKEEPER,
        DEFENDER,
        MIDFIELDER,
        FORWARD,
        INVINCIBLE
    }

    public enum Move {
        ATTACK,
        CONTROL,
        DEFENSE;

        static final Map<Move, Move> reponseMap = new EnumMap<>(Move.class);

        static {
            reponseMap.put(ATTACK, DEFENSE);
            reponseMap.put(CONTROL, CONTROL);
            reponseMap.put(DEFENSE, ATTACK);
        }

        public Move response() {
            return reponseMap.get(this);
        }

        public static Move getOfType(String type){
            switch (type){
                case "A":
                    return ATTACK;
                case "C":
                    return CONTROL;
                case "D":
                    return DEFENSE;
            }
            return null;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Card card = (Card) o;
        return id == card.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
