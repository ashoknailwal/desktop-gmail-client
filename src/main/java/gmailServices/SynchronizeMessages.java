package gmailServices;

import database.HelperValues;
import database.InitDbTables;
import database.SaveMessages;
import javafx.concurrent.Task;

import java.io.IOException;

/**
 * Created by Ashok on 4/12/2017.
 */
public class SynchronizeMessages {

    public void fullSync() throws IOException {
        GmailOperations.loadMailBox();
        new InitDbTables();
        SaveMessages.saveMailbox();
        HelperValues.setHelperValues(HelperValues.loggedIn, "true");
        System.out.println("Sync done");
    }

    public void partialSync(){
        Task<Void> syncNewMails = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                while (true) {
                    try {
                        Thread.sleep(5000);
                    }catch (InterruptedException e){
                        e.printStackTrace();
                    }
                    GmailOperations.getNewInboxMails();
                    GmailOperations.getNewSentMails();
                    GmailOperations.getNewDraftMails();
                    GmailOperations.getNewTrashMails();

                }
            }
        };

        Thread backgroundThread = new Thread(syncNewMails);
        backgroundThread.setDaemon(true);
        backgroundThread.start();

    }

}
