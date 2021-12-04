package com.ripplesolutions.nfc.cardreader;

import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.jfoenix.controls.JFXButton;
import com.ripplesolutions.firebase.FireBaseConnection;
import com.ripplesolutions.nfc.NFC;
import com.ripplesolutions.utils.Utils;
import io.github.palexdev.materialfx.font.MFXFontIcon;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class NfcController implements Initializable {
    private static final String BRANCH = "KIRA_RD";
    private static final String DATA_BASE_NAME = "vag";
    private static final String CARD_COL_NAME = "loyalty-programs";
    private static final String CARD_DOC_ID = "loyalty-program-" + BRANCH;
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
        if (message == null) {
            statusText.setText("");
            statusText.setVisible(false);
        } else {
            statusText.setText(message);
            statusText.setVisible(message.length() > 0);
        }
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
        try {
            connection = new FireBaseConnection(DATA_BASE_NAME); // pass arguments to customize connection in future
            connection.setCollectionName(CARD_COL_NAME);
            checkServer();
        } catch (Exception e) {
            setStatusText(e);
        }
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
                                    checkReaderStatus(); // update ui with card reader name
                                } else {
                                    options.message = "Customer ID unknown, register customer and try again";
                                    setStatusText(options.message);
                                    connection.editDoc(CARD_DOC_ID, options.toFirebase());
                                }
                            }
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
        readerName.setText(NFC.readerName);
        boolean readerPresent = NFC.readerPluggedIn;
        if (!readerPresent) {
            startStopIcon.setFill(Paint.valueOf("black"));
            return;
        }
        boolean cardPresent = NFC.cardInRange;
        if (cardPresent) {
            startStopIcon.setFill(Paint.valueOf("green"));
        } else {
            startStopIcon.setFill(Paint.valueOf("#c5a70d"));
        }
    }

    static class CardOptions {
        String customerId;
        String branch;
        String functionName;
        String operation;
        String message;

        public CardOptions(String customerId, String branch, String functionName) {
            this.customerId = customerId;
            this.branch = branch;
            this.functionName = functionName;
            this.operation = "WRITE";
            this.message = "";
        }

        public CardOptions(String customerId,
                           String branch,
                           String functionName,
                           String operation,
                           String message) {
            this.customerId = customerId;
            this.branch = branch;
            this.functionName = functionName;
            this.operation = operation;
            this.message = message;
        }

        public CardOptions() {
            this.customerId = "";
            this.branch = "";
            this.functionName = "";
            this.operation = "READ";
            this.message = "";
        }

        boolean isRead() {
            if (this.operation == null) {
                return true; // read by default
            }
            return this.operation.equalsIgnoreCase("READ");
        }

        boolean isWrite() {
            if (this.operation == null) {
                return false; // don't overwrite useful data in case of wrong object set up
            }
            return this.operation.equalsIgnoreCase("WRITE");
        }

        public Map<String, Object> toFirebase() {
            Map<String, Object> map = new HashMap<>();
            map.put("customerId", customerId);
            map.put("branch", branch);
            map.put("functionName", functionName);
            map.put("operation", operation);
            map.put("message", message);
            return map;
        }

        public CardOptions fromFirebase(DocumentSnapshot snapshot) {
            customerId = snapshot.getString("customerId");
            branch = snapshot.getString("branch");
            functionName = snapshot.getString("functionName");
            operation = snapshot.getString("operation");
            message = snapshot.getString("message");
            return this;
        }
    }
}