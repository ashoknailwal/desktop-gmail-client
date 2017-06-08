package dialogActivities;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebHistory;
import javafx.scene.web.WebView;

/**
 * Created by Ashok on 4/29/2017.
 */
public class ZoomInMailView {
    BorderPane container;
    WebView webView ;
    WebEngine webEngine;
    JFXButton back;
    JFXButton close;
    JFXDialog d;
    String body;

    public ZoomInMailView(){
        container = new BorderPane();
        container.setPrefHeight(600);
        container.setPrefWidth(800);
        webView = new WebView();
        webEngine = webView.getEngine();
        webEngine.setJavaScriptEnabled(true);
        back = new JFXButton("back");
        close = new JFXButton("close");
        back.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                goBack();
            }
        });
        close.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                d.close();
            }
        });
        HBox childContainer = new HBox();
        childContainer.setPrefHeight(50);
        childContainer.setAlignment(Pos.CENTER_LEFT);
        childContainer.getChildren().addAll(back, close);
        HBox.setMargin(back, new Insets(0,20,0,20));
        container.setTop(childContainer);
        container.setCenter(webView);

    }

    public void setInfo(String body, JFXDialog d){
        this.d = d;
        this.body = body;
        webEngine.loadContent(body);
    }

    public  BorderPane getContainer() { return container;}

    public void goBack(){
        final WebHistory history = webEngine.getHistory();
        ObservableList<WebHistory.Entry> entryList = history.getEntries();
        int currentIndex = history.getCurrentIndex();
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                try {
                    history.go(-1);
                }catch(Exception e){
                    webEngine.loadContent(body);
                }
            }
        });
    }


}
