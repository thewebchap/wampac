package com.rubix.WAMPAC.DID;

import com.rubix.Resources.Functions;
import com.rubix.Resources.IPFSNetwork;
import io.ipfs.api.IPFS;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.*;

import static com.rubix.Resources.Functions.*;

public class RequestTokens {
    public static Logger getTokensLogger = Logger.getLogger(RequestTokens.class);
    private static final String USER_AGENT = "Mozilla/5.0";
    public static BufferedReader serverInput;
    public static PrintStream serverOutput;

    public static String getTokens() {
        String result = "";
        pathSet();
        IPFS ipfs = new IPFS("/ip4/127.0.0.1/tcp/" + IPFS_PORT);
        PropertyConfigurator.configure(LOGGER_PATH + "log4jWallet.properties");
        try {
            String userUrl = "http://172.17.128.102:9090/gettokens";
            URL userObj = new URL(userUrl);
            HttpURLConnection userCon = (HttpURLConnection) userObj.openConnection();

            // Setting basic get request
            userCon.setRequestMethod("GET");
            userCon.setRequestProperty("User-Agent", USER_AGENT);
            userCon.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
            userCon.setRequestProperty("Accept", "application/json");
            userCon.setRequestProperty("Content-Type", "application/json");
            userCon.setRequestProperty("Authorization", "null");

            serverInput = new BufferedReader(new InputStreamReader(userCon.getInputStream()));
            String userResponse;
            StringBuffer tokenList = new StringBuffer();

            while ((userResponse = serverInput.readLine()) != null) {
                tokenList.append(userResponse);
            }
            serverInput.close();

            getTokensLogger.debug("Received tokenList from Server: " + tokenList);
            JSONArray tokensArray = new JSONArray(tokenList.toString());
            if(tokensArray.length() > 0) {
                File tokenListFile = new File(WALLET_DATA_PATH + "tokenList.json");
                if (!tokenListFile.exists()) {
                    tokenListFile.createNewFile();
                    writeToFile(WALLET_DATA_PATH + "tokenList.json", String.valueOf(new JSONArray()), false);
                }
                String tokenListData = readFile(WALLET_DATA_PATH + "tokenList.json");
                JSONArray tokenListArray = new JSONArray(tokenListData);


                for (int i = 0; i < tokensArray.length(); i++) {
                    String tokenHash = tokensArray.getString(i);

                    String tokenIpfsGet = IPFSNetwork.get(tokenHash, ipfs);
                    System.out.println(tokenIpfsGet);
                    File tokenFile = new File(TOKENS_PATH + tokenHash);
                    tokenFile.createNewFile();
                    writeToFile(TOKENS_PATH + tokenHash, tokenIpfsGet, false);
                    IPFSNetwork.add(Functions.TOKENS_PATH + tokenHash, ipfs);

                    File tokenChainFile = new File(TOKENCHAIN_PATH + tokenHash + ".json");
                    tokenChainFile.createNewFile();
                    writeToFile(TOKENCHAIN_PATH + tokenHash + ".json", "[]", false);
                    IPFSNetwork.add(TOKENCHAIN_PATH + tokenHash + ".json", ipfs);

                    JSONObject tokenObject = new JSONObject();
                    tokenObject.put("tokenHash", tokenHash);
                    tokenListArray.put(tokenObject);

                    getTokensLogger.debug("IPFS get Success: " + tokenHash);

                }
                writeToFile(WALLET_DATA_PATH + "tokenList.json", tokenListArray.toString(), false);
                result = "Success";
            }else
                result = "Failure";

        } catch (IOException | JSONException e) {
            result = "Failure";
            e.printStackTrace();
        }
        return result;
    }
}
