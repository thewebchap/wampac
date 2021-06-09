package com.rubix.WAMPAC.FileAccess;

import com.rubix.Resources.Functions;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

import static com.rubix.Resources.Functions.*;
import static com.rubix.WAMPAC.FileAccess.FileAccessFunctions.DRIVE_PATH;

public class UIFunctions {
    public static Logger UIFunctions = Logger.getLogger(UIFunctions.class);
    public static int sharedFilesCount() {
        Functions.pathSet();
        FileAccessFunctions.createRbxDrive();
        PropertyConfigurator.configure(LOGGER_PATH + "log4jWallet.properties");
        File file = new File(DRIVE_PATH + "fileTokenList.json");
        if (file.exists()) {
            String fileList = readFile(file.toString());
            JSONArray fileArray = null;
            try {
                fileArray = new JSONArray(fileList);
            } catch (JSONException e) {
                UIFunctions.error("JSON Exception Occurred", e);
                e.printStackTrace();
            }
            UIFunctions.debug("Shared Files Length: " + fileArray.length());
            return fileArray.length();
        }
        else
            return 0;
    }

    public static ArrayList<String> sharedFilesList(){
        Functions.pathSet();
        FileAccessFunctions.createRbxDrive();
        PropertyConfigurator.configure(LOGGER_PATH + "log4jWallet.properties");
        ArrayList<String> filesList = new ArrayList<String>();
        File file = new File(DRIVE_PATH + "Contracts.json");
        if (file.exists()) {
            String contractList = readFile(file.toString());
            JSONArray fileArray = null;
            try {
                fileArray = new JSONArray(contractList);
                for(int i = 0; i < fileArray.length(); i++){
                    JSONObject fileObject = fileArray.getJSONObject(i);
                    filesList.add(i, fileObject.getString("fileName"));
                }
            } catch (JSONException e) {
                UIFunctions.error("JSON Exception Occurred", e);
                e.printStackTrace();
            }
            UIFunctions.debug("List of files: " + filesList);
        }
        else
            UIFunctions.debug("File does not exist");
        return filesList;
    }

    public static JSONObject sharedFileDetails(String fileName) throws JSONException {
        Functions.pathSet();
        FileAccessFunctions.createRbxDrive();
        PropertyConfigurator.configure(LOGGER_PATH + "log4jWallet.properties");
        JSONObject details = new JSONObject();
        File file = new File(DRIVE_PATH + "Contracts.json");
        if (file.exists()) {
            String contractList = readFile(file.toString());
            try {
                JSONArray fileArray = new JSONArray(contractList);
                for(int i = 0; i < fileArray.length(); i++){
                    JSONObject fileObject = fileArray.getJSONObject(i);
                    if(fileObject.getString("fileName").equals(fileName)) {
                        details = fileObject;
                        String token = details.getString("token");
                        File tokenChainFile = new File(TOKENCHAIN_PATH + token + ".json");
                        if(tokenChainFile.exists()) {
                            String tokenChainData = readFile(tokenChainFile.toString());
                            JSONArray tokenChainArray = new JSONArray(tokenChainData);
                            details.put("accessCount", tokenChainArray.length());
                            break;
                        }else {
                            details.put("accessCount", 0);
                            break;
                        }
                    }
                }
            } catch (JSONException e) {
                UIFunctions.error("JSON Exception Occurred", e);
                e.printStackTrace();
            }
        }
        else {
            UIFunctions.debug("File does not exist");
            details.put("Error", "File does not exist");
        }
        return details;
    }

    public static JSONObject sharedFileMetaData(String fileName) throws JSONException {
        Functions.pathSet();
        FileAccessFunctions.createRbxDrive();
        PropertyConfigurator.configure(LOGGER_PATH + "log4jWallet.properties");
        JSONObject details = new JSONObject();
        File file = new File(DRIVE_PATH + "Contracts.json");
        if (file.exists()) {
            String contractList = readFile(file.toString());
            try {
                JSONArray fileArray = new JSONArray(contractList);
                for(int i = 0; i < fileArray.length(); i++){
                    JSONObject fileObject = fileArray.getJSONObject(i);
                    if(fileObject.getString("fileName").equals(fileName)) {
                        details.put("token", fileObject.getString("token"));
                        details.put("fileHash", fileObject.getString("fileHash"));
                        break;
                    }
                }
            } catch (JSONException e) {
                UIFunctions.error("JSON Exception Occurred", e);
                e.printStackTrace();
            }
        }
        else {
            UIFunctions.debug("File does not exist");
            details.put("Error", "File does not exist");
        }
        return details;
    }


}
