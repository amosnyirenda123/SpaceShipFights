package com.aen.spaceship_fights;

import com.aen.spaceship_fights.database.Db;
import com.aen.spaceship_fights.levels.*;
import com.aen.spaceship_fights.networking.ChatContext;
import com.aen.spaceship_fights.networking.ChatServer;
import com.aen.spaceship_fights.networking.ChatServiceFXGL;
import com.aen.spaceship_fights.ui.CustomButton;
import com.aen.spaceship_fights.utils.Selection;
import com.aen.spaceship_fights.utils.UserData;
import com.almasb.fxgl.animation.Interpolators;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.core.serialization.Bundle;
import com.almasb.fxgl.dsl.components.HealthIntComponent;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.multiplayer.MultiplayerService;
import com.almasb.fxgl.net.Connection;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Duration;

import static com.aen.spaceship_fights.Config.ENEMIES_PER_LEVEL;
import static com.aen.spaceship_fights.Config.LEVEL_START_DELAY;
import static com.almasb.fxgl.dsl.FXGL.*;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class SpaceShipFightsApp extends GameApplication {

    private Entity player;
    private GameController controller;
    private int highScore;
    private String highScoreName;
    private Connection<Bundle> connection;

    @Override
    protected void initGameVars(Map<String, Object> vars){
        vars.put("score", 0);
        vars.put("lives", 4);
        vars.put("level", 0);
        vars.put("enemiesKilled", 0);
        vars.put("planeChoice", "player");
    }

    @Override
    protected void initSettings(GameSettings gameSettings) {
        gameSettings.setWidth(1280);
        gameSettings.setHeight(720);
        gameSettings.setTitle("Shooter");
        gameSettings.setVersion("0.1");
        gameSettings.setIntroEnabled(true);
        gameSettings.setIntroEnabled(false);
        gameSettings.setFullScreenAllowed(true);
        gameSettings.setMainMenuEnabled(true);
        gameSettings.setSceneFactory(new MySceneFactory());
        gameSettings.getCredits().addAll(Arrays.asList(
                "Nyirenda Amos",
                "Ouedraogo Luther Arthur",
                "Magne Lidivine Merveille"
        ));
    }

    @Override
    protected void initInput() {
        onKey(KeyCode.A, () -> player.getComponent(PlayerComponent.class).rotateLeft());
        onKey(KeyCode.D, () -> player.getComponent(PlayerComponent.class).rotateRight());
        onKey(KeyCode.W, () -> player.getComponent(PlayerComponent.class).move());
        onKeyDown(KeyCode.F, () -> player.getComponent(PlayerComponent.class).shoot());
        onKeyDown(KeyCode.P, () -> getGameController().pauseEngine());
        onKeyDown(KeyCode.R, () -> getGameController().resumeEngine());
        onKeyDown(KeyCode.M, () -> getGameController().startNewGame());
    }

    private List<GameLevel> gameLevelList;
    private ServerSocket serverSocket = null;
    private ChatServiceFXGL chatUI = null;

    private void connectToDatabase(){
        Db db = new Db();
        db.loadUserTable();
    }



    private boolean isServer;
    private String currentUserName;

    @Override
    protected void initGame() {
//        loopBGM("bgm.mp3");
        getExecutor().startAsync(this::connectToDatabase);
        getGame();
    }

    private void getGame(){

        getGameWorld().addEntityFactory(new GameEntityFactory());
        initializeGame();
        runOnce(()->{

                currentUserName = Selection.getUsername();
                Label playerNameLabel = new Label("Player: " + currentUserName);
                playerNameLabel.setStyle("-fx-text-fill: #fff");
                VBox playerNameBox = new VBox(playerNameLabel);
                playerNameBox.setStyle("""
                        -fx-font-size: 14px;
                        -fx-font-weight: bold;
                        -fx-background-color: rgba(30, 30, 30, 0.85);
                        -fx-text-fill: white;
                        -fx-padding: 10;
                        -fx-border-color: crimson;
                        -fx-border-width: 1px;
                        """);
                addUINode(playerNameBox, getAppWidth() - 280, 10);

        }, Duration.seconds(0.2));

    }
    private boolean runningFirstTime = true;


    @Override
    protected void onUpdate(double tpf) {
        if (runningFirstTime) {
            nextLevel();
            runningFirstTime = false;
        }

        if (geti("level") > gameLevelList.size()) {
            showGameOver();
            return;
        }

        if(geti("lives") == 0) {
//            showMessage("You have failed... But warriors rise again!", getGameController()::gotoGameMenu);
            showGameOver();
        }

        getCurrentLevel().onUpdate(tpf);

        if (geti("enemiesKilled") >= ENEMIES_PER_LEVEL) {
            nextLevel();
        }

        //runOnce(this::askForUserName, Duration.seconds(LEVEL_START_DELAY));
    }

    private void initializeGame(){
        gameLevelList = Arrays.asList(
                new Level0(),
                new Level1(),
                new Level2(),
                new Level3(),
                new Level4(),
                new Level5()
        );

        spawn("background");
        handleSpawnPlayer();
        if (!runningFirstTime)
            nextLevel();
    }


    private void playInCutscene(Runnable onFinished) {
        getCurrentLevel().playInCutscene(onFinished);
    }

    private void playOutCutscene(Runnable onFinished) {
        getCurrentLevel().playOutCutscene(onFinished);
    }

    private void nextLevel() {
        if (geti("level") > 0) {
            cleanupLevel();
        }
        inc("level", +1);
        set("enemiesKilled", 0);
        if(geti("level") > gameLevelList.size()) {
            showGameOver();
        }

        playInCutscene(() -> {
            spawn("levelInfo");

            runOnce(this::initLevel, Duration.seconds(LEVEL_START_DELAY));

            play(Config.Asset.SOUND_NEW_LEVEL);
        });
    }

    private void initLevel(){
        getCurrentLevel().init();
    }

    private void cleanupLevel(){
        getGameWorld().getEntitiesByType(EntityType.BULLET, EntityType.ENEMY).forEach(Entity::removeFromWorld);
        getCurrentLevel().destroy();
    }
    private GameLevel getCurrentLevel() {
        return gameLevelList.get(geti("level") - 1);
    }



    @Override
    protected void initPhysics() {
        onCollisionBegin(EntityType.BULLET, EntityType.ENEMY, (bullet, enemy) -> {
            var hp = enemy.getComponent(HealthIntComponent.class);

            if(hp.getValue() > 1){
                bullet.removeFromWorld();
                hp.damage(1);
                return;
            }
            spawn("scoreText", new SpawnData(enemy.getX(), enemy.getY()).put("text", "+100"));
            killEnemy(enemy);
            bullet.removeFromWorld();

            inc("score", +100);
            inc("enemiesKilled", +1);
        });

        onCollisionBegin(EntityType.PLAYER, EntityType.ENEMY, (player, enemy) -> {
           handlePlayerDeathAndRespawn(enemy);
        });

        onCollisionBegin(EntityType.CC, EntityType.ENEMY, (cc, enemy)->{
           handlePlayerDeathAndRespawn(enemy);
        });

        onCollisionBegin(EntityType.PPP, EntityType.ENEMY, (ppp, enemy)->{
            handlePlayerDeathAndRespawn(enemy);
        });

        onCollisionBegin(EntityType.FFF, EntityType.ENEMY, (fff, enemy)->{
            handlePlayerDeathAndRespawn(enemy);
        });

        onCollisionBegin(EntityType.DURRRSPACESHIP, EntityType.ENEMY, (durr, enemy)->{
            handlePlayerDeathAndRespawn(enemy);
        });

        onCollisionBegin(EntityType.FIGHTER, EntityType.ENEMY, (fighter, enemy)->{
            handlePlayerDeathAndRespawn(enemy);
        });


    }

    public void handlePlayerDeathAndRespawn(Entity enemy){
        killEnemy(enemy);

        player.setPosition(getAppWidth() / 2.0, getAppHeight() / 2.0);

        inc("lives", -1);
        inc("enemiesKilled", +1);
    }

    private void killEnemy(Entity enemy) {
        Point2D explosionSpawnPoint = enemy.getCenter().subtract(64, 64);

        spawn("explosion", explosionSpawnPoint);
        enemy.removeFromWorld();
    }

    private void handleSpawnPlayer(){
        System.out.println("Player: " + gets("planeChoice"));
        if(Selection.getPlaneName().equals("player")){
            player = spawn("player", 500, 300);
        }else if(Selection.getPlaneName().equals("fighter")){
            player = spawn("fighter", getAppWidth() / 2.0, getAppHeight() / 2.0);
        }else if(Selection.getPlaneName().equals("PPP")){
            player = spawn("PPP", getAppWidth() / 2.0, getAppHeight() / 2.0);
        }else if(Selection.getPlaneName().equals("durrrSpaceShip")){
            player = spawn("durrrSpaceShip", getAppWidth() / 2.0, getAppHeight() / 2.0);
        }else if(Selection.getPlaneName().equals("fff")){
            player = spawn("fff", getAppWidth() / 2.0, getAppHeight() / 2.0);
        }else if(Selection.getPlaneName().equals("cc")){
            player = spawn("cc", getAppWidth() / 2.0, getAppHeight() / 2.0);
        }
    }

    private void showGameOver() {
        getDialogService().showConfirmationBox("Game Over. Play Again?", yes -> {
            if (yes) {
                getGameWorld().getEntitiesCopy().forEach(Entity::removeFromWorld);
                getGameController().startNewGame();
            } else {
                int score = geti("score");

                if(score > highScore) {
                    //TODO: Add score to list of high score
                }else{
                    getGameController().exit();
                }

            }
        });
    }


    @Override
    protected void initUI() {

        var chatUI = ChatContext.getInstance().showChatUI();
        var notificationPane = ChatContext.getInstance().showNotificationPane();
        var timerUI = ChatContext.getInstance().showGameTimerUI();

        chatUI.setTranslateX(20);
        chatUI.setTranslateY(150);
        timerUI.setTranslateX(200);
        timerUI.setTranslateY(5);

        notificationPane.setTranslateX(20);
        notificationPane.setTranslateY(400);

        getGameScene().addUINode(chatUI);
        getGameScene().addUINode(notificationPane);
        getGameScene().addUINode(timerUI);

        Image sendIcon = new Image(getClass().getResource("/assets/chat/message.png").toExternalForm());
        Image notificationIcon = new Image(getClass().getResource("/assets/chat/notification-bell.png").toExternalForm());
        ImageView imageView = new ImageView(sendIcon);
        ImageView notificationImageView = new ImageView(notificationIcon);
        imageView.setFitWidth(32);
        imageView.setFitHeight(32);
        notificationImageView.setFitWidth(32);
        notificationImageView.setFitHeight(32);


        CustomButton displayNotificationPane = new CustomButton("", () -> notificationPane.setVisible(!notificationPane.isVisible()));
        CustomButton chatButton = new CustomButton("", () -> chatUI.setVisible(!chatUI.isVisible()));
        chatButton.setGraphic(imageView);
        displayNotificationPane.setGraphic(notificationImageView);


        chatButton.setTranslateX(20);
        chatButton.setTranslateY(80);
        displayNotificationPane.setTranslateX(80);
        displayNotificationPane.setTranslateY(80);

        getGameScene().addUINode(chatButton);
        getGameScene().addUINode(displayNotificationPane);

        controller = new GameController(getGameScene());
        var text = getUIFactoryService().newText("", 24);
        var killed = getUIFactoryService().newText("", 24);
        text.textProperty().bind(getip("score").asString("Score: [%d]"));
        killed.textProperty().bind(getip("enemiesKilled").asString("Killed: [%d]"));

        getWorldProperties().addListener("score", (prev, now) -> {
            animationBuilder()
                    .duration(Duration.seconds(0.5))
                    .interpolator(Interpolators.BOUNCE.EASE_OUT())
                    .repeat(2)
                    .autoReverse(true)
                    .scale(text)
                    .from(new Point2D(1, 1))
                    .to(new Point2D(1.2, 1.2))
                    .buildAndPlay();
        });

        getWorldProperties().addListener("enemiesKilled", (prev, now) -> {
            animationBuilder()
                    .duration(Duration.seconds(0.5))
                    .interpolator(Interpolators.BOUNCE.EASE_OUT())
                    .repeat(2)
                    .autoReverse(true)
                    .scale(text)
                    .from(new Point2D(1, 1))
                    .to(new Point2D(1.2, 1.2))
                    .buildAndPlay();
        });

        addUINode(text, 20, 30);
        addUINode(killed, 20, 70);


        for(int i = 0; i < geti("lives"); i++){
            controller.addLife();
        }

        getWorldProperties().addListener("lives", (prev, now) -> {
            controller.loseLife();
        });



    }




    public static void main(String[] args) {
        launch(args);
    }


}
