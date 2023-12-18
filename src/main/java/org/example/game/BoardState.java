package org.example.game;

import javax.security.auth.kerberos.KerberosTicket;
import java.util.*;

public class BoardState {
    private List<Card> homeCards = new ArrayList<>();
    private List<Card> awayCards = new ArrayList<>();
    private Deque<Move> roundToHomePlayedMove = new LinkedList<>();
    private Score score = new Score(0,0);
    private Move homeMove;
    private Move awayMove;
    private int whichPlayerWonRound;
    private static int homeSum;
    private static int awaySum;
    private static final Random randomCard = new Random();
    private static final Random randomMove = new Random();
    private static final Random randomRemover = new Random();

    public BoardState(List<Card> homeCards, List<Card> awayCards) {
        this.homeCards = homeCards;
        this.awayCards = awayCards;

        homeSum = homeCards.stream().mapToInt(Card::sumOfValues).sum();
        awaySum = awayCards.stream().mapToInt(Card::sumOfValues).sum();
    }

    public BoardState(BoardState prevState, Move homeMove, Move awayMove){

        //if awayMove CardID is null that means that we create Board state for TreeNode expansion.
        //If it has some Id, that means that we create BoardState for random play

        this.homeCards = prevState.homeCards.stream().filter(c -> !c.equals(homeMove.getCard())).toList();
        this.awayCards = prevState.awayCards;

        this.roundToHomePlayedMove = new LinkedList<>(prevState.roundToHomePlayedMove);
        this.roundToHomePlayedMove.addLast(homeMove);

        this.score = prevState.getScore();
        this.homeMove = homeMove;
        this.awayMove = awayMove;
    }

    public Score getScore() {
        return this.score;
    }

    public List<Card> getAwayCards() {
        return awayCards;
    }

    public int getWhichPlayerWonRound() {
        return whichPlayerWonRound;
    }

    public BoardState(BoardState prevState, Move homeMove, Move awayMove, List<Card> awayRemaining, Score score){

        //if awayMove CardID is null that means that we create Board state for TreeNode expansion.
        //If it has some Id, that means that we create BoardState for random play

        this.homeCards = prevState.homeCards.stream().filter(c -> !c.equals(homeMove.getCard())).toList();
        this.awayCards = awayRemaining.stream().filter(c -> !c.equals(awayMove.getCard())).toList();

        this.homeMove = null;
        this.awayMove = null;

        var playerWonScor = calculateScore(homeMove, awayMove, new Score(0,0));
        whichPlayerWonRound = roundResult(playerWonScor);

        this.score = calculateScore(homeMove, awayMove, score);
    }

    public Move getHomeMove() {
        return homeMove;
    }

    public Score calculateScore(Move homeMove, Move awayMove, Score currentScore){
        switch (homeMove.getMove()){
            case ATTACK:
                if(homeMove.getCard().getAttack() > awayMove.getCard().getDefense()){
                    return new Score(currentScore.roundsHomeWon()+1, currentScore.roundsAwayWon());
                }
                else if(homeMove.getCard().getAttack() < awayMove.getCard().getDefense()){
                    return new Score(currentScore.roundsHomeWon(), currentScore.roundsAwayWon()+1);
                }
                else {
                    if(homeMove.getCard().sumOfValues() > awayMove.getCard().sumOfValues()){
                        return new Score(currentScore.roundsHomeWon()+1, currentScore.roundsAwayWon());
                    }
                    else if(homeMove.getCard().sumOfValues() < awayMove.getCard().sumOfValues()){
                        return new Score(currentScore.roundsHomeWon(), currentScore.roundsAwayWon()+1);
                    }
                    else{
                        //draw
                        return currentScore;
                    }
                }
            case CONTROL:
                if(homeMove.getCard().getControl() > awayMove.getCard().getControl()){
                    return new Score(currentScore.roundsHomeWon()+1, currentScore.roundsAwayWon());
                }
                else if(homeMove.getCard().getControl() < awayMove.getCard().getControl()){
                    return new Score(currentScore.roundsHomeWon(), currentScore.roundsAwayWon()+1);
                }
                else {
                    if(homeMove.getCard().sumOfValues() > awayMove.getCard().sumOfValues()){
                        return new Score(currentScore.roundsHomeWon()+1, currentScore.roundsAwayWon());
                    }
                    else if(homeMove.getCard().sumOfValues() < awayMove.getCard().sumOfValues()){
                        return new Score(currentScore.roundsHomeWon(), currentScore.roundsAwayWon()+1);
                    }
                    else{
                        //draw
                        return currentScore;
                    }
                }
            case DEFENSE:
                if(homeMove.getCard().getDefense() > awayMove.getCard().getAttack()){
                    return new Score(currentScore.roundsHomeWon()+1, currentScore.roundsAwayWon());
                }
                else if(homeMove.getCard().getDefense() < awayMove.getCard().getAttack()){
                    return new Score(currentScore.roundsHomeWon(), currentScore.roundsAwayWon()+1);
                }
                else {
                    if(homeMove.getCard().sumOfValues() > awayMove.getCard().sumOfValues()){
                        return new Score(currentScore.roundsHomeWon()+1, currentScore.roundsAwayWon());
                    }
                    else if(homeMove.getCard().sumOfValues() < awayMove.getCard().sumOfValues()){
                        return new Score(currentScore.roundsHomeWon(), currentScore.roundsAwayWon()+1);
                    }
                    else{
                        //draw
                        return currentScore;
                    }
                }
        }
        return null;    //shouldnt happen
    }

    public int remainingCardsPerPlayer(){
        return homeCards.size();    //can be awayCards also; home and away has same number of cards
    }

    public boolean isGameOver(){
        return (Math.abs(score.roundsAwayWon() - score.roundsHomeWon()) > remainingCardsPerPlayer()) || remainingCardsPerPlayer()==0;
    }

    private int resultBySum(){
        return (homeSum < awaySum) ? 1 : ((homeSum > awaySum) ? -1 : 0);
    }

    public int result(){
        return (score.roundsHomeWon() > score.roundsAwayWon()) ? 1 : ((score.roundsHomeWon() < score.roundsAwayWon()) ? -1 : resultBySum());
    }

    public int roundResult(Score score){
        return (score.roundsHomeWon() > score.roundsAwayWon()) ? 1 : ((score.roundsHomeWon() < score.roundsAwayWon()) ? -1 : 0);
    }

    private static Card.Move getRandomMove(){
        return Card.Move.values()[randomMove.nextInt(3)];   //which move we do with card - attack, control or defense
    }

    public Card selectAwayCard(List<Card> calculatedAway){
        return calculatedAway.get(randomCard.nextInt(calculatedAway.size()));
    }

    public Card selectHomeCard(){
        return homeCards.get(randomCard.nextInt(homeCards.size()));
    }

    public List<BoardState> getAllPossibleNextStates(Move awayMove){
        List<BoardState> result = new ArrayList<>();
        for(Card homeCard : homeCards){
            if(awayMove==null) {    //check all possibilities
                for (Card.Move move : Card.Move.values()) {
                    switch (move) {
                        case ATTACK:
                            result.add(new BoardState(this, new Move(homeCard, Card.Move.ATTACK), new Move(new Card(), Card.Move.DEFENSE)));
                            break;
                        case CONTROL:
                            result.add(new BoardState(this, new Move(homeCard, Card.Move.CONTROL), new Move(new Card(), Card.Move.CONTROL)));
                            break;
                        case DEFENSE:
                            result.add(new BoardState(this, new Move(homeCard, Card.Move.DEFENSE), new Move(new Card(), Card.Move.ATTACK)));
                            break;
                    }
                }
            }
            else{   //we exactly know which card type was played by opponent
                result.add(new BoardState(this, new Move(homeCard, awayMove.getMove().response()), awayMove));
            }
        }
        return result;
    }

    public BoardState randomPlay() {

        Score scr = this.score;
        List<Card> awayRemaining = new ArrayList<>(this.awayCards);

        if(!roundToHomePlayedMove.isEmpty() && this.awayMove==null){
            //means we are in the leaf node, so adjust away cards to match randomly current awayTypeToNumberOfPlayedCards MAP
            //before starting the simulation expansion
            //we will adjust number of away remaining cards, by playing random moves from away matching already played roundToHomePlayedMove

            Set<Integer> removeIndexes = new HashSet<>();

            int removeTotalElements = roundToHomePlayedMove.size();
            while (removeIndexes.size() < removeTotalElements) {
                int randomValue = randomRemover.nextInt(awayRemaining.size()); //we can remove any of the away cards, but not twice
                removeIndexes.add(randomValue);
            }

            var remIndIter = removeIndexes.iterator();
            var roundIter = roundToHomePlayedMove.iterator();
            while (remIndIter.hasNext() && roundIter.hasNext()){
                //go move by move and simulate some game up to this point
                int remIdx = remIndIter.next();
                Move homeMov = roundIter.next();
                Move awayMov = new Move(awayRemaining.get(remIdx), homeMov.getMove().response());
                scr = calculateScore(homeMov, awayMov, scr);
            }

            List<Card> awayRemainingNew = new ArrayList<>(this.awayCards);
            for(int remIdx : removeIndexes){
                awayRemainingNew.remove(awayRemaining.get(remIdx));
            }
            awayRemaining = awayRemainingNew;
        }

        List<Card> awayPossibleMoves = new ArrayList<>(awayRemaining);
        if(awayMove!=null && awayMove.getCard().isUnknown() && awayMove.getCard().getType()!=null){
            awayPossibleMoves = awayRemaining.stream().filter(x -> x.getType()==awayMove.getCard().getType()).toList();
        }

        //Card homeCard = selectHomeCard();
        Card awayCard = selectAwayCard(awayPossibleMoves);
        Card.Move homeCardMoveType = getRandomMove();
        Move homeMove = this.homeMove == null ? new Move(selectHomeCard(), getRandomMove()) : this.homeMove;
        Move awayMove = this.awayMove == null ? new Move(selectAwayCard(awayPossibleMoves), homeMove.getMove().response()) : new Move(awayCard, homeMove.getMove().response());
        return new BoardState(this, homeMove, awayMove, awayRemaining, scr);
    }
}
