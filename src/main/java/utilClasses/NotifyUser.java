package utilClasses;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;

/**
 * Created by Ashok on 4/28/2017.
 */
public class NotifyUser {
    /***************************************************************************
     * * Constructors * *
     **************************************************************************/
    private static Notifications notifications = null;

    public static Notifications getNotification(String title, String text){
        notifications = Notifications.create().title(title).text(text).graphic(null).hideAfter(Duration.seconds(10)).position(Pos.BOTTOM_RIGHT);
        return notifications;
    }

}
