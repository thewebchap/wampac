package com.rubix.WAMPAC.Anomaly.Resources;

import com.profesorfalken.jpowershell.PowerShell;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import static com.rubix.Resources.Functions.DATA_PATH;
import static com.rubix.Resources.Functions.readFile;

public class Functions {
    public static JSONArray getVerifierIPList() throws JSONException {
        JSONObject temp;
        JSONArray ip = new JSONArray();
        File file = new File(DATA_PATH+"vip.json");
        if(file.exists()) {
            JSONArray verifiers = new JSONArray(readFile(DATA_PATH + "vip.json"));
            for (int i = 0; i < verifiers.length(); i++) {
                temp = verifiers.getJSONObject(i);
                if (temp.getString("role").equals("Verifier")) {
                    ip.put(temp.getString("ip"));
                }
            }
            return ip;
        }
        else return new JSONArray();
    }

    public static String executeShell(String command) {
        String response = PowerShell.executeSingleCommand(command).getCommandOutput();
        return response;
    }
}
