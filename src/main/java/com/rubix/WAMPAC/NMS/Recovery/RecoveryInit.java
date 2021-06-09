package com.rubix.WAMPAC.NMS.Recovery;


import com.rubix.Resources.Functions;
import com.rubix.WAMPAC.NMS.NLSS.Interact;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.Socket;
import java.util.Iterator;

import static com.rubix.Resources.Functions.readFile;
import static com.rubix.Resources.Functions.writeToFile;
import static com.rubix.WAMPAC.NMS.Constants.PathConstants.RecoveryFolder;
import static com.rubix.WAMPAC.NMS.Constants.PathConstants.nmsFolder;


public class RecoveryInit {
    static int PORT = 15050;
    public static Logger RecoveryInitLogger = Logger.getLogger(RecoveryInit.class);

    public static void Recover(String did) throws JSONException {
        PropertyConfigurator.configure(Functions.LOGGER_PATH + "log4jWallet.properties");
        JSONArray records = new JSONArray(readFile(nmsFolder+"log.json"));
        JSONArray shares = new JSONArray();
        JSONArray quorum = null;
        String tid = null;
        for (int i = records.length()-1; i >= 0 ; i--) {
            if(records.getJSONObject(i).getString("status").equals("backup")){
                quorum = records.getJSONObject(i).getJSONArray("quorumiplist");
                tid = records.getJSONObject(i).getString("tid");
                break;
            }
        }

        Socket socket;
        try {
            socket = new Socket(quorum.getString(0), PORT);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintStream out = new PrintStream(socket.getOutputStream());
            JSONObject details = new JSONObject();
            details.put("tid",tid);
            details.put("did",did);
            out.println(details);
            String essentialResponse = in.readLine();
            if(essentialResponse!=null || !essentialResponse.equals("Not_Found")) {
                JSONObject temp = new JSONObject(essentialResponse);
                shares.put(temp);

                in.close();
                out.close();
                socket.close();


                for (int i = 1; i < quorum.length() && shares.length()<2; i++) {
                    socket = new Socket(quorum.getString(i), PORT);
                    in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    out = new PrintStream(socket.getOutputStream());
                    out.println(details);
                    String response = in.readLine();
                    if(response!=null || !response.equals("Not_Found")) {
                        temp = new JSONObject(response);
                        shares.put(temp);
                    }
                    in.close();
                    out.close();
                    socket.close();
                }

                JSONObject essential = shares.getJSONObject(0);
                JSONObject cand1 = shares.getJSONObject(1);
//                JSONObject cand2 = shares.getJSONObject(2);

                Iterator<String> keys = essential.keys();

                while(keys.hasNext()) {
                    String key = keys.next();
//                    File recovered = new File(nmsFolder+"Recovered");RecoveryFolder
                    File recovered = new File(RecoveryFolder);
                    if(!recovered.exists())
                        recovered.mkdirs();
                    writeToFile(RecoveryFolder + key, Interact.getback(essential.getString(key),cand1.getString(key)),false);
                }
                RecoveryInitLogger.debug("Recovery Successful");
            }
            else{
                // disconnect
                RecoveryInitLogger.error("Could not fetch essential share");
                in.close();
                out.close();
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }




    }
}
