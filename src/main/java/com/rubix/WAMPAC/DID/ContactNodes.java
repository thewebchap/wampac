package com.rubix.WAMPAC.DID;

import com.rubix.AuthenticateNode.Interact;
import com.rubix.Resources.IPFSNetwork;
import io.ipfs.api.IPFS;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

import static com.rubix.AuthenticateNode.Authenticate.verifySignature;
import static com.rubix.Resources.Functions.*;
import static com.rubix.Resources.Functions.calculateHash;
import static com.rubix.Resources.IPFSNetwork.add;
import static com.rubix.Resources.IPFSNetwork.executeIPFSCommands;

public class ContactNodes {
    public static volatile JSONArray signatureDetails = new JSONArray();
    public static volatile JSONArray rawDetails = new JSONArray();
    public static volatile int rawCount, loopCount =0;
    public static String fileHash;
    public static Logger ContactNodesLogger = Logger.getLogger(ContactNodes.class);
    public static volatile int verifiedcount;
    public static volatile int nonverifiedcount;
    private static final Object countLock = new Object();
    private static final Object signLock = new Object();

    public static synchronized void rawDataSync(JSONObject data){
        PropertyConfigurator.configure(LOGGER_PATH + "log4jWallet.properties");
        synchronized (countLock) {
            rawDetails.put(data);
            ContactNodesLogger.debug("added to raw details in sync block: \n" + rawDetails.toString());
        }
    }

    public static synchronized void signatureSync(JSONObject data, String initiatordid, JSONArray contractMembers) throws JSONException, IOException {
        PropertyConfigurator.configure(LOGGER_PATH + "log4jWallet.properties");
        synchronized (signLock) {
            signatureDetails.put(data);

            if(signatureDetails.length() == contractMembers.length()){
                JSONObject signObject = new JSONObject();
                signObject.put("did", initiatordid);
                signObject.put("data", rawDetails);
                String mySign = sign(signObject.toString());

                JSONObject responseObject = new JSONObject();
                responseObject.put("did", initiatordid);
                responseObject.put("signature", mySign);
                responseObject.put("status", "Verifiers Matched");
                signatureDetails.put(responseObject);
            }

            ContactNodesLogger.debug("added to signature details in sync block: \n" + signatureDetails.toString());
        }
    }


    public static boolean contact(JSONArray contractMembers, JSONObject datatosign, String initiatordid) throws JSONException, IOException {
        PropertyConfigurator.configure(LOGGER_PATH + "log4jWallet.properties");
        int sizeofverifierpeers = contractMembers.length();
        PrintStream[] cout = new PrintStream[sizeofverifierpeers];
        BufferedReader[] cin = new BufferedReader[sizeofverifierpeers];
        Socket[] senderSocket = new Socket[sizeofverifierpeers];
        IPFS ipfs = new IPFS("/ip4/127.0.0.1/tcp/" + IPFS_PORT);
        ArrayList<String> DID = new ArrayList<>();
        ArrayList<String> PeerID = new ArrayList<>();
        ArrayList<String> appName = new ArrayList<>();
        String vipFile = readFile(DATA_PATH + "vip.json");
        JSONArray vipArray = new JSONArray(vipFile);
        Thread[] verifierThreads = new Thread[contractMembers.length()];
        verifiedcount=0;
        nonverifiedcount=0;
        rawDetails = new JSONArray();
        signatureDetails = new JSONArray();
        rawDetails.put(datatosign);
        rawCount = 0;
        loopCount =0;



        for (int i = 0; i < sizeofverifierpeers; i++) {
            int j = i;
            verifierThreads[i] = new Thread(() -> {
                try {
                    //initiating threads to rest of the verifiers with appname = peerid+verifierdid
//                    PeerID.add(contractMembers.get(j).toString());
//                    appName.add(PeerID.get(j).concat("verifierdid"));
//
//                    IPFSNetwork.swarmConnect(PeerID.get(j), ipfs);
//                    IPFSNetwork.forward(appName.get(j), SEND_PORT + j, PeerID.get(j));
                    ContactNodesLogger.debug("Initiating Socket communication");
                    senderSocket[j] = new Socket(contractMembers.get(j).toString(), SEND_PORT);
                    cin[j] = new BufferedReader(new InputStreamReader(senderSocket[j].getInputStream()));
                    cout[j] = new PrintStream(senderSocket[j].getOutputStream());

                    //Initiating request for data = did, peerid, wid, ip
                    ContactNodesLogger.debug("Sending REQ for Initializing connection with other verifiers");
                    cout[j].println("REQ");

                    //TODO: If req is not received by other verifiers, they close the connection.
                    // But Verifier 1 - what to do?

                    //get data from other verifiers
                    rawDataSync(new JSONObject(cin[j].readLine()));

                    ContactNodesLogger.debug("Fetched raw data from other verifiers");

                    while (rawDetails.length() <= contractMembers.length()) {}

                    ContactNodesLogger.debug("Raw details length: " + rawDetails.length());
                    ContactNodesLogger.debug("RawCount: " + rawCount);
                    //send all the data collectively = IP and PeerID pair
                    ContactNodesLogger.debug("checking raw count and vip array length");

                    if (rawDetails.length() == (contractMembers.length()+1)) {
                        //checking the pairs
                        ContactNodesLogger.debug("checking raw count and raw details length");
                        for (int k = 0; k < rawDetails.length(); k++) {
                            JSONObject rawObject = rawDetails.getJSONObject(k);
                            for (int l = 0; l < vipArray.length(); l++) {
                                JSONObject vipObject = vipArray.getJSONObject(l);
                                if (vipObject.getString("peerid").equals(rawObject.getString("peerid"))) {
                                    if (vipObject.getString("ip").equals(rawObject.getString("ip"))) {
                                        if (DIDFunctions.getRole(rawObject.getString("ip")).contains("Verifier"))
                                            rawCount++;
                                    }
                                }
                                loopCount++;
                            }
                        }

                        while (loopCount < (vipArray.length() * rawDetails.length() * contractMembers.length())){}

                        ContactNodesLogger.debug("IP-Peer Mappings Verified");
                        ContactNodesLogger.debug("Raw Count: " + rawCount);
                        ContactNodesLogger.debug("loop Count: " + loopCount);
                        ContactNodesLogger.debug("Raw Details Length: " + rawDetails.length());
                        ContactNodesLogger.debug("Raw Details: " + rawDetails);

                        if ((rawCount/contractMembers.length()) == rawDetails.length()) {
                            ContactNodesLogger.debug("Sending Raw Details: " + rawDetails.toString());
                            cout[j].println(rawDetails.toString());
                            ContactNodesLogger.debug("Waiting for signatures");

                            signatureSync(new JSONObject(cin[j].readLine()), initiatordid, contractMembers);

                            ContactNodesLogger.debug("Signatures Length: " + signatureDetails.length());
                            ContactNodesLogger.debug("Contract Mem length: " + contractMembers.length());
                            ContactNodesLogger.debug("Signatures : " + signatureDetails.toString());

                            while (signatureDetails.length() < contractMembers.length()) {
                            }

                            JSONObject nodeSignObject = new JSONObject();
                            nodeSignObject.put("did", signatureDetails.getJSONObject(j).getString("did"));
                            nodeSignObject.put("data", rawDetails);
                            String hash = calculateHash(nodeSignObject.toString(), "SHA3-256");
                            ContactNodesLogger.debug("hash for verification : " + hash);
                            JSONObject verifyData = new JSONObject();
                            ContactNodesLogger.debug(signatureDetails.getJSONObject(j).getString("did"));
                            verifyData.put("did", signatureDetails.getJSONObject(j).getString("did"));
                            verifyData.put("hash", hash);
                            verifyData.put("signature", signatureDetails.getJSONObject(j).getString("signature"));
                            ContactNodesLogger.debug("Verifying signatures");
                            if (verifySignature(verifyData.toString()))
                                verifiedcount++;
                            else
                                nonverifiedcount++;

                            ContactNodesLogger.debug("Verified Count" + verifiedcount);
                            ContactNodesLogger.debug("Non Verified Count" + nonverifiedcount);

                            while (verifiedcount + nonverifiedcount < contractMembers.length()) {
                            }
                            if (nonverifiedcount == 0) {
                                ContactNodesLogger.debug("Sending ipfs hash of signatures list");
                                cout[j].println(signatureDetails.toString());
                            }

                        }
                        //Sending Invalid Data if all verifiers does not respond with their data
                        else{
                            cout[j].println("IP-PeerID mapping mismatch");
                            ContactNodesLogger.debug("IP-PeerID mapping mismatch");
                            nonverifiedcount++;
                            //  executeIPFSCommands(" ipfs p2p close -t /p2p/" + PeerID.get(j));

                            cout[j].close();
                            cin[j].close();
                            ContactNodesLogger.debug("Connection closed");
                        }
                    }
                    //Sending Invalid Data if all verifiers does not respond with their data
                    else{
                        cout[j].println("Invalid Raw data");
                        ContactNodesLogger.debug("Raw data not received from all verifiers");
                        nonverifiedcount++;
                        //  executeIPFSCommands(" ipfs p2p close -t /p2p/" + PeerID.get(j));

                        cout[j].close();
                        cin[j].close();
                        ContactNodesLogger.debug("Connection closed");
                    }

                } catch (JSONException | IOException e) {
                    nonverifiedcount++;
                    // executeIPFSCommands(" ipfs p2p close -t /p2p/" + PeerID.get(j));
                    ContactNodesLogger.error("IO / JSON Exception", e);
                    cout[j].close();
                    try {
                        cin[j].close();
                    } catch (IOException ioException) {
                        ContactNodesLogger.error("IO Exception", ioException);
                        ioException.printStackTrace();
                    }
                    ContactNodesLogger.debug("Connection closed");
                    e.printStackTrace();
                }
            });

            verifierThreads[j].start();
            ContactNodesLogger.debug("Thread " + j + "Started");
        }

        while (verifiedcount + nonverifiedcount < contractMembers.length()){}

        ContactNodesLogger.debug("Verified Count: "+ verifiedcount);
        ContactNodesLogger.debug("Signature details Count: "+ signatureDetails.length());
        if(verifiedcount == contractMembers.length() && signatureDetails.length() == contractMembers.length()+1) {
            ContactNodesLogger.debug("true");
            writeToFile(DATA_PATH + "allsigndata.json", signatureDetails.toString(), false);
            ContactNodesLogger.debug("All sign data is written into file - allsigndata.json");
            fileHash=add(DATA_PATH + "allsigndata.json", ipfs);
            String dataTableFile = readFile(DATA_PATH + "DataTable.json");
            JSONArray dataTableArray = new JSONArray(dataTableFile);

            JSONArray newDataTableArray = new JSONArray();
            for(int l=0; l<dataTableArray.length(); l++){
                JSONObject dataTableObject = dataTableArray.getJSONObject(l);
                dataTableObject.put("signHash", fileHash);
                newDataTableArray.put(dataTableObject);
            }

            ContactNodesLogger.debug("signHash - dataTable.json");
            writeToFile(DATA_PATH + "DataTable.json", newDataTableArray.toString() , false);
            JSONArray newVipArray = new JSONArray();
            for (int k = 0; k < vipArray.length(); k++) {
                JSONObject object = vipArray.getJSONObject(k);
                object.put("status", "true");
                newVipArray.put(object);
            }
            ContactNodesLogger.debug("vip.json updated");
            writeToFile(DATA_PATH + "vip.json", newVipArray.toString(), false);

            return true;
        }
        else {
            ContactNodesLogger.debug("false");
            return false;
        }
    }
}
