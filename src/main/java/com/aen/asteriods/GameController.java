package com.aen.asteriods;

import com.aen.asteriods.networking.Client;
import com.aen.asteriods.networking.NetworkManager;
import javafx.animation.Animation;
import com.almasb.fxgl.app.scene.GameScene;
import com.almasb.fxgl.app.scene.Viewport;
import com.almasb.fxgl.texture.Texture;
import com.almasb.fxgl.ui.UIController;
import javafx.animation.ScaleTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;
import static com.almasb.fxgl.dsl.FXGL.*;

import static com.almasb.fxgl.dsl.FXGLForKtKt.getAssetLoader;

public class GameController implements UIController {

    private List<Texture> lives = new ArrayList<>();
    private GameScene gameScene;

    public GameController(GameScene gameScene) {
        this.gameScene = gameScene;
    }
    @Override
    public void init() {

    }

    public Client getClient(Client client) {
        return client;
    }

    public void sendClientMessage(Client client, String message) {
        client.sendMessage(message);
    }

    public Node receiveClientMessage(Client client) {
        if (client == null) {
            Text text = new Text("Loading Messages...");
            text.setStyle("-fx-color: #fff");
            return text;
        }
        return client.receiveMessage();
    }

    public void loseLife() {
        Texture t = lives.get(lives.size() - 1);

        lives.remove(t);

        Animation animation = getAnimationLoseLife(t);
        animation.setOnFinished(e -> gameScene.removeUINode(t));
        animation.play();

        Viewport viewport = gameScene.getViewport();

        Node flash = new Rectangle(viewport.getWidth(), viewport.getHeight(), Color.rgb(190, 10, 15, 0.5));

        gameScene.addUINode(flash);

        runOnce(() -> gameScene.removeUINode(flash), Duration.seconds(1));
    }

    private Animation getAnimationLoseLife(Texture texture) {
        texture.setFitWidth(64);
        texture.setFitHeight(64);

        Viewport viewport = gameScene.getViewport();

        TranslateTransition tt = new TranslateTransition(Duration.seconds(0.66), texture);
        tt.setToX(viewport.getWidth() / 2 - texture.getFitWidth() / 2);
        tt.setToY(viewport.getHeight() / 2 - texture.getFitHeight() / 2);

        ScaleTransition st = new ScaleTransition(Duration.seconds(0.66), texture);
        st.setToX(0);
        st.setToY(0);

        return new SequentialTransition(tt, st);
    }

    public void addLife() {
        int numLives = lives.size();

        Texture texture = getAssetLoader().loadTexture("life.png", 16, 16);
        texture.setTranslateX(20 + 32 * numLives);
        texture.setTranslateY(15);

        lives.add(texture);
        gameScene.addUINode(texture);
    }

    private Node chatBox;

    public void toggleChatBox(){
        Image sendIcon = new Image(getClass().getResource("/assets/chat/message.png").toExternalForm());
        ImageView imageView = new ImageView(sendIcon);
        imageView.setFitWidth(32);
        imageView.setFitHeight(32);

        Button messageButton = new Button();
        messageButton.setGraphic(imageView);
        messageButton.setStyle("-fx-background-color: transparent; -fx-padding: 4;");
        messageButton.setTranslateX(20);
        messageButton.setTranslateY(120);

        gameScene.addUINode(messageButton);
        messageButton.setOnAction(e -> {
            if (chatBox == null) {
                chatBox = createChatNode();
                gameScene.addUINode(chatBox);
            } else {
                chatBox.setVisible(!chatBox.isVisible());
            }
        });
    }

    public Node createChatNode(){
        HBox container = new HBox(10);
        container.setTranslateX(10);
        container.setTranslateY(10);


        Image sendIcon = new Image(getClass().getResource("/assets/chat/send.png").toExternalForm());
        ImageView imageView = new ImageView(sendIcon);
        imageView.setFitWidth(32);
        imageView.setFitHeight(32);


        TextArea textArea = new TextArea();
        textArea.setWrapText(true);
        textArea.setPrefSize(gameScene.getAppWidth() - 400, 50);
        textArea.setMinHeight(36);
        textArea.setMaxHeight(36);
        textArea.setPrefRowCount(1);


        textArea.setStyle(
                "-fx-background-color: #F5F5F5;" +
                        "-fx-control-inner-background: #F5F5F5;" +
                        "-fx-border-color: #CCCCCC;" +
                        "-fx-border-radius: 12;" +
                        "-fx-background-radius: 12;" +
                        "-fx-padding: 6;" +
                        "-fx-font-size: 14px;" +
                        "-fx-focus-color: transparent;" +
                        "-fx-faint-focus-color: transparent;"
        );


        textArea.textProperty().addListener((obs, oldText, newText) -> {
            textArea.setScrollTop(Double.MAX_VALUE);
        });


        DropShadow redGlow = new DropShadow();
        redGlow.setColor(Color.RED);
        redGlow.setRadius(10);
        redGlow.setSpread(0.5);
        textArea.setEffect(redGlow);

        Button sendButton = new Button();
        sendButton.setGraphic(imageView);
        sendButton.setStyle("-fx-background-color: transparent; -fx-padding: 4;");
        sendButton.setOnAction(e -> {
            //Do something
            //send message to other clients
            new Thread(()->{
                sendClientMessage(NetworkManager.getClient(), textArea.getText());
            }).start();

            textArea.clear();
        });


        container.getChildren().addAll(textArea, sendButton);
        container.setTranslateY(gameScene.getAppHeight() - 50);
        container.setTranslateX(140);



        return container;
    }
}
