package com.rubix.WAMPAC.DID;

import com.rubix.Resources.IPFSNetwork;
import io.ipfs.api.IPFS;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

import static com.rubix.Resources.Functions.*;
import static com.rubix.Resources.IPFSNetwork.*;

public class VerifierUserListen implements Runnable {

    public static Logger VerifierUserLogger = Logger.getLogger(VerifierUserListen.class);
    public static IPFS ipfs = new IPFS("/ip4/127.0.0.1/tcp/" + IPFS_PORT);

    @Override
    public void run() {
        while(true) {
            pathSet();
            PropertyConfigurator.configure(LOGGER_PATH + "log4jWallet.properties");
            String peerID, appName, senderPID = "";
            ServerSocket serverSocket = null;
            Socket socket = null;
            try {

                //listening on QUORUM_PORT & APPNAME = Peerid+verifierdid
//                peerID = getPeerID(DATA_PATH + "DID.json");
//                appName = peerID.concat("verifieruserdid");

                //  IPFSNetwork.listen(appName, QUORUM_PORT);


                VerifierUserLogger.debug("Verifier Listening on " + QUORUM_PORT);
                serverSocket = new ServerSocket(QUORUM_PORT);
                socket = serverSocket.accept();


                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintStream out = new PrintStream(socket.getOutputStream());

                String didFile = readFile(DATA_PATH + "DID.json");
                JSONArray didArray = new JSONArray(didFile);
                JSONObject didObject = didArray.getJSONObject(0);
                String verifierDid = didObject.getString("didHash");

                String incomingRawData;

                VerifierUserLogger.debug("Waiting for IP-Peer Pairs");
                while ((incomingRawData = in.readLine()) == null) {
                }
                VerifierUserLogger.debug("IP-Peer Pairs: " + incomingRawData);
                VerifierUserLogger.debug("Verifying pairs");
                JSONObject userRawData = new JSONObject(incomingRawData);
                // senderPID = userRawData.getString("peerid");
                String vipFile = readFile(DATA_PATH + "vip.json");
                JSONArray vipArray = new JSONArray(vipFile);
                boolean userMapped = false;
                for (int l = 0; l < vipArray.length(); l++) {
                    JSONObject vipObject = vipArray.getJSONObject(l);
                    if (vipObject.getString("peerid").equals(userRawData.getString("peerid")))
                        if (vipObject.getString("ip").equals(userRawData.getString("ip")))
                            if (vipObject.getString("status").equals("false"))
                                userMapped = true;

                }
                VerifierUserLogger.debug("Mapping status: " + userMapped);
                if (userMapped) {
                    VerifierUserLogger.debug("IP_pair of user mapped");
                    JSONObject signObject = new JSONObject();
                    signObject.put("did", verifierDid);
                    signObject.put("data", userRawData);
                    String mySign = sign(signObject.toString());

                    JSONObject responseObject = new JSONObject();
                    responseObject.put("did", verifierDid);
                    responseObject.put("signature", mySign);
                    responseObject.put("status", "Valid IP - Peer Mapping");
                    out.println(responseObject.toString());
                    VerifierUserLogger.debug("Sending sign to user: " + responseObject.toString());

                    VerifierUserLogger.debug("Waiting for IPFS Hash of Signatures List");
                    String signatures;
                    while ((signatures = in.readLine()) == null) {
                    }
                    //fetch the hash from IPFS
                    VerifierUserLogger.debug("ipfs hash" + signatures);
                    JSONArray signsArray = new JSONArray(signatures);

                    for (int l = 0; l < vipArray.length(); l++) {
                        JSONObject userObject = vipArray.getJSONObject(l);
                        if (userObject.getString("peerid").equals(userRawData.getString("peerid"))) {
                            userObject.put("status", "true");
                            vipArray.remove(l);
                            vipArray.put(userObject);
                        }
                    }
                    VerifierUserLogger.debug("Updating vip.json");
                    writeToFile(DATA_PATH + "vip.json", vipArray.toString(), false);

                    File signaturesFile = new File(DATA_PATH + "VerifiedDidSignatures.json");
                    if (!signaturesFile.exists())
                        signaturesFile.createNewFile();
                    VerifierUserLogger.debug("All sign data is written into file - VerifiedDidSignatures.json");
                    writeToFile(signaturesFile.toString(), signsArray.toString(), false);
                    VerifierUserLogger.debug("pushing signatures into ipfs");
                    String fileHash = add(signaturesFile.toString(), ipfs);

                    String dataTableFile = readFile(DATA_PATH + "DataTable.json");
                    JSONArray dataTableArray = new JSONArray(dataTableFile);
                    JSONArray newDataTableArray = new JSONArray();
                    for (int l = 0; l < dataTableArray.length(); l++) {
                        JSONObject dataTableObject = dataTableArray.getJSONObject(l);
                        if (dataTableObject.getString("peerid").equals(userRawData.getString("peerid")))
                            dataTableObject.put("signHash", fileHash);
                        newDataTableArray.put(dataTableObject);
                    }

                    VerifierUserLogger.debug("signHash of user - dataTable.json");
                    writeToFile(DATA_PATH + "DataTable.json", newDataTableArray.toString(), false);

                    out.close();
                    in.close();
                    socket.close();
                    serverSocket.close();

                } else {
                    JSONObject responseObject = new JSONObject();
                    responseObject.put("did", verifierDid);
                    responseObject.put("signature", "");
                    responseObject.put("status", "Invalid IP - Peer Mapping");
                    out.println(responseObject.toString());
                    VerifierUserLogger.debug("Sending status to user: " + responseObject.toString());
                    out.close();
                    in.close();
                    socket.close();
                    serverSocket.close();
                    VerifierUserLogger.debug("Invalid IP - Peer Mapping");
                    VerifierUserLogger.debug("Connection Closed");
                }
            } catch (IOException e) {
                //executeIPFSCommands(" ipfs p2p close -t /p2p/" + senderPID);
                VerifierUserLogger.error("IOException Occurred", e);
                e.printStackTrace();
                VerifierUserLogger.debug("Connection Closed");

            } catch (JSONException e) {
                //executeIPFSCommands(" ipfs p2p close -t /p2p/" + senderPID);
                VerifierUserLogger.error("JSONException Occurred", e);
                e.printStackTrace();
                VerifierUserLogger.debug("Connection Closed");


            } finally {
                try {
                    //   executeIPFSCommands(" ipfs p2p close -t /p2p/" + senderPID);
                    socket.close();
                    serverSocket.close();
                    VerifierUserLogger.debug("Connection Closed");
                } catch (IOException e) {
                    // executeIPFSCommands(" ipfs p2p close -t /p2p/" + senderPID);
                    VerifierUserLogger.error("IOException Occurred", e);
                    e.printStackTrace();
                }
            }
            VerifierUserLogger.debug("User Verified");
        }
    }
}
