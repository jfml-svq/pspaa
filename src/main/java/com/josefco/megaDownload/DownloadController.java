package com.josefco.megaDownload;

import javafx.concurrent.Worker;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ResourceBundle;

public class DownloadController implements Initializable {

    public TextField tfUrl;
    public Label lbStatus;
    public ProgressBar pbProgress;
    private String urlText;
    private DownloadTask downloadTask;
    private Stage stage;

    private static final Logger logger = LogManager.getLogger(DownloadController.class);

    public DownloadController(String urlText) {
        //R.escribirLog("Descarga " + urlText + " creada");
        logger.info("Descarga " + urlText + " creada");
        this.urlText = urlText;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        tfUrl.setText(urlText);
    }

    @FXML
    public void start(ActionEvent event) {
        try {
            FileChooser fileChooser = new FileChooser();
            File file = fileChooser.showSaveDialog(tfUrl.getScene().getWindow());
            if (file == null)
                return;

            downloadTask = new DownloadTask(urlText, file);

            pbProgress.progressProperty().unbind();
            pbProgress.progressProperty().bind(downloadTask.progressProperty());

            downloadTask.stateProperty().addListener((observableValue, oldState, newState) -> {
                System.out.println(observableValue.toString());
                if (newState == Worker.State.SUCCEEDED) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setContentText("La descarga ha terminado");
                    alert.show();
                }
            });

            downloadTask.messageProperty().addListener((observableValue, oldValue, newValue) -> lbStatus.setText(newValue));

            new Thread(downloadTask).start();
        } catch (MalformedURLException murle) {
            murle.printStackTrace();
            logger.error("URL mal formada", murle.fillInStackTrace());
            //R.escribirLog(/*logger.error(*/"URL mal formada"/*, murle.fillInStackTrace())*/);;
        }
    }


    @FXML private Button btnClose;
    public static int tabIndex;
    @FXML private void closeTab(ActionEvent event){
        Button closeBtn = (Button) event.getSource();
        Scene btnScene = closeBtn.getScene();

        TabPane thisTabpane = (TabPane) btnScene.lookup("#tpDownloads");
        thisTabpane.getTabs().remove(tabIndex);

    }

    @FXML
    public void stop(ActionEvent event) {
        stop();
    }

    public void stop() {
        if (downloadTask != null)
            downloadTask.cancel();
    }




    public String getUrlText() {
        return urlText;
    }
}
