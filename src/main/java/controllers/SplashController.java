package controllers; /**
 * Created by ashok on 21/3/17.
 */

import database.HelperValues;
import javafx.animation.*;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

public class SplashController implements ControlledScreen{

    ScreenController myController;

    @FXML
    private ImageView gmailLogo;

    @FXML
    private StackPane topBackground;

    @FXML
    void initialize() {
            gmailLogo.setOpacity(0.3f);
            splashAnimations();

    }

    public void splashAnimations(){

        Task<Boolean> checkNextScreen = new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {
                return skipSplashGuide();
            }
        };


        FadeTransition fadein = new FadeTransition(Duration.seconds(4),gmailLogo);
        fadein.setFromValue(0.3);
        fadein.setToValue(1);
        fadein.setCycleCount(1);
        RotateTransition rotate = new RotateTransition(Duration.seconds(4),gmailLogo);
        rotate.setByAngle(360f);
        rotate.setCycleCount(1);
        ScaleTransition scaleTransition =  new ScaleTransition(Duration.seconds(4), gmailLogo);
        scaleTransition.setToX(4f);
        scaleTransition.setToY(4f);
        scaleTransition.setToZ(4f);
        scaleTransition.setCycleCount(1);
        ParallelTransition pt = new ParallelTransition();
        pt.getChildren().addAll(fadein,rotate,scaleTransition);
        pt.setCycleCount(1);

        Timeline slideDown = new Timeline();
        slideDown.getKeyFrames().add(new KeyFrame(Duration.seconds(1),new KeyValue(topBackground.translateYProperty(),600)));

        Timeline slidingBounce = new Timeline();
        slidingBounce.setCycleCount(2);
        slidingBounce.setAutoReverse(true);
        slidingBounce.getKeyFrames().add(new KeyFrame(Duration.millis(300),new KeyValue(topBackground.translateYProperty(),580)));

        Timeline slideLeft = new Timeline();
        slideLeft.getKeyFrames().add(new KeyFrame(Duration.millis(300), new KeyValue(topBackground.translateXProperty(), -1080)));
        slideLeft.setCycleCount(1);


        SequentialTransition sq = new SequentialTransition();
        sq.getChildren().addAll(new PauseTransition(Duration.seconds(3)), slideDown,
                slidingBounce, new PauseTransition(Duration.millis(300)), pt);
        sq.play();

        checkNextScreen.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
                slideLeft.play();
                if(checkNextScreen.getValue()) {
                    SplashWaitController.startBackgroundTasks();
                    myController.setScreen(AmailMain.splashWaitId);
                    SplashWaitController.installed = true;
                }
                else
                    myController.setScreen(AmailMain.splashGuideId);
            }
        });

        sq.setOnFinished((e) -> {
            Thread t = new Thread(checkNextScreen);
            t.setDaemon(true);
            t.start();
        });

    }

    public boolean skipSplashGuide(){
        try{
            if(HelperValues.getHelperValues(HelperValues.loggedIn).equals("true"))
                return true;
            else
                return false;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    @Override
    public void setScreenParent(ScreenController screenPage) {
        myController = screenPage;
    }
}