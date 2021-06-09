//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.rubix.WAMPAC.NMS.LogsConsensus;


import com.rubix.AuthenticateNode.Authenticate;
import com.rubix.Resources.Functions;
import com.rubix.WAMPAC.NMS.Recovery.RecoveryInit;
import io.ipfs.api.IPFS;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.Socket;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;

import static com.rubix.Resources.Functions.*;
import static com.rubix.WAMPAC.NMS.Constants.PathConstants.nmsFolder;
import static com.rubix.WAMPAC.NMS.Resources.Functions.getVerifierIPList;

public class LogInitConsensus {
    public static Logger LogInitConsensusLogger = Logger.getLogger(LogInitConsensus.class);
    private static Socket[] qSocket;
    private static PrintStream[] qOut;
    private static BufferedReader[] qIn;
    private static String[] qResponse;
    public static volatile JSONObject quorumSignature;
    public static JSONArray allShares;
    private static final Object countLock;
    private static final Object signLock;
    private static String[] quorumID;
    public static ArrayList<String> quorumWithShares;
    public static volatile String status = null;
    public static volatile JSONArray verificationList,ipList;
    public static volatile int quorumResponse;
    public static volatile int count;
    public static volatile int quorumConfirmationResponse;
    public static volatile int quorumPosVerdict,quorumNegVerdict;



    public LogInitConsensus() {
    }

    private static synchronized void voteNCount() {
        PropertyConfigurator.configure(Functions.LOGGER_PATH + "log4jWallet.properties");
        synchronized (countLock) {
            ++quorumResponse;
            if (quorumResponse >= Functions.minQuorum()) {
                LogInitConsensusLogger.debug("Consensus Reached");
            }

        }
    }


    private static synchronized void confirmationCount(int votes) {
        PropertyConfigurator.configure(Functions.LOGGER_PATH + "log4jWallet.properties");
        synchronized (countLock) {
            ++quorumConfirmationResponse;
            if (quorumConfirmationResponse == votes) {
                LogInitConsensusLogger.debug("Confirmation Reached");
            }

        }
    }

    private static synchronized void verdictPosCount() {
        PropertyConfigurator.configure(Functions.LOGGER_PATH + "log4jWallet.properties");
        synchronized (countLock) {
            ++quorumPosVerdict;
            LogInitConsensusLogger.debug("quorumPosVerdict " +quorumPosVerdict);

        }
    }

    private static synchronized void verdictNegCount() {
        PropertyConfigurator.configure(Functions.LOGGER_PATH + "log4jWallet.properties");
        synchronized (countLock) {
            ++quorumNegVerdict;
            LogInitConsensusLogger.debug("quorumNegVerdict " + quorumNegVerdict);

        }
    }


    private static synchronized void quorumSign(String quorumDID, String quorumResponse) {
        PropertyConfigurator.configure(Functions.LOGGER_PATH + "log4jWallet.properties");
        synchronized (signLock) {
            try {
                quorumSignature.put(quorumDID, quorumResponse);
                if (quorumSignature.length() >= Functions.minQuorum()) {
                    LogInitConsensusLogger.debug("Signatures length " + quorumSignature.length());
                }
            } catch (JSONException var5) {
                LogInitConsensusLogger.error("JSON Exception Occurred", var5);
                var5.printStackTrace();
            }

        }
    }

    private static synchronized void addtoSignatureDetails(JSONObject record) {
        PropertyConfigurator.configure(Functions.LOGGER_PATH + "log4jWallet.properties");
        synchronized (countLock) {
            verificationList.put(record);
        }
    }

    private static synchronized void setStatus(String stat) {
        PropertyConfigurator.configure(Functions.LOGGER_PATH + "log4jWallet.properties");
        synchronized (countLock) {
            status = stat;
        }
    }

    private static synchronized JSONObject getShareForIndex(String quorum) throws JSONException {
        PropertyConfigurator.configure(Functions.LOGGER_PATH + "log4jWallet.properties");
        synchronized (countLock) {
            System.out.println("Added to list: "+quorum);
            ipList.put(quorum);
            if (count < minQuorum()) {
                count++;
            }
            System.out.println("count "+(count-1)+" sending to "+quorum);
            return allShares.getJSONObject(count - 1);
        }
    }

    public static void start(String data, IPFS ipfs, int PORT) throws JSONException {
        PropertyConfigurator.configure(Functions.LOGGER_PATH + "log4jWallet.properties");
        JSONObject dataObject = new JSONObject(data);
        String hash = dataObject.getString("Hash");
        JSONArray logfiles = dataObject.getJSONArray("logfiles");
        allShares = dataObject.getJSONArray("shares");
//        String signature = dataObject.getString("sign");
        JSONObject temp = new JSONObject(hash);
        String log = temp.getString("logHash");
        String did = temp.getString("did");
        Timestamp instant= Timestamp.from(Instant.now());
        String tid = calculateHash(log+instant,"SHA-256");

        JSONArray quorumPeersObject = getVerifierIPList();
        quorumResponse = 0;
        quorumConfirmationResponse = 0;
        quorumPosVerdict = 0;
        quorumNegVerdict=0;
        count = 0;
        quorumSignature = new JSONObject();
        verificationList = new JSONArray();
        ipList = new JSONArray();
        try {

            int i;

            for (int j = 0; j < quorumPeersObject.length(); ++j) {
                quorumID[j] = quorumPeersObject.getString(j);
            }

            Thread[] quorumThreads = new Thread[quorumPeersObject.length()];

            for (i = 0; i < quorumPeersObject.length(); ++i) {
                int j = i;
                quorumThreads[i] = new Thread(() -> {
                    String var10000;
                    try {
//                        IPFSNetwork.swarmConnect(quorumID[j], ipfs);
                        String peerID = Functions.getValues(DATA_PATH + "vip.json", "peerid", "ip", quorumID[j]);
                        String quorumDidIpfsHash = Functions.getValues(Functions.DATA_PATH + "DataTable.json", "didHash", "peerid", peerID);
                        String quorumWidIpfsHash = Functions.getValues(Functions.DATA_PATH + "DataTable.json", "walletHash", "peerid", peerID);
                        Functions.nodeData(quorumDidIpfsHash, quorumWidIpfsHash, ipfs);
//                        String appName = quorumID[j].concat("consensus");
//                        IPFSNetwork.forward(appName, PORT + j, quorumID[j]);
                        String var10001 = quorumID[j];
                        LogInitConsensusLogger.debug("Connected to " + var10001);
                        qSocket[j] = new Socket(quorumID[j], 15070);
//                        qSocket[j].setSoTimeout(60*1000);
                        qIn[j] = new BufferedReader(new InputStreamReader(qSocket[j].getInputStream()));
                        qOut[j] = new PrintStream(qSocket[j].getOutputStream());
                        dataObject.remove("shares");
                        dataObject.put("tid",tid);
                        qOut[j].println(dataObject);
                        qResponse[j] = qIn[j].readLine();
                        String quorumDidHash = qIn[j].readLine();

//                        String quorumHash = qIn[j].readLine();
                        //-----------------

                        temp.put("did", quorumDidHash);
                        String quorumHash = temp.toString();
                        var10001 = quorumID[j];
                        LogInitConsensusLogger.debug("Signature Received from " + var10001 + " " + qResponse[j]);
                        if (quorumResponse > Functions.minQuorum()) {
                            qOut[j].println("null");
//                            var10000 = quorumID[j];
//                            IPFSNetwork.executeIPFSCommands("ipfs p2p close -t /p2p/" + var10000);
                            qOut[j].close();
                            qIn[j].close();
                        } else {
                            String verifierPeerid = Functions.getValues(Functions.DATA_PATH + "vip.json", "peerid", "ip", quorumID[j]);
                            String didHash = Functions.getValues(Functions.DATA_PATH + "dataTable.json", "didHash", "peerid", verifierPeerid);
                            JSONObject detailsToVerify = new JSONObject();
                            detailsToVerify.put("did", didHash);
                            detailsToVerify.put("hash", calculateHash(quorumHash, "SHA3-256"));
                            detailsToVerify.put("signature", qResponse[j]);
                            if (Authenticate.verifySignature(detailsToVerify.toString())) {
                                voteNCount();
                                if (quorumResponse > Functions.minQuorum()) {
                                    qOut[j].println("null");
//                                        var10000 = quorumID[j];
//                                        IPFSNetwork.executeIPFSCommands("ipfs p2p close -t /p2p/" + var10000);
                                    qOut[j].close();
                                    qIn[j].close();

                                } else {

                                    JSONObject record = new JSONObject();
                                    record.put("hash", quorumHash);
                                    record.put("sign", qResponse[j]);
                                    addtoSignatureDetails(record);

                                    while (verificationList.length() < Functions.minQuorum()) {
                                        Thread.onSpinWait();
                                    }

                                    qOut[j].println(verificationList);
                                    if (qIn[j].readLine().equals("Verification_Success"))
                                        confirmationCount(verificationList.length());


                                    while (quorumConfirmationResponse < Functions.minQuorum()) {
                                        Thread.onSpinWait();
                                    }

                                    quorumSign(didHash, qResponse[j]);


                                    qOut[j].println(logfiles);
                                    String responseString = qIn[j].readLine();
                                    LogInitConsensusLogger.debug("Received "+responseString+" from quorum "+quorumID[j]);

                                    if (responseString.contains("restore"))
                                        verdictPosCount();
                                    else
                                        verdictNegCount();

                                    while (quorumPosVerdict+quorumNegVerdict < Functions.minQuorum()) {
                                        Thread.onSpinWait();
                                    }

                                    if(quorumPosVerdict>((Functions.minQuorum())/2)){
                                        qOut[j].println("restore");
                                        setStatus("restore");
                                        qOut[j].close();
                                        qIn[j].close();
                                    }
                                    else{
                                        qOut[j].println(getShareForIndex(quorumID[j]));
                                        setStatus("backup");
                                        qOut[j].close();
                                        qIn[j].close();
                                    }

//------------------------------------------------------------------------------------------------------------
//                                    quorumWithShares.add(quorumPeersObject.getString(j));
//                                    int index = getShareIndex();
//                                    JSONArray shareToQuorum = allShares.getJSONArray("q" + index);
//                                    JSONObject sendToQuorum = new JSONObject();
//                                    sendToQuorum.put("shareID", index);
//                                    sendToQuorum.put("share", shareToQuorum);
//                                    qOut[j].println(sendToQuorum);
////                                    var10000 = quorumID[j];
////                                    IPFSNetwork.executeIPFSCommands("ipfs p2p close -t /p2p/" +
//                                    qOut[j].close();
//                                    qIn[j].close();
//----------------------------------------------------------------------------------------------------------
                                }

                                int var14 = quorumResponse;
                                LogInitConsensusLogger.debug("Quorum Count : " + var14 + "Signature count : " + quorumSignature.length());
                            }
                        }
                    } catch (JSONException | IOException var13) {
                        var10000 = quorumID[j];
//                        IPFSNetwork.executeIPFSCommands("ipfs p2p close -t /p2p/" + var10000);
                        try {
                            qOut[j].close();
                            qIn[j].close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        LogInitConsensusLogger.error("IOException Occurred", var13);
                        var13.printStackTrace();
                    }

                });
                quorumThreads[i].start();
            }


            do {
                while (quorumResponse < Functions.minQuorum()) {
                }
            } while (status==null);

            if(status.equals("backup"))
                while (ipList.length()<Functions.minQuorum()) {}

            LogInitConsensusLogger.debug("status is "+status);
            if(status.equals("restore"))
                RecoveryInit.Recover(did);

            File logFile = new File(nmsFolder + "log.json");

            JSONArray array;
            if (!logFile.exists()) {
                array = new JSONArray();
                logFile.createNewFile();
            }
            else
                array = new JSONArray(readFile( nmsFolder+ "log.json"));
            for (int k = 0; k < verificationList.length(); k++) {
                JSONObject record = verificationList.getJSONObject(k);
                record.put("tid",tid);
                record.put("timestamp",instant);
                record.put("status",status);
                record.put("quorumiplist",ipList);
                array.put(verificationList.getJSONObject(k));

            }
            writeToFile(nmsFolder + "log.json", array.toString(), false);

        } catch (JSONException var14) {
            LogInitConsensusLogger.error("JSON Exception Occurred", var14);
            var14.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    static {
        qSocket = new Socket[Functions.QUORUM_COUNT];
        qOut = new PrintStream[Functions.QUORUM_COUNT];
        qIn = new BufferedReader[Functions.QUORUM_COUNT];
        qResponse = new String[Functions.QUORUM_COUNT];
        quorumSignature = new JSONObject();
        countLock = new Object();
        signLock = new Object();
        quorumID = new String[Functions.QUORUM_COUNT];
        quorumWithShares = new ArrayList();
        quorumResponse = 0;
    }
}
