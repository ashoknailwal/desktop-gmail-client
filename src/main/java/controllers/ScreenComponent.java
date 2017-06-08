package controllers;

import com.google.api.services.gmail.model.Message;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import com.jfoenix.controls.JFXSnackbar;
import dialogActivities.ComposeActivity;
import dialogActivities.ZoomInMailView;
import gmailServices.FormattedMessage;
import gmailServices.GmailMessages;
import gmailServices.GmailOperations;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import sun.awt.PlatformFont;
import utilClasses.NotifyUser;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by Ashok on 4/18/2017.
 */
public class ScreenComponent extends GridPane {

    private static ScreenComponent screenComponent;
    private static StackPane componentParent;
    private static BorderPane inboxComponent;
    private static BorderPane draftComponent;
    private static BorderPane sentComponent;
    private static BorderPane trashComponent;

    private Label subjectLabel;
    private Label toFromLabel;
    private Label dateLabel;
    private WebView messageDisplay;
    private WebEngine messageEngine;
    private JFXButton attachments;
    private TextArea messageTextArea;
    private JFXButton replyButton;
    private JFXButton editDraft;
    private JFXButton forwardSent;
    private JFXButton restore;
    private JFXButton deleteInboxMessage;
    private JFXButton deleteSentMessage;
    private JFXButton zoomMail;
    private JFXButton forwardInbox;
    private String folderLabel;

    private Message message = null;
    private FormattedMessage formattedMessage = null;
    int index;

    public ScreenComponent(){
        initComponent();
    }

    private void setActions(){
        attachments.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                JFXSnackbar snackbar = new JFXSnackbar(MainUI3Controller.getScreenParent());
                snackbar.show("Downloading", 2000);

                Task<Void> downloadAttachments = new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        try {
                            List<File> attachmentsList = GmailOperations.downloadAttachments(message, System.getProperty("user.home"));
                            if (attachmentsList != null && !attachmentsList.isEmpty()) {
                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        NotifyUser.getNotification("Attachments Downloaded", "" + attachmentsList.size() + " Attachments successfully downloaded to" + System.getProperty("user.home")).showInformation();

                                    }
                                });
                            } else {
                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        JFXSnackbar snackbar = new JFXSnackbar(MainUI3Controller.getScreenParent());
                                        snackbar.show("No attachments with this mail", 5000);
                                    }
                                });
                            }
                        }catch (IOException e) {
                            e.printStackTrace();
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    NotifyUser.getNotification("Internet connection has lost", "Please check your internet connection").showInformation();
                                }
                            });
                        }
                        return null;
                    }
                };


                Thread startDownload = new Thread(downloadAttachments);
                startDownload.setDaemon(true);
                startDownload.start();



                //int count;
                /*
                JFXSnackbar snackbar;
                try {
                    List<File> attachmentsList = GmailOperations.downloadAttachments(message, System.getProperty("user.home"));
                    if(attachmentsList != null && !attachmentsList.isEmpty())
                        NotifyUser.getNotification("Attachments Downloaded", ""+attachmentsList.size()+" Attachments successfully downloaded to"+System.getProperty("user.home")).showInformation();
                    else {
                        snackbar = new JFXSnackbar(MainUI3Controller.getScreenParent());
                        //snackbar.getStylesheets().add(getClass().getResource("/sneckbar.css").toExternalForm());
                        snackbar.show("No attachments with this mail", 5000);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    NotifyUser.getNotification("Internet connection has lost", "Please check your internet connection").showInformation();

                } */
            }
        });

        replyButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    GmailOperations.sendMessage(formattedMessage.getFromEmailId(), GmailMessages.USERS_EMAIL_ADDRESS, "Reply for "+formattedMessage.getSubject(),
                            messageTextArea.getText(), false, null );
                    messageTextArea.setText("");
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("Change it with sneckbar");
                    NotifyUser.getNotification("Internet connection has lost", "Please check your internet connection").showInformation();
                }
            }
        });

        editDraft.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                ComposeActivity composeActivity = new ComposeActivity(formattedMessage, message, true, false);

                composeActivity.setStage(AmailMain.getStage());
                JFXDialogLayout content = new JFXDialogLayout();
                content.setHeading(new Text("Compose"));
                content.setBody(composeActivity.getContent());
                JFXDialog dialog = new JFXDialog(MainUI3Controller.getScreenParent(), content,JFXDialog.DialogTransition.CENTER);
                composeActivity.setAction(dialog);
                dialog.show();
            }
        });

        restore.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    GmailOperations.untrashMessage(formattedMessage.getMessageId());

                } catch (IOException e) {
                    NotifyUser.getNotification("Internet connection has lost", "Please check your internet connection").showInformation();
                }
            }
        });

        zoomMail.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                ZoomInMailView zoomInMailView = new ZoomInMailView();
                JFXDialogLayout content = new JFXDialogLayout();
                content.setHeading(new Text("Compose"));
                content.setBody(zoomInMailView.getContainer());
                JFXDialog dialog = new JFXDialog(MainUI3Controller.getScreenParent(), content,JFXDialog.DialogTransition.CENTER);
                zoomInMailView.setInfo(formattedMessage.getBodyText(),dialog);
                dialog.show();
                dialog.setOverlayClose(false);
            }
        });

        forwardSent.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                ComposeActivity composeActivity = new ComposeActivity(formattedMessage, message, false, true);
                composeActivity.setStage(AmailMain.getStage());
                JFXDialogLayout content = new JFXDialogLayout();
                content.setHeading(new Text("Compose"));
                content.setBody(composeActivity.getContent());
                JFXDialog dialog = new JFXDialog(MainUI3Controller.getScreenParent(), content,JFXDialog.DialogTransition.CENTER);
                composeActivity.setAction(dialog);
                dialog.show();
            }
        });

        forwardInbox.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                ComposeActivity composeActivity = new ComposeActivity(formattedMessage, message, false, true);
                composeActivity.setStage(AmailMain.getStage());
                JFXDialogLayout content = new JFXDialogLayout();
                content.setHeading(new Text("Compose"));
                content.setBody(composeActivity.getContent());
                JFXDialog dialog = new JFXDialog(MainUI3Controller.getScreenParent(), content,JFXDialog.DialogTransition.CENTER);
                composeActivity.setAction(dialog);
                dialog.show();
            }
        });

    }

    private void initComponent(){
        initVariables();
        //this.getStylesheets().add(controllers.ScreenComponent.class.getResource("/uiComponents.css").toExternalForm());
        ColumnConstraints column0 = new ColumnConstraints(10,100,USE_COMPUTED_SIZE);
        ColumnConstraints column1 = new ColumnConstraints(10,100,USE_COMPUTED_SIZE);
        //column0.setHgrow(Priority.ALWAYS);
        //column1.setHgrow(Priority.ALWAYS);
        RowConstraints row0 = new RowConstraints(10,110, 195);
        RowConstraints row1 = new RowConstraints(10,360,800);
        RowConstraints row2 = new RowConstraints(10,140,250);
        row1.setVgrow(Priority.ALWAYS);
        this.getColumnConstraints().add(column0);
        this.getColumnConstraints().add(column1);
        this.getRowConstraints().add(row0);
        this.getRowConstraints().add(row1);
        this.getRowConstraints().add(row2);
        this.add(setRow0(),0,0,2,1 );
        this.add(setRow1(), 0,1,2,1);
        this.add(componentParent,0,2,2,1);
        inboxComponent = setInboxComponent();
        draftComponent = setDraftComponent();
        sentComponent = setSentComponent();
        trashComponent = setTrashComponent();
        setScreenComponent("INBOX");
        //screenComponent.setGridLinesVisible(true);

        setActions();

    }

    private BorderPane setRow0(){
        BorderPane rowParent = new BorderPane();
        BorderPane childParent = new BorderPane();
        HBox childContainer = new HBox();
        childContainer.setAlignment(Pos.CENTER);
        Region region = new Region();
        region.setPrefHeight(90);
        region.setPrefWidth(30);
        //subjectLabel = new Label();
        subjectLabel.setPrefHeight(20);
        subjectLabel.setPrefWidth(430);
        subjectLabel.setFont(new Font(24));
        subjectLabel.setText("Hello");
        childContainer.getChildren().add(region);
        childContainer.getChildren().add(subjectLabel);
        childParent.setLeft(childContainer);
        HBox childContainer2 = new HBox();
        childContainer2.setAlignment(Pos.CENTER_LEFT);
        Region region1 = new Region();
        region1.setPrefWidth(30);
        region1.setPrefHeight(40);
        //toFromLabel = new Label();
        toFromLabel.setText("Hello");
        //dateLabel = new Label();
        dateLabel.setText("hello");
        childContainer2.getChildren().add(region1);
        childContainer2.getChildren().add(toFromLabel);
        childContainer2.getChildren().add(dateLabel);
        childContainer2.setMargin(dateLabel,new Insets(0,0,0,20));
        rowParent.setCenter(childParent);
        rowParent.setBottom(childContainer2);
        return rowParent;
    }

    private VBox setRow1(){
        VBox parentContainer = new VBox();
        // messageDisplay = new WebView();
        //messageEngine = messageDisplay.getEngine();
        messageDisplay.setPrefHeight(320);
        parentContainer.getChildren().add(messageDisplay);
        VBox.setVgrow(messageDisplay, Priority.ALWAYS);
        HBox childContainer = new HBox();
        childContainer.setPrefHeight(50);
        childContainer.setAlignment(Pos.CENTER_LEFT);
        //attachments.getStyleClass().add("button-raised");
        setButtonStyle(attachments);
        childContainer.getChildren().add(attachments);
        Region region = new Region();
        childContainer.getChildren().add(region);
        childContainer.getChildren().add(zoomMail);
        childContainer.setMargin(attachments, new Insets(0,0,0,20));
        childContainer.setMargin(zoomMail, new Insets(0,20,0,0));
        HBox.setHgrow(region, Priority.ALWAYS);

        parentContainer.getChildren().add(childContainer);

        return parentContainer;
    }

    private BorderPane setInboxComponent(){
        BorderPane container = new BorderPane();
        HBox childContainer = new HBox();
        childContainer.setPrefHeight(93);
        //childContainer.setAlignment(Pos.CENTER_RIGHT);
        //messageTextArea = new TextArea();
        messageTextArea.setPromptText("Reply");
        messageTextArea.setPrefHeight(200);
        childContainer.getChildren().add(messageTextArea);
        HBox.setHgrow(messageTextArea, Priority.ALWAYS);
        HBox.setMargin(messageTextArea,new Insets(0,0,0,5));
        Region region = new Region();
        region.setPrefHeight(90);
        region.setPrefWidth(45);
        childContainer.getChildren().add(region);

        container.setCenter(childContainer);
        HBox childContainer1 = new HBox();
        childContainer1.setPrefHeight(45);
        childContainer1.setAlignment(Pos.CENTER_RIGHT);
        //replyButton = new JFXButton("Send");
        setButtonStyle(replyButton);
        setButtonStyle(deleteInboxMessage);
        //childContainer1.getChildren().add(deleteInboxMessage);
        childContainer1.getChildren().add(forwardInbox);
        childContainer1.getChildren().add(replyButton);

        HBox.setMargin(replyButton, new Insets(0,20,0,20));
        container.setBottom(childContainer1);

        deleteInboxMessage.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try{
                    GmailOperations.trashMessage(formattedMessage.getMessageId());
                    GmailMessages.inboxMessages.remove(index);

                } catch (IOException e) {
                    e.printStackTrace();
                    NotifyUser.getNotification("Internet connection has lost", "Please check your internet connection").showInformation();
                }
            }
        });

        return container;
    }

    private BorderPane setDraftComponent(){
        BorderPane container = new BorderPane();
        HBox childContainer = new HBox();
        childContainer.setPrefHeight(93);
        //childContainer.setAlignment(Pos.CENTER_RIGHT);
        //messageTextArea = new TextArea();

        container.setCenter(childContainer);
        HBox childContainer1 = new HBox();
        childContainer1.setPrefHeight(45);
        childContainer1.setAlignment(Pos.CENTER_RIGHT);
        //replyButton = new JFXButton("Send");
        setButtonStyle(editDraft);
        childContainer1.getChildren().add(editDraft);
        HBox.setMargin(editDraft, new Insets(0,20,0,0));
        container.setBottom(childContainer1);

        return container;
    }

    private BorderPane setSentComponent(){
        BorderPane container = new BorderPane();
        HBox childContainer = new HBox();
        childContainer.setPrefHeight(93);
        //childContainer.setAlignment(Pos.CENTER_RIGHT);
        //messageTextArea = new TextArea();

        container.setCenter(childContainer);
        HBox childContainer1 = new HBox();
        childContainer1.setPrefHeight(45);
        childContainer1.setAlignment(Pos.CENTER_RIGHT);
        setButtonStyle(forwardSent);
        setButtonStyle(deleteSentMessage);
        //childContainer1.getChildren().add(deleteSentMessage);
        childContainer1.getChildren().add(forwardSent);
        HBox.setMargin(forwardSent, new Insets(0,20,0,20));
        container.setBottom(childContainer1);
        deleteSentMessage.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try{
                    GmailOperations.trashMessage(formattedMessage.getMessageId());
                    GmailMessages.inboxMessages.remove(index);

                } catch (IOException e) {
                    e.printStackTrace();
                    NotifyUser.getNotification("Internet connection has lost", "Please check your internet connection").showInformation();
                }
            }
        });


        return container;
    }

    private BorderPane setTrashComponent(){
        BorderPane container = new BorderPane();
        HBox childContainer = new HBox();
        childContainer.setPrefHeight(93);
        //childContainer.setAlignment(Pos.CENTER_RIGHT);
        //messageTextArea = new TextArea();

        container.setCenter(childContainer);
        HBox childContainer1 = new HBox();
        childContainer1.setPrefHeight(45);
        childContainer1.setAlignment(Pos.CENTER_RIGHT);
        setButtonStyle(restore);
        childContainer1.getChildren().add(restore);
        HBox.setMargin(restore, new Insets(0,20,0,0));
        container.setBottom(childContainer1);

        return container;
    }
    private void initVariables(){
        componentParent = new StackPane();
        subjectLabel = new Label();
        toFromLabel = new Label();
        dateLabel = new Label();
        messageDisplay = new WebView();
        messageEngine = messageDisplay.getEngine();
        messageEngine.setJavaScriptEnabled(true);
        attachments = new JFXButton("Attachments");
        messageTextArea = new TextArea();
        replyButton = new JFXButton("Send");
        editDraft = new JFXButton("Edit");
        forwardSent = new JFXButton("Edit");
        restore = new JFXButton("Restore");
        deleteInboxMessage = new JFXButton();
        deleteSentMessage = new JFXButton();
        ImageView deleteImage = new ImageView(new Image(getClass().getResourceAsStream("/delete.png")));
        deleteImage.setFitWidth(20);
        deleteImage.setFitHeight(20);
        deleteInboxMessage.setGraphic(deleteImage);
        deleteSentMessage.setGraphic(deleteImage);
        zoomMail = new JFXButton("Zoom");
        setButtonStyle(zoomMail);
        forwardInbox = new JFXButton("Forward");
        setButtonStyle(forwardInbox);
    }

    public void setScreenComponent(String folderName){
        folderLabel = folderName;
        switch(folderName){
            case "INBOX": setComponent(inboxComponent);
                break;
            case "DRAFT": setComponent(draftComponent);
                break;
            case "SENT": setComponent(sentComponent);
                break;
            case "TRASH": setComponent(trashComponent);
        }

    }
    public void setScreenComponent(String folderName, int index){
        this.index = index;
        folderLabel = folderName;
        switch(folderName){
            case "INBOX": setComponent(inboxComponent);
                break;
            case "DRAFT": setComponent(draftComponent);
                break;
            case "SENT": setComponent(sentComponent);
                break;
            case "TRASH": setComponent(trashComponent);
        }

    }

    private void setComponent(BorderPane component){
        if(componentParent.getChildren().isEmpty())
            componentParent.getChildren().add(0,component);
        else{
            componentParent.getChildren().remove(0);
            componentParent.getChildren().add(0,component);
        }
    }


    // load message into the ui elements , IOException due to internet error

    public void setInfo(FormattedMessage formattedMessage){
        this.formattedMessage = formattedMessage;
        try {
            if(formattedMessage.getBodyText()==null || formattedMessage.getBodyText().equals("")) {
                message = GmailOperations.getMessage(formattedMessage.getMessageId());
                formattedMessage.setBodyText(GmailOperations.getMessageBody(message));
                messageEngine.loadContent(formattedMessage.getBodyText());
            }
            else
                messageEngine.loadContent(formattedMessage.getBodyText());
        }catch (IOException e) {
            e.printStackTrace();
            if(AmailMain.isInternetUp){
                AmailMain.isInternetUp = false;
                NotifyUser.getNotification("Internet connection has lost", "Please check your internet connection").showInformation();
            }
        }
        subjectLabel.setText(formattedMessage.getSubject());
        dateLabel.setText(formattedMessage.getDate());
        if(folderLabel == "INBOX" || folderLabel == "TRASH"){
            toFromLabel.setText("From "+formattedMessage.getFrom()+" to you");
        }
        else
            toFromLabel.setText("From you to "+formattedMessage.getTo());

    }



    public void setButtonStyle(JFXButton button){
        button.setStyle("-fx-background-color: #0091EA;");
        button.setButtonType(JFXButton.ButtonType.RAISED);
        button.setTextFill(Paint.valueOf("WHITE"));
    }

    public static ScreenComponent getInstance(){
        if(screenComponent == null)
            screenComponent = new ScreenComponent();
        return screenComponent;
    }

}
