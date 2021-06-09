package com.rubix.WAMPAC.NMSCollection;

import org.json.JSONException;

import java.io.IOException;

import static com.rubix.WAMPAC.NMSCollection.FileSystem.FileSystemMain;


public class FileSystemThread implements Runnable {
    @Override
    public void run() {
        while (true) {
            try {
                FileSystemMain();
                Thread.sleep( 60000);
            } catch (JSONException | IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
