package com.rubix.WAMPAC.NMSCollection;

import org.json.JSONException;

import java.io.IOException;


public class DiskThread implements Runnable {
    @Override
    public void run() {
        while (true) {
            try {
                DiskStorage.DiskStorageMain();
                Thread.sleep( 60000);
            } catch (JSONException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
