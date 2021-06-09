package com.rubix.WAMPAC.DID.User;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.util.Random;

import static com.rubix.Resources.Functions.*;

public class getDataFromVerifier {
    public static Logger getVerifierIpListLogger = Logger.getLogger(getDataFromVerifier.class);
    private static final String USER_AGENT = "Mozilla/5.0";
    private static String result = "";
    public static Socket verifierServerSocket;
    public static BufferedReader serverInput, input;
    public static PrintStream output;
    public static String getData() {
        pathSet();
        PropertyConfigurator.configure(LOGGER_PATH + "log4jWallet.properties");
        try {
            String userUrl = "http://172.17.128.102:9090/getverifiers";
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
            StringBuffer ipList = new StringBuffer();

            while ((userResponse = serverInput.readLine()) != null) {
                ipList.append(userResponse);
            }
            serverInput.close();
            JSONArray verifiersIpArray = new JSONArray(ipList.toString());

            if(verifiersIpArray.length() > 0) {

                /**
                 * Contact one of the verifier
                 */
                Random rn = new Random();
                int index = rn.nextInt(verifiersIpArray.length());

                getVerifierIpListLogger.debug("Connecting to verifier: " + verifiersIpArray.getString(index));
                verifierServerSocket = new Socket(verifiersIpArray.getString(index), 8787);
                input = new BufferedReader(new InputStreamReader(verifierServerSocket.getInputStream()));
                output = new PrintStream(verifierServerSocket.getOutputStream());

                output.println("Request");

                String incomingVipList;
                getVerifierIpListLogger.debug("Waiting for vip.json");
                while ((incomingVipList = input.readLine()) == null) {
                }

                JSONObject data = new JSONObject(incomingVipList);
                JSONArray vipArray = data.getJSONArray("vip");
                JSONArray dataArray = data.getJSONArray("datatable");
                if (vipArray != null && dataArray != null) {
                    output.println("ACK");


                    File vipFile = new File(DATA_PATH + "vip.json");
                    if (!vipFile.exists())
                        vipFile.createNewFile();
                    writeToFile(DATA_PATH + "vip.json", vipArray.toString(), false);

                    File dataTableFile = new File(DATA_PATH + "DataTable.json");
                    if (!dataTableFile.exists())
                        dataTableFile.createNewFile();
                    writeToFile(DATA_PATH + "DataTable.json", dataArray.toString(), false);

                    result = "Success";
                    getVerifierIpListLogger.debug("Success");
                }else {
                    output.println("No-ACK");
                    result = "Sync Failure. Try again";
                }

                serverInput.close();
                verifierServerSocket.close();
                input.close();
                output.close();

            }
            else{
                serverInput.close();
                verifierServerSocket.close();
                input.close();
                output.close();
                result = "Sync Failure. Try again";
            }

        } catch (IOException e) {

            result = "Sync Failure. Try again";
            getVerifierIpListLogger.error("IOException Occurred", e);
            e.printStackTrace();

        } catch (JSONException e) {
            result = "Sync Failure. Try again";
            getVerifierIpListLogger.error("JSONException Occurred", e);
            e.printStackTrace();

        } finally {
            try {
                serverInput.close();
                verifierServerSocket.close();
                input.close();
                output.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            getVerifierIpListLogger.debug("Connection Closed");
        }
        return result;
    }
}
