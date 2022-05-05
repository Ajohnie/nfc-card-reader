package com.ripplesolutions.nfc.cardreader;

import com.jfoenix.controls.JFXButton;
import com.ripplesolutions.server.AppServer;
import com.ripplesolutions.utils.Utils;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ProgressBar;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ResourceBundle;

public class NfcController implements Initializable {
    private static final String ON = "-fx-background-radius:10;-fx-border-color:green;-fx-border-radius:10;-fx-border-width:10";
    private static final String RUNNING = "-fx-background-radius:10;-fx-border-color:#c5a70d;-fx-border-radius:10;-fx-border-width:10";
    private static final String OFF = "-fx-background-radius:10;-fx-border-color:none;-fx-border-radius:10;-fx-border-width:10";
    private static final String PORT_FILE_PATH = "config.txt";
    // added this to allow non-main thread messages to be shown and updating any interested subscribers like emailing/sms feature when card is read
    private final SimpleBooleanProperty runningProperty = new SimpleBooleanProperty(false);
    private final SimpleStringProperty startStopButtonColorProperty = new SimpleStringProperty("");
    private AppServer server;
    @FXML
    private Text statusText;
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

    private int getPort() {
        int portNumber = 8090;
        try {
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            URL portFile = loader.getResource(PORT_FILE_PATH);
            if (portFile != null) {
                String port = Files.readString(Path.of(portFile.toURI()));
                System.out.println(port);
                portNumber = Integer.parseInt(port);
            } else {
                setStatusText("Set Port Config, port number can not be determined");
            }
        } catch (Exception e) {
            setStatusText("Configuration Failed, check config file and try again");
        }
        return portNumber;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        int portNumber = getPort();
        server = new AppServer(runningProperty, portNumber);
        hideProgressBar();
        cancelBtn.setOnAction(event -> System.exit(0));
        minimizeBtn.setOnAction(event -> ((Stage) minimizeBtn.getScene().getWindow()).setIconified(true));
        exitBtn.setOnAction(event -> System.exit(0));
        startStopBtn.setOnAction(event -> {
            try {
                boolean isOn = server.toggle();
                startStopButtonColorProperty.setValue(isOn ? ON : OFF);
            } catch (Exception e) {
                setStatusText(e);
            }
        });
        runningProperty.addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                showProgressBar();
                startStopButtonColorProperty.setValue(RUNNING);
            } else {
                hideProgressBar();
                startStopButtonColorProperty.setValue(server.isRunning() ? ON : OFF);
            }
        });
        startStopButtonColorProperty.addListener((observable, oldValue, newValue) -> runLater(() -> startStopBtn.setStyle(newValue)));
    }

    private void runLater(Runnable action) {
        Platform.runLater(action);
    }

    private void hideProgressBar() {
        progressBar.setVisible(false);
    }

    private void showProgressBar() {
        progressBar.setVisible(true);
    }
}
