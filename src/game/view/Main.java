package game.view;

import game.engine.Game;
import game.engine.Role;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Glow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) {
        showStartScreen(stage);
    }

    private void showStartScreen(Stage stage) {
        VBox root = new VBox(22);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(50));
        root.setStyle("-fx-background-color: #070b14;");

       
        Label title = new Label("DoorDash");
        title.setStyle(
            "-fx-font-size: 56px; -fx-font-weight: bold;" +
            "-fx-text-fill: #00e5ff;");
        DropShadow glow = new DropShadow();
        glow.setColor(Color.web("#00e5ff"));
        glow.setRadius(28); glow.setSpread(0.3);
        title.setEffect(glow);

        Label subtitle = new Label("✦  Scare vs Laugh Touchdown  ✦");
        subtitle.setStyle("-fx-font-size: 17px; -fx-text-fill: #ea80fc;");

        Label choose = new Label("Choose your side to begin");
        choose.setStyle("-fx-font-size: 14px; -fx-text-fill: #546e7a;");

      
        Button scarerBtn = makeBtn("👻  SCARER", "#ff4500", "#cc3700");
        Button laugherBtn = makeBtn("😂  LAUGHER", "#00e676", "#00a050");

        HBox btnBox = new HBox(24, scarerBtn, laugherBtn);
        btnBox.setAlignment(Pos.CENTER);

       
        Label instructions = new Label(
            "📜  How to Play\n\n" +
            "🎲  Roll the dice each turn to move your monster\n" +
            "🚪  Land on doors to gain or lose energy\n" + 
            "🃏  Card cells draw special effect cards\n" +
            "👾  Monster cells trigger powerup battles\n" +
            "⚡  Use your powerup for a special ability\n" +
            "🏆  Reach position 99 with 1000+ energy to WIN!"
        );
        instructions.setStyle(
            "-fx-font-size: 13px; -fx-text-fill: #b0bec5;" +
            "-fx-background-color: #0c1220;" +
            "-fx-border-color: #1a2540;" +
            "-fx-border-radius: 8; -fx-background-radius: 8;" +
            "-fx-padding: 18;");
        instructions.setTextAlignment(TextAlignment.LEFT);
        instructions.setMaxWidth(480);

        scarerBtn.setOnAction(e -> startGame(stage, Role.SCARER));
        laugherBtn.setOnAction(e -> startGame(stage, Role.LAUGHER));

        root.getChildren().addAll(title, subtitle, choose, btnBox, instructions);

        Scene scene = new Scene(root, 620, 560);
        stage.setTitle("DoorDash");
        stage.setScene(scene);
        stage.show();
    }

    private Button makeBtn(String text, String bg, String hover) {
        Button btn = new Button(text);
        String style =
            "-fx-font-size: 17px; -fx-font-weight: bold;" +
            "-fx-background-color: " + bg + "; -fx-text-fill: white;" +
            "-fx-padding: 14 36; -fx-cursor: hand;" +
            "-fx-background-radius: 8;";
        btn.setStyle(style);
        Glow g = new Glow(0.4);
        btn.setEffect(g);
        btn.setOnMouseEntered(e -> btn.setStyle(style.replace(bg, hover)));
        btn.setOnMouseExited(e  -> btn.setStyle(style));
        return btn;
    }

    private void startGame(Stage stage, Role role) {
        try {
            Game game = new Game(role);
            new GameView(game, stage).show();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Error starting game: " + e.getMessage());
            alert.show();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
  
}

/*
 * AI USAGE:
 * AI helped generate the initial JavaFX start screen structure
 * AI helped with layout using VBox, buttons, and scene setup
 * AI helped with hover effects on buttons and glow effects
 * AI helped connect the start screen to the game engine via Game constructor
 */