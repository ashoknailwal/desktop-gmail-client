package customList;

import com.jfoenix.controls.JFXListCell;
import gmailServices.FormattedMessage;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;

/**
 * Created by Ashok on 4/17/2017.
 */
public class CustomListCell extends JFXListCell<FormattedMessage> {

    private CustomListCellView1 clcv;
    private GridPane container = null;
    private String labelId;

    public CustomListCell(String labelId) {
        super();
        this.labelId = labelId;
        clcv = new CustomListCellView1(labelId);
        setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {

            }
        });

    }


    @Override
    public void updateItem(FormattedMessage data, boolean empty) {
        super.updateItem(data, empty);
        if (data != null && !empty) {
            container = clcv.getListCellBox();
            container.setMouseTransparent(true);
            clcv.setInfo(data);
            this.setPrefWidth(0);
            container.prefWidthProperty().bind(this.widthProperty());
            setGraphic(container);
        }

    }
}
