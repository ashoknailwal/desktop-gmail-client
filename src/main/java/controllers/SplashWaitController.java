package controllers;

import gmailServices.Login;
import gmailServices.SynchronizeMessages;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import utilClasses.NotifyUser;

import java.io.IOException;

/**
 * Created by Ashok on 4/12/2017.
 */
public class SplashWaitController implements ControlledScreen {

    ScreenController myController;
    public static boolean installed;

    private static Task<Boolean> backgroundTasks;


    @FXML
    private BorderPane splashGuideWait;

    @FXML
    private Label splashWaitLabel;

    @FXML
    void initialize(){

        backgroundTasks = new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {
                try{
                    Login.startAuthentication();
                    new SynchronizeMessages().fullSync();

                }catch (IOException e){
                    e.printStackTrace();
                    System.out.println("Internet Error at splashWait Task");
                    AmailMain.isInternetUp = false;
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            NotifyUser.getNotification("Internet Connection has lost",
                                    "Please check your internet connection").showInformation();
                        }
                    });
                    return false;
                }
                return true;
            }
        };

        backgroundTasks.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
                if(backgroundTasks.getValue()) {
                    //controllers.AmailMain.getStage().setResizable(true);
                    myController.setScreen(AmailMain.mainUIId);
                    new SynchronizeMessages().partialSync();
                }
            }
        });


    }

    public static void startBackgroundTasks(){
        Thread bgTasks = new Thread(backgroundTasks);
        bgTasks.setDaemon(true);
        bgTasks.start();
    }

    @Override
    public void setScreenParent(ScreenController screenPage) {
        myController = screenPage;
    }
}
