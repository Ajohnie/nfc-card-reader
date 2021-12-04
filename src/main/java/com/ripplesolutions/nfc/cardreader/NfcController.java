package com.ripplesolutions.nfc.cardreader;

import com.google.cloud.firestore.DocumentReference;
import com.jfoenix.controls.JFXButton;
import com.ripplesolutions.firebase.CardOptions;
import com.ripplesolutions.firebase.FireBaseConnection;
import com.ripplesolutions.nfc.NFC;
import com.ripplesolutions.utils.Utils;
import io.github.palexdev.materialfx.font.MFXFontIcon;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class NfcController implements Initializable {
    private static final String BRANCH = "KIRA_RD";
    private static final String DATA_BASE_NAME = "vag";
    private static final String CARD_COL_NAME = "loyalty-programs";
    private static final String CARD_DOC_ID = "loyalty-program-" + BRANCH;
    private static final String COLOR_CARD_PRESENT = "green";
    private static final String COLOR_CARD_ABSENT = "#c5a70d";
    private static final String COLOR_READER_ABSENT = "black";
    // added this to allow non-main thread messages to be shown
    private final SimpleStringProperty appMessagesProperty = new SimpleStringProperty("");
    private final SimpleStringProperty readerNameProperty = new SimpleStringProperty("");
    private final SimpleStringProperty startStopIconColorProperty = new SimpleStringProperty("");
    private FireBaseConnection connection;
    @FXML
    private Text statusText;
    @FXML
    private Label branchName, readerName;
    @FXML
    private JFXButton startStopBtn, exitBtn, cancelBtn, minimizeBtn;
    @FXML
    private MFXFontIcon startStopIcon;
    // private FontAwesomeIconView startStopIcon;
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
        // MFXFontIcon xIcon = new MFXFontIcon("mfx-x-circle", 16);
        branchName.setText(BRANCH.replace("_", " "));
        readerName.setText("");
        cancelBtn.setOnAction(event -> System.exit(0));
        minimizeBtn.setOnAction(event -> ((Stage) minimizeBtn.getScene().getWindow()).setIconified(true));
        exitBtn.setOnAction(event -> System.exit(0));
        startStopBtn.setOnAction(event -> checkServer());
        appMessagesProperty.addListener((observable, oldValue, newValue) -> setStatusText(newValue));
        readerNameProperty.addListener((observable, oldValue, newValue) -> runLater(() -> readerName.setText(newValue)));
        startStopIconColorProperty.addListener((observable, oldValue, newValue) -> runLater(() -> startStopIcon.setFill(Paint.valueOf(newValue))));

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
                document.addSnapshotListener((snapshot, e) -> {
                    hideProgressBar();
                    if (e != null) {
                        setStatusText(e);
                        return;
                    }
                    boolean configExists = snapshot != null && snapshot.exists();
                    if (configExists) {
                        try {
                            CardOptions options = new CardOptions().fromFirebase(snapshot);
                            if (options.isRead()) {
                                options.customerId = Utils.getCustomerId();
                                checkReaderStatus(); // update ui with card reader name
                                boolean cardHasId = options.customerId.length() > 0;
                                if (cardHasId) {
                                    options.message = "Customer Id has been read successfully";
                                } else {
                                    options.message = "Card is not registered, register card and try again";
                                    setStatusText(options.message);
                                }
                                connection.editDoc(CARD_DOC_ID, options.toFirebase());
                            } else {
                                boolean idIsSet = options.customerId != null && options.customerId.length() > 0;
                                if (idIsSet) {
                                    Utils.setCustomerId(options.customerId);
                                } else {
                                    options.message = "Customer ID unknown, register customer and try again";
                                    setStatusText(options.message);
                                    connection.editDoc(CARD_DOC_ID, options.toFirebase());
                                }
                            }
                            checkReaderStatus(); // update ui with card reader name
                        } catch (Exception ex) {
                            setStatusText(ex);
                        }
                    } else {
                        // first time program running
                        CardOptions options = new CardOptions();
                        options.message = "Loyalty Program Not Yet Used";
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
            startStopIconColorProperty.setValue(COLOR_READER_ABSENT);
            return;
        }
        boolean cardPresent = NFC.cardInRange;
        if (cardPresent) {
            startStopIconColorProperty.setValue(COLOR_CARD_PRESENT);
        } else {
            startStopIconColorProperty.setValue(COLOR_CARD_ABSENT);
        }
    }
}