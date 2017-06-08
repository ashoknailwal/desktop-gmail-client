package customList;

import com.jfoenix.controls.JFXButton;
import gmailServices.FormattedMessage;
import gmailServices.GmailMessages;
import gmailServices.GmailOperations;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import utilClasses.NotifyUser;
import utilClasses.TextDraw;

import java.io.IOException;

/**
 * Created by Ashok on 4/20/2017.
 */
public class CustomListCellView1 {
    GridPane listCellBox = new GridPane();
    StackPane profilePic = new StackPane();
    Label name = new Label();
    Label subject = new Label();
    Label dateText = new Label();
    String labelId;


    public CustomListCellView1(String labelId) {
        this.labelId = labelId;
        sceneLayout(); }


    public void sceneLayout(){
        ColumnConstraints column0 = new ColumnConstraints(10,45,50);
        ColumnConstraints column1 = new ColumnConstraints(10,120,2048);
        RowConstraints row = new RowConstraints(10,40,40);
        column0.setHgrow(Priority.ALWAYS);
        column1.setHgrow(Priority.ALWAYS);


        BorderPane colParent = new BorderPane();
        profilePic.setPrefWidth(50);
        profilePic.setPrefHeight(50);
        colParent.setCenter(profilePic);


        VBox colParentContainer = new VBox();

        HBox colChildContainer1 = new HBox();
        colChildContainer1.setAlignment(Pos.BOTTOM_LEFT);
        name.setPrefHeight(20);
        name.setPrefWidth(265);
        name.setFont(new Font(14));
        dateText.setAlignment(Pos.CENTER_RIGHT);
        dateText.setPrefWidth(110);
        dateText.setPrefHeight(20);
        colChildContainer1.getChildren().add(0,name);
        colChildContainer1.getChildren().add(1,dateText);
        HBox.setMargin(dateText, new Insets(0,10,2,0));
        HBox.setHgrow(name, Priority.ALWAYS);



        HBox colChildContainer2 = new HBox();
        subject.setPrefHeight(20);
        subject.setPrefWidth(665);
        //materialize delete button by removing comments




        colChildContainer2.getChildren().add(0,subject);
        HBox.setHgrow(subject,Priority.ALWAYS);


        colParentContainer.getChildren().add(0,colChildContainer1);
        colParentContainer.getChildren().add(1,colChildContainer2);
        listCellBox.add(colParent,0,0);
        listCellBox.add(colParentContainer, 1,0);

        listCellBox.setPrefHeight(40);



    }

    public void setInfo(FormattedMessage formattedMessage){
        TextDraw td = null ;
        subject.setText(formattedMessage.getSubject());
        switch(labelId){
            case "INBOX" : name.setText(formattedMessage.getFrom());
                td = new TextDraw(formattedMessage.getFromProfilePicString(), profilePic);
                break;
            case "SENT" : name.setText(formattedMessage.getTo());
                td = new TextDraw(formattedMessage.getToProfilePicString(), profilePic);
                break;
            case "DRAFT" : name.setText(formattedMessage.getTo());
                td = new TextDraw(formattedMessage.getToProfilePicString(), profilePic);
                break;
            case "TRASH" : name.setText(formattedMessage.getFrom());
                td = new TextDraw(formattedMessage.getFromProfilePicString(), profilePic);
        }
        //profilePic.setImage(new Image(getClass().getResourceAsStream("/account_circle_grey_192x192.png")));
        //TextDraw td = new TextDraw(data.getProfilePicString(), profilePic);
        td.buildCircularTextImage();

        dateText.setText(formattedMessage.getDate());

    }

    public GridPane getListCellBox() { return listCellBox;}

}
