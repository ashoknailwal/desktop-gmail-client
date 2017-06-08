package gmailServices;

import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Draft;
import com.google.api.services.gmail.model.Message;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ashok on 4/15/2017.
 */
public class GmailMessages {

    public static String USERS_EMAIL_ADDRESS;

    public static int inboxLastIndex = 0;
    public static int draftLastIndex = 0;
    public static int sentLastIndex = 0;
    public static int trashLastIndex = 0;
    public static int searchLastIndex = 0;

    public static int inboxMaxSize;
    public static int sentMaxSize;
    public static int draftMaxSize;
    public static int trashMaxSize;
    public static int searchMaxSize;

    public static List<Message> inboxList = null;
    public static List<Draft> draftList = null;
    public static List<Message> sentList = null;
    public static List<Message> trashList = null;
    public static List<Message> searchList = null;

    public static String inboxStartHistoryId = null;
    public static String sentStartHistoryId = null;
    public static String draftStartHistoryId = null;
    public static String trashStartHistoryId = null;

    public static ObservableList<FormattedMessage> inboxMessages = FXCollections.observableArrayList();
    public static ObservableList<FormattedMessage> draftMessages = FXCollections.observableArrayList();
    public static ObservableList<FormattedMessage> sentMessages = FXCollections.observableArrayList();
    public static ObservableList<FormattedMessage> trashMessages = FXCollections.observableArrayList();
    public static ObservableList<FormattedMessage> searchMessages = FXCollections.observableArrayList();

    public static List<Service<Void>> activeSearches = new ArrayList<>();

 }
