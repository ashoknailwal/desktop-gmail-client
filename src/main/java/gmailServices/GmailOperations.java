package gmailServices;

import com.google.api.client.googleapis.batch.BatchRequest;
import com.google.api.client.googleapis.batch.json.JsonBatchCallback;
import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.repackaged.org.apache.commons.codec.binary.Base64;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.*;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import utilClasses.NotifyUser;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.internet.*;
import javax.mail.util.ByteArrayDataSource;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.*;

/**
 * Created by Ashok on 4/4/2017.
 */
public class GmailOperations {


    //***********************************************************************************
    //Inbox Specific operations

    public static List<FormattedMessage> getInboxMessages(int maxResults) throws IOException {
        List<FormattedMessage> messages = new ArrayList<>();
        int max = maxResults + GmailMessages.inboxLastIndex;
        int count = 0;
        BatchRequest b = Login.service.batch();
        JsonBatchCallback<Message> bc = new JsonBatchCallback<Message>() {
            @Override
            public void onFailure(GoogleJsonError e, HttpHeaders responseHeaders) throws IOException {
                System.out.println("Internet Connection Error");
            }

            @Override
            public void onSuccess(Message message, HttpHeaders responseHeaders) throws IOException {
                messages.add(new FormattedMessage(message));
            }
        };
        Message m = null;
        while (GmailMessages.inboxLastIndex < max && GmailMessages.inboxLastIndex < GmailMessages.inboxMaxSize) {
            m = GmailMessages.inboxList.get(GmailMessages.inboxLastIndex);
            Login.service.users().messages().get("me", m.getId()).setFormat("metadata").setMetadataHeaders(Arrays.asList("To", "From", "Date", "Subject")).queue(b, bc);
            GmailMessages.inboxLastIndex++;
            count++;
        }
        if (count != 0) {
            b.execute();
        }
        return messages;
    }

    public static void loadInbox() throws IOException {
        GmailMessages.inboxList = getAllMessagesListInLabelId(Arrays.asList("INBOX"));
        GmailMessages.inboxMaxSize = GmailMessages.inboxList.size();
        List<FormattedMessage> temp = getInboxMessages(50);
        GmailMessages.inboxMessages.addAll(temp);
        if (temp.size() != 0)
            GmailMessages.inboxStartHistoryId = temp.get(0).getHistoryId();
        //SaveMessages.saveInbox(temp);
        System.out.println("Inbox Loaded");
    }

    public static void getNewInboxMails() throws IOException {
        if (GmailMessages.inboxStartHistoryId != null) {
            List<History> historyList = getHistoryMessages("INBOX", new BigInteger(GmailMessages.inboxStartHistoryId));
            for (History history : historyList) {
                List<HistoryMessageAdded> messageAddedList = history.getMessagesAdded();
                if (messageAddedList != null) {
                    for (HistoryMessageAdded messageAdded : messageAddedList) {
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    GmailMessages.inboxMessages.add(0, new FormattedMessage(Login.service.users().messages().get("me",
                                            messageAdded.getMessage().getId()).setFormat("metadata").setMetadataHeaders(Arrays.asList("To", "From", "Date", "Subject")).execute()));
                                    GmailMessages.inboxStartHistoryId = GmailMessages.inboxMessages.get(0).getHistoryId();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            NotifyUser.getNotification("New Mails", ""+messageAddedList.size()+ " new Mails arrived");
                        }
                    });
                }
            }

        } else {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    try {
                        loadInbox();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }


    //***********************************************************************************
    //Sent folder specific operations

    public static List<FormattedMessage> getSentMessages(int maxResults) throws IOException {
        List<FormattedMessage> messages = new ArrayList<>();
        int count = 0;
        int max = GmailMessages.sentLastIndex + maxResults;
        BatchRequest b = Login.service.batch();
        JsonBatchCallback<Message> bc = new JsonBatchCallback<Message>() {
            @Override
            public void onFailure(GoogleJsonError e, HttpHeaders responseHeaders) throws IOException {
                System.out.println("Internet Connection Error");
            }

            @Override
            public void onSuccess(Message message, HttpHeaders responseHeaders) throws IOException {
                messages.add(new FormattedMessage(message));
            }
        };
        Message m = null;
        while (GmailMessages.sentLastIndex < max && GmailMessages.sentLastIndex < GmailMessages.sentMaxSize) {
            m = GmailMessages.sentList.get(GmailMessages.sentLastIndex);
            Login.service.users().messages().get("me", m.getId()).setFormat("metadata").setMetadataHeaders(Arrays.asList("To", "From", "Date", "Subject")).queue(b, bc);
            GmailMessages.sentLastIndex++;
            count++;
        }
        if (count != 0)
            b.execute();
        return messages;
    }

    public static void loadSent() throws IOException {
        GmailMessages.sentList = getAllMessagesListInLabelId(Arrays.asList("SENT"));
        GmailMessages.sentMaxSize = GmailMessages.sentList.size();
        List<FormattedMessage> temp = getSentMessages(50);
        GmailMessages.sentMessages.addAll(temp);
        if (temp.size() != 0)
            GmailMessages.sentStartHistoryId = temp.get(0).getHistoryId();
        //SaveMessages.saveSent(temp);
        System.out.println("sent Loaded");
    }

    public static void getNewSentMails() throws IOException {
        if (GmailMessages.sentStartHistoryId != null) {
            List<History> historyList = getHistoryMessages("SENT", new BigInteger(GmailMessages.sentStartHistoryId));
            for (History history : historyList) {
                List<HistoryMessageAdded> messageAddedList = history.getMessagesAdded();
                if (messageAddedList != null) {
                    for (HistoryMessageAdded messageAdded : messageAddedList) {
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    GmailMessages.sentMessages.add(0, new FormattedMessage(Login.service.users().messages().get("me", messageAdded.getMessage()
                                            .getId()).setFormat("metadata").setMetadataHeaders(Arrays.asList("To", "From", "Date", "Subject")).execute()));
                                    GmailMessages.sentStartHistoryId = GmailMessages.sentMessages.get(0).getHistoryId();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                }
            }

        } else {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    try {
                        loadSent();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        }

    }


    //***********************************************************************************
    //Drafts specific operations
    public static List<Draft> getAllDraftsList() throws IOException {
        ListDraftsResponse response = Login.service.users().drafts().list("me").execute();
        List<Draft> drafts = new ArrayList<Draft>();
        while (response.getDrafts() != null) {
            drafts.addAll(response.getDrafts());
            if (response.getNextPageToken() != null) {
                String pageToken = response.getNextPageToken();
                response = Login.service.users().drafts().list("me").setPageToken(pageToken).execute();
            } else
                break;
        }
        // messagesList = messages;
        return drafts;
    }

    public static List<FormattedMessage> getDraftMessages(int maxResults) throws IOException {
        List<FormattedMessage> messages = new ArrayList<>();
        int count = 0;
        int max = maxResults + GmailMessages.draftLastIndex;
        BatchRequest b = Login.service.batch();
        JsonBatchCallback<Draft> bc = new JsonBatchCallback<Draft>() {
            @Override
            public void onFailure(GoogleJsonError e, HttpHeaders responseHeaders) throws IOException {
                System.out.println("Internet Connection Error");
            }

            @Override
            public void onSuccess(Draft draft, HttpHeaders responseHeaders) throws IOException {
                Message message = draft.getMessage();
                FormattedMessage formattedMessage = new FormattedMessage(message);
                formattedMessage.setDraftId(draft.getId());
                //formattedMessage.setBodyText(GmailOperations.getMessageBody(message));
                messages.add(formattedMessage);
            }
        };
        Draft d = null;
        while (GmailMessages.draftLastIndex < max && GmailMessages.draftLastIndex < GmailMessages.draftMaxSize) {
            d = GmailMessages.draftList.get(GmailMessages.draftLastIndex);
            Login.service.users().drafts().get("me", d.getId()).queue(b, bc);
            GmailMessages.draftLastIndex++;
            count++;
        }
        if (count != 0)
            b.execute();
        return messages;
    }

    public static void loadDraft() throws IOException {
        //GmailMessages.draftList = getAllMessagesListInLabelId(Arrays.asList("DRAFT"));
        GmailMessages.draftList = getAllDraftsList();
        GmailMessages.draftMaxSize = GmailMessages.draftList.size();
        List<FormattedMessage> temp = getDraftMessages(50);
        GmailMessages.draftMessages.setAll(temp);
        if (temp.size() != 0)
            GmailMessages.draftStartHistoryId = temp.get(0).getHistoryId();
        //SaveMessages.saveDraft(temp);
        System.out.println("draft Loaded");
    }

    public static void getNewDraftMails() throws IOException {
        List<History> historyList = getHistoryMessages("DRAFT", new BigInteger(GmailMessages.draftStartHistoryId));
        if (historyList != null && !historyList.isEmpty()) {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    try {
                        GmailMessages.draftLastIndex = 0;
                        loadDraft();
                    } catch (IOException e) {
                        e.printStackTrace();

                    }
                }
            });
        }
    }


    //***********************************************************************************
    //Trash related operations
    public static List<FormattedMessage> getTrashMessages(int maxResults) throws IOException {
        List<FormattedMessage> messages = new ArrayList<>();
        BatchRequest b = Login.service.batch();
        JsonBatchCallback<Message> bc = new JsonBatchCallback<Message>() {
            @Override
            public void onFailure(GoogleJsonError e, HttpHeaders responseHeaders) throws IOException {
                System.out.println("Internet Connection Error");
            }

            @Override
            public void onSuccess(Message message, HttpHeaders responseHeaders) throws IOException {
                messages.add(new FormattedMessage(message));
            }
        };
        Message m = null;
        while (GmailMessages.trashLastIndex < maxResults && GmailMessages.trashLastIndex < GmailMessages.trashMaxSize) {
            m = GmailMessages.trashList.get(GmailMessages.trashLastIndex);
            Login.service.users().messages().get("me", m.getId()).setFormat("metadata").setMetadataHeaders(Arrays.asList("To", "From", "Date", "Subject")).queue(b, bc);
            GmailMessages.trashLastIndex++;
        }
        if (Math.abs(GmailMessages.trashLastIndex - maxResults) != maxResults)
            b.execute();
        return messages;
    }

    public static void loadTrash() throws IOException {
        GmailMessages.trashList = getAllMessagesListInLabelId(Arrays.asList("TRASH"));
        GmailMessages.trashMaxSize = GmailMessages.trashList.size();
        GmailMessages.trashLastIndex = 0;
        List<FormattedMessage> temp = getTrashMessages(50);
        GmailMessages.trashMessages.setAll(temp);
        if (temp.size() != 0)
            GmailMessages.trashStartHistoryId = temp.get(0).getHistoryId();
        //SaveMessages.saveTrash(temp);
        System.out.println("trash Loaded");
    }

    public static void getNewTrashMails() throws IOException {
        if (GmailMessages.trashStartHistoryId != null) {
            List<History> historyList = getHistoryMessages("TRASH", new BigInteger(GmailMessages.trashStartHistoryId));
            for (History history : historyList) {
                List<HistoryMessageAdded> messageAddedList = history.getMessagesAdded();
                if (messageAddedList != null) {
                    for (HistoryMessageAdded messageAdded : messageAddedList) {
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    GmailMessages.trashMessages.add(0, new FormattedMessage(Login.service.users().messages().get("me", messageAdded.getMessage()
                                            .getId()).setFormat("metadata").setMetadataHeaders(Arrays.asList("To", "From", "Date", "Subject")).execute()));
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                }
            }
        } else {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    try {
                        loadTrash();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        }

    }

    public static void trashMessage(String id) throws IOException {
        Login.service.users().messages().trash("me", id).execute();
    }

    public static void untrashMessage(String id) throws IOException {
        Login.service.users().messages().untrash("me", id).execute();
        loadTrash();
    }









    //***********************************************************************************
    //common messages operation

    public static Message getMessage(String id) throws IOException {
        return Login.service.users().messages().get("me", id).execute();
    }

    //****Retrieve message body ****
    public static String getMessageBody(Message m) throws IOException {
        String string = null;
        if (m.getPayload().getParts() == null)
            string = m.getPayload().getBody().getData().toString();
        else {
            string = getHtmlParts(m.getPayload().getParts());
        }
        String decodedString = new String(Base64.decodeBase64(string.getBytes()));
        return decodedString;
    }

    //****Retrieve multipart html content****
    public static String getHtmlParts(List<MessagePart> m) {
        StringBuilder stringBuilder = new StringBuilder();
        for (MessagePart mp : m) {
            if (mp.getParts() == null) {
                if (mp.getMimeType().equals("text/html"))
                    stringBuilder.append(mp.getBody().getData());
            } else {
                stringBuilder.append(getHtmlParts(mp.getParts()));
            }
        }
        return new String(stringBuilder);
    }

    //****this method will return messages with messageid and threadids****
    public static List<Message> getAllMessagesListInLabelId(List<String> labelIds) throws IOException {
        ListMessagesResponse response = Login.service.users().messages().list("me").setLabelIds(labelIds).execute();
        List<Message> messages = new ArrayList<Message>();
        while (response.getMessages() != null) {
            messages.addAll(response.getMessages());
            if (response.getNextPageToken() != null) {
                String pageToken = response.getNextPageToken();
                response = Login.service.users().messages().list("me").setLabelIds(labelIds).setPageToken(pageToken).execute();
            } else
                break;
        }
        // messagesList = messages;
        return messages;
    }

    public static Message sendMessage(String to, String from, String sub, String bodyText, boolean isHtml, List<File> attachments) throws IOException, MessagingException {
        Message message = createMessage(to, from, sub, bodyText, isHtml, attachments);
        message = Login.service.users().messages().send("me", message).execute();
        return message;
    }

    public static Draft createDraft(String to, String from, String sub, String bodyText, boolean isHtml, List<File> attachments) throws IOException, MessagingException {
        Draft createdDraft = new Draft();
        createdDraft.setMessage(createMessage(to, from, sub, bodyText, isHtml, attachments));
        createdDraft = Login.service.users().drafts().create("me", createdDraft).execute();
        return createdDraft;
    }

    public static Draft updateDraft(String draftId, String to, String from, String sub, String bodyText, boolean isHtml, List<File> attachments) throws IOException, MessagingException {
        Draft updatedDraft = new Draft();
        updatedDraft.setMessage(createMessage(to, from, sub, bodyText, isHtml, attachments));
        updatedDraft = Login.service.users().drafts().update("me", draftId, updatedDraft).execute();
        return updatedDraft;
    }


    public static Message createMessage(String to, String from, String sub, String bodyText, boolean isHtml, List<File> attachments) throws MessagingException, IOException {
        if (to == null || from == null) {
            System.out.println("Fields cannot be empty");
            return null;
        }
        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);

        MimeMessage email = new MimeMessage(session);

        if (!from.equals(""))
            email.setFrom(new InternetAddress(from));
        if (!to.equals(""))
            email.addRecipient(javax.mail.Message.RecipientType.TO, new InternetAddress(to));
        //if (!sub.equals(""))
            email.setSubject(sub);

        if (attachments == null || attachments.isEmpty()) {
            if (isHtml)
                email.setContent(bodyText, "text/html");
            else
                email.setText(bodyText);
        } else {
            MimeBodyPart mimeBodyPart = new MimeBodyPart();
            if (isHtml)
                mimeBodyPart.setContent(bodyText, "text/plain");
            else
                mimeBodyPart.setContent(bodyText, "text/plain");

            for (File attachmentFile : attachments) {
                Multipart multipart = new MimeMultipart();
                multipart.addBodyPart(mimeBodyPart);

                mimeBodyPart = new MimeBodyPart();
                DataSource source = new FileDataSource(attachmentFile);

                mimeBodyPart.setDataHandler(new DataHandler(source));
                mimeBodyPart.setFileName(attachmentFile.getName());

                multipart.addBodyPart(mimeBodyPart);
                email.setContent(multipart);
            }
        }
        Message message = createFromMimeMessage(email);
        return message;
    }

    private static Message createFromMimeMessage(MimeMessage email) throws IOException, MessagingException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        email.writeTo(baos);
        String encodedEmail = Base64.encodeBase64URLSafeString(baos.toByteArray());
        Message message = new Message();
        message.setRaw(encodedEmail);
        return message;
    }

    private static List<History> getHistoryMessages(String labelId, BigInteger startHistoryId) throws IOException {
        List<History> histories = new ArrayList<>();
        ListHistoryResponse response = Login.service.users().history().list("me").setHistoryTypes(Arrays.asList("messageAdded"))
                .setLabelId(labelId).setStartHistoryId(startHistoryId).execute();
        while (response.getHistory() != null) {
            histories.addAll(response.getHistory());
            if (response.getNextPageToken() != null) {
                String pageToken = response.getNextPageToken();
                response = Login.service.users().history().list("me").setHistoryTypes(Arrays.asList("messageAdded"))
                        .setLabelId(labelId).setPageToken(pageToken).setStartHistoryId(startHistoryId).execute();
            } else {
                break;
            }
        }
        return histories;
    }

    public static void listSearchMessages(String query) throws IOException {
        GmailMessages.searchLastIndex = 0;
        ListMessagesResponse response = Login.service.users().messages().list("me")
                .setQ(query).execute();
        List<Message> messages = new ArrayList<Message>();
        while (response.getMessages() != null) {
            messages.addAll(response.getMessages());
            if (response.getNextPageToken() != null) {
                String pageToken = response.getNextPageToken();
                response = Login.service.users().messages().list("me").setQ(query)
                        .setPageToken(pageToken).execute();
            } else
                break;
        }
        GmailMessages.searchList = messages;
        GmailMessages.searchMaxSize = messages.size();
    }

    public static void getSearchMessages(String query) throws IOException {
        int max = GmailMessages.searchLastIndex + 20;
        int count = 0;
        BatchRequest b = Login.service.batch();
        JsonBatchCallback<Message> bc = new JsonBatchCallback<Message>() {
            @Override
            public void onFailure(GoogleJsonError e, HttpHeaders responseHeaders) throws IOException {
                System.out.println("Internet Connection Error");
            }

            @Override
            public void onSuccess(Message message, HttpHeaders responseHeaders) throws IOException {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        GmailMessages.searchMessages.add(new FormattedMessage(message));
                    }
                });

            }
        };

        Message m = null;
        while (GmailMessages.searchLastIndex < max && GmailMessages.searchLastIndex < GmailMessages.searchMaxSize) {
            m = GmailMessages.searchList.get(GmailMessages.searchLastIndex);
            Login.service.users().messages().get("me", m.getId()).setFormat("metadata").setMetadataHeaders(Arrays.asList("To", "From", "Date", "Subject")).queue(b, bc);
            GmailMessages.searchLastIndex++;
            count++;
        }
        if (count != 0)
            b.execute();
    }













    //***********************************************************************************
    //attachments related operations

    public static List<File> downloadAttachments(Message message, String downloadLocation) throws IOException {
        List<File> attachmentsList = new ArrayList<>();
        List<MessagePart> parts = message.getPayload().getParts();
        if(parts !=null){
        for(MessagePart part: parts){
            if(part.getFilename() != null && part.getFilename().length() >0){
                String filename = part.getFilename();
                String attId = part.getBody().getAttachmentId();
                MessagePartBody attachPart = Login.service.users().messages().attachments()
                        .get("me", message.getId(), attId).execute();

                Base64 base64Url = new Base64(true);
                byte[] fileByteArray = base64Url.decodeBase64(attachPart.getData());
                File temp = new File(downloadLocation);
                if(!temp.exists())
                    temp.mkdir();
                FileOutputStream fileOutputStream = new FileOutputStream(temp+"/"+filename);
                fileOutputStream.write(fileByteArray);
                fileOutputStream.close();
                attachmentsList.add(new File(temp+"/"+filename));
            }
        }
        }
        return attachmentsList;
    }










    //this method will return drafts with draft id, message id and thread id


    // This method will return message list having specified labels
    /*public static List<Message> getMessagesListByLabel(List labelIds, long maxResults) throws IOException {
        ListMessagesResponse response = gmailInstance.users().messages().list("me").setLabelIds(labelIds).setMaxResults(maxResults).execute();
        return getMessages(response.getMessages());
    }*/


    //This method will return message list depending upon the query
    /*public static List<Message> getMessagesListByQuery(String query, long maxResults) throws IOException {
        ListMessagesResponse response = gmailInstance.users().messages().list("me").setQ(query).setMaxResults(maxResults).execute();
        return getMessages(response.getMessages());
    }*/










    //Test method to retrieve messages as batch request
   /* public static List<Message> getMessagesListAsBatch(List labelId, long maxResults) throws IOException {
        //ListMessagesResponse response = gmailInstance.users().messages().list("me").setLabelIds(labelId).setMaxResults(maxResults).execute();


        return getMessagesAsBatch(response.getMessages());
    }*/







   public static void loadMailBox() throws IOException {
        GmailMessages.USERS_EMAIL_ADDRESS = Login.service.users().getProfile("me").execute().getEmailAddress();
        loadInbox();
        loadSent();
        loadDraft();
        loadTrash();
   }

}
