package com.rubix.WAMPAC.DID.Verifier;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;

import static com.rubix.Resources.Functions.*;

public class addUserVipListen {

    public static Logger addUserVipListenLogger = Logger.getLogger(addUserVipListen.class);
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

            String newIp = userRawData.getString("ip");
            File vipFileCon = new File(DATA_PATH + "vip.json");
            if (!vipFileCon.exists())
                return "No VIP File";

            String vipFile = readFile(DATA_PATH + "vip.json");
            JSONArray vipArray = new JSONArray(vipFile);

            boolean flag = false;
            int index = 0;
            for(int i = 0; i < vipArray.length(); i++){
                if(vipArray.getJSONObject(i).getString("ip").equals(newIp)) {
                    flag = true;
                    index = i;
                }
            }
            if(flag)
                vipArray.remove(index);

            vipArray.put(userRawData);
            writeToFile(DATA_PATH + "vip.json", vipArray.toString(), false);

            result = "User Added To Vip";

        } catch (JSONException e) {
            result = "IOException Occurred" + e;
            addUserVipListenLogger.error("JSONException Occurred ", e);
            e.printStackTrace();
            addUserVipListenLogger.debug("Connection Closed");
        }
        return result;
    }
}
