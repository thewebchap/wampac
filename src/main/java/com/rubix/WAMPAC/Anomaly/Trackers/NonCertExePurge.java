package com.rubix.WAMPAC.Anomaly.Trackers;


import com.rubix.WAMPAC.Anomaly.Communication.Sender;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import static com.rubix.WAMPAC.Anomaly.Resources.Functions.executeShell;
import static com.rubix.WAMPAC.Anomaly.Trackers.Monitor.checkExeCert;


public class NonCertExePurge implements Runnable {

    @Override
    public void run() {
        try {
            while (true) {
                checkExeCert();
                JSONArray records = new JSONArray(executeShell("Get-ChildItem -Path D:\\ -Filter \"*.exe\" -Recurse  | select Directory,Name | ConvertTo-Json"));
                for (int i = 0; i < records.length(); i++){
                    JSONObject record = records.getJSONObject(i);
                    JSONObject directory = record.getJSONObject("Directory");
                    String dir = directory.getString("FullName");
                    String filename = record.getString("Name");
                    executeShell("Remove-Item \""+dir+"\\"+filename+"\"");
                }

                Sender.start("Uncertified EXE", 15060);
            }
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
    }
}
