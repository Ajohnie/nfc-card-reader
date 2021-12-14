package com.ripplesolutions.nfc.cardreader;

import com.google.cloud.firestore.DocumentReference;
import com.jfoenix.controls.JFXButton;
import com.ripplesolutions.firebase.CardOptions;
import com.ripplesolutions.firebase.FireBaseConnection;
import com.ripplesolutions.nfc.NFC;
import com.ripplesolutions.utils.Utils;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.net.URL;
import java.util.Date;
import java.util.ResourceBundle;

public class NfcController implements Initializable {
    // private static final String BRANCH = "MAKINDYE"; // branch 2
    private static final String BRANCH = "KIRA_RD"; // branch 1
    private static final String DATA_BASE_NAME = "vag";
    private static final String CARD_COL_NAME = "loyalty-programs";
    private static final String CARD_DOC_ID = "loyalty-program-" + BRANCH;
    private static final String COLOR_CARD_PRESENT = "-fx-background-radius:10;-fx-border-color:green;-fx-border-radius:10;-fx-border-width:10";
    private static final String COLOR_CARD_ABSENT = "-fx-background-radius:10;-fx-border-color:#c5a70d;-fx-border-radius:10;-fx-border-width:10";
    private static final String COLOR_READER_ABSENT = "-fx-background-radius:10;-fx-border-color:none;-fx-border-radius:10;-fx-border-width:10";
    // added this to allow non-main thread messages to be shown and updating any interested subscribers like emailing/sms feature when card is read
    private final SimpleStringProperty appMessagesProperty = new SimpleStringProperty("");
    private final SimpleStringProperty readerNameProperty = new SimpleStringProperty("");
    private final SimpleStringProperty startStopButtonColorProperty = new SimpleStringProperty("");
    private FireBaseConnection connection;
    @FXML
    private Text statusText;
    @FXML
    private Label branchName, readerName;
    @FXML
    private JFXButton startStopBtn, exitBtn, cancelBtn, minimizeBtn;
    @FXML
    private ProgressBar progressBar;

    private void setStatusText(Exception exception) {
        setStatusText(Utils.getExceptionMessage(exception));
        hideProgressBar();
    }

    private void setStatusText(String message) {
        runLater(() -> {
            if (message == null) {
                statusText.setText("");
                statusText.setVisible(false);
            } else {
                statusText.setText(message);
                statusText.setVisible(message.length() > 0);
            }
        });
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        branchName.setText(BRANCH.replace("_", " "));
        readerName.setText("");
        cancelBtn.setOnAction(event -> System.exit(0));
        minimizeBtn.setOnAction(event -> ((Stage) minimizeBtn.getScene().getWindow()).setIconified(true));
        exitBtn.setOnAction(event -> System.exit(0));
        startStopBtn.setOnAction(event -> checkServer());
        appMessagesProperty.addListener((observable, oldValue, newValue) -> setStatusText(newValue));
        readerNameProperty.addListener((observable, oldValue, newValue) -> runLater(() -> readerName.setText(newValue)));
        startStopButtonColorProperty.addListener((observable, oldValue, newValue) -> runLater(() -> {
            startStopBtn.setStyle(newValue);
        }));

        try {
            connection = new FireBaseConnection(DATA_BASE_NAME); // pass arguments to customize connection in future
            connection.setCollectionName(CARD_COL_NAME);
            checkServer();
        } catch (Exception e) {
            setStatusText(e);
        }
    }

    private void runLater(Runnable action) {
        Platform.runLater(action);
    }

    private void checkServer() {
        try {
            showProgressBar();
            setStatusText(""); // reset message panel
            if (connection != null) {
                DocumentReference document = connection.observerDoc(CARD_DOC_ID);
                /*the program goes into a read/write listen loop and
                 * behaves unexpectedly, so I introduced buildNo's to know
                 * who made the changes to the firebase document*/
                CardOptions options = new CardOptions();
                options.branch = BRANCH;
                Date buildNo = new Date();
                options.setModified(buildNo);
                document.addSnapshotListener((snapshot, e) -> {
                    boolean configExists = snapshot != null && snapshot.exists();
                    CardOptions newOptions = new CardOptions().fromFirebase(snapshot);
                    if (configExists) {
                        if (options.wasModified(newOptions.getModified())) {
                            options.fromFirebase(snapshot);
                            try {
                                boolean cardHasId = options.hasId();
                                if (options.isRead()) {
                                    options.setCustomerId(Utils.getCustomerId());
                                    checkReaderStatus(); // update ui with card reader name
                                    cardHasId = options.hasId(); // check id again
                                    if (cardHasId) {
                                        options.setModified(buildNo); // mark as updated
                                        options.message = "Customer Id has been read successfully";
                                        options.write(); // change operation so that it is detected by web app
                                    } else {
                                        options.message = "Card is not registered, register card and try again";
                                        setStatusText(options.message);
                                    }
                                    connection.editDoc(CARD_DOC_ID, options.toFirebase());
                                } else {
                                    if (cardHasId) {
                                        boolean wasSet = Utils.setCustomerId(options.getCustomerId());
                                        if (wasSet) {
                                            options.setModified(buildNo); // mark as updated
                                            options.message = "Customer was registered Successfully";
                                            options.read();// change operation so that it is detected by web app
                                        } else {
                                            options.setCustomerId("");
                                            options.message = "Customer registration failed";
                                        }
                                    } else {
                                        options.setCustomerId("");
                                        options.message = "Customer ID unknown, register customer and try again";
                                    }
                                    setStatusText(options.message);
                                    connection.editDoc(CARD_DOC_ID, options.toFirebase());
                                }
                                checkReaderStatus(); // update ui with card reader name
                            } catch (Exception ex) {
                                setStatusText(ex);
                                options.message = Utils.getExceptionMessage(ex);
                                options.setCustomerId("");
                                try {
                                    connection.editDoc(CARD_DOC_ID, options.toFirebase());
                                } catch (Exception exp) {
                                    setStatusText(Utils.getExceptionMessage(exp));
                                }
                            }
                        }
                    } else {
                        // first time program running
                        options.message = "it must be the first time using Loyalty Program, try again";
                        try {
                            connection.editDoc(CARD_DOC_ID, options.toFirebase());
                            setStatusText(options.message);
                        } catch (Exception ex) {
                            setStatusText(Utils.getExceptionMessage(ex));
                        }
                    }
                });
            } else {
                setStatusText("Cloud Server is not configured, contact admin");
            }
        } catch (Exception e) {
            setStatusText(e);
        }
    }

    private void hideProgressBar() {
        progressBar.setVisible(false);
        checkReaderStatus();
    }

    private void showProgressBar() {
        progressBar.setVisible(true);
        checkReaderStatus();
    }

    private void checkReaderStatus() {
        readerNameProperty.setValue(NFC.readerName);
        boolean readerPresent = NFC.readerPluggedIn;
        if (!readerPresent) {
            startStopButtonColorProperty.setValue(COLOR_READER_ABSENT);
            return;
        }
        boolean cardPresent = NFC.cardInRange;
        if (cardPresent) {
            startStopButtonColorProperty.setValue(COLOR_CARD_PRESENT);
        } else {
            startStopButtonColorProperty.setValue(COLOR_CARD_ABSENT);
        }
    }
}
