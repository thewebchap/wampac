package com.rubix.WAMPAC.NMS.LogsConsensus;

import com.rubix.AuthenticateNode.Authenticate;
import com.rubix.Resources.Functions;
import com.rubix.Resources.IPFSNetwork;
import com.rubix.WAMPAC.NMS.logAnalysis.Analyze;
import io.ipfs.api.IPFS;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Iterator;


import static com.rubix.Resources.Functions.*;
import static com.rubix.WAMPAC.NMS.Constants.PathConstants.nmsFolder;

public class LogQuorumConsensus implements Runnable {
    public static Logger QuorumConsensusLogger = Logger.getLogger(com.rubix.Consensus.QuorumConsensus.class);
    int port;
    IPFS ipfs;

    public LogQuorumConsensus() {
        this.port = 15070;
        this.ipfs = new IPFS("/ip4/127.0.0.1/tcp/" + Functions.IPFS_PORT);
    }

    public void run() {
        while (true) {
            PropertyConfigurator.configure(Functions.LOGGER_PATH + "log4jWallet.properties");
            String senderDidIpfsHash = "";
            String senderPID = "";
            ServerSocket serverSocket = null;
            Socket socket = null;

            try {
                String peerID = Functions.getPeerID(Functions.DATA_PATH + "DID.json");
                String didHash = Functions.getValues(Functions.DATA_PATH + "DataTable.json", "didHash", "peerid", peerID);
//                String appName = peerID.concat("consensus");
//                IPFSNetwork.listen(appName, this.port);
                QuorumConsensusLogger.debug("Quorum Listening on " + this.port);
                serverSocket = new ServerSocket(this.port);
                socket = serverSocket.accept();
                System.out.println("Accepted");
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintStream out = new PrintStream(socket.getOutputStream());
                String getData = in.readLine();
                QuorumConsensusLogger.debug("Received Details from initiator: " + getData);
                JSONObject readSenderData = new JSONObject(getData);
                String tid = readSenderData.getString("tid");
                readSenderData.remove("tid");
                String senderPrivatePos = readSenderData.getString("sign");
//                senderDidIpfsHash = readSenderData.getString("senderDID");

//                String transactionID = readSenderData.getString("Tid");
                String verifySenderHash = readSenderData.getString("Hash");
                JSONObject temp = new JSONObject(verifySenderHash);
                senderDidIpfsHash = temp.getString("did");
//                String receiverDID = readSenderData.getString("RID");
                senderPID = Functions.getValues(Functions.DATA_PATH + "DataTable.json", "peerid", "didHash", senderDidIpfsHash);
                String senderWidIpfsHash = Functions.getValues(Functions.DATA_PATH + "DataTable.json", "walletHash", "didHash", senderDidIpfsHash);
//                Functions.nodeData(senderDidIpfsHash, senderWidIpfsHash, this.ipfs);
//                String quorumHash = Functions.calculateHash(verifySenderHash.concat(receiverDID), "SHA3-256");
                JSONObject detailsToVerify = new JSONObject();
                detailsToVerify.put("did", senderDidIpfsHash);
//                detailsToVerify.put("hash", verifySenderHash);
                detailsToVerify.put("hash", calculateHash(verifySenderHash, "SHA3-256"));
                detailsToVerify.put("signature", senderPrivatePos);
                if (Authenticate.verifySignature(detailsToVerify.toString())) {
                    QuorumConsensusLogger.debug("Quorum Authenticated Sender");
//                    String QuorumSignature = Functions.getSignFromShares(Functions.DATA_PATH + didHash + "/PrivateShare.png", quorumHash);
                    JSONObject hashToSign = new JSONObject(verifySenderHash);
                    hashToSign.put("did", didHash);
                    verifySenderHash = hashToSign.toString();
                    String QuorumSignature = sign(verifySenderHash);
                    out.println(QuorumSignature);
                    out.println(didHash);

                    String list = in.readLine();
                    if (list.contains("null")) {
                        socket.close();
                        serverSocket.close();
                    } else {
                        JSONArray listOfSign = new JSONArray(list);
                        Boolean flag = true;
                        for (int i = 0; i < listOfSign.length(); i++) {
                            JSONObject record = listOfSign.getJSONObject(i);
                            String hash = record.getString("hash");
                            JSONObject temprecord = new JSONObject(hash);
                            String did = temprecord.getString("did");
                            if (!did.equals(didHash)) {
                                String sign = record.getString("sign");
                                detailsToVerify = new JSONObject();
                                detailsToVerify.put("did", did);
                                detailsToVerify.put("hash", calculateHash(hash, "SHA3-256"));
                                detailsToVerify.put("signature", sign);
                                if (!Authenticate.verifySignature(detailsToVerify.toString())) {
                                    flag = false;
                                    break;
                                }
                            }
                        }
                        if (flag) {
                            out.println("Verification_Success");
                            String info = in.readLine();

                            JSONArray log = new JSONArray(info);
                            File logFile = new File(nmsFolder+senderDidIpfsHash);
                            if(!logFile.exists())
                                logFile.mkdirs();

                            for (int i = 0; i < log.length(); i++) {
                                JSONObject logRecord = log.getJSONObject(i);
                                Iterator<String> keys = logRecord.keys();

                                while(keys.hasNext()) {
                                    String key = keys.next();
                                    writeToFile(logFile+"\\"+key,logRecord.getString(key),false);
                                }
                            }
                            String status;
                            if(Analyze.rawAnalyze(senderDidIpfsHash))
                                status = "restore";
                            else
                                status = "backup";

                            out.println(status);

                            String response = in.readLine();
                            if(response.contains("restore")){

                                File unsafe = new File(nmsFolder+"unsafe.json");
                                JSONArray records;
                                if(!unsafe.exists()) {
                                    records = new JSONArray();
                                    unsafe.createNewFile();
                                }
                                else
                                    records = new JSONArray(readFile(unsafe.getAbsolutePath()));

                                boolean found = false;
                                JSONObject record;
                                for (int i = 0; i < records.length(); i++) {
                                    record = records.getJSONObject(i);
                                    if(record.has(senderDidIpfsHash)){
                                        found = true;
                                        int count = record.getInt(senderDidIpfsHash);
                                        count++;
                                        record.put(senderDidIpfsHash,count);
                                        records.remove(i);
                                        records.put(record);
                                        writeToFile(unsafe.getAbsolutePath(),records.toString(),false);
                                        break;
                                    }
                                }

                                if(!found){
                                    record = new JSONObject();
                                    record.put(senderDidIpfsHash,1);
                                    records.put(record);
                                    writeToFile(unsafe.getAbsolutePath(),records.toString(),false);
                                }

                                JSONObject storeDetailsQuorum = new JSONObject();
                                storeDetailsQuorum.put("tid", tid);
                                storeDetailsQuorum.put("loghash", temp.getString("logHash"));
                                storeDetailsQuorum.put("sign", senderPrivatePos);
                                storeDetailsQuorum.put("status", "restore");
//                                File nms = new File(nmsFolder+"\\"+senderDidIpfsHash);
//                                if (!nms.exists())
//                                    nms.mkdirs();

                                File senderDIDFile = new File(logFile + "\\log.json");
                                JSONArray data;
                                if(!senderDIDFile.exists()) {
                                    data = new JSONArray();
                                    senderDIDFile.createNewFile();
                                }
                                else
                                    data = new JSONArray(readFile(logFile + "\\log.json"));
                                data.put(storeDetailsQuorum);
                                Functions.writeToFile(senderDIDFile.getAbsolutePath(),data.toString(),false);
                            }
                            else{
                                JSONObject share = new JSONObject(response);
//                                int index = details.getInt("shareID");
                                if (!share.equals("null")) {
                                    FileWriter shareWriter = new FileWriter("MyShare.txt", true);
                                    shareWriter.write(share.toString());
                                    shareWriter.close();
                                    File readShare = new File("MyShare.txt");
                                    String shareHash = IPFSNetwork.add(readShare.toString(), this.ipfs);
                                    JSONObject storeDetailsQuorum = new JSONObject();
                                    storeDetailsQuorum.put("tid", tid);
                                    storeDetailsQuorum.put("loghash", temp.getString("logHash"));
                                    storeDetailsQuorum.put("sign", senderPrivatePos);
                                    storeDetailsQuorum.put("share", shareHash);
//                                    storeDetailsQuorum.put("Shareid", index);
                                    storeDetailsQuorum.put("status", status);
//                                    File nms = new File(nmsFolder+"\\"+senderDidIpfsHash);
//                                    if (!nms.exists())
//                                        nms.mkdirs();

                                    File senderDIDFile = new File(logFile + "\\log.json");
                                    JSONArray data;
                                    if(!senderDIDFile.exists()) {
                                        data = new JSONArray();
                                        senderDIDFile.createNewFile();
                                    }
                                    else
                                        data = new JSONArray(readFile(logFile + "\\log.json"));
                                    data.put(storeDetailsQuorum);
                                    QuorumConsensusLogger.debug("Quorum Share: " + share);
                                    Functions.writeToFile(senderDIDFile.getAbsolutePath(),data.toString(),false);
                                    Functions.deleteFile("MyShare.txt");
                            }


                            }
                        } else {
                            QuorumConsensusLogger.debug("Quorum Authentication Failure - Quorum");
                            out.println("Auth_Failed");
                        }
                    }


                } else {
                    QuorumConsensusLogger.debug("Sender Authentication Failure - Quorum");
                    out.println("Auth_Failed");
                }
            } catch (IOException var37) {
//                IPFSNetwork.executeIPFSCommands(" ipfs p2p close -t /p2p/" + senderPID);
                try {
                    socket.close();
                    serverSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                QuorumConsensusLogger.error("IOException Occurred", var37);
                var37.printStackTrace();
            } catch (JSONException var38) {
//                IPFSNetwork.executeIPFSCommands(" ipfs p2p close -t /p2p/" + senderPID);
                try {
                    socket.close();
                    serverSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                QuorumConsensusLogger.error("JSONException Occurred", var38);
                var38.printStackTrace();
            } finally {
                try {
//                    IPFSNetwork.executeIPFSCommands(" ipfs p2p close -t /p2p/" + senderPID);
                    socket.close();
                    serverSocket.close();
                } catch (IOException var36) {
//                    IPFSNetwork.executeIPFSCommands(" ipfs p2p close -t /p2p/" + senderPID);
                    try {
                        socket.close();
                        serverSocket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    QuorumConsensusLogger.error("IOException Occurred", var36);
                    var36.printStackTrace();
                }

            }
        }
    }

//    public static void main(String[] args) {
//        pathSet();
//        LogQuorumConsensus q1 = new LogQuorumConsensus();
//        q1.run();
//
//    }
}


