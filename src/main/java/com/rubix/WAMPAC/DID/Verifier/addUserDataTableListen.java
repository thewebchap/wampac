package com.rubix.WAMPAC.DID.Verifier;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;

import static com.rubix.Resources.Functions.*;

public class addUserDataTableListen {

    public static Logger addUserDataTableListenLogger = Logger.getLogger(addUserDataTableListen.class);
    public static String result = "";
    public static String listen(JSONObject userRawData) {
        setDir();
        setConfig();
        File didFolder = new File(configPath);
        if (!didFolder.exists())
            return "No DID";

        pathSet();
        PropertyConfigurator.configure(LOGGER_PATH + "log4jWallet.properties");

        try {
            String newDid = userRawData.getString("didHash");

            File vipFileCon = new File(DATA_PATH + "vip.json");
            if(!vipFileCon.exists())
                return "No VIP File";

            String dataFile = readFile(DATA_PATH + "DataTable.json");
            JSONArray dataTableList = new JSONArray(dataFile);
            boolean flag = false;
            int index = 0;
            for(int i = 0; i < dataTableList.length(); i++){
                if(dataTableList.getJSONObject(i).getString("didHash").equals(newDid)) {
                    flag = true;
                    index = i;
                }
            }
            if(flag)
                dataTableList.remove(index);

            dataTableList.put(userRawData);
            writeToFile(DATA_PATH + "DataTable.json", dataTableList.toString(), false);


        } catch (JSONException e) {
            result = "IOException Occurred" + e;
            addUserDataTableListenLogger.error("JSONException Occurred", e);
            e.printStackTrace();
            addUserDataTableListenLogger.debug("Connection Closed");


        }
        return result;
    }
}
