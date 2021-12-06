package com.josefco.megaDownload;


import javafx.concurrent.Task;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class DownloadTask extends Task<Integer> {

    private URL url;
    private File file;

    private static final Logger logger = LogManager.getLogger(DownloadController.class);

    public DownloadTask(String urlText, File file) throws MalformedURLException {
        this.url = new URL(urlText);
        this.file = file;
    }

    @Override
    protected Integer call() throws Exception {
        logger.trace("Descarga " + url.toString() + " iniciada");
        updateMessage("Conectando con el servidor . . .");

        URLConnection urlConnection = url.openConnection();

        int fileSize = urlConnection.getContentLength();
        BufferedInputStream in = new BufferedInputStream(url.openStream());
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        byte dataBuffer[] = new byte[1024];
        double downloaded = 0.00;
        int read = 0;
        double percentDownloaded = 0.00;
        int sizeDownloaded = 0;
        int sizeStart = 0;
        System.out.println("sizestart "+sizeStart);
        int sizeMegas = (int)(fileSize * 0.000001);
        System.out.println("sizemegas "+ sizeMegas);

        while((read = in.read(dataBuffer,0,1024))>=0) {
            fileOutputStream.write(dataBuffer,0,read);
            downloaded += read;
            percentDownloaded = (downloaded*100)/fileSize;
            sizeStart++;
            String percent = String.format("%.2f",percentDownloaded);
            updateMessage(percent + "% / Descargado "+ sizeStart/1000 + "MB" + " de " + sizeMegas +"MB a " + (read*0.000001)+" MBs");

            if (isCancelled()) {
                logger.trace("Descarga " + url.toString() + " cancelada");
                return null;
            }
        }


        updateProgress(1, 1);
        updateMessage("100 % / Descargado " + sizeMegas+"MB de "+ sizeMegas+"MB");

        logger.trace("Descarga " + url.toString() + " finalizada");
        return null;
    }
}
