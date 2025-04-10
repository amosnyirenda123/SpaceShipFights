package com.aen.spaceship_fights;

import com.aen.spaceship_fights.database.Db;
import com.aen.spaceship_fights.levels.GameLevel;
import com.aen.spaceship_fights.levels.Level0;
import com.aen.spaceship_fights.levels.Level1;
import com.aen.spaceship_fights.networking.Client;
import com.aen.spaceship_fights.networking.NetworkManager;
import com.aen.spaceship_fights.networking.Server;
import com.aen.spaceship_fights.utils.Selection;
import com.aen.spaceship_fights.utils.UserData;
import com.almasb.fxgl.animation.Interpolators;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
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
import java.util.List;
import java.util.Map;


public class SpaceShipFightsApp extends GameApplication {

    private Entity player;
    private GameController controller;
    private int highScore;
    private String highScoreName;

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
    private Client client = null;

    private void connectToDatabase(){
        Db db = new Db();
        db.loadUserTable();
    }

    private void connectToServer() {
        try {
            serverSocket = new ServerSocket(Config.PORT_NUMBER);
            Server server = new Server(serverSocket);
            server.startServer();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void startClient() {
        Socket socket = null;
        try {
            socket = new Socket("localhost", Config.PORT_NUMBER);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        client = new Client(socket, currentUserName);
        NetworkManager.setClient(client);
        getExecutor().startAsyncFX(()->{
            addUINode(receiveClientMessage(client), 200, 300);
        });


    }

    public Node receiveClientMessage(Client client) {
        if (client == null) {
            Text text = new Text("Loading Messages...");
            text.setStyle("-fx-color: #fff");
            return text;
        }
        return client.receiveMessage();
    }

    private boolean isServer;
    private String currentUserName;

    @Override
    protected void initGame() {
        loopBGM("bgm.mp3");
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
                new Level1()
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
        controller.toggleChatBox();


        addUINode(controller.receiveClientMessage(client), getAppWidth() / 2.5, getAppHeight() - 200);




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
