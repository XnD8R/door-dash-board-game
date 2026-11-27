package game.view;

import game.engine.Board;
import game.engine.Game;
import game.engine.Role;
import game.engine.cards.Card;
import game.engine.monsters.Monster;
import game.engine.monsters.Dasher;
import game.engine.monsters.Dynamo;
import game.engine.monsters.MultiTasker;
import game.engine.monsters.Schemer;
import game.engine.cells.Cell;
import game.engine.cells.DoorCell;
import game.engine.cells.MonsterCell;
import game.engine.cells.CardCell;
import game.engine.cells.ConveyorBelt;
import game.engine.cells.ContaminationSock;
import game.engine.exceptions.*;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Glow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.util.Random;

public class GameView {

    private static final String BG_MAIN            = "#070b14";
    private static final String BG_PANEL           = "#0c1220";
    private static final String BG_CARD            = "#101828";
    private static final String DOOR_SCARER_BG     = "#1a0505";
    private static final String DOOR_SCARER_BORDER = "#ff4500";
    private static final String DOOR_LAUGHER_BG    = "#041a08";
    private static final String DOOR_LAUGHER_BORDER= "#00e676";
    private static final String DOOR_USED_BG       = "#111518";
    private static final String DOOR_USED_BORDER   = "#37474f";
    private static final String MONSTER_BG         = "#1a1000";
    private static final String MONSTER_BORDER     = "#ffc400";
    private static final String CARD_BG            = "#12021a";
    private static final String CARD_BORDER        = "#d500f9";
    private static final String BELT_BG            = "#001a2e";
    private static final String BELT_BORDER        = "#00b0ff";
    private static final String SOCK_BG            = "#1a0010";
    private static final String SOCK_BORDER        = "#ff1744";
    private static final String NORMAL_BG          = "#0a1018";
    private static final String NORMAL_BORDER      = "#1a2540";
    private static final String PLAYER_BG          = "#002a2a";
    private static final String PLAYER_BORDER      = "#00e5ff";
    private static final String PLAYER_TEXT        = "#00e5ff";
    private static final String OPP_BG             = "#1a0a2a";
    private static final String OPP_BORDER         = "#ea80fc";
    private static final String OPP_TEXT           = "#ea80fc";
    private static final String GOLD               = "#ffc400";
    private static final String TEAL               = "#00e5ff";
    private static final String MAGENTA            = "#ea80fc";
    private static final String WHITE              = "#e8f4f8";
    private static final String MUTED              = "#546e7a";
    private static final String[] DICE_FACES       = {"⚀","⚁","⚂","⚃","⚄","⚅"};

    private Game     game;
    private Stage    stage;
    private GridPane boardGrid;
    private Label    statusLabel;
    private Label    playerInfoLabel;
    private Label    opponentInfoLabel;
    private Label    playerAvatarLabel;
    private Label    opponentAvatarLabel;
    private VBox       logBox;      // holds one Label per log entry
    private ScrollPane logScroll;   // makes the log scrollable
    private Label    diceLabel;
    private Button   rollBtn;
    private Button   powerupBtn;
    private boolean  opponentPlaying = false;

    public GameView(Game game, Stage stage) {
        this.game  = game;
        this.stage = stage;
    }

    public void show() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + BG_MAIN + ";");

        statusLabel = new Label();
        statusLabel.setMaxWidth(Double.MAX_VALUE);
        statusLabel.setAlignment(Pos.CENTER);
        statusLabel.setPadding(new Insets(12));
        root.setTop(statusLabel);

        boardGrid = new GridPane();
        boardGrid.setHgap(3);
        boardGrid.setVgap(3);
        boardGrid.setPadding(new Insets(10));
        boardGrid.setStyle("-fx-background-color: " + BG_MAIN + ";");

        // Split the board area into 10 equal columns and 10 equal rows, so the
        // board always stretches to fill the whole space (no scrolling needed).
        for (int i = 0; i < 10; i++) {
            ColumnConstraints cc = new ColumnConstraints();
            cc.setPercentWidth(10);
            cc.setHgrow(Priority.ALWAYS);
            boardGrid.getColumnConstraints().add(cc);

            RowConstraints rc = new RowConstraints();
            rc.setPercentHeight(10);
            rc.setVgrow(Priority.ALWAYS);
            boardGrid.getRowConstraints().add(rc);
        }
        buildBoard();

        root.setCenter(boardGrid);
        root.setRight(buildRightPanel());
        root.setBottom(buildLegend());

        updateUI();

        Scene scene = new Scene(root, 1160, 790);
        stage.setScene(scene);
        stage.setTitle("DooR DasH  ✦  Aurora Edition");
        stage.show();
    }

    private ScrollPane buildRightPanel() {
        VBox panel = new VBox(10);
        panel.setPadding(new Insets(12));
        panel.setStyle("-fx-background-color: " + BG_PANEL + ";");
        panel.setPrefWidth(275);

        Label playerTitle = makeTitle("▶  YOUR MONSTER", PLAYER_TEXT);
        playerAvatarLabel = new Label();
        playerAvatarLabel.setStyle("-fx-font-size: 40px;");
        playerAvatarLabel.setAlignment(Pos.CENTER);
        playerInfoLabel = makeInfoLabel(PLAYER_BORDER);
        VBox playerCard = new VBox(4, playerAvatarLabel, playerInfoLabel);
        playerCard.setStyle(
            "-fx-background-color: " + BG_CARD + ";" +
            "-fx-border-color: " + PLAYER_BORDER + ";" +
            "-fx-border-width: 1; -fx-border-radius: 6;" +
            "-fx-background-radius: 6; -fx-padding: 8;");

        Label oppTitle = makeTitle("▶  OPPONENT", OPP_TEXT);
        opponentAvatarLabel = new Label();
        opponentAvatarLabel.setStyle("-fx-font-size: 40px;");
        opponentAvatarLabel.setAlignment(Pos.CENTER);
        opponentInfoLabel = makeInfoLabel(OPP_BORDER);
        VBox oppCard = new VBox(4, opponentAvatarLabel, opponentInfoLabel);
        oppCard.setStyle(
            "-fx-background-color: " + BG_CARD + ";" +
            "-fx-border-color: " + OPP_BORDER + ";" +
            "-fx-border-width: 1; -fx-border-radius: 6;" +
            "-fx-background-radius: 6; -fx-padding: 8;");

        diceLabel = new Label("🎲  Roll to start!");
        diceLabel.setMaxWidth(Double.MAX_VALUE);
        diceLabel.setAlignment(Pos.CENTER);
        diceLabel.setStyle(
            "-fx-font-size: 22px; -fx-text-fill: " + GOLD + ";" +
            "-fx-font-weight: bold; -fx-background-color: " + BG_CARD + ";" +
            "-fx-padding: 10; -fx-border-color: " + GOLD + ";" +
            "-fx-border-radius: 6; -fx-background-radius: 6;");

        powerupBtn = makeButton("⚡  Use Powerup  (500 energy)", GOLD, "#e65100");
        rollBtn    = makeButton("🎲  Roll Dice", TEAL, "#008fa3");
        rollBtn.setStyle(rollBtn.getStyle() + "-fx-font-size: 17px;");
        rollBtn.setEffect(new Glow(0.5));

        Label logTitle = new Label("  📜  GAME LOG");
        logTitle.setStyle(
            "-fx-text-fill: " + MUTED + "; -fx-font-weight: bold;" +
            "-fx-font-size: 11px; -fx-padding: 6 0 2 0;");

        // Game log: every message is its own Label inside a scrollable VBox,
        // so older messages stay available instead of being cut off.
        logBox = new VBox(4);
        logBox.setPadding(new Insets(8));
        logBox.setStyle("-fx-background-color: #060c16;");

        logScroll = new ScrollPane(logBox);
        logScroll.setFitToWidth(true);
        logScroll.setPrefHeight(180);
        logScroll.setMinHeight(120);
        logScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        logScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        logScroll.setStyle(
            "-fx-background: #060c16; -fx-background-color: #060c16;" +
            "-fx-border-color: #1a2540; -fx-border-radius: 4; -fx-background-radius: 4;");
        // Whenever a new message makes the log taller, scroll down to show it.
        logBox.heightProperty().addListener((obs, oldH, newH) -> logScroll.setVvalue(1.0));
        log("Game started!");

        // Stop the monster cards from being squashed (that cut off the
        // Energy / Position / Shield lines before).
        playerCard.setMinHeight(Region.USE_PREF_SIZE);
        oppCard.setMinHeight(Region.USE_PREF_SIZE);

        panel.getChildren().addAll(
            playerTitle, playerCard,
            oppTitle, oppCard,
            diceLabel, powerupBtn, rollBtn,
            logTitle, logScroll
        );

        powerupBtn.setOnAction(e -> handlePowerup());
        rollBtn.setOnAction(e -> handleRoll());

        // The whole side panel scrolls too, so nothing is hidden on smaller screens.
        ScrollPane panelScroll = new ScrollPane(panel);
        panelScroll.setFitToWidth(true);
        panelScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        panelScroll.setPrefWidth(290);
        panelScroll.setStyle(
            "-fx-background: " + BG_PANEL + "; -fx-background-color: " + BG_PANEL + ";");
        return panelScroll;
    }

    private HBox buildLegend() {
        HBox legend = new HBox(8);
        legend.setPadding(new Insets(7, 12, 7, 12));
        legend.setAlignment(Pos.CENTER_LEFT);
        legend.setStyle("-fx-background-color: " + BG_PANEL + ";");
        legend.getChildren().addAll(
            legendChip("🚪","Scarer Door",  DOOR_SCARER_BORDER),
            legendChip("🚪","Laugher Door", DOOR_LAUGHER_BORDER),
            legendChip("🚪","Used Door",    DOOR_USED_BORDER),
            legendChip("👾","Monster Cell", MONSTER_BORDER),
            legendChip("🃏","Card Cell",    CARD_BORDER),
            legendChip("➡️","Conveyor",     BELT_BORDER),
            legendChip("🧦","Sock",         SOCK_BORDER),
            legendChip("P", "You",          PLAYER_BORDER),
            legendChip("O", "Opponent",     OPP_BORDER)
        );
        return legend;
    }

    private HBox legendChip(String sym, String lbl, String color) {
        Label s = new Label(sym);
        s.setStyle("-fx-font-size: 12px;");
        Label t = new Label(lbl);
        t.setStyle("-fx-text-fill:"+color+"; -fx-font-size:10px; -fx-font-weight:bold;");
        HBox box = new HBox(4, s, t);
        box.setAlignment(Pos.CENTER_LEFT);
        box.setStyle(
            "-fx-background-color:"+BG_CARD+";" +
            "-fx-border-color:"+color+"; -fx-border-width:1;" +
            "-fx-padding:4 8; -fx-background-radius:4; -fx-border-radius:4;");
        return box;
    }

    private void buildBoard() {
        boardGrid.getChildren().clear();
        for (int index = 0; index < 100; index++) {
            int row        = index / 10;
            int col        = index % 10;
            int displayCol = (row % 2 == 0) ? col : (9 - col);
            int displayRow = 9 - row;
            Cell cell = game.getBoard().getBoardCells()[row][displayCol];
            boardGrid.add(createCellBox(index, cell), displayCol, displayRow);
        }
    }

    private StackPane createCellBox(int index, Cell cell) {
        // The cell is a StackPane: the number sits in the top-left corner and
        // the icon/energy/monster sit in the middle, so nothing overlaps or
        // gets pushed outside the border.
        StackPane cellPane = new StackPane();
        cellPane.setPrefSize(72, 63);
        // Let each cell grow/shrink to fill its slot in the grid.
        cellPane.setMinSize(0, 0);
        cellPane.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        boolean isPlayer   = game.getPlayer().getPosition()   == index;
        boolean isOpponent = game.getOpponent().getPosition() == index;

        cellPane.setStyle(getCellStyle(cell, isPlayer, isOpponent));

        if (isPlayer || isOpponent) {
            DropShadow glow = new DropShadow();
            glow.setColor(isPlayer ? Color.web("#00e5ff") : Color.web("#ea80fc"));
            glow.setRadius(14); glow.setSpread(0.3);
            cellPane.setEffect(glow);
        }

        Label idxLabel = new Label(String.valueOf(index));
        idxLabel.setStyle(
            "-fx-font-size: 10px; -fx-font-weight: bold; -fx-text-fill: #cfd8dc;");
        StackPane.setAlignment(idxLabel, Pos.TOP_LEFT);
        StackPane.setMargin(idxLabel, new Insets(3, 0, 0, 5));

        VBox box = new VBox(0);              // centre content, no extra spacing
        box.setAlignment(Pos.CENTER);
        box.setMinSize(0, 0);

        Label symLabel = new Label(getCellSymbol(cell));
        // "Segoe UI Emoji" is the Windows emoji font, so icons show instead of a box.
        symLabel.setStyle("-fx-font-size: 16px; -fx-font-family: 'Segoe UI Emoji';");

        String monsterText = getMonsterOnCell(index);
        Label monsterLabel = new Label(monsterText);
        monsterLabel.setStyle(
            "-fx-font-size: 14px; -fx-font-weight: bold; -fx-font-family: 'Segoe UI Emoji';" +
            "-fx-text-fill: " + (isPlayer ? PLAYER_TEXT : OPP_TEXT) + ";");
        // An empty label still takes up a line, so hide it when no monster is here.
        boolean hasMonster = monsterText != null && !monsterText.isEmpty();
        monsterLabel.setVisible(hasMonster);
        monsterLabel.setManaged(hasMonster);

        if (cell instanceof DoorCell) {
            DoorCell door = (DoorCell) cell;
            String icon = door.getRole() == Role.SCARER ? "👻" : "😂";
            Label eLabel = new Label(icon + " " + door.getEnergy());
            eLabel.setStyle(
                "-fx-font-size:9px; -fx-text-fill:"+WHITE+"; -fx-font-weight:bold;");
            box.getChildren().addAll(symLabel, eLabel, monsterLabel);
        } else if (cell instanceof MonsterCell) {
            MonsterCell mc = (MonsterCell) cell;
            String name = mc.getCellMonster() != null ?
                mc.getCellMonster().getName().split(" ")[0] : "";
            Label nLabel = new Label(name);
            nLabel.setStyle("-fx-font-size:8px; -fx-text-fill:"+GOLD+";");
            box.getChildren().addAll(symLabel, nLabel, monsterLabel);
        } else {
            box.getChildren().addAll(symLabel, monsterLabel);
        }

        cellPane.getChildren().addAll(box, idxLabel);
        return cellPane;
    }

    private String getCellStyle(Cell cell, boolean isPlayer, boolean isOpponent) {
        String base = "-fx-border-width:1.5; -fx-padding:3;" +
                      "-fx-background-radius:5; -fx-border-radius:5;";
        if (isPlayer && isOpponent)
            return base+"-fx-background-color:#003838;-fx-border-color:#ffffff;";
        if (isPlayer)
            return base+"-fx-background-color:"+PLAYER_BG+
                   ";-fx-border-color:"+PLAYER_BORDER+";";
        if (isOpponent)
            return base+"-fx-background-color:"+OPP_BG+
                   ";-fx-border-color:"+OPP_BORDER+";";
        if (cell instanceof DoorCell) {
            DoorCell d = (DoorCell) cell;
            if (d.isActivated())
                return base+"-fx-background-color:"+DOOR_USED_BG+
                       ";-fx-border-color:"+DOOR_USED_BORDER+";";
            return d.getRole() == Role.SCARER ?
                base+"-fx-background-color:"+DOOR_SCARER_BG+
                     ";-fx-border-color:"+DOOR_SCARER_BORDER+";" :
                base+"-fx-background-color:"+DOOR_LAUGHER_BG+
                     ";-fx-border-color:"+DOOR_LAUGHER_BORDER+";";
        }
        if (cell instanceof MonsterCell)
            return base+"-fx-background-color:"+MONSTER_BG+
                   ";-fx-border-color:"+MONSTER_BORDER+";";
        if (cell instanceof CardCell)
            return base+"-fx-background-color:"+CARD_BG+
                   ";-fx-border-color:"+CARD_BORDER+";";
        if (cell instanceof ConveyorBelt)
            return base+"-fx-background-color:"+BELT_BG+
                   ";-fx-border-color:"+BELT_BORDER+";";
        if (cell instanceof ContaminationSock)
            return base+"-fx-background-color:"+SOCK_BG+
                   ";-fx-border-color:"+SOCK_BORDER+";";
        return base+"-fx-background-color:"+NORMAL_BG+
               ";-fx-border-color:"+NORMAL_BORDER+";";
    }

    private String getCellSymbol(Cell cell) {
        if (cell instanceof DoorCell)          return "🚪";
        if (cell instanceof MonsterCell)       return "👾";
        if (cell instanceof CardCell)          return "🃏";
        if (cell instanceof ConveyorBelt)      return "➡️";
        if (cell instanceof ContaminationSock) return "🧦";
        return "·";
    }

    private String getMonsterOnCell(int index) {
        boolean p = game.getPlayer().getPosition()   == index;
        boolean o = game.getOpponent().getPosition() == index;
        if (p && o)
            return getMonsterEmoji(game.getPlayer()) +
                   getMonsterEmoji(game.getOpponent());
        if (p) return getMonsterEmoji(game.getPlayer());
        if (o) return getMonsterEmoji(game.getOpponent());
        return "";
    }

    private String getMonsterEmoji(Monster m) {
        if (m instanceof Dasher)      return "💨";
        if (m instanceof Dynamo)      return "⚡";
        if (m instanceof MultiTasker) return "🌀";
        if (m instanceof Schemer)     return "🎭";
        return "👾";
    }

    private String getMonsterInfo(Monster m) {
        String confused = m.getConfusionTurns() > 0
            ? "😵 " + m.getConfusionTurns() + " turns" : "–";
        String extra = "";
        if (m instanceof Dasher) {
            int mt = ((Dasher) m).getMomentumTurns();
            if (mt > 0) extra = "\n💨  Momentum: " + mt + " turns";
        }
        if (m instanceof MultiTasker) {
            int ns = ((MultiTasker) m).getNormalSpeedTurns();
            if (ns > 0) extra = "\n🌀  Focus Mode: " + ns + " turns";
        }
        String info =
            "📛  " + m.getName() + "\n" +
            "🦾  " + m.getClass().getSimpleName() + "\n" +
            "⚙️   Original:  " + m.getOriginalRole()  + "\n" +
            "🎭  Current:   " + m.getRole()           + "\n" +
            "⚡  Energy:    " + m.getEnergy()          + "\n" +
            "📍  Position:  " + m.getPosition()        + "\n" +
            "🛡️   Shield:   " + (m.isShielded() ? "Active ✅" : "–") + "\n" +
            "🧊  Frozen:   " + (m.isFrozen()    ? "Yes ❄️"   : "–") + "\n" +
            "😵  Confused: " + confused + extra;
        if (!m.getRole().equals(m.getOriginalRole()))
            info += "\n⚠️  ROLE SWAPPED!";
        return info;
    }

    private void updateUI() {
        Monster current      = game.getCurrent();
        Monster player       = game.getPlayer();
        boolean isPlayerTurn = (current == player) && !opponentPlaying;

        if (current.isFrozen()) {
            statusLabel.setText(
                "🧊  " + current.getName() + "  is FROZEN — turn skipped!");
            statusLabel.setStyle(
                "-fx-font-size:15px; -fx-font-weight:bold;" +
                "-fx-text-fill:#80deea; -fx-background-color:#001a22;");
        } else if (isPlayerTurn) {
            statusLabel.setText(
                "🎮  YOUR TURN  ✦  " + current.getName() +
                "  [" + current.getRole() + "]");
            statusLabel.setStyle(
                "-fx-font-size:15px; -fx-font-weight:bold;" +
                "-fx-text-fill:"+TEAL+"; -fx-background-color:#001828;");
        } else {
            statusLabel.setText(
                "⏳  OPPONENT'S TURN  ✦  " + current.getName() +
                "  [" + current.getRole() + "]");
            statusLabel.setStyle(
                "-fx-font-size:15px; -fx-font-weight:bold;" +
                "-fx-text-fill:"+MAGENTA+"; -fx-background-color:#14001f;");
        }

        playerAvatarLabel.setText(getMonsterEmoji(player));
        opponentAvatarLabel.setText(getMonsterEmoji(game.getOpponent()));
        playerInfoLabel.setText(getMonsterInfo(player));
        opponentInfoLabel.setText(getMonsterInfo(game.getOpponent()));

        rollBtn.setDisable(!isPlayerTurn);
        powerupBtn.setDisable(!isPlayerTurn);

        buildBoard();
    }

    private void animateDice(Runnable onComplete) {
        Timeline timeline = new Timeline();
        for (int i = 0; i < 12; i++) {
            KeyFrame kf = new KeyFrame(Duration.millis(i * 70), e -> {
                int r = (int)(Math.random() * 6);
                diceLabel.setText(DICE_FACES[r] + "  " + (r + 1));
            });
            timeline.getKeyFrames().add(kf);
        }
        timeline.getKeyFrames().add(
            new KeyFrame(Duration.millis(12 * 70), e -> onComplete.run()));
        timeline.play();
    }

    private void handlePowerup() {
        try {
            int before = game.getPlayer().getEnergy();
            game.usePowerup();
            SoundManager.playPowerupSound();
            log("⚡ Powerup! " + before + " → " + game.getPlayer().getEnergy());
            updateUI();
        } catch (OutOfEnergyException e) {
            showError("Not enough energy!\nYou need 500 energy to use a powerup.");
        }
    }

    private void handleRoll() {
        Monster winner = game.getWinner();
        if (winner != null) { showWinner(winner); return; }

        opponentPlaying = false;
        rollBtn.setDisable(true);
        powerupBtn.setDisable(true);
        SoundManager.playRollSound();

        ScaleTransition bounce = new ScaleTransition(Duration.millis(120), rollBtn);
        bounce.setFromX(1.0); bounce.setToX(0.93);
        bounce.setFromY(1.0); bounce.setToY(0.93);
        bounce.setAutoReverse(true); bounce.setCycleCount(2);
        bounce.play();

        animateDice(() -> {
            Monster current  = game.getCurrent();
            int posBefore    = current.getPosition();
            int energyBefore = current.getEnergy();
            Card cardBefore  = Board.getLastDrawnCard();

            try {
                if (current.isFrozen())
                    log("🧊 " + current.getName() + " FROZEN — skipping!");

                game.playTurn();

                int posAfter    = current.getPosition();
                int energyAfter = current.getEnergy();
                int rolled      = Math.abs(posAfter - posBefore);

                if (rolled >= 1 && rolled <= 6)
                    diceLabel.setText(DICE_FACES[rolled-1] + "  Rolled: " + rolled);
                else
                    diceLabel.setText("🎲  Rolled!");

                if (posAfter != posBefore)
                    log("🎲 " + current.getName() + ": " +
                        posBefore + " → " + posAfter);
                if (energyAfter != energyBefore) {
                    String sign = energyAfter > energyBefore ? "+" : "";
                    log("⚡ Energy: " + energyBefore + " → " + energyAfter
                        + " (" + sign + (energyAfter - energyBefore) + ")");
                }

                Card cardAfter = Board.getLastDrawnCard();
                if (cardAfter != null && cardAfter != cardBefore)
                    showCardPopup(current.getName(),
                        cardAfter.getName(), cardAfter.getDescription());

                Monster w = game.getWinner();
                if (w != null) { updateUI(); showWinner(w); return; }

                opponentPlaying = true;
                updateUI();
                playOpponentTurn();

            } catch (InvalidMoveException e) {
                showError("Invalid move!\n" + e.getMessage());
                opponentPlaying = false;
                updateUI();
            }
        });
    }

    private void playOpponentTurn() {
        Monster opp = game.getOpponent();
        statusLabel.setText("⏳  " + opp.getName() + " is thinking...");

        PauseTransition safety = new PauseTransition(Duration.seconds(15));
        safety.setOnFinished(ev -> {
            if (opponentPlaying) {
                opponentPlaying = false;
                game.setCurrent(game.getPlayer());
                updateUI();
            }
        });
        safety.play();

        PauseTransition pause = new PauseTransition(Duration.seconds(1.2));
        pause.setOnFinished(e -> animateDice(() -> {
            try {
                int posBefore    = opp.getPosition();
                int energyBefore = opp.getEnergy();
                Card cardBefore  = Board.getLastDrawnCard();

                if (opp.isFrozen())
                    log("🧊 " + opp.getName() + " FROZEN — skipping!");

                SoundManager.playRollSound();
                game.playTurn();

                int posAfter    = opp.getPosition();
                int energyAfter = opp.getEnergy();
                int rolled      = Math.abs(posAfter - posBefore);

                if (rolled >= 1 && rolled <= 6)
                    diceLabel.setText(
                        DICE_FACES[rolled-1] + "  Opp rolled: " + rolled);
                else
                    diceLabel.setText("🎲  Opp rolled!");

                if (posAfter != posBefore)
                    log("🤖 " + opp.getName() + ": " +
                        posBefore + " → " + posAfter);
                if (energyAfter != energyBefore) {
                    String sign = energyAfter > energyBefore ? "+" : "";
                    log("⚡ Opp energy: " + energyBefore + " → " + energyAfter
                        + " (" + sign + (energyAfter - energyBefore) + ")");
                }

                Card cardAfter = Board.getLastDrawnCard();
                if (cardAfter != null && cardAfter != cardBefore)
                    showCardPopup(opp.getName(),
                        cardAfter.getName(), cardAfter.getDescription());

            } catch (Exception ex) {
                
                game.setCurrent(game.getPlayer());
            }

            safety.stop();
            opponentPlaying = false;
            Monster winner = game.getWinner();
            if (winner != null) {
                updateUI();
                showWinner(winner);
            } else {
                updateUI();
            }
        }));
        pause.play();
    }

    private void showWinner(Monster winner) {
        boolean playerWon = winner == game.getPlayer();
        if (playerWon) SoundManager.playWinSound();
        else           SoundManager.playLoseSound();

        StackPane root = new StackPane();
        root.setStyle("-fx-background-color: " + BG_MAIN + ";");

        if (playerWon) {
            Canvas confettiCanvas = new Canvas(680, 520);
            root.getChildren().add(confettiCanvas);
            startConfetti(confettiCanvas);
        }

        VBox content = new VBox(22);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(50));

        Label icon = new Label(playerWon ? "🏆" : "💀");
        icon.setStyle("-fx-font-size: 80px;");
        FadeTransition ft = new FadeTransition(Duration.millis(600), icon);
        ft.setFromValue(0); ft.setToValue(1); ft.play();
        ScaleTransition st = new ScaleTransition(Duration.millis(600), icon);
        st.setFromX(0.3); st.setToX(1.0);
        st.setFromY(0.3); st.setToY(1.0); st.play();

        Label title = new Label(playerWon ? "VICTORY!" : "DEFEATED");
        String titleColor = playerWon ? GOLD : SOCK_BORDER;
        title.setStyle(
            "-fx-font-size:58px; -fx-font-weight:bold;" +
            "-fx-text-fill:" + titleColor + ";");
        DropShadow glow = new DropShadow();
        glow.setColor(Color.web(titleColor));
        glow.setRadius(25); glow.setSpread(0.4);
        title.setEffect(glow);

        Label winnerLbl = new Label(
            "✦  " + winner.getName() +
            "  wins  (" + winner.getOriginalRole() + ")  ✦");
        winnerLbl.setStyle(
            "-fx-font-size:20px; -fx-text-fill:"+WHITE+
            "; -fx-font-weight:bold;");

        VBox scoreBox = new VBox(8);
        scoreBox.setAlignment(Pos.CENTER);
        scoreBox.setStyle(
            "-fx-background-color:"+BG_CARD+";" +
            "-fx-border-color:"+MUTED+";" +
            "-fx-border-radius:8; -fx-background-radius:8;" +
            "-fx-padding:18 36;");

        Label p1Score = new Label(
            "⚡  " + game.getPlayer().getName() +
            "  →  " + game.getPlayer().getEnergy() + " energy");
        p1Score.setStyle("-fx-font-size:16px; -fx-text-fill:"+TEAL+";");

        Label p2Score = new Label(
            "⚡  " + game.getOpponent().getName() +
            "  →  " + game.getOpponent().getEnergy() + " energy");
        p2Score.setStyle("-fx-font-size:16px; -fx-text-fill:"+MAGENTA+";");

        scoreBox.getChildren().addAll(p1Score, p2Score);

        Button restartBtn = makeButton("🔄  Play Again", TEAL, "#008fa3");
        restartBtn.setStyle(
            restartBtn.getStyle() + "-fx-font-size:16px; -fx-padding:14 40;");
        restartBtn.setOnAction(ev -> new Main().start(stage));

        content.getChildren().addAll(
            icon, title, winnerLbl, scoreBox, restartBtn);
        root.getChildren().add(content);

        Scene winScene = new Scene(root, 680, 520);
        stage.setScene(winScene);
    }

    private void startConfetti(Canvas canvas) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        Random rand = new Random();
        int NUM = 120;
        double[] x    = new double[NUM], y  = new double[NUM];
        double[] vx   = new double[NUM], vy = new double[NUM];
        double[] w    = new double[NUM], h  = new double[NUM];
        double[] rot  = new double[NUM], dr = new double[NUM];
        Color[]  cols = new Color[NUM];
        Color[] palette = {
            Color.web("#ff4500"), Color.web("#00e676"),
            Color.web("#00e5ff"), Color.web("#d500f9"),
            Color.web("#ffc400"), Color.web("#ff1744"),
            Color.web("#64ffda"), Color.web("#ea80fc")
        };
        for (int i = 0; i < NUM; i++) {
            x[i]   = rand.nextDouble() * 680;
            y[i]   = -rand.nextDouble() * 400;
            vx[i]  = (rand.nextDouble() - 0.5) * 3;
            vy[i]  = rand.nextDouble() * 4 + 2;
            w[i]   = rand.nextDouble() * 12 + 6;
            h[i]   = rand.nextDouble() * 8  + 4;
            rot[i] = rand.nextDouble() * 360;
            dr[i]  = (rand.nextDouble() - 0.5) * 8;
            cols[i] = palette[rand.nextInt(palette.length)];
        }
        AnimationTimer timer = new AnimationTimer() {
            public void handle(long now) {
                gc.clearRect(0, 0, 680, 520);
                for (int i = 0; i < NUM; i++) {
                    x[i] += vx[i]; y[i] += vy[i]; rot[i] += dr[i];
                    if (y[i] > 540) {
                        y[i] = -10;
                        x[i] = rand.nextDouble() * 680;
                    }
                    gc.save();
                    gc.translate(x[i], y[i]);
                    gc.rotate(rot[i]);
                    gc.setFill(cols[i]);
                    gc.fillRect(-w[i]/2, -h[i]/2, w[i], h[i]);
                    gc.restore();
                }
            }
        };
        timer.start();
        new Timeline(
            new KeyFrame(Duration.seconds(5), e -> timer.stop())).play();
    }

    private void showCardPopup(String monsterName,
                                String cardName,
                                String cardEffect) {
        Platform.runLater(() -> {
            Alert a = new Alert(Alert.AlertType.INFORMATION);
            a.setTitle("🃏 Card Drawn!");
            a.setHeaderText(monsterName + " drew a card!");
            a.setContentText(
                "🃏 Card: "    + cardName   + "\n" +
                "📝 Effect: " + cardEffect + "\n\n" +
                "Check the game log for changes.");
            SoundManager.playCardSound();
            a.showAndWait();
        });
    }

    private void showError(String msg) {
        Platform.runLater(() -> {
            Alert a = new Alert(Alert.AlertType.WARNING);
            a.setTitle("Invalid Action");
            a.setHeaderText("⚠️  Action could not be performed");
            a.setContentText(msg);
            a.showAndWait();
        });
    }

    private Label makeTitle(String text, String color) {
        Label l = new Label(text);
        l.setStyle(
            "-fx-text-fill:"+color+"; -fx-font-weight:bold;" +
            "-fx-font-size:13px; -fx-padding:4 0 2 0;");
        return l;
    }

    private Label makeInfoLabel(String borderColor) {
        Label l = new Label();
        l.setStyle(
            "-fx-text-fill:"+WHITE+"; -fx-font-size:12px; -fx-padding:6;");
        l.setWrapText(true);
        return l;
    }

    private Button makeButton(String text, String bg, String hoverBg) {
        Button btn = new Button(text);
        String style =
            "-fx-background-color:"+bg+"; -fx-text-fill:white;" +
            "-fx-font-size:13px; -fx-padding:10; -fx-cursor:hand;" +
            "-fx-background-radius:6; -fx-font-weight:bold;";
        btn.setStyle(style);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setOnMouseEntered(e -> btn.setStyle(style.replace(bg, hoverBg)));
        btn.setOnMouseExited(e  -> btn.setStyle(style));
        return btn;
    }

    private void log(String msg) {
        Label entry = new Label(msg);
        entry.setWrapText(true);                 // long lines wrap instead of "..."
        entry.setMaxWidth(Double.MAX_VALUE);
        entry.setMinHeight(Region.USE_PREF_SIZE);
        entry.setStyle("-fx-text-fill: #90a4ae; -fx-font-size: 11px;");
        logBox.getChildren().add(entry);         // newest message at the bottom
    }
}