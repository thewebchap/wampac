package com.rubix.WAMPAC.DID;


import com.rubix.Resources.Functions;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Timer;

import static com.rubix.Resources.Functions.*;
import static com.rubix.WAMPAC.NMS.Constants.PathConstants.*;

public class DIDFunctions {
    public static Logger DIDFunctionsLogger = Logger.getLogger(DIDFunctions.class);
    public static boolean checkIPStatus() throws JSONException, UnknownHostException {

        String vipFile = readFile( "vip.json");
        JSONArray vipArray = new JSONArray(vipFile);

//        InetAddress myIP = InetAddress.getLocalHost();
//        String ip = myIP.getHostAddress();
//        System.out.println("IP Check" + ip);

        String ip = ipClass.getIP();
        System.out.println("/checkIpStatus IP Address: " + ip);

        for(int i = 0; i < vipArray.length(); i++){
            JSONObject object = vipArray.getJSONObject(i);
            if(object.getString("ip").equals(ip)){
                if(object.getString("status").equals("false")){
                    System.out.println("Status : " + object.getString("status"));
                    return true;
                }
            }
        }
        return false;
    }
//    public static boolean syncVip(String role) throws IOException, JSONException {
//        PropertyConfigurator.configure(LOGGER_PATH + "log4jWallet.properties");
//        Functions.pathSet();
//        boolean syncFlag = false;
//        StringBuilder result = new StringBuilder();
//        URL url = new URL(SYNC_IP + "/getvip");
//        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//        conn.setRequestMethod("GET");
//        BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
//        String line;
//
//        while ((line = rd.readLine()) != null) {
//            result.append(line);
//            syncFlag = true;
//        }
//        rd.close();
//        if(role.equals("Verifier"))
//            writeToFile(DATA_PATH + "vip.json", result.toString(), false);
//        else
//            writeToFile("vip.json", result.toString(), false);
//        return syncFlag;
//    }

    public static void changePath() throws JSONException {
        Functions.pathSet();
        PropertyConfigurator.configure(LOGGER_PATH + "log4jWallet.properties");
        File vip = new File("vip.json");
        String vipFile = readFile(vip.toString());
        JSONArray vipArray = new JSONArray(vipFile);
        writeToFile(DATA_PATH + "vip.json", vipArray.toString(), false);
        DIDFunctionsLogger.debug("vip.json File moved from current location");
        vip.delete();
        DIDFunctionsLogger.debug("vip.json File Deleted from current location");
    }

    public static String getRole(String ip) throws JSONException, IOException {
        PropertyConfigurator.configure(LOGGER_PATH + "log4jWallet.properties");
        File file = new File(DATA_PATH + "vip.json");
        if(file.exists()) {
            String vipFile = readFile(DATA_PATH + "vip.json");
            JSONArray vipArray = new JSONArray(vipFile);

            for (int i = 0; i < vipArray.length(); i++) {
                JSONObject object = vipArray.getJSONObject(i);
                if (object.getString("ip").equals(ip)) {
                    DIDFunctionsLogger.debug("role " + object.getString("role"));
                    return object.getString("role");
                }
            }
        }
        return "";
    }


    public static String verifierDID(String ip) throws JSONException, IOException {
        PropertyConfigurator.configure(LOGGER_PATH + "log4jWallet.properties");
        File file = new File(DATA_PATH + "vip.json");
        if(file.exists()) {
            String vipFile = readFile(DATA_PATH + "vip.json");
            JSONArray vipArray = new JSONArray(vipFile);

            //Check if already verified
            for (int i = 0; i < vipArray.length(); i++) {
                JSONObject vipArrayJSONObject = vipArray.getJSONObject(i);
                if (vipArrayJSONObject.getString("ip").equals(ip)) {
                    if (vipArrayJSONObject.getString("status").equals("true"))
                        return "Success";
                }
            }

            // Get all members peerid from vip.json
            JSONArray contactMembers = new JSONArray();
            for (int i = 0; i < vipArray.length(); i++) {
                JSONObject vipArrayJSONObject = vipArray.getJSONObject(i);
                if (vipArrayJSONObject.getString("role").equals("Verifier") && !vipArrayJSONObject.getString("ip").equals(ip)) {
                    contactMembers.put(vipArrayJSONObject.getString("ip"));
                }
            }
            DIDFunctionsLogger.debug("contact members" + contactMembers);
            //Create data to send the other verifiers
            String didFile = readFile(DATA_PATH + "DID.json");
            JSONArray didArray = new JSONArray(didFile);
            JSONObject didObject = didArray.getJSONObject(0);
            didObject.put("ip", ip);

            String mydid = didObject.getString("didHash");

            //contactmembers is list of peerids to contact
            // datatoquorum contain did,wid,peerid,ip of node - to be signed later

            DIDFunctionsLogger.debug("contacting contact members for verification");
            if (ContactNodes.contact(contactMembers, didObject, mydid))
                return "Success";
            else
                return "Failure";
        }
        else return "Failure";

//        //Get all members from vip.json
//        String vipFile = readFile(DATA_PATH + "vip.json");
//        JSONArray vipArray = new JSONArray(vipFile);
//
//        JSONArray contactMembers = new JSONArray();
//        for(int i = 0; i < vipArray.length(); i++){
//            JSONObject object = vipArray.getJSONObject(i);
//            if(object.getString("role").equals("Verifier") && !object.getString("ip").equals(ip)){
//                contactMembers.put(object.getString("peerid"));
//            }
//        }
//
//        JSONObject dataToQuorum = new JSONObject();
//
//        //Create data to send the other verifiers
//        String didFile = readFile(DATA_PATH + "DID.json");
//        JSONArray didArray = new JSONArray(didFile);
//        JSONObject didObject = didArray.getJSONObject(0);
//        didObject.put("ip", ip);
//        dataToQuorum.put("data", didObject);
//
//        //Contact all other verifiers
//        int count = 0;
//        String[] verifierCount = ContactNodes.contact(contactMembers, dataToQuorum);
//
//        //TODO: resp - true r false
//        //check the number of signatures verified
//        if (verifierCount.length == contactMembers.length()) {
//            for(int i=0; i< verifierCount.length; i++) {
//                JSONObject object = new JSONObject(verifierCount[i]);
//                if(object.get("count").equals(contactMembers.length()))
//                    count++;
//            }
//        }
//
//        //if all verifiers count = 7, then change in the DataTable and Vip.json
//        if (count == contactMembers.length()) {
//            String dataTableFile = readFile(DATA_PATH + "DataTable.json");
//            JSONArray dataTableArray = new JSONArray(dataTableFile);
//            JSONObject dataTableObject = dataTableArray.getJSONObject(0);
//            dataTableObject.put("signHash", fileHash);
//            writeToFile(DATA_PATH + "DataTable.json", new JSONArray().put(dataTableObject).toString(), false);
//            JSONArray newVipArray = new JSONArray();
//            for(int k = 0; k < vipArray.length(); k++){
//                JSONObject vipObject = vipArray.getJSONObject(k);
//                vipObject.put("status", true);
//                newVipArray.put(vipObject);
//            }
//            writeToFile(DATA_PATH + "vip.json", newVipArray.toString(), false);
//            return "Verifiers Agreed";
//        }
//        //count doesnot match - exit
//        else {
//            return "Invalid Signatures";
//        }
    }

    public static String userDID(String ip) throws JSONException, IOException {
        pathSet();
        PropertyConfigurator.configure(LOGGER_PATH + "log4jWallet.properties");
        File file = new File(DATA_PATH + "vip.json");
        if(file.exists()) {
            String vipFile = readFile(DATA_PATH + "vip.json");
            JSONArray vipArray = new JSONArray(vipFile);

            // Get all members peerid from vip.json

            JSONArray contactMembers = new JSONArray();
            for (int i = 0; i < vipArray.length(); i++) {
                JSONObject vipArrayJSONObject = vipArray.getJSONObject(i);
                if (vipArrayJSONObject.getString("role").equals("Verifier") && !vipArrayJSONObject.getString("ip").equals(ip)) {
                    contactMembers.put(vipArrayJSONObject.getString("ip"));
                }
            }
            DIDFunctionsLogger.debug("contact members" + contactMembers);
            //Create data to send the other verifiers
            String didFile = readFile(DATA_PATH + "DID.json");
            JSONArray didArray = new JSONArray(didFile);
            JSONObject didObject = didArray.getJSONObject(0);
            didObject.put("ip", ip);
            DIDFunctionsLogger.debug("contacting contact members for verification");
            if (UserContactNodes.contact(contactMembers, didObject))
                return "Success";
            else
                return "Failure";
        }else return "Failure";
    }

    public static int minVotes(int count) {
        return (count - 1) / 3 * 2 + 1;
    }

    public static boolean vipSync() throws IOException, JSONException {
        pathSet();
        String url = "http://172.17.128.102:9090/getvip";
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        // Setting basic get request
        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", "Mozilla/5.0");
        con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
        con.setRequestProperty("Accept", "application/json");
        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestProperty("Authorization", "null");

        int responseCode = con.getResponseCode();
        DIDFunctionsLogger.debug("Sending 'GET' request to URL : " + url + " for latest VIP");
        DIDFunctionsLogger.debug("Response Code : " + responseCode);

        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String response;
        StringBuffer vipList = new StringBuffer();

        while ((response = in.readLine()) != null) {
            vipList.append(response);
        }
        in.close();
        DIDFunctionsLogger.debug("VIP List String buffer" + vipList.toString());

        JSONArray vipArray = new JSONArray(vipList.toString());
        DIDFunctionsLogger.debug("VIP List JSON Array" + vipArray);

        if(vipArray != null){
            File vipFile = new File(DATA_PATH + "vip.json");
            if(!vipFile.exists())
                vipFile.createNewFile();

            writeToFile(vipFile.toString(), vipArray.toString(), false);
            return true;
        }
        else{
            return false;
        }
    }

    public static boolean rulesSync() throws IOException, JSONException {
        pathSet();
        String url = "http://172.17.128.102:9090/getrules";
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        // Setting basic get request
        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", "Mozilla/5.0");
        con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
        con.setRequestProperty("Accept", "application/json");
        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestProperty("Authorization", "null");

        int responseCode = con.getResponseCode();
        DIDFunctionsLogger.debug("Sending 'GET' request to URL : " + url + " for latest VIP");
        DIDFunctionsLogger.debug("Response Code : " + responseCode);

        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String response;
        StringBuffer rulesList = new StringBuffer();

        while ((response = in.readLine()) != null) {
            rulesList.append(response);
        }
        in.close();
        DIDFunctionsLogger.debug("Rules List String buffer" + rulesList.toString());

        JSONObject rulesObject = new JSONObject(rulesList.toString());
        DIDFunctionsLogger.debug("Rules List JSON Array" + rulesObject);

        pathSet();
        File nms = new File(nmsFolder);
        if (!nms.exists())
            nms.mkdirs();

        if(rulesObject != null){
            File rulesFile = new File(nmsFolder + "rules.json");
            if(!rulesFile.exists())
                rulesFile.createNewFile();

            writeToFile(rulesFile.toString(), rulesObject.toString(), false);
            return true;
        }
        else{
            return false;
        }
    }

    public static String macAddress(){
        String firstInterface = null;
        HashMap<String, String> addressByNetwork = new HashMap<>();
        String MAC = "";
        Enumeration<NetworkInterface> networkInterfaces;
        try {
            networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface network = networkInterfaces.nextElement();

                byte[] bmac = network.getHardwareAddress();
                if (bmac != null) {
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < bmac.length; i++)
                        sb.append(String.format("%02X%s", bmac[i], (i < bmac.length - 1) ? "-" : ""));

                    if (!sb.toString().isEmpty())
                        addressByNetwork.put(network.getName(), sb.toString());

                    if (!sb.toString().isEmpty() && firstInterface == null)
                        firstInterface = network.getName();
                }
            }
        } catch (SocketException e) {
            DIDFunctionsLogger.error("Socket Exception Occurred", e);
            e.printStackTrace();
        }
        if (firstInterface != null) {
            MAC = addressByNetwork.get(firstInterface);
            DIDFunctionsLogger.debug("MAC Address : " + MAC);
        }
        return MAC;
    }

}
