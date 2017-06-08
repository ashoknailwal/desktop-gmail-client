package controllers;

import customList.CustomListCell;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXRippler;
import database.HelperValues;
import gmailServices.FormattedMessage;
import gmailServices.GmailMessages;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.util.Callback;

/**
 * Created by Ashok on 4/14/2017.
 */
public class MainUIController implements ControlledScreen{

    ScreenController myController;
    @FXML
    private Region inboxButton;

    @FXML
    private Region sentButton;

    @FXML
    private Region draftButton;

    @FXML
    private Region trashButton;

    @FXML
    private JFXButton logout;

    @FXML
    private JFXListView<FormattedMessage> messageListView;

    @FXML
    private Label folderLabel;

    @FXML
    private Label fromLabel;

    @FXML
    private Label dateLabel;

    @FXML
    private Label subjectLabel;

    @FXML
    private WebView messageWebview;



    @FXML
    void draftClicked(MouseEvent event) {

    }

    @FXML
    void inboxClicked(MouseEvent event) {

    }

    @FXML
    void logoutFromGmail(ActionEvent event) {
        try {
            java.io.File DATA_STORE_DIR = new java.io.File(
                    System.getProperty("user.home"), ".credentials/Amail");
            DATA_STORE_DIR.delete();
            HelperValues.deleteHelperValue(HelperValues.loggedIn);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @FXML
    void sentClicked(MouseEvent event) {

    }

    @FXML
    void trashClicked(MouseEvent event) {

    }

    private WebEngine webEngine;

    @FXML
    void initialize(){
        webEngine = messageWebview.getEngine();

        folderLabel.setText("INBOX");
        webEngine.setJavaScriptEnabled(true);

        messageListView.setItems(GmailMessages.inboxMessages);
        messageListView.setCellFactory(new Callback<ListView<FormattedMessage>, ListCell<FormattedMessage>>() {
            @Override
            public ListCell<FormattedMessage> call(ListView<FormattedMessage> param) {
                return new CustomListCell(folderLabel.getText());
            }
        });
        messageListView.setExpanded(true);
        messageListView.depthProperty().set(2);

        messageListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<FormattedMessage>() {
            @Override
            public void changed(ObservableValue<? extends FormattedMessage> observable, FormattedMessage oldValue, FormattedMessage newValue) {
                setMessage(newValue);
            }
        });

    }

    private JFXRippler setRippler(Node node){
        JFXRippler rippler = new JFXRippler(node);
        return rippler;
    }

    private void setMessage(FormattedMessage m){
        subjectLabel.setText(m.getSubject());
        fromLabel.setText(m.getFrom());
        dateLabel.setText(m.getDate());

        /*try {

            //webEngine.loadContent(GmailOperations.getMessageBody(m.getMessageId()));
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }



    @Override
    public void setScreenParent(ScreenController screenPage) {
        myController = screenPage;
    }
}
