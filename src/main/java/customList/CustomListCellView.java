package customList;

import gmailServices.FormattedMessage;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;

/**
 * Created by Ashok on 4/17/2017.
 */
public class CustomListCellView {

        GridPane listCellBox = new GridPane();
        ImageView profilePic = new ImageView();
        Label name = new Label();
        Label subject = new Label();
        Label dateText = new Label();

    public CustomListCellView(){
            sceneLayout();
        }

        public void sceneLayout(){

            ColumnConstraints column0 = new ColumnConstraints(50,50,100);
            ColumnConstraints column1 = new ColumnConstraints(50,120,250);
            ColumnConstraints column2 = new ColumnConstraints(50, 230, 250);
            ColumnConstraints column3 = new ColumnConstraints(50, 100, 150);
            column0.setHgrow(Priority.ALWAYS);
            column1.setHgrow(Priority.ALWAYS);
            column2.setHgrow(Priority.ALWAYS);
            column3.setHgrow(Priority.ALWAYS);
            listCellBox.getColumnConstraints().add(column0);
            listCellBox.getColumnConstraints().add(column1);
            listCellBox.getColumnConstraints().add(column2);
            listCellBox.getColumnConstraints().add(column3);
            listCellBox.getRowConstraints().add(new RowConstraints());

            name.setAlignment(Pos.CENTER_LEFT);
            dateText.setAlignment(Pos.CENTER_RIGHT);
            subject.setAlignment(Pos.CENTER_LEFT);

            profilePic.setFitWidth(40);
            profilePic.setFitHeight(40);


            BorderPane col0 = new BorderPane();
            col0.setCenter(profilePic);

            listCellBox.add(col0, 0,0);
            listCellBox.add(name,1,0);
            listCellBox.add(subject, 2,0);
            listCellBox.add(dateText,3,0);


        }

        public void setInfo(FormattedMessage data, String labelId){
            subject.setText(data.getSubject());
            switch(labelId){
                case "INBOX" : name.setText(data.getFrom());
                    break;
                case "SENT" : name.setText(data.getTo());
                    break;
                case "DRAFT" : name.setText(data.getTo());
                    break;
                case "TRASH" : name.setText(data.getFrom());
            }
            profilePic.setImage(new Image(getClass().getResourceAsStream("/account_circle_grey_192x192.png")));
            dateText.setText(data.getDate());

        }

        public GridPane getListCellBox() { return listCellBox;}

}
