package com.rubix.WAMPAC.Anomaly.Trackers;


import com.rubix.WAMPAC.Anomaly.Communication.Sender;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;

import static com.rubix.WAMPAC.Anomaly.Resources.Functions.executeShell;
import static com.rubix.WAMPAC.Anomaly.Trackers.Monitor.checkLocalAccount;

public class PrivilageDrop implements Runnable {

    @Override
    public void run() {
        try {
            while (true) {
                checkLocalAccount();
                JSONArray users = new JSONArray(executeShell("net localgroup administrators | select -skip 6 | ? {$_ -and $_ -notmatch 'successfully|^administrator|^msp.localadmin|^xyz.devadmin$'}  | ConvertTo-Json"));
                for (int i = 0; i < users.length(); i++)
                    executeShell("net localgroup administrators'"+users.getString(i)+"'/delete");

                Sender.start("PrivilageDrop", 15060);
            }
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
    }
}
