package database;

import gmailServices.FormattedMessage;
import gmailServices.GmailMessages;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by Ashok on 4/15/2017.
 */
public class SaveMessages {

    public static void saveMailbox(){
        try{
            saveInbox(GmailMessages.inboxMessages);
            saveSent(GmailMessages.sentMessages);
            saveDraft(GmailMessages.draftMessages);
            saveTrash(GmailMessages.trashMessages);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error at saveMailBox");
        }

    }
    public static void saveInbox(List<FormattedMessage> inboxMessages) throws SQLException, ClassNotFoundException {
        PreparedStatement statement = h2DBConnection.getConnection().prepareStatement("INSERT INTO inbox VALUES(?, ?, ?, ?, ?, ?, ?, ?)");
        for(FormattedMessage message: inboxMessages){
            statement.setString(1,message.getMessageId());
            statement.setString(2, message.getThreadId());
            statement.setString(3,message.getHistoryId());
            statement.setString(4,message.getTo());
            statement.setString(5,message.getFrom());
            statement.setString(6,message.getDate());
            statement.setString(7,message.getSubject());
            statement.setInt(8,message.getIsUnread());
            statement.executeUpdate();
        }
        statement.close();

    }

    public static void saveSent(List<FormattedMessage> sentMessages) throws SQLException, ClassNotFoundException {
        PreparedStatement statement = h2DBConnection.getConnection().prepareStatement("INSERT INTO sent VALUES(?, ?, ?, ?, ?, ?, ?, ?)");
        for(FormattedMessage message: sentMessages){
            statement.setString(1,message.getMessageId());
            statement.setString(2, message.getThreadId());
            statement.setString(3,message.getHistoryId());
            statement.setString(4,message.getTo());
            statement.setString(5,message.getFrom());
            statement.setString(6,message.getDate());
            statement.setString(7,message.getSubject());
            statement.setInt(8,message.getIsUnread());
            statement.executeUpdate();
        }
        statement.close();

    }

    public static void saveDraft(List<FormattedMessage> draftMessages) throws SQLException, ClassNotFoundException {
        PreparedStatement statement = h2DBConnection.getConnection().prepareStatement("INSERT INTO draft VALUES(?, ?, ?, ?, ?, ?, ?, ?)");
        for(FormattedMessage message: draftMessages){
            statement.setString(1,message.getMessageId());
            statement.setString(2, message.getThreadId());
            statement.setString(3,message.getHistoryId());
            statement.setString(4,message.getTo());
            statement.setString(5,message.getFrom());
            statement.setString(6,message.getDate());
            statement.setString(7,message.getSubject());
            statement.setInt(8,message.getIsUnread());
            statement.executeUpdate();
        }
        statement.close();

    }

    public static void saveTrash(List<FormattedMessage> trashMessages) throws SQLException, ClassNotFoundException {
        PreparedStatement statement = h2DBConnection.getConnection().prepareStatement("INSERT INTO trash VALUES(?, ?, ?, ?, ?, ?, ?, ?)");
        for(FormattedMessage message: trashMessages){
            statement.setString(1,message.getMessageId());
            statement.setString(2, message.getThreadId());
            statement.setString(3,message.getHistoryId());
            statement.setString(4,message.getTo());
            statement.setString(5,message.getFrom());
            statement.setString(6,message.getDate());
            statement.setString(7,message.getSubject());
            statement.setInt(8,message.getIsUnread());
            statement.executeUpdate();
        }
        statement.close();

    }

}
