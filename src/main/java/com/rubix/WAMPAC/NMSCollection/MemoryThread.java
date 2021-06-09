package com.rubix.WAMPAC.NMSCollection;

import org.json.JSONException;

import java.io.IOException;


public class MemoryThread implements Runnable {
    @Override
    public void run() {
        while (true) {
            try {
                MemoryInfo.MemoryMain();
                Thread.sleep( 60000 );
            } catch (JSONException | IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
