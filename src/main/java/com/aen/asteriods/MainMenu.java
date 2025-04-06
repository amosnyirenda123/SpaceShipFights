package com.aen.asteriods;

import com.aen.asteriods.ui.MenuButton;
import com.aen.asteriods.ui.ScoreBoardUI;
import com.aen.asteriods.utils.UserData;
import com.almasb.fxgl.app.scene.FXGLMenu;
import com.almasb.fxgl.app.scene.MenuType;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.app.scene.FXGLMenu;
import com.almasb.fxgl.app.scene.MenuType;
import com.almasb.fxgl.dsl.FXGL;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;

import static com.almasb.fxgl.dsl.FXGLForKtKt.showMessage;
import static com.almasb.fxgl.dsl.FXGL.*;


public class MainMenu extends FXGLMenu {

    private AnchorPane mainArea = null;
    private ImageView imageView = null;
    private Rectangle overlay = null;
    public MainMenu() {
        super(MenuType.MAIN_MENU);

//        loopBGM("menu_sound.mp3");

        Image background = new Image(getClass().getResource("/assets/textures/wpp.jpg").toExternalForm());
        overlay = new Rectangle(getAppWidth(), getAppHeight(), Color.BLACK);
        overlay.setOpacity(0.5);

        Text title = new Text("Main Menu");
        title.setFont(Font.loadFont(getClass().getResourceAsStream("/assets/fonts/BruceForever.ttf"), 40));

        LinearGradient gradientFill = new LinearGradient(
                0, 0, 1, 0, true, javafx.scene.paint.CycleMethod.NO_CYCLE,
                new Stop(0.1, Color.color(0.5, 0.0, 0.0, 1)),  // Dark Blood Red
                new Stop(0.5, Color.color(0.6, 0.1, 0.1, 1)),  // Deep Crimson
                new Stop(0.9, Color.color(0.4, 0.0, 0.0, 1))   // Darker Red
        );
        title.setFill(gradientFill);


        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.BLACK);
        shadow.setRadius(20);
        shadow.setSpread(0.7);
        title.setEffect(shadow);

        var MenuButtonContainer = new VBox(15,

                new MenuButton("Play Game", () -> setContent(startGame())),
                new MenuButton("Extras", () -> setContent(new ScoreBoardUI().showScoreBoard())),
                new MenuButton("Upgrades", () -> showMessage("TODO: not implemented")),
                new MenuButton("Settings", () -> showMessage("TODO: not implemented")),
                new MenuButton("Exit", this::fireExit)
                );


        AnchorPane menuOptions = new AnchorPane();

        //Left Pane
        menuOptions.setPrefWidth(getAppWidth() / 4.0);
        menuOptions.setPrefHeight(getAppHeight());
        menuOptions.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, null, null)));

        //Menu Title
        menuOptions.getChildren().add(title);
        AnchorPane.setLeftAnchor(title, 40.0d);
        AnchorPane.setTopAnchor(title, getAppHeight() / 3.5);
        AnchorPane.setRightAnchor(title, 10.0d);

        //Chron Button Container
        menuOptions.getChildren().add(MenuButtonContainer);
        AnchorPane.setBottomAnchor(MenuButtonContainer, 20.0d);
        AnchorPane.setLeftAnchor(MenuButtonContainer, 40.0d);

        mainArea = new AnchorPane();
        mainArea.setPrefWidth(getAppWidth());
        mainArea.setPrefHeight(getAppHeight());

        imageView = new ImageView(background);
        imageView.setFitWidth(getAppWidth());
        imageView.setFitHeight(getAppHeight());
        imageView.setPreserveRatio(false);

        mainArea.getChildren().addAll(imageView, overlay);
        mainArea.getChildren().add(menuOptions);

        HBox hbox = new HBox(mainArea);
        getContentRoot().getChildren().add(hbox);
    }


    private void setContent(Node view) {
        mainArea.getChildren().setAll(view);
    }

    private String planeChoice = "None";
    private String levelChoice = "None";
    private Button startButton;
    private Text selectedInfo;
    private SaveData savedData = null;


    private Parent showScoreBoard(){
        getFileSystemService().<SaveData>readDataTask(Config.SAVE_DATA_NAME)
                .onSuccess(data -> savedData = data)
                .onFailure(error -> {})
                .run();

        if(savedData == null){
            new SaveData("COMPUTER", Config.ACHIEVEMENT_MASTER_SCORER);
        }



        VBox scoreBoard = new VBox();
        scoreBoard.setSpacing(10);
        scoreBoard.setPadding(new Insets(20));
        scoreBoard.getChildren().add(new Label("Scoreboard for: " + savedData.getName()));
        scoreBoard.getChildren().add(new Label("Achievement: " + savedData.getHighScore()));

        return scoreBoard;

    }



    private Parent startGame() {


        Text title = new Text("Choose Your Plane and Level");
        title.setFont(Font.loadFont(getClass().getResourceAsStream("/assets/fonts/BruceForever.ttf"), 20));

        LinearGradient gradientFill = new LinearGradient(
                0, 0, 1, 0, true, javafx.scene.paint.CycleMethod.NO_CYCLE,
                new Stop(0.1, Color.color(0.5, 0.0, 0.0, 1)),  // Dark Blood Red
                new Stop(0.5, Color.color(0.6, 0.1, 0.1, 1)),  // Deep Crimson
                new Stop(0.9, Color.color(0.4, 0.0, 0.0, 1))   // Darker Red
        );
        title.setFill(gradientFill);


        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.BLACK);
        shadow.setRadius(20);
        shadow.setSpread(0.7);
        title.setEffect(shadow);

        // Loading planes
        Image plane1 = new Image(getClass().getResource("/assets/textures/player.png").toExternalForm());
        Image plane2 = new Image(getClass().getResource("/assets/textures/fighter.png").toExternalForm());
        Image plane3 = new Image(getClass().getResource("/assets/textures/DurrrSpaceShip.png").toExternalForm());
        Image plane4 = new Image(getClass().getResource("/assets/textures/ppp.png").toExternalForm());


        // Create ImageViews
        ImageView imageView1 = new ImageView(plane1);
        ImageView imageView2 = new ImageView(plane2);
        ImageView imageView3 = new ImageView(plane3);
        ImageView imageView4 = new ImageView(plane4);


        double imageSize = 100;
        ImageView[] imageViews = {imageView1, imageView2, imageView3, imageView4};
        String[] planeNames = {"player", "fighter", "durrrSpaceShip", "PPP"};



        for (int i = 0; i < imageViews.length; i++) {
            ImageView imgView = imageViews[i];
            String planeName = planeNames[i];
            imgView.setFitWidth(imageSize);
            imgView.setFitHeight(imageSize);

            String planeInfo = switch (planeName) {
                case "player" -> "Name: Classic Jet\nPower: 80\nSpeed: Medium\nAgility: High";
                case "fighter" -> "Name: Fighter X\nPower: 95\nSpeed: Fast\nAgility: Medium";
                case "durrrSpaceShip" -> "Name: Durrr SpaceShip\nPower: 70\nSpeed: Slow\nAgility: High\nShield: Ultra";
                case "PPP" -> "Name: Phantom PPP\nPower: 85\nSpeed: Very Fast\nAgility: Low";
                default -> "Unknown";
            };

            Tooltip tooltip = new Tooltip(planeInfo);
            tooltip.setStyle("""
                    -fx-font-size: 14px;
                    -fx-font-weight: bold;
                    -fx-background-color: rgba(30, 30, 30, 0.85);
                    -fx-text-fill: white;
                    -fx-padding: 10;
                    -fx-border-color: crimson;
                    -fx-border-width: 1px;
        """);

            tooltip.setShowDelay(Duration.ZERO);
            tooltip.setShowDuration(Duration.INDEFINITE);
            tooltip.setHideDelay(Duration.ZERO);

            Tooltip.install(imgView, tooltip);


            imgView.setOnMouseClicked(event -> {
                planeChoice = planeName;
                highlightSelection(imgView, imageViews);
            });


        }


        HBox imageBox = new HBox(60, imageView1, imageView2, imageView3, imageView4);
        imageBox.setAlignment(Pos.CENTER);

        HBox buttonBox = new HBox(30,
                new MenuButton("Start Game", this::fireNewGame),
                new MenuButton("Go Back", getGameController()::gotoGameMenu)
                );
        buttonBox.setAlignment(Pos.CENTER);

        VBox layout = new VBox(50, title, imageBox, buttonBox);
        layout.setAlignment(Pos.CENTER);
        layout.setBackground(new Background(new BackgroundFill(Color.BLACK, null, null)));
        layout.setPrefHeight(getAppHeight());
        layout.setPrefWidth(getAppWidth() );
        layout.setStyle("-fx-padding: 20;");

        return new StackPane(imageView,overlay, layout);
    }



    private void highlightSelection(ImageView selected, ImageView[] images) {
        for (ImageView img : images) {
            img.setStyle("-fx-effect: null;");
        }
        selected.setStyle("-fx-effect: dropshadow(gaussian, red, 20, 0.5, 0, 0);");
    }



}
