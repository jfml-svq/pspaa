package com.josefco.megaDownload;

import com.josefco.megaDownload.util.R;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AppController {

    public TextField tfUrl;
    public Button btDownload;
    public TabPane tpDownloads;
    public ScrollPane initScrollPane;
    public File fichero;


    private Map<String, DownloadController> allDownloads;

    public AppController() {
        allDownloads = new HashMap<>();
    }

    @FXML
    private void newExitDirectory(ActionEvent event) {
        DirectoryChooser dirChooser = new DirectoryChooser();
        Stage stage = (Stage) initScrollPane.getScene().getWindow();
        fichero = dirChooser.showDialog(stage);
    }

    @FXML
    public void launchDownload(ActionEvent event) {
        String urlText = tfUrl.getText();
        tfUrl.clear();
        tfUrl.requestFocus();

        launchDownload(urlText, fichero);
    }

    private void launchDownload(String url, File fichero) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(R.getUI("download.fxml"));

            DownloadController downloadController = new DownloadController(url, fichero);
            loader.setController(downloadController);
            VBox downloadBox = loader.load();

            Tab newTab = new Tab();

            //String filename = url.substring(url.lastIndexOf("/") + 1);
            //tpDownloads.getTabs().add(new Tab(filename, downloadBox));

            newTab.setText(url.substring(url.lastIndexOf("/") + 1));
            newTab.setContent(downloadBox);
            newTab.setClosable(true);
            tpDownloads.getTabs().add(newTab);

            DownloadController.tabIndex = tpDownloads.getTabs().indexOf(newTab);

            allDownloads.put(url, downloadController);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }



    @FXML
    public void stopAllDownloads() {
        for (DownloadController downloadControllers : allDownloads.values())
            downloadControllers.stop();
    }

    @FXML
    public void closeAllTabs(){
        tpDownloads.getTabs().clear();
    }


    @FXML
    public void addList() {
        FileChooser newFileChoosed = new FileChooser();
        File newFichero = newFileChoosed.showOpenDialog(null);
        if (newFichero == null)
            return;

        try (BufferedReader reader = new BufferedReader(new FileReader(newFichero))) {
            String line;
            while ((line = reader.readLine()) != null)
                launchDownload(line, fichero);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
