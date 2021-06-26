package checkers;

import java.util.ArrayList;
import java.util.List;

public class Player {

    String name;
    int score;
    List<Piece> pieces = new ArrayList<>();

    public Player(String name, int score) {
        this.name = name;
        this.score = score;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }


    public void addPiece(Piece piece){
        pieces.add(piece);
    }

    public void removePiece(Piece piece){
        pieces.remove(piece);
    }

    public Piece getPiece(int a){

        return pieces.get(a);
    }
}
