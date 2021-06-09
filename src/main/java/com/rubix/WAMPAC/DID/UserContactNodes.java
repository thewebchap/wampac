package com.rubix.WAMPAC.DID;

import com.rubix.Resources.IPFSNetwork;
import io.ipfs.api.IPFS;
import org.apache.catalina.User;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

import static com.rubix.Resources.Functions.*;
import static com.rubix.Resources.IPFSNetwork.add;
import static com.rubix.Resources.IPFSNetwork.executeIPFSCommands;
import static com.rubix.WAMPAC.DID.DIDFunctions.minVotes;

public class UserContactNodes {
    public static volatile JSONArray signatureDetails = new JSONArray();
    public static Logger UserContactNodesLogger = Logger.getLogger(UserContactNodes.class);
    private static final Object signLock = new Object();
    public static String fileHash;

    public static synchronized void signatureSync(JSONObject data, JSONArray contractMembers){
        PropertyConfigurator.configure(LOGGER_PATH + "log4jWallet.properties");
        synchronized (signLock) {
            if(signatureDetails.length() < minVotes(contractMembers.length()))
                signatureDetails.put(data);
            UserContactNodesLogger.debug("added to signature details in sync block: \n" + signatureDetails.toString());
        }
    }

    public static boolean contact(JSONArray contractMembers, JSONObject userRawData) throws JSONException, IOException {
        PropertyConfigurator.configure(LOGGER_PATH + "log4jWallet.properties");
        int sizeofverifierpeers = contractMembers.length();
        PrintStream[] cout = new PrintStream[sizeofverifierpeers];
        BufferedReader[] cin = new BufferedReader[sizeofverifierpeers];
        Socket[] senderSocket = new Socket[sizeofverifierpeers];
        IPFS ipfs = new IPFS("/ip4/127.0.0.1/tcp/" + IPFS_PORT);
        String vipFile = readFile(DATA_PATH + "vip.json");
        JSONArray vipArray = new JSONArray(vipFile);
        ArrayList<String> PeerID = new ArrayList<>();
        ArrayList<String> appName = new ArrayList<>();

        signatureDetails = new JSONArray();
        Thread[] verifierThreads = new Thread[contractMembers.length()];
        for (int i = 0; i < sizeofverifierpeers; i++) {
            int j = i;
            verifierThreads[i] = new Thread(() -> {
                try {
                    //initiating threads to rest of the verifiers with appname = peerid+verifierdid
                   // PeerID.add(contractMembers.get(j).toString());
                  //  appName.add(PeerID.get(j).concat("verifieruserdid"));

                  //  IPFSNetwork.swarmConnect(PeerID.get(j), ipfs);
                    //  IPFSNetwork.forward(appName.get(j), SEND_PORT + j, PeerID.get(j));
                    UserContactNodesLogger.debug("Initiating socket communication");
                    senderSocket[j] = new Socket(contractMembers.get(j).toString(), QUORUM_PORT);
                    cin[j] = new BufferedReader(new InputStreamReader(senderSocket[j].getInputStream()));
                    cout[j] = new PrintStream(senderSocket[j].getOutputStream());
                    UserContactNodesLogger.debug("Sending raw data for initializing connection with other verifiers");
                    cout[j].println(userRawData);
                    UserContactNodesLogger.debug("raw data: " + userRawData);
                    //Get Sign from verifiers
                    String response = cin[j].readLine();
                    UserContactNodesLogger.debug("response from verifiers: " + response);
                    JSONObject responseObject = new JSONObject(response);
                    if (!responseObject.getString("status").contains("Invalid"))
                        signatureSync(responseObject, contractMembers);

                    UserContactNodesLogger.debug("Fetched signatures from other verifiers: \n" + signatureDetails.toString());
                    while (signatureDetails.length() < minVotes(contractMembers.length())) {
                    }

                    UserContactNodesLogger.debug("Signatures length" + signatureDetails.length());

                    UserContactNodesLogger.debug("Contract members length" + contractMembers.length());

                    UserContactNodesLogger.debug("Minimum Votes" + minVotes(contractMembers.length()));

                    if(signatureDetails.length() == minVotes(contractMembers.length())) {
                        UserContactNodesLogger.debug("Sending signs to thread: " + j);
                        cout[j].println(signatureDetails.toString());
                    }

                   // executeIPFSCommands(" ipfs p2p close -t /p2p/" + PeerID.get(j));
                    cout[j].close();
                    cin[j].close();
                    UserContactNodesLogger.debug("Connection Closed after success");

                } catch (JSONException | IOException e) {
                   // executeIPFSCommands(" ipfs p2p close -t /p2p/" + PeerID.get(j));
                    UserContactNodesLogger.error("IO / JSON Exception", e);
                    cout[j].close();
                    try {
                        cin[j].close();
                    } catch (IOException ioException) {
                        UserContactNodesLogger.error("IO Exception", ioException);
                        ioException.printStackTrace();
                    }
                    UserContactNodesLogger.debug("Connection Closed");
                    e.printStackTrace();
                }
            });

            verifierThreads[j].start();
            UserContactNodesLogger.debug("Thread " + j + "Started");
        }
        while (signatureDetails.length() < minVotes(contractMembers.length())) {
        }
        UserContactNodesLogger.debug("Minimum votes req: "+ minVotes(contractMembers.length()));
        UserContactNodesLogger.debug("Signature details Count: "+ signatureDetails.length());

        if (signatureDetails.length()== minVotes(contractMembers.length())) {

            File signaturesFile = new File(DATA_PATH + "VerifiedDidSignatures.json");
            if (!signaturesFile.exists())
                signaturesFile.createNewFile();
            UserContactNodesLogger.debug("All sign data is written into file - VerifiedDidSignatures.json");
            writeToFile(signaturesFile.toString(), signatureDetails.toString(), false);
            UserContactNodesLogger.debug("pushing signatures into ipfs");
            fileHash = add(signaturesFile.toString(), ipfs);


            String dataTableFile = readFile(DATA_PATH + "DataTable.json");
            JSONArray dataTableArray = new JSONArray(dataTableFile);
            JSONArray newDataTableArray = new JSONArray();
            for (int l = 0; l < dataTableArray.length(); l++) {
                JSONObject dataTableObject = dataTableArray.getJSONObject(l);
                if (dataTableObject.getString("peerid").equals(userRawData.getString("peerid")))
                    dataTableObject.put("signHash", fileHash);
                newDataTableArray.put(dataTableObject);
            }

            UserContactNodesLogger.debug("signHash of user - dataTable.json");
            writeToFile(DATA_PATH + "DataTable.json", newDataTableArray.toString(), false);

            for (int l = 0; l < vipArray.length(); l++) {
                JSONObject userObject = vipArray.getJSONObject(l);
                if (userObject.getString("peerid").equals(userRawData.getString("peerid"))) {
                    userObject.put("status", "true");
                    vipArray.remove(l);
                    vipArray.put(userObject);
                }
            }
            UserContactNodesLogger.debug("Updating vip.json");
            writeToFile(DATA_PATH + "vip.json", vipArray.toString(), false);

            return true;
        }
        else{
            return false;
        }
    }
}
