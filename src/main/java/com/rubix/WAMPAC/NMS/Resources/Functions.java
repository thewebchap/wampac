package com.rubix.WAMPAC.NMS.Resources;

import com.profesorfalken.jpowershell.PowerShell;
import com.profesorfalken.jpowershell.PowerShellResponse;
import io.ipfs.api.MerkleNode;
import io.ipfs.api.NamedStreamable;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.List;


import static com.rubix.Resources.APIHandler.ipfs;
import static com.rubix.Resources.Functions.DATA_PATH;
import static com.rubix.Resources.Functions.readFile;
import static com.rubix.WAMPAC.NMS.Constants.PathConstants.logsFolder;

public class Functions {

    public static void populateLogs(){
        PowerShellResponse response = PowerShell.executeSingleCommand("Get-WinEvent -FilterHashTable @{logname='system'; starttime=(Get-Date) - (New-TimeSpan -Hours 6)} | Select TimeCreated,Id,LevelDisplayName | Export-Csv -Path "+logsFolder+"system.csv -NoTypeInformation");
        response = PowerShell.executeSingleCommand("Get-WinEvent -FilterHashTable @{logname='Application'; starttime=(Get-Date) - (New-TimeSpan -Hours 6)} | Select TimeCreated,Id,LevelDisplayName | Export-Csv -Path "+logsFolder+"Application.csv -NoTypeInformation");
        response = PowerShell.executeSingleCommand("Get-WinEvent -FilterHashTable @{logname='Security'; starttime=(Get-Date) - (New-TimeSpan -Hours 6)} | Select TimeCreated,Id,LevelDisplayName | Export-Csv -Path "+logsFolder+"Security.csv -NoTypeInformation");
//        response = PowerShell.executeSingleCommand("Get-WinEvent -FilterHashTable @{logname='Microsoft-Windows-NetworkProfile%4Operational'; level=1,2,3; starttime=(Get-Date) - (New-TimeSpan -Hours 24)} | Select TimeCreated,Id,LevelDisplayName | Export-Csv -Path "+logsFolder+"\\Microsoft-Windows-NetworkProfile%4Operational.csv -NoTypeInformation");
    }

    public static List<MerkleNode> addFolder(String folder){
        List<NamedStreamable> file = new NamedStreamable.FileWrapper(new File(folder)).getChildren();
        try {
            List<MerkleNode> response = ipfs.add(file, true, true);
            return response;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String convertToBinary(String strs){
        strs=strs.replaceAll("\\s+","");
        byte[] bytes = strs.getBytes();
        StringBuilder binary = new StringBuilder();
        for (byte b : bytes){
            int val = b;
            for (int i = 0; i < 8; i++){
                binary.append((val & 128) == 0 ? 0 : 1);
                val <<= 1;
            }
            binary.append(' ');
        }
        return binary.toString();
    }

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

}
