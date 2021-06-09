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

import static com.rubix.AuthenticateNode.Authenticate.verifySignature;
import static com.rubix.Resources.Functions.*;
import static com.rubix.Resources.IPFSNetwork.*;

public class VerifierListen implements Runnable {


    public static Logger VerifierLogger = Logger.getLogger(VerifierListen.class);

    /**
     * This method is used to run a thread for Quorum Members
     * <p>This involves <ol> <li>Verify sender signature</li>
     * <li>Signing the transaction</li>
     * <li>Receiving share from sender</li></ol>
     */
    public static IPFS ipfs = new IPFS("/ip4/127.0.0.1/tcp/" + IPFS_PORT);

    @Override
    public void run() {
        while (true) {
            PropertyConfigurator.configure(LOGGER_PATH + "log4jWallet.properties");
            String peerID, appName, senderPID = "";
            ServerSocket serverSocket = null;
            Socket socket = null;
            try {

                //listening on QUORUM_PORT & APPNAME = Peerid+verifierdid


                //  peerID = getPeerID(DATA_PATH + "DID.json");
                //   appName = peerID.concat("verifierdid");

                //  IPFSNetwork.listen(appName, QUORUM_PORT);


                VerifierLogger.debug("Verifier Listening on " + SEND_PORT);
                serverSocket = new ServerSocket(SEND_PORT);
                socket = serverSocket.accept();

                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintStream out = new PrintStream(socket.getOutputStream());

                //Sending data = did, peerid, wid, ip : jsonobject

                String didFile = readFile(DATA_PATH + "DID.json");
//                InetAddress myIP = InetAddress.getLocalHost();
//                String ip = myIP.getHostAddress();
//                VerifierLogger.debug("Verifier IP  " + ip);

                String ip = ipClass.getIP();
                System.out.println("Verifier IP: " + ip);

                JSONArray didArray = new JSONArray(didFile);
                JSONObject didObject = didArray.getJSONObject(0);
                didObject.put("ip", ip);


                String incomingCode;
                VerifierLogger.debug("Verifier waiting for initiate request from verifier 1");
                while ((incomingCode = in.readLine()) == null) {
                }
                VerifierLogger.debug("Incoming code" + incomingCode);
                if (incomingCode.equals("REQ")) {

                    out.println(didObject);
                    VerifierLogger.debug("sent object for IP an PeerID verification" + didObject);
                    VerifierLogger.debug("Waiting for IP PeerID collective list");
                    String getData;
                    while ((getData = in.readLine()) == null) {
                    }
                    VerifierLogger.debug("data received : " + getData);
                    //received data(IP, Peerid) and checking whether it is valid or invalid
                    //Invalid - when 7 members data is not obtained
                    if (!getData.contains("IP-PeerID mapping mismatch")) {
                        VerifierLogger.debug("Received Details from Verifier 1: " + getData);
                        int rawCount = 0;
                        JSONArray rawDetails = new JSONArray(getData);

                        //read vip.json and check the IP PeerID mapping from the data received
                        String vipFile = readFile(DATA_PATH + "vip.json");
                        JSONArray vipArray = new JSONArray(vipFile);
                        for (int k = 0; k < rawDetails.length(); k++) {
                            JSONObject rawObject = rawDetails.getJSONObject(k);
                            for (int l = 0; l < vipArray.length(); l++) {
                                JSONObject vipObject = vipArray.getJSONObject(l);
                                if (vipObject.getString("peerid").equals(rawObject.getString("peerid")))
                                    if (vipObject.getString("ip").equals(rawObject.getString("ip")))
                                        if (DIDFunctions.getRole(rawObject.getString("ip")).contains("Verifier"))
                                            rawCount++;
                            }
                        }

                        VerifierLogger.debug("Mapping verified");
                        // If mapping is correct, send the signature on the IP-PeerID mapping list
                        JSONObject signObject = new JSONObject();
                        signObject.put("did", didObject.getString("didHash"));
                        signObject.put("data", rawDetails);
                        String mySign = sign(signObject.toString());
                        VerifierLogger.debug("Raw count: " + rawCount);
                        VerifierLogger.debug("Raw Details Length: " + rawDetails.length());
                        VerifierLogger.debug("Raw Details : " + rawDetails);

                        if (rawCount == rawDetails.length()) {
                            JSONObject responseObject = new JSONObject();
                            responseObject.put("did", didObject.getString("didHash"));
                            responseObject.put("signature", mySign);
                            responseObject.put("status", "Verifiers Matched");
                            VerifierLogger.debug("Sending Signature" + responseObject.toString());
                            out.println(responseObject.toString());

                            VerifierLogger.debug("Waiting for IPFS Hash of Signatures List");
                            String signDetails;
                            while ((signDetails = in.readLine()) == null) {
                            }
                            //fetch the hash from IPFS
                            VerifierLogger.debug("Signature Details" + signDetails);
//                            String getHash = get(ipfsSignHash, ipfs);
                            JSONArray signatureArray = new JSONArray(signDetails);
                            int verifyCount = 0;

                            VerifierLogger.debug("verifying all the received signatures");
                            //verify all the received signatures
                            for (int i = 0; i < signatureArray.length(); i++) {
                                JSONObject nodeSignObject = new JSONObject();
                                nodeSignObject.put("did", signatureArray.getJSONObject(i).getString("did"));
                                nodeSignObject.put("data", rawDetails);
                                String hash = calculateHash(nodeSignObject.toString(), "SHA3-256");
                                VerifierLogger.debug("hash for verification : " + hash);
                                JSONObject verifyData = new JSONObject();
                                verifyData.put("did", signatureArray.getJSONObject(i).getString("did"));
                                verifyData.put("hash", hash);
                                verifyData.put("signature", signatureArray.getJSONObject(i).getString("signature"));
                                if (verifySignature(verifyData.toString())) {
                                    verifyCount++;
                                }
                            }
                            //send the number of signatures verified to verifier 1
//                        JSONObject signatureResponseObject = new JSONObject();
//                        signatureResponseObject.put("count", verifyCount);
//                        out.println(signatureResponseObject.toString());

                            //if count = 7, then write into table and vip.json
                            VerifierLogger.debug("Verification count : " + verifyCount);
                            if (verifyCount == signatureArray.length()) {
                                writeToFile(DATA_PATH + "allsigndata.json", signatureArray.toString(), false);
                                VerifierLogger.debug("All sign data is written into file - allsigndata.json");
                                String fileHash=add(DATA_PATH + "allsigndata.json", ipfs);

                                String dataTableFile = readFile(DATA_PATH + "DataTable.json");
                                JSONArray dataTableArray = new JSONArray(dataTableFile);
                                JSONArray newDataTableArray = new JSONArray();
                                for (int l = 0; l < dataTableArray.length(); l++) {
                                    JSONObject dataTableObject = dataTableArray.getJSONObject(l);
                                    dataTableObject.put("signHash", fileHash);
                                    newDataTableArray.put(dataTableObject);
                                }

                                VerifierLogger.debug("signHash - dataTable.json");
                                writeToFile(DATA_PATH + "DataTable.json", newDataTableArray.toString(), false);

                                JSONArray newVipArray = new JSONArray();
                                for (int k = 0; k < vipArray.length(); k++) {
                                    JSONObject object = vipArray.getJSONObject(k);
                                    object.put("status", "true");
                                    newVipArray.put(object);
                                }
                                VerifierLogger.debug("Status - vip.json");
                                writeToFile(DATA_PATH + "vip.json", newVipArray.toString(), false);
                            }
                            //count not equal to 7 - exit
                            else {
                                VerifierLogger.debug("Invalid Signatures");
                                // executeIPFSCommands(" ipfs p2p close -t /p2p/" + senderPID);

                                socket.close();
                                serverSocket.close();
                                VerifierLogger.debug("Connection closed");
                                VerifierLogger.debug("Invalid Signatures");
                            }
                        }
                        //if IP-PeerID mapping fails, send empty signatures
                        else {
                            JSONObject responseObject = new JSONObject();
                            responseObject.put("did", didObject.getString("did"));
                            responseObject.put("signature", "");
                            responseObject.put("status", "Verifiers doesnot Match");
                            out.println(responseObject);
                            VerifierLogger.debug("IP-PeerID mapping mismatch");
                            // executeIPFSCommands(" ipfs p2p close -t /p2p/" + senderPID);

                            socket.close();
                            serverSocket.close();
                            VerifierLogger.debug("Connection closed");
                            VerifierLogger.debug("Verifiers does not Match");
                        }
                    }
                    //when 7 members data is not sent to verifier 1
                    else {
                        VerifierLogger.debug("Invalid Raw Data");
                        //  executeIPFSCommands(" ipfs p2p close -t /p2p/" + senderPID);
                        socket.close();
                        serverSocket.close();
                        VerifierLogger.debug("Connection closed");

                        VerifierLogger.debug("Invalid Raw Data");
                    }
                    out.close();
                    in.close();
                    socket.close();
                    serverSocket.close();
                }
                //if REQ is not sent by Verifier 1
                else {
                    VerifierLogger.debug("REQ not sent by Verifier");
                    //executeIPFSCommands(" ipfs p2p close -t /p2p/" + senderPID);

                    out.close();
                    in.close();
                    socket.close();
                    serverSocket.close();
                    VerifierLogger.debug("Connection closed");
                    VerifierLogger.debug("Invalid Request");
                }

            } catch (IOException e) {
                //executeIPFSCommands(" ipfs p2p close -t /p2p/" + senderPID);
                VerifierLogger.debug("Connection closed");
                VerifierLogger.error("IOException Occurred", e);
                e.printStackTrace();
            } catch (JSONException e) {
                //executeIPFSCommands(" ipfs p2p close -t /p2p/" + senderPID);
                VerifierLogger.debug("Connection closed");
                VerifierLogger.error("JSONException Occurred", e);
                e.printStackTrace();
            } finally {
                try {
                    //  executeIPFSCommands(" ipfs p2p close -t /p2p/" + senderPID);

                    socket.close();
                    serverSocket.close();
                    VerifierLogger.debug("Connection closed");
                } catch (IOException e) {
                    //  executeIPFSCommands(" ipfs p2p close -t /p2p/" + senderPID);

                    VerifierLogger.debug("Connection closed");
                    VerifierLogger.error("IOException Occurred", e);
                    e.printStackTrace();
                }
            }
            VerifierLogger.debug("Verifiers Agreed");
        }
    }
}
