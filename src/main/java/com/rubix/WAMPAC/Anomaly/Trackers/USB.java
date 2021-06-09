package com.rubix.WAMPAC.Anomaly.Trackers;


import com.rubix.WAMPAC.Anomaly.Communication.Sender;
import org.json.JSONException;

import java.io.IOException;

import static com.rubix.WAMPAC.Anomaly.Resources.Functions.executeShell;
import static com.rubix.WAMPAC.Anomaly.Trackers.Monitor.checkUSB;


public class USB implements Runnable {

    @Override
    public void run() {
        try {
            while (true) {
                checkUSB();
                executeShell("Set-ItemProperty -Path \"HKLM:\\SYSTEM\\CurrentControlSet\\Services\\USBSTOR\\\" -Name \"start\" -Value 4");
                Sender.start("USB", 15060);
            }
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
    }
}
