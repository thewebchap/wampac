package com.rubix.WAMPAC.Anomaly.Trackers;


import com.rubix.WAMPAC.Anomaly.Communication.Sender;
import org.json.JSONException;

import java.io.IOException;

import static com.rubix.WAMPAC.Anomaly.Resources.Functions.executeShell;
import static com.rubix.WAMPAC.Anomaly.Trackers.Monitor.checkWIFI;


public class WIFI implements Runnable {

    @Override
    public void run() {
        try {
            while (true) {
                checkWIFI();
                executeShell("Disable-NetAdapter -Name \"Wi-Fi\" -Confirm:$false");
                Sender.start("WIFI", 15060);
            }
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
    }
}
