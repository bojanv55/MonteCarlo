package org.example.game;

import java.util.*;

public class BoardState {
    private List<Card> homeCards = new ArrayList<>();
    private List<Card> awayCards = new ArrayList<>();
    private Deque<TreeMove> roundToHomePlayedMove = new LinkedList<>();
    private Score score = new Score(0,0);
    private Move homeMove;
    private Move awayMove;
    private int homeVsAwayMoves;
    private Integer whichPlayerWonRound;
    private Integer whichPlayerWonPreviousRound;

    private Integer whichPlayerPlayedPreviousRound;
    private Integer whichPlayerPlaysRound;

    private static int homeSum;
    private static int awaySum;
    private final Random randomCard = new Random();
    private final Random randomMove = new Random();

    public BoardState(List<Card> homeCards, List<Card> awayCards) {
        this.homeCards = homeCards;
        this.awayCards = awayCards;

        homeSum = homeCards.stream().mapToInt(Card::sumOfValues).sum();
        awaySum = awayCards.stream().mapToInt(Card::sumOfValues).sum();
    }

    public boolean awayPlays(boolean awayPlayed){
        return this.whichPlayerWonRound==0 ? this.whichPlayerWonPreviousRound==-1 : this.whichPlayerWonRound==-1;
    }

    public BoardState(BoardState prevState, Move homeMove, Move awayMove){

        //if awayMove CardID is null that means that we create Board state for TreeNode expansion.
        //If it has some Id, that means that we create BoardState for random play

        this.homeCards = prevState.homeCards.stream().filter(c -> !c.equals(homeMove.getCard())).toList();
        this.awayCards = prevState.awayCards;

        this.roundToHomePlayedMove = new LinkedList<>(prevState.roundToHomePlayedMove);
        this.roundToHomePlayedMove.addLast(new TreeMove(homeMove, awayMove));

        this.score = prevState.getScore();
        this.homeMove = null;
        this.awayMove = null;
    }



    public BoardState(BoardState prevState, Move homeMove, Move awayMove, Object smth){

        //if awayMove CardID is null that means that we create Board state for TreeNode expansion.
        //If it has some Id, that means that we create BoardState for random play

        this.homeCards = prevState.homeCards.stream().filter(c -> !c.equals(homeMove.getCard())).toList();
        this.awayCards = prevState.awayCards.stream().filter(c -> !c.equals(awayMove.getCard())).toList();

        this.score = calculateScore(homeMove, awayMove, prevState.getScore());

        var playerWonScor = calculateScore(homeMove, awayMove, new Score(0,0));
        if(prevState.whichPlayerWonRound!=null){
            whichPlayerWonPreviousRound = prevState.whichPlayerWonRound;
        }
        whichPlayerWonRound = roundResult(playerWonScor);


        if(prevState.whichPlayerPlayedPreviousRound!=null){
            whichPlayerPlayedPreviousRound = prevState.whichPlayerPlaysRound;
        }
        whichPlayerPlaysRound = whichPlayerWonRound==0 ? whichPlayerPlayedPreviousRound : whichPlayerWonRound;

        this.homeMove = homeMove;
        this.awayMove = awayMove;
    }

    public Integer getWhichPlayerPlayedPreviousRound() {
        return whichPlayerPlayedPreviousRound;
    }

    public void setWhichPlayerPlayedPreviousRound(Integer whichPlayerPlayedPreviousRound) {
        this.whichPlayerPlayedPreviousRound = whichPlayerPlayedPreviousRound;
    }

    public Integer getWhichPlayerPlaysRound() {
        return whichPlayerPlaysRound;
    }

    public void setWhichPlayerPlaysRound(Integer whichPlayerPlaysRound) {
        this.whichPlayerPlaysRound = whichPlayerPlaysRound;
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

    public int getHomeVsAwayMoves() {
        return homeVsAwayMoves;
    }

    public Move getFirstHome(){
        return this.roundToHomePlayedMove.getFirst().homeTM();
    }

    public BoardState(BoardState prevState, Move homeMove, Move awayMove, List<Card> awayRemaining, Score score){

        //if awayMove CardID is null that means that we create Board state for TreeNode expansion.
        //If it has some Id, that means that we create BoardState for random play

        this.homeCards = prevState.homeCards.stream().filter(c -> !c.equals(homeMove.getCard())).toList();
        this.awayCards = awayRemaining.stream().filter(c -> !c.equals(awayMove.getCard())).toList();

        this.homeMove = null;
        this.awayMove = null;

        var playerWonScor = calculateScore(homeMove, awayMove, new Score(0,0));
        if(whichPlayerWonRound!=null){
            whichPlayerWonPreviousRound = whichPlayerWonRound;
        }
        whichPlayerWonRound = roundResult(playerWonScor);

        this.homeVsAwayMoves = prevState.homeVsAwayMoves + (whichPlayerWonRound==0? (whichPlayerWonPreviousRound!=null ? whichPlayerWonPreviousRound : 0) : whichPlayerWonRound);

        this.score = calculateScore(homeMove, awayMove, score);
    }

    public Move getHomeMove() {
        return homeMove;
    }

    public Move getAwayMove(){
        return awayMove;
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

    public boolean isAwayMove() {
        return this.whichPlayerPlaysRound==-1;
    }

    public int remainingCardsPerPlayer(){
        return Math.max(homeCards.size(), awayCards.size());    //can be awayCards also; home and away has same number of cards
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

    private Card.Move getRandomMove(){
        return Card.Move.values()[randomMove.nextInt(3)];   //which move we do with card - attack, control or defense
    }

    public Card selectAwayCard(List<Card> calculatedAway){
        return calculatedAway.get(randomCard.nextInt(calculatedAway.size()));
    }

    public Card selectHomeCard(){
        return homeCards.get(randomCard.nextInt(homeCards.size()));
    }

    public List<BoardState> getAllPossibleNextStates(Move awayMove, boolean isExpand){
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

        if(!roundToHomePlayedMove.isEmpty()){
            var roundIter = roundToHomePlayedMove.iterator();
            while (roundIter.hasNext()) {
                var ri = roundIter.next();
                var possibleSel = awayRemaining.stream().filter(x -> ri.awayTM().getCard().getType() == null || x.getType() == ri.awayTM().getCard().getType()).toList();
                Move homeMov = ri.homeTM();
                Move awayMov = new Move(selectAwayCard(possibleSel), homeMov.getMove().response());

                ri.adjustVisits(homeMov, awayMov);

                scr = calculateScore(homeMov, awayMov, scr);
                awayRemaining.remove(awayMov.getCard());    //remove used card

                if(awayRemaining.isEmpty()){
                    //final round
                    return new BoardState(this, homeMov, awayMov, awayRemaining, scr);
                }
            }
        }


        //Card homeCard = selectHomeCard();
        Card awayKard = selectAwayCard(awayRemaining);
        //Card.Move homeCardMoveType = getRandomMove();
        Move homeMove = new Move(selectHomeCard(), getRandomMove());
        Move awayMove = new Move(awayKard, homeMove.getMove().response());
        return new BoardState(this, homeMove, awayMove, awayRemaining, scr);
    }

    public List<BoardState> getAllPossibleNextMinMaxStates(Move awayMove) {
        List<BoardState> result = new ArrayList<>();
        for(Card homeCard : homeCards){
            for(Card awayCard: awayCards.stream().filter(x -> awayMove==null || x.getType()==awayMove.getCard().getType()).toList()){
                for (Card.Move move : Arrays.stream(Card.Move.values()).filter(x -> awayMove==null || x.response()==awayMove.getMove()).toList()) {
                    result.add(new BoardState(this, new Move(homeCard, move), new Move(awayCard, move.response()), null));
                }
            }
        }
        return result;
    }

    public void setPreviousRoundWinner(int i) {
        this.whichPlayerWonPreviousRound = i;
    }

    public void setWinner(int i) {
        this.whichPlayerWonRound = i;
    }
}
