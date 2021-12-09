package com.ripplesolutions.nfc.cardreader;

import com.ripplesolutions.utils.Utils;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;

public class Main extends Application {
    private static final String FXML_PATH = "launcher.fxml";
    private static final String CSS_PATH = "launcher.css";
    private static final String ICONS_PATH = "icons/";
    // private static final String WINDOW_TITLE = "NFC CARD READER CONTROLLER";
    private static final String WINDOW_TITLE = "NFC CARD READER CONTROLLER-DEV";

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        primaryStage = createStage(loader);
        Parent root = new AnchorPane();
        URL fxmlFile = loader.getResource(FXML_PATH);
        if (fxmlFile != null) {
            root = FXMLLoader.load(fxmlFile);
        }
        Scene scene = new Scene(root);

        final ObservableList<String> stylesheets = scene.getStylesheets();
        URL resource = loader.getResource(CSS_PATH);
        if (resource != null) {
            stylesheets.addAll(resource.toExternalForm());
        }
        // add extra space for the margin
        // in launcher.fxml, width of borderPane is 600
        primaryStage.setWidth(640);
        // in launcher.fxml, width of borderPane is 230
        primaryStage.setHeight(270);
        primaryStage.setResizable(false);
        primaryStage.setScene(scene);
        primaryStage.setTitle(WINDOW_TITLE);
        primaryStage.show();
    }


    public Stage createStage(ClassLoader loader) {
        Stage stage = new Stage();
        try {
            URL resource = loader.getResource(ICONS_PATH);
            if (resource != null) {
                File file = new File(resource.getFile());
                File[] iconList = file.listFiles();
                if (iconList != null) {
                    for (File f : iconList) {
                        stage.getIcons().add(new Image(f.toURI().toURL().openStream()));
                    }
                    // System.out.println("ICONS NOT NULL");
                }
                // System.out.println("NOT NULL");
            }
        } catch (Exception e) {
            System.out.println(Utils.getExceptionMessage(e));
        }
        return stage;
    }
}