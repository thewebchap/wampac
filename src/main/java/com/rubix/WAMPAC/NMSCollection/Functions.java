package com.rubix.WAMPAC.NMSCollection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;

import java.nio.charset.StandardCharsets;

import static com.rubix.Resources.Functions.*;
import static com.rubix.Resources.IPFSNetwork.executeIPFSCommands;

public class Functions {
    public static JSONArray readDataFromCSV(String fileName) {

        String nameString="", vendorString="";
        JSONArray jsonArray = new JSONArray();
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line = "";
            br.readLine();
            br.readLine();
            br.readLine();
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (!line.equals("")) {
                    String[] attributes = line.split(",");
                    String name = attributes[1];
                    String vendor = attributes[2];
                    nameString = new String(name.getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_16);
                    vendorString = new String(vendor.getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_16);
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("name", nameString.substring(0, nameString.length()-1));
                    jsonObject.put("vendor", vendorString);
                    jsonArray.put(jsonObject);
                }
            }
        } catch (IOException | JSONException ioe) {
            ioe.printStackTrace();
        }
        return jsonArray;
    }

    public static void createDIR(JSONArray array) throws JSONException {
        for(int i = 0; i < array.length(); i++){
            File file = new File(array.getString(i));
            if(!file.exists())
                file.mkdir();
        }
    }

}
