package checkers;

import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.text.Text;


import java.util.Random;

import static checkers.PieceType.RED;
import static checkers.PieceType.WHITE;

public class Board {
    public static final int TILE_SIZE = 100;
    public static final int WIDTH = 8;
    public static final int HEIGHT = 8;

    private Tile[][] board = new Tile[WIDTH][HEIGHT];

    private Group tileGroup = new Group();
    private Group pieceGroup = new Group();
    private Player gracz;
    private Player komp;
    private int tura = 0;
    private boolean playerTurn = true;
    private boolean aiTurn = true;

    public Board(Player gracz, Player komp) {
        this.gracz = gracz;
        this.komp = komp;
    }

    public Group createContent() {
        Group root = new Group();

        root.getChildren().addAll(tileGroup, pieceGroup);


        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                Tile tile = new Tile((x + y) % 2 == 0, x, y);
                board[x][y] = tile;

                tileGroup.getChildren().add(tile);

                Piece piece = null;

                if (y <= 2 && (x + y) % 2 != 0) {
                    piece = makePiece(RED, x, y);
                    komp.addPiece(piece);
                }

                if (y >= 5 && (x + y) % 2 != 0) {
                    piece = makePiece(WHITE, x, y);
                    gracz.addPiece(piece);
                }

                if (piece != null) {
                    tile.setPiece(piece);
                    pieceGroup.getChildren().add(piece);
                }
            }
        }



        return root;
    }

    public Group createControls(){

        Group controls = new Group();
        Button nowaGra = new Button("Nowa gra");
        Button zapiszGre = new Button("Zapisz gre");
        Button wczytajGre = new Button(" Wczytaj gre");
        Button wyniki = new Button("Wyniki");

        controls.getChildren().addAll(nowaGra, zapiszGre, wczytajGre, wyniki );

        for( int i = 0; i < controls.getChildren().size(); i++) {
            controls.getChildren().get(i).setStyle("-fx-background-color: darkslateblue; -fx-text-fill: white;");
            controls.getChildren().get(i).setScaleX(2);
            controls.getChildren().get(i).setScaleY(2);
            controls.getChildren().get(i).setTranslateX((i * 150) + TILE_SIZE/2);
        }

        nowaGra.setOnMouseClicked((e) -> {
            newGame();

        });
        zapiszGre.setOnMouseClicked((e) -> {
            saveGame();
        });

        wczytajGre.setOnMouseClicked((e) -> {
            loadGame();
        });

        wyniki.setOnMouseClicked((e) -> {
            results();
        });

        return controls;
    }

    private void newGame(){
        System.out.println("Nowa gra");


    }

    private void saveGame(){
        System.out.println("zapisz gre");
    }
    private void loadGame(){
        System.out.println("wczytaj gre");

    }
    private void results(){
        System.out.println("wyniki");
    }

    private MoveResult tryMove(Piece piece, int newX, int newY) {
        if (board[newX][newY].hasPiece() || (newX + newY) % 2 == 0) {
            return new MoveResult(MoveType.NONE);
        }

        if((playerTurn && piece.getType() == WHITE) || (!playerTurn && piece.getType() == RED)) {

            int x0 = toBoard(piece.getOldX());
            int y0 = toBoard(piece.getOldY());

            if (Math.abs(newX - x0) == 1 && newY - y0 == piece.getType().moveDir) {

                return new MoveResult(MoveType.NORMAL);
            } else if (Math.abs(newX - x0) == 2 && newY - y0 == piece.getType().moveDir * 2) {

                int x1 = x0 + (newX - x0) / 2;
                int y1 = y0 + (newY - y0) / 2;

                if (board[x1][y1].hasPiece() && board[x1][y1].getPiece().getType() != piece.getType()) {
                    return new MoveResult(MoveType.KILL, board[x1][y1].getPiece());
                }
            }
        }


        return new MoveResult(MoveType.NONE);
    }

    private int toBoard(double pixel) {
        return (int)(pixel + TILE_SIZE / 2) / TILE_SIZE;
    }



    private Piece makePiece(PieceType type, int x, int y) {
        Piece piece = new Piece(type, x, y);

        piece.setOnMouseReleased(e -> {
            int newX = toBoard(piece.getLayoutX());
            int newY = toBoard(piece.getLayoutY());

            System.out.println("Wspolrzedne x: " + newX + "y: " + newX);

            MoveResult result;

            if (newX < 0 || newY < 0 || newX >= WIDTH || newY >= HEIGHT) {
                result = new MoveResult(MoveType.NONE);
            } else {
                result = tryMove(piece, newX, newY);
            }

            int x0 = toBoard(piece.getOldX());
            int y0 = toBoard(piece.getOldY());

            switch (result.getType()) {
                case NONE:
                    piece.abortMove();
                    break;
                case NORMAL:
                    piece.move(newX, newY);
                    board[x0][y0].setPiece(null);
                    board[newX][newY].setPiece(piece);
                    playerTurn = false;
                    break;
                case KILL:
                    piece.move(newX, newY);
                    board[x0][y0].setPiece(null);
                    board[newX][newY].setPiece(piece);

                    Piece otherPiece = result.getPiece();
                    board[toBoard(otherPiece.getOldX())][toBoard(otherPiece.getOldY())].setPiece(null);
                    pieceGroup.getChildren().remove(otherPiece);
                    break;
            }
            if(!playerTurn){
                System.out.println("Tutaj moze sie ruszac komputer");
                 aiMove();
            }

        });



        return piece;
    }

    private void aiMove() {
        int liczbaProb = 0;

        while (!playerTurn) {
            Piece piece = getAiPiece();

            int xDir;
            Random rand = new Random();
            int a = rand.nextInt(100);
            if(a > 45){
                xDir = 1;
            }else {
                xDir = -1;
            }


            System.out.println("Bierzace wysolrzedne x: " + piece.getOldX() / TILE_SIZE +
                    "y: " + piece.getOldY() / TILE_SIZE );
            System.out.println("Wspolczynnik xDir: " + xDir);

            int newX = (int) ((piece.getOldX() / TILE_SIZE) + xDir);
            int newY = (int)piece.getOldY() / TILE_SIZE + 1;

            if(newX < 0){ newX = 1; }
            if(newX > 7){ newX = 6; }

            System.out.println("Nowe wspolrzedne x: " + newX + "y: " + newY);

            if(board[newX][newY].hasPiece() && board[newX][newY].getPiece().getType() != piece.getType()){
                newX = newX + xDir;
                newY = newY + 1;
            }

            MoveResult result;

            if (newX < 0 || newY < 0 || newX >= WIDTH || newY >= HEIGHT) {
                result = new MoveResult(MoveType.NONE);
            } else {
                result = tryMove(piece, newX, newY);
            }

            int x0 = toBoard(piece.getOldX());
            int y0 = toBoard(piece.getOldY());

            switch (result.getType()) {
                case NONE:
                    piece.abortMove();
                    break;
                case NORMAL:
                    piece.move(newX, newY);
                    board[x0][y0].setPiece(null);
                    board[newX][newY].setPiece(piece);
                    playerTurn = true;
                    tura++;
                    System.out.println("Tura:" + tura);
                    break;
                case KILL:
                    piece.move(newX, newY);
                    board[x0][y0].setPiece(null);
                    board[newX][newY].setPiece(piece);

                    Piece otherPiece = result.getPiece();
                    board[toBoard(otherPiece.getOldX())][toBoard(otherPiece.getOldY())].setPiece(null);
                    pieceGroup.getChildren().remove(otherPiece);
                    break;
            }
           liczbaProb++;
            if(liczbaProb == 20){
                playerTurn = true;
            }
        }

    }

    private Piece getAiPiece(){

        Random rand = new Random();
        int pieceNumber;
        boolean hasPiece = false;
        Piece piece = null;

        while(!hasPiece) {

            pieceNumber = rand.nextInt(11);
            piece = komp.getPiece(pieceNumber);
            int x = (int) piece.getOldX() / TILE_SIZE;
            int y = (int) piece.getOldY() / TILE_SIZE;
            hasPiece = board[x][y].hasPiece();
            System.out.println(" ");
            System.out.println(hasPiece);
            System.out.println("AI wybral pionek nr: " + pieceNumber);
        }
        return piece;
    }


}
