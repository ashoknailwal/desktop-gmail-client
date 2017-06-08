package controllers;

import com.jfoenix.controls.*;
import dialogActivities.ComposeActivity;
import customList.CustomListCell;
import database.HelperValues;
import gmailServices.FormattedMessage;
import gmailServices.GmailMessages;
import gmailServices.GmailOperations;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.util.Callback;

import java.io.IOException;

/**
 * Created by Ashok on 4/14/2017.
 */
public class MainUI3Controller implements ControlledScreen{

    static ScreenController myController;
    private ScreenComponent screenComponent;
    private boolean componentFlag;
    private String currentFolderName;
    private String previousFolderName;
    private JFXListView<FormattedMessage> inboxMessageListView;
    private JFXListView<FormattedMessage> sentMessageListView;
    private JFXListView<FormattedMessage> draftMessageListView;
    private JFXListView<FormattedMessage> trashMessageListView;
    private JFXListView<FormattedMessage> searchMessageListView;

    private Region inboxButton;
    private Region sentButton;
    private Region draftButton;
    private Region trashButton;


    @FXML
    private TextField searchTextField;

    @FXML
    private ImageView searchImage;

    @FXML
    private JFXButton composeButton;


    @FXML
    private StackPane messageListViewParent;

    @FXML
    private StackPane inboxButtonParent;

    @FXML
    private StackPane sentButtonParent;

    @FXML
    private StackPane draftButtonParent;

    @FXML
    private StackPane trashButtonParent;

    @FXML
    private JFXButton logout;

    @FXML
    private Label folderLabel;


    @FXML
    private StackPane componentDisplayContainer;


    @FXML
    void logoutFromGmail(ActionEvent event) {
        try {
            java.io.File DATA_STORE_FILE = new java.io.File(
                    System.getProperty("user.home"), ".credentials/Amail/StoredCredential");
            DATA_STORE_FILE.delete();
            HelperValues.deleteHelperValue(HelperValues.loggedIn);
            System.exit(1);
        }catch (Exception e){
            e.printStackTrace();
        }
    }



    @FXML
    void composeClicked(ActionEvent event) {
        ComposeActivity composeActivity = new ComposeActivity(null, null,false, false);
        composeActivity.setStage(AmailMain.getStage());
        JFXDialogLayout content = new JFXDialogLayout();
        content.setHeading(new Text("Compose"));
        content.setBody(composeActivity.getContent());
        JFXDialog dialog = new JFXDialog(myController, content,JFXDialog.DialogTransition.CENTER);
        composeActivity.setAction(dialog);
        dialog.show();
    }


    @FXML
    void performSearchForTextField(ActionEvent event) {
        //GmailMessages.searchMessages = FXCollections.observableArrayList();
        GmailMessages.searchMessages.clear();
        setMessageListView(searchMessageListView);
        backgroundSearch(searchTextField.getText());

    }

    @FXML
    void performSearchFromImage(MouseEvent event) {
        //GmailMessages.searchMessages = FXCollections.observableArrayList();
        GmailMessages.searchMessages.clear();
        setMessageListView(searchMessageListView);
        backgroundSearch(searchTextField.getText());
    }



    @FXML
    void initialize(){
        currentFolderName = "INBOX";
        folderLabel.setText(currentFolderName);
        previousFolderName = null;

        inboxButtonParent.setStyle("-fx-background-color: #ffffff");
        screenComponent = ScreenComponent.getInstance();
        //screenComponent.setGridLinesVisible(true);
        componentFlag = false;


        //set listviews
        setInboxListView();
        setSentListView();
        setDraftListView();
        setTrashListView();
        setSearchListView();

        //set the current listview as inbox
        setMessageListView(inboxMessageListView);


        //adding folder buttons
        setFolderButtons();


        //adding actions to folder buttons

        inboxButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                previousFolderName = currentFolderName;
                currentFolderName = "INBOX";
                folderLabel.setText(currentFolderName);
                resetFolderButtonParent();
                inboxButtonParent.setStyle("-fx-background-color:  #0091EA");
                removeScreenComponent();
                inboxMessageListView.getSelectionModel().clearSelection();
                //removeListviewSelection();
                //messageListView.setItems(GmailMessages.inboxMessages);
                setMessageListView(inboxMessageListView);

            }
        });

        sentButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                previousFolderName = currentFolderName;
                currentFolderName = "SENT";
                folderLabel.setText(currentFolderName);
                resetFolderButtonParent();
                sentButtonParent.setStyle("-fx-background-color: #0091EA");
                removeScreenComponent();

                //removeListviewSelection();
                //messageListView.setItems(GmailMessages.sentMessages);
                setMessageListView(sentMessageListView);

            }
        });

        draftButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                previousFolderName = currentFolderName;
                currentFolderName = "DRAFT";
                folderLabel.setText(currentFolderName);
                resetFolderButtonParent();
                draftButtonParent.setStyle("-fx-background-color: #0091EA");
                removeScreenComponent();
                //removeListviewSelection();
                //messageListView.setItems(GmailMessages.draftMessages);
                setMessageListView(draftMessageListView);
            }
        });


        trashButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                previousFolderName = currentFolderName;
                currentFolderName = "TRASH";
                folderLabel.setText(currentFolderName);
                resetFolderButtonParent(); bvb
                trashButtonParent.setStyle("-fx-background-color: #0091EA");
                removeScreenComponent();
                //removeListviewSelection();
                //messageListView.setItems(GmailMessages.trashMessages);
                setMessageListView(trashMessageListView);
            }
        });

    }


    private void backgroundSearch(String query){
        if(query != null) {
            if (!GmailMessages.activeSearches.isEmpty()) {
                for (Service<Void> service : GmailMessages.activeSearches) {
                    service.cancel();
                }
            }
            Service<Void> search = new Service<Void>() {
                @Override
                protected Task<Void> createTask() {
                    try {
                        GmailOperations.listSearchMessages(query);
                        GmailOperations.getSearchMessages(query);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return null;
                }
            };
            GmailMessages.activeSearches.add(search);
            search.start();
        }
    }


    //this method resets the background of folder button parents
    private void resetFolderButtonParent(){
        if(previousFolderName != null && !previousFolderName.equals(currentFolderName))
        {
            switch (previousFolderName){
                case "INBOX": inboxButtonParent.setStyle("-fx-background-color: transparent");
                    break;
                case "SENT": sentButtonParent.setStyle("-fx-background-color: transparent");
                    break;
                case "DRAFT": draftButtonParent.setStyle("-fx-background-color: transparent");
                    break;
                case "TRASH": trashButtonParent.setStyle("-fx-background-color: transparent");
                    break;
            }
        }
    }



    //this method add listener to inbox listview
    public void setInboxListView(){
        inboxMessageListView = new JFXListView<>();
        inboxMessageListView.setItems(GmailMessages.inboxMessages);
        inboxMessageListView.setCellFactory(new Callback<ListView<FormattedMessage>, ListCell<FormattedMessage>>() {
            @Override
            public ListCell<FormattedMessage> call(ListView<FormattedMessage> param) {
                return new CustomListCell(currentFolderName);
            }
        });
        inboxMessageListView.getStylesheets().add(String.valueOf(getClass().getResource("/listview.css")));
        inboxMessageListView.setExpanded(true);
        inboxMessageListView.depthProperty().set(1);

        inboxMessageListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<FormattedMessage>() {
            @Override
            public void changed(ObservableValue<? extends FormattedMessage> observable, FormattedMessage oldValue, FormattedMessage newValue) {
                if (newValue != null) {
                    if (!componentFlag) {
                        componentFlag = true;
                        screenComponent.setScreenComponent(currentFolderName, inboxMessageListView.getSelectionModel().getSelectedIndex());
                        setScreenComponent();
                    }
                    screenComponent.setInfo(newValue);
                }
            }
        });
        /*ScrollBar listViewScrollBar = getListViewScrollBar(inboxMessageListView);
        listViewScrollBar.valueProperty().addListener((observable, oldValue, newValue) -> {
            double position = newValue.doubleValue();
            ScrollBar scrollBar = getListViewScrollBar(inboxMessageListView);
            if (position == scrollBar.getMax()) {
                try {
                    GmailMessages.inboxMessages.addAll(GmailOperations.getInboxMessages(10));
                } catch (IOException e) {
                    e.printStackTrace();
                    NotifyUser.getNotification("Internet connection has lost", "Please check your internet connection").showInformation();
                }
            }
        });*/
    }

    public void setSentListView(){
        sentMessageListView = new JFXListView<>();
        sentMessageListView.setItems(GmailMessages.sentMessages);
        sentMessageListView.setCellFactory(new Callback<ListView<FormattedMessage>, ListCell<FormattedMessage>>() {
            @Override
            public ListCell<FormattedMessage> call(ListView<FormattedMessage> param) {
                return new CustomListCell(currentFolderName);
            }
        });
        sentMessageListView.getStylesheets().add(String.valueOf(getClass().getResource("/listview.css")));
        sentMessageListView.setExpanded(true);
        sentMessageListView.depthProperty().set(1);

        sentMessageListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<FormattedMessage>() {
            @Override
            public void changed(ObservableValue<? extends FormattedMessage> observable, FormattedMessage oldValue, FormattedMessage newValue) {
                if (newValue != null) {
                    if (!componentFlag) {
                        componentFlag = true;
                        screenComponent.setScreenComponent(currentFolderName, sentMessageListView.getSelectionModel().getSelectedIndex());
                        setScreenComponent();
                    }
                    screenComponent.setInfo(newValue);
                }
            }
        });
        /*ScrollBar listViewScrollBar = getListViewScrollBar(sentMessageListView);
        listViewScrollBar.valueProperty().addListener((observable, oldValue, newValue) -> {
            double position = newValue.doubleValue();
            ScrollBar scrollBar = getListViewScrollBar(sentMessageListView);
            if (position == scrollBar.getMax()) {
                try {
                    GmailMessages.sentMessages.addAll(GmailOperations.getSentMessages(10));
                } catch (IOException e) {
                    e.printStackTrace();
                    NotifyUser.getNotification("Internet connection has lost", "Please check your internet connection").showInformation();
                }
            }
        });*/
    }


    public void setDraftListView(){
        draftMessageListView = new JFXListView<>();
        draftMessageListView.setItems(GmailMessages.draftMessages);
        draftMessageListView.setCellFactory(new Callback<ListView<FormattedMessage>, ListCell<FormattedMessage>>() {
            @Override
            public ListCell<FormattedMessage> call(ListView<FormattedMessage> param) {
                return new CustomListCell(currentFolderName);
            }
        });
        draftMessageListView.getStylesheets().add(String.valueOf(getClass().getResource("/listview.css")));
        draftMessageListView.setExpanded(true);
        draftMessageListView.depthProperty().set(1);

        draftMessageListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<FormattedMessage>() {
            @Override
            public void changed(ObservableValue<? extends FormattedMessage> observable, FormattedMessage oldValue, FormattedMessage newValue) {
                if (newValue != null) {
                    if (!componentFlag) {
                        componentFlag = true;
                        screenComponent.setScreenComponent(currentFolderName);
                        setScreenComponent();
                    }
                    screenComponent.setInfo(newValue);
                }
            }
        });
    }


    public void setTrashListView(){
        trashMessageListView = new JFXListView<>();
        trashMessageListView.setItems(GmailMessages.trashMessages);
        trashMessageListView.setCellFactory(new Callback<ListView<FormattedMessage>, ListCell<FormattedMessage>>() {
            @Override
            public ListCell<FormattedMessage> call(ListView<FormattedMessage> param) {
                return new CustomListCell(currentFolderName);
            }
        });
        trashMessageListView.getStylesheets().add(String.valueOf(getClass().getResource("/listview.css")));
        trashMessageListView.setExpanded(true);
        trashMessageListView.depthProperty().set(1);

        trashMessageListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<FormattedMessage>() {
            @Override
            public void changed(ObservableValue<? extends FormattedMessage> observable, FormattedMessage oldValue, FormattedMessage newValue) {
                if (newValue != null) {
                    if (!componentFlag) {
                        componentFlag = true;
                        screenComponent.setScreenComponent(currentFolderName);
                        setScreenComponent();
                    }
                    screenComponent.setInfo(newValue);
                }
            }
        });
        /*ScrollBar listViewScrollBar = getListViewScrollBar(trashMessageListView);
        listViewScrollBar.valueProperty().addListener((observable, oldValue, newValue) -> {
            double position = newValue.doubleValue();
            ScrollBar scrollBar = getListViewScrollBar(trashMessageListView);
            if (position == scrollBar.getMax()) {
                try {
                    GmailMessages.trashMessages.addAll(GmailOperations.getTrashMessages(10));
                } catch (IOException e) {
                    e.printStackTrace();
                    NotifyUser.getNotification("Internet connection has lost", "Please check your internet connection").showInformation();
                }
            }
        });*/
    }

    public void setSearchListView() {
        searchMessageListView = new JFXListView<>();
        searchMessageListView.setItems(GmailMessages.searchMessages);
        searchMessageListView.setCellFactory(new Callback<ListView<FormattedMessage>, ListCell<FormattedMessage>>() {
            @Override
            public ListCell<FormattedMessage> call(ListView<FormattedMessage> param) {
                return new CustomListCell(currentFolderName);
            }
        });
        searchMessageListView.getStylesheets().add(String.valueOf(getClass().getResource("/listview.css")));
        searchMessageListView.setExpanded(true);
        searchMessageListView.depthProperty().set(1);

        searchMessageListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<FormattedMessage>() {
            @Override
            public void changed(ObservableValue<? extends FormattedMessage> observable, FormattedMessage oldValue, FormattedMessage newValue) {
                if (newValue != null) {
                    if (!componentFlag) {
                        componentFlag = true;
                        screenComponent.setScreenComponent(currentFolderName);
                        setScreenComponent();
                    }
                    screenComponent.setInfo(newValue);
                }
            }
        });
    }

    private ScrollBar getListViewScrollBar(ListView<?> listView) {
        ScrollBar scrollbar = null;
        for (Node node : listView.lookupAll(".scroll-bar")) {
            if (node instanceof ScrollBar) {
                ScrollBar bar = (ScrollBar) node;
                if (bar.getOrientation().equals(Orientation.VERTICAL)) {
                    scrollbar = bar;
                }
            }
        }
        return scrollbar;
    }

    private void setMessageListView(JFXListView<FormattedMessage> listView){
        if(!messageListViewParent.getChildren().isEmpty())
            messageListViewParent.getChildren().remove(0);
        messageListViewParent.getChildren().add(0,listView);
    }

    //this method will set the folder buttons and add the rippler
    private void setFolderButtons(){
        inboxButton = new Region();
        sentButton = new Region();
        draftButton = new Region();
        trashButton = new Region();

        inboxButtonParent.getChildren().add(setRippler(inboxButton));
        sentButtonParent.getChildren().add(setRippler(sentButton));
        draftButtonParent.getChildren().add(setRippler(draftButton));
        trashButtonParent.getChildren().add(setRippler(trashButton));
    }



    private JFXRippler setRippler(Node node){
        JFXRippler rippler = new JFXRippler(node);
        return rippler;
    }


    public void setScreenComponent(){
        removeScreenComponent();
        componentDisplayContainer.getChildren().add(0,screenComponent);


    }

    public void removeScreenComponent(){
        if(!componentDisplayContainer.getChildren().isEmpty())
            componentDisplayContainer.getChildren().remove(0);
        componentFlag = false;
    }

    private void removeListviewSelection(JFXListView<FormattedMessage> listView){
        /*List<FormattedMessage> selectedItemsCopy = new ArrayList<FormattedMessage>(messageListView.getSelectionModel().getSelectedItems());
        messageListView.getItems().removeAll(selectedItemsCopy);*/
        listView.getSelectionModel().clearSelection();
    }


    @Override
    public void setScreenParent(ScreenController screenPage) {
        myController = screenPage;
    }

    public static ScreenController getScreenParent() {return myController;}
}
