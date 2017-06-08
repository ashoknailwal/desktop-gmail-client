package dialogActivities;

import com.google.api.services.gmail.model.Message;
import com.jfoenix.controls.*;
import controllers.MainUI3Controller;
import gmailServices.FormattedMessage;
import gmailServices.GmailOperations;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import utilClasses.NotifyUser;

import javax.mail.MessagingException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ashok on 4/20/2017.
 */
public class ComposeActivity {

    private Stage stage;

    private String to = "";
    private String from = "";
    private String sub = " ";
    private String body = " ";
    private FormattedMessage formattedMessage;
    private Message message;
    private boolean isEditDraft;
    private boolean isForward;
    public List<File> attachments = new ArrayList<File>();
    long attachmentFilesSize = 0;
   // HashMap<String , byte[]> previouslyAddedAttachments;

    VBox parentContainer = new VBox();
    Label fromLabel = new Label("From");
    Label toLabel = new Label("To");
    Label subLabel = new Label("Subject");
    JFXCheckBox isHtml = new JFXCheckBox("Html");
    JFXTextField fromTextField = new JFXTextField();
    JFXTextField toTextField = new JFXTextField();
    JFXTextField subTextField = new JFXTextField();
    JFXTextArea composeTextArea = new JFXTextArea();
    JFXButton attachFile = new JFXButton();
    JFXButton sendButton = new JFXButton("Send");
    JFXButton saveButton = new JFXButton("Save");
    JFXButton discardButton = new JFXButton("Discard");
    ButtonBar attachedFilesButtonParent = new ButtonBar();
    long attatchedFileSize = 0;

    FileChooser fileChooser = new FileChooser();


    public ComposeActivity(FormattedMessage m, Message message, boolean isEditDraft, boolean isForward){
        formattedMessage = m;
        this.message = message;
        this.isEditDraft = isEditDraft;
        this.isForward = isForward;
        sceneLayout();}

    public  VBox getContent(){  return parentContainer;}


    public void setInfo(){
        List<File> previouslyAddedAttachments = null;
        if(formattedMessage != null && message != null){
            toTextField.setText(formattedMessage.getToEmailId());
            fromTextField.setText(formattedMessage.getFromEmailId());
            subTextField.setText(formattedMessage.getSubject());
            composeTextArea.setText(formattedMessage.getBodyText());
            try {
                previouslyAddedAttachments = GmailOperations.downloadAttachments(message,System.getProperty("user.home")+"/temp");
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (previouslyAddedAttachments != null) {
                for(File f: previouslyAddedAttachments){
                    Button b = new Button(f.getName());
                    attachedFilesButtonParent.getButtons().add(b);
                    b.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent event) {
                            attachments.remove(attachedFilesButtonParent.getButtons().indexOf(b));
                            attachedFilesButtonParent.getButtons().remove(b);
                        }
                    });
                    attachments.add(f);
                }
            }
            /*try {
                previouslyAddedAttachments = GmailOperations.getAttachments(message);
            } catch (IOException e) {
                e.printStackTrace();
            }*/
        }

    }


    public void sceneLayout(){

        parentContainer.setPrefHeight(430);
        parentContainer.setPrefWidth(460);

        HBox childContainer1 = new HBox();
        childContainer1.setAlignment(Pos.CENTER_LEFT);
        childContainer1.setPrefHeight(40);
        fromTextField.setPrefHeight(30);
        fromTextField.setPrefWidth(275);
        childContainer1.getChildren().add(0,fromLabel);
        childContainer1.getChildren().add(1,fromTextField);
        HBox.setMargin(fromLabel,new Insets(0,20,0,20));


        HBox childContainer2 = new HBox();
        childContainer2.setAlignment(Pos.CENTER_LEFT);
        childContainer2.setPrefHeight(40);
        toTextField.setPrefHeight(30);
        toTextField.setPrefWidth(275);
        childContainer2.getChildren().add(0,toLabel);
        childContainer2.getChildren().add(1,toTextField);
        HBox.setMargin(toLabel,new Insets(0,35,0,20));



        HBox childContainer3 = new HBox();
        childContainer3.setAlignment(Pos.CENTER_LEFT);
        childContainer3.setPrefHeight(40);
        subTextField.setPrefHeight(30);
        subTextField.setPrefWidth(275);
        childContainer3.getChildren().add(0,subLabel);
        childContainer3.getChildren().add(1,subTextField);
        HBox.setMargin(subLabel,new Insets(0,10,0,20));



        HBox childContainer4 = new HBox();
        childContainer4.setAlignment(Pos.CENTER_RIGHT);
        childContainer4.getChildren().add(isHtml);
        HBox.setMargin(isHtml, new Insets(0,20,0,0));



        HBox childContainer5 = new HBox();
        childContainer5.getChildren().add(composeTextArea);
        HBox.setHgrow(composeTextArea, Priority.ALWAYS);



        HBox childContainer6 = new HBox();
        ImageView attachPinImage = new ImageView(new Image(getClass().getResourceAsStream("/ic_attach_file_black.png")));
        attachPinImage.setFitHeight(20);
        attachPinImage.setFitWidth(20);
        attachFile.setGraphic(attachPinImage);
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setContent(attachedFilesButtonParent);
        scrollPane.setStyle("-fx-background: #ffffff");
        scrollPane.getStylesheets().add(getClass().getResource("/scrollpane.css").toExternalForm());
        childContainer6.getChildren().add(attachFile);
        childContainer6.getChildren().add(scrollPane);
        HBox.setMargin(attachFile, new Insets(5,5,5,20));
        HBox.setMargin(scrollPane, new Insets(5,0,5,0));
        HBox.setHgrow(scrollPane,Priority.ALWAYS);


        HBox childContainer7 = new HBox();
        childContainer7.setAlignment(Pos.CENTER_RIGHT);
        childContainer7.setPrefHeight(50);
        childContainer7.getChildren().add(sendButton);
        childContainer7.getChildren().add(saveButton);
        childContainer7.getChildren().add(discardButton);
        HBox.setMargin(sendButton,new Insets(0,10,0,5));
        HBox.setMargin(saveButton,new Insets(0,5,0,5));
        HBox.setMargin(discardButton,new Insets(0,5,0,5));



        parentContainer.getChildren().add(childContainer1);
        parentContainer.getChildren().add(childContainer2);
        parentContainer.getChildren().add(childContainer3);
        parentContainer.getChildren().add(childContainer4);
        parentContainer.getChildren().add(childContainer5);
        parentContainer.getChildren().add(childContainer6);
        parentContainer.getChildren().add(childContainer7);

        VBox.setVgrow(childContainer5,Priority.ALWAYS);

        setInfo();

    }

    public void setAction(JFXDialog d){
        sendButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                setLocalVariables();
                JFXSnackbar snackbar;
                try {
                    if(checkEmptyValues()) {
                        GmailOperations.sendMessage(to, from, sub, body, isHtml.isSelected(), attachments);
                        d.close();
                    }
                    else{
                        snackbar = new JFXSnackbar(parentContainer);
                        //snackbar.getStylesheets().add(getClass().getResource("/sneckbar.css").toExternalForm());
                        snackbar.show("Fields cannot be empty", 3000);
                    }
                } catch (MessagingException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });

        attachFile.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

                fileChooser.setTitle("Open Attachment File");
                fileChooser.getExtensionFilters().addAll(
                        new FileChooser.ExtensionFilter("Text Files", "*.txt"),
                        new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif"),
                        new FileChooser.ExtensionFilter("Audio Files", "*.wav", "*.mp3", "*.aac"),
                        new FileChooser.ExtensionFilter("All Files", "*.*"));
                List<File> fileList = fileChooser.showOpenMultipleDialog(stage);
                if (fileList != null && checkFilesSize(fileList)) {
                    attachFilesToUI(fileList);
                }
            }
        });
        saveButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                setLocalVariables();
                if(isEditDraft){
                    try {
                        GmailOperations.updateDraft(formattedMessage.getDraftId(),to,from,sub,body,isHtml.isSelected(),attachments);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (MessagingException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    try {
                        GmailOperations.createDraft(to, from, sub, body, isHtml.isSelected(), attachments);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (MessagingException e) {
                        e.printStackTrace();
                    }
                }
                d.close();
            }
        });


        discardButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                d.close();
            }
        });

        composeTextArea.setOnDragOver(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                if(event.getDragboard().hasFiles())
                  event.acceptTransferModes(TransferMode.ANY);
            }
        });
        composeTextArea.setOnDragDropped(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                List<File> files = event.getDragboard().getFiles();
                if(files != null && checkFilesSize(files)){
                    attachFilesToUI(files);
                }
                else{
                    NotifyUser.getNotification("Attention", "Attachments size exceeded the limit of 35mb").showWarning();
                }
            }
        });

    }

    public boolean checkFilesSize(List<File> files){
        long size = 0;
        for(File file: files){
            size = size + file.length();
        }
        if((attachmentFilesSize + size) < 35000000) {
            System.out.println(size);
            return true;
        }
        System.out.println("Size exceeded : "+size);
        return false;
    }

    public void attachFilesToUI(List<File> fileList){
        for(File f: fileList){
            Button b = new Button(f.getName());
            attachedFilesButtonParent.getButtons().add(b);
            b.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    attachments.remove(attachedFilesButtonParent.getButtons().indexOf(b));
                    attachedFilesButtonParent.getButtons().remove(b);
                }
            });
            attachments.add(f);
            attachmentFilesSize += f.length();
        }
    }




    public void setStage(Stage s){
        stage = s;
    }




    public boolean checkEmptyValues(){
        if((toTextField.getText() != null && !toTextField.getText().equals("")) && (fromTextField.getText()!= null && fromTextField.getText().equals("")) )
            return true;
        return false;
    }




    public void setLocalVariables(){
        if(toTextField.getText() != null)
            to = toTextField.getText();
        if(fromTextField.getText() != null)
            from = fromTextField.getText();
        if(subTextField.getText() != null)
            sub = subTextField.getText();
        if(composeTextArea.getText() != null)
            body = composeTextArea.getText();
    }

}
