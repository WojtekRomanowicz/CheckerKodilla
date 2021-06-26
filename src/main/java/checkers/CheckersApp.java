package checkers;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;



//biale pionki zaczynają i zaczyna gracz




    public  class CheckersApp extends Application {

        public static final int TILE_SIZE =100 ;
        public static final int WIDTH = 8;
        public static final int HEIGHT = 8;



        Player player = new Player("gracz", 8);
        Player komp = new AI("Komputer", 8);
        Board board = new Board(player, komp);




        @Override
        public void start(Stage primaryStage) throws Exception {

           Group root = board.createContent();
           root.getChildren().addAll(board.createControls());

            Scene scene = new Scene(root, (WIDTH * TILE_SIZE), (HEIGHT * TILE_SIZE) );
            primaryStage.setTitle("CheckersApp");
            primaryStage.setScene(scene);
            primaryStage.show();




        }


        public static void main(String[] args) {
            launch(args);



        }
    }

