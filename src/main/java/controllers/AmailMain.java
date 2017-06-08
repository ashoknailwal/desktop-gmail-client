package controllers;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Created by ashok on 20/3/17.
 */
public class AmailMain extends Application {

    private static Stage stage;
    public static boolean isInternetUp = true;

    public static String splashId = "splash";
    public static String splashFile = "/splash.fxml";
    public static String splashGuideId = "splashGuide";
    public static String splashGuideFile = "/splashGuide.fxml";
    public static String mainUIId = "mainUI";
    public static String mainUIFile = "/mainUI3.fxml";
    public static String splashWaitId = "splashWait";
    public static String splashWaitFile = "/splashWait.fxml";

   // public static boolean isSplashLoaded = false;

    public static void main(String[] args){
        launch(args);
    }
    public void start(Stage primaryStage) throws Exception {
        stage = primaryStage;
        //Parent root = (AnchorPane) FXMLLoader.load(getClass().getResource("/splash.fxml"));
        ScreenController mainController = new ScreenController();
        mainController.loadScreen(AmailMain.splashGuideId, AmailMain.splashGuideFile);
        mainController.loadScreen(AmailMain.mainUIId, AmailMain.mainUIFile);
        mainController.loadScreen(AmailMain.splashWaitId, AmailMain.splashWaitFile);
        mainController.loadScreen(AmailMain.splashId, AmailMain.splashFile);



        //Group root = new Group();
        //root.getChildren().addAll(mainController);
        primaryStage.setTitle("Desktop Gmail Client");
        Scene scene = new Scene(mainController, 1080, 600);
        scene.getStylesheets().add(getClass().getResource("/sneckbar.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.sizeToScene();
        mainController.setScreen(AmailMain.splashId);
        primaryStage.show();
    }

    public static Stage getStage(){
        return stage;
    }

}
