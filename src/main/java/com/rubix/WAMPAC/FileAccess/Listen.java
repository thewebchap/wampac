package com.rubix.WAMPAC.FileAccess;

import com.rubix.Resources.Functions;
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
import static com.rubix.WAMPAC.FileAccess.FileAccessFunctions.*;

public class Listen implements Runnable {
    public static Socket sklaunch;
    public static ServerSocket sslaunch;
    public static PrintStream output;
    public static BufferedReader input;
    public static String status;
    public static Logger LaunchListenLogger = Logger.getLogger(Listen.class);

    @Override
    public void run() {
        while (true) {
            Functions.pathSet();
            createRbxDrive();
            PropertyConfigurator.configure(LOGGER_PATH + "log4jWallet.properties");

            IPFSNetwork.executeIPFSCommands("ipfs daemon");
            IPFS ipfs = new IPFS("/ip4/127.0.0.1/tcp/" + IPFS_PORT);

            String didFile = readFile(DATA_PATH + "DID.json");
            JSONArray didArray = null;
            try {
                didArray = new JSONArray(didFile);
                JSONObject didObject = didArray.getJSONObject(0);
                String myDID = didObject.getString("didHash");

//        JSONObject establishObject = new JSONObject();
//        establishObject.put("did", myDID);
//        establishObject.put("appName", "rec");
//        listenThread(establishObject);
//        String myPID = getValues(DATA_PATH + "DID.json", "peerid", "didHash", myDID);
                //listen
                LaunchListenLogger.debug("Listening for file access operations on " + RECEIVER_PORT);
                sslaunch = new ServerSocket(RECEIVER_PORT);
                sklaunch = sslaunch.accept();
                input = new BufferedReader(new InputStreamReader(sklaunch.getInputStream()));
                output = new PrintStream(sklaunch.getOutputStream());

                //fetch Token, TC, JSONObject, Sign(Token+JSONObject+TC)
                LaunchListenLogger.debug("Waiting for operation from init");
                String op = "";
                while ((op = input.readLine()) == null) {
                }

                String fileName = "";
                if(op.equals("launch")){
                    while ((fileName = input.readLine()) == null) {
                    }
                }
                //fetch Token, TC, JSONObject, Sign(Token+JSONObject+TC)
                LaunchListenLogger.debug("Waiting for data from Initiator");
                String dataCheck = "";
                while ((dataCheck = input.readLine()) == null) {
                }
                LaunchListenLogger.debug("Received data from initiator" + dataCheck);
                JSONObject dataCheckObject = new JSONObject(dataCheck);
                String signature = dataCheckObject.getString("signature");
                JSONObject hashObject = dataCheckObject.getJSONObject("data");
                String token = dataCheckObject.getString("token");
                String fileShared = hashObject.getString("fileHash");
                JSONArray launchContractData = hashObject.getJSONArray("contractData");
                String senderID="", oldHash="";
                File contract = new File(DRIVE_PATH + "Contracts.json");
                if (!contract.exists()) {
                    contract.createNewFile();
                    writeToFile(DRIVE_PATH + "Contracts.json", new JSONArray().toString(), false);
                }
                
                String contractFile = readFile(DRIVE_PATH + "Contracts.json");
                String fileHashFetched = "";
                JSONArray contractDataFetched = new JSONArray();
                JSONArray contractArray = new JSONArray(contractFile);

                if (op.equals("update")) {
                    oldHash = hashObject.getString("oldHash");
                    senderID = hashObject.getString("did");
                    String senderIP = hashObject.getString("ip");
                    LaunchListenLogger.debug("oldhash: " + oldHash);
                    LaunchListenLogger.debug("newhash: " + fileShared);
                    if (tokenCheck(token)) {
                        LaunchListenLogger.debug("fetching contractData, prevFileIpfsHash using token from contracts.json");
                       
                        for (int i = 0; i < contractArray.length(); i++) {
                            JSONObject innerObject = contractArray.getJSONObject(i);
                            if ((innerObject.get("token")).equals(token)) {
                                contractDataFetched = innerObject.getJSONArray("contractData");
                                fileHashFetched = innerObject.getString("fileHash");
                            }
                        }
                        //Check verifyWriteCount(InitDID,  contract) from contractdata
                        LaunchListenLogger.debug("Checking write count for the did, version of the file, contract data");
                        LaunchListenLogger.debug(verifyWriteCount(senderID, contractDataFetched, senderIP));
                        LaunchListenLogger.debug(fileHashFetched.equals(oldHash));
                        String launchDataHash = Functions.calculateHash(launchContractData.toString(),"SHA3-256");
                        String contractDataFetchedHash = Functions.calculateHash(contractDataFetched.toString(), "SHA3-256");

                        if (verifyWriteCount(senderID, contractDataFetched, senderIP) && launchDataHash.equals(contractDataFetchedHash)&& fileHashFetched.equals(oldHash)) {
                            status = "true";
                            output.println("valid");
                            launchContractData = contractDataFetched;
                        } else {
                            status = "false";
                            LaunchListenLogger.debug("No permission to update / Invalid Contract Data / Invalid FileHash");
                            output.println("Error: Invalid Operation");
                            sklaunch.close();
                            sslaunch.close();
                            LaunchListenLogger.debug( "No permission to update");
                        }
                    } else {
                        status = "false";
                        LaunchListenLogger.debug("Invalid token/ file not launched");
                        output.println("Error: Invalid Operation");
                        sklaunch.close();
                        sslaunch.close();
                        LaunchListenLogger.debug("Invalid Token");
                    }
                } else {
                    output.println("valid");
                    status = "true";
                }
            if(status.equals("true")) {
                JSONObject reHashObject = new JSONObject();
                reHashObject.put("did", hashObject.getString("did"));
                reHashObject.put("data", token + hashObject);
                LaunchListenLogger.debug("Calculating hash for Initiator signature verification");
                String hash = Functions.calculateHash(reHashObject.toString(), "SHA3-256");

                JSONObject dataToVerify = new JSONObject();
                dataToVerify.put("did", hashObject.getString("did"));
                dataToVerify.put("signature", signature);
                dataToVerify.put("hash", hash);

                //Verify the signature, if yes, replace InitDID with verifierDID and sign back
                LaunchListenLogger.debug("Verifying Signatures");
                if (verifySignature(dataToVerify.toString())) {
                    output.println("selfverified");
                    LaunchListenLogger.debug("Initiator Signature verified");
                    hashObject.put("did", myDID);

                    JSONObject signObject = new JSONObject();
                    signObject.put("did", myDID);
                    signObject.put("data", token + hashObject);
                    String mySignature = sign(signObject.toString());

                    JSONObject details = new JSONObject();
                    details.put("data", hashObject);
                    details.put("signature", mySignature);
                    //Send Sign or error
                    LaunchListenLogger.debug("Sending my signature");
                    output.println(details.toString());

                    //fetch TC and contracts.json -> update the same
                    String SignatureData = "";
                    while ((SignatureData = input.readLine()) == null) {
                    }
                    if (!SignatureData.contains("error")) {
                        LaunchListenLogger.debug("Received signatures list from initiator" + SignatureData);
                        JSONArray signaturesArray = new JSONArray(SignatureData);
                        int verifiedCount = 0, nonverifiedCount = 0;
                        LaunchListenLogger.debug("Signatures length" + signaturesArray.length());
                        LaunchListenLogger.debug("contractMem length" + launchContractData.length());

                        if (signaturesArray.length() == launchContractData.length()) {

                            LaunchListenLogger.debug("received all signatures and verifying");
                            for (int k = 0; k < signaturesArray.length(); k++) {
                                JSONObject dataObj = signaturesArray.getJSONObject(k);
                                JSONObject dataObject = new JSONObject();
                                dataObject.put("did", dataObj.getJSONObject("data").getString("did"));
                                dataObject.put("data", token + dataObj.getJSONObject("data"));
                                String hashMember = calculateHash(dataObject.toString(), "SHA3-256");

                                JSONObject dataToVerifyMember = new JSONObject();
                                dataToVerifyMember.put("did", dataObj.getJSONObject("data").getString("did"));
                                dataToVerifyMember.put("signature", dataObj.getString("signature"));
                                dataToVerifyMember.put("hash", hashMember);
                                LaunchListenLogger.debug("Verifying signatures");
                                if (verifySignature(dataToVerifyMember.toString()))
                                    verifiedCount++;
                                else
                                    nonverifiedCount++;
                            }
                            LaunchListenLogger.debug("verified count:" + verifiedCount);
                            if (nonverifiedCount == 0) {
                                if (verifiedCount == launchContractData.length()) {
                                    pathSet();
                                    createRbxDrive();
                                    LaunchListenLogger.debug("All signatures verified");
                                    output.println("Verified");

                                    //write into tokenchain
                                    LaunchListenLogger.debug("Writing Initiator signature to tokenchain");
                                    File tokenchainfile = new File(TOKENCHAIN_PATH + token + ".json");
                                    if (!tokenchainfile.exists()) {
                                        tokenchainfile.createNewFile();
                                        writeToFile(TOKENCHAIN_PATH + token + ".json", new JSONArray().toString(), false);
                                    }

                                    String tokenchainData = readFile(tokenchainfile.getPath());
                                    JSONArray tokenChainArray = new JSONArray(tokenchainData);
                                    dataToVerify.remove("hash");
                                    dataToVerify.put("fileHash", hashObject.getString("fileHash"));
                                    tokenChainArray.put(dataToVerify);
                                    writeToFile(TOKENCHAIN_PATH + token + ".json", tokenChainArray.toString(), false);

                                    if (op.equals("update")) {
                                        pathSet();
                                        createRbxDrive();
                                        //update contract file
                                        LaunchListenLogger.debug("Updating contract file");
                                        updateContract(senderID, fileShared, token);

                                        //adding new file to datapath
                                        File oldfile = new File(DRIVE_PATH + oldHash);
                                        if (oldfile.exists())
                                            oldfile.delete();

                                        LaunchListenLogger.debug("Adding file shared to data path");
                                        File newFile = new File(DRIVE_PATH + fileShared);
                                        if (!newFile.exists())
                                            newFile.createNewFile();
                                        String newData = IPFSNetwork.get(fileShared, ipfs);
                                        writeToFile(DRIVE_PATH + fileShared, newData, false);
                                    }
                                    //write into fileTokenList
                                    if (op.equals("launch")) {
                                        pathSet();
                                        createRbxDrive();
                                        LaunchListenLogger.debug("Writing token to fileTokenList.json");
                                        File tokenfileList = new File(DRIVE_PATH + "fileTokenList.json");
                                        if (!tokenfileList.exists()) {
                                            tokenfileList.createNewFile();
                                            writeToFile(DRIVE_PATH + "fileTokenList.json", new JSONArray().toString(), false);
                                        }
                                        String tokenData = readFile(DRIVE_PATH + "fileTokenList.json");
                                        JSONArray tokenArray = new JSONArray(tokenData);
                                        JSONObject tokenObject = new JSONObject();
                                        tokenObject.put("tokenHash", token);
                                        tokenArray.put(tokenObject);
                                        writeToFile(DRIVE_PATH + "fileTokenList.json", tokenArray.toString(), false);

                                        LaunchListenLogger.debug("Updating token");

                                        // write token in tokens path

                                        File tokenFile = new File(TOKENS_PATH + token);
                                        LaunchListenLogger.debug("token path" + tokenFile);
                                        if (!tokenFile.exists()) {
                                            tokenFile.createNewFile();
                                        }

                                        String tokenContent = IPFSNetwork.get(token, ipfs);
                                        LaunchListenLogger.debug("Updating token content: " + tokenContent);
                                        writeToFile(TOKENS_PATH + token, tokenContent, false);

                                        LaunchListenLogger.debug("Updating contracts.json");

                                        String contractFileData = readFile(DRIVE_PATH + "Contracts.json");
                                        JSONArray contractData = new JSONArray(contractFileData);
                                        JSONObject innerObject = new JSONObject();
                                        innerObject.put("fileHash", hashObject.getString("fileHash"));
                                        innerObject.put("contractData", hashObject.getJSONArray("contractData"));
                                        innerObject.put("token", token);
                                        innerObject.put("fileName", fileName);
                                        innerObject.put("status", true);
                                        contractData.put(innerObject);

                                        writeToFile(DRIVE_PATH + "Contracts.json", contractData.toString(), false);

                                        LaunchListenLogger.debug("Adding file shared to drive path");

                                        File newFile = new File(DRIVE_PATH + fileShared);
                                        if (!newFile.exists())
                                            newFile.createNewFile();

                                        String data = IPFSNetwork.get(fileShared, ipfs);
                                        writeToFile(DRIVE_PATH + fileShared, data, false);

                                        LaunchListenLogger.debug("Launch Successful" + token);
                                    }
                                    sklaunch.close();
                                    sslaunch.close();
                                } else {
                                    LaunchListenLogger.debug("All signatures not verified");
                                    output.println("Error: Invalid Signatures (Verification Failed)");
                                    sklaunch.close();
                                    sslaunch.close();
                                    //  executeIPFSCommands("ipfs p2p close -l /p2p/" + myPID);
                                }
                            } else {
                                LaunchListenLogger.debug("All signatures not verified");
                                output.println("Error: Invalid Signatures (Verification Failed)");
                                sklaunch.close();
                                sslaunch.close();
                                //  executeIPFSCommands("ipfs p2p close -l /p2p/" + myPID);
                            }
                        } else {
                            LaunchListenLogger.debug("All signatures(length) not collected");
                            output.println("Error: Invalid Signatures (length)");
                            sklaunch.close();
                            sslaunch.close();
                            //  executeIPFSCommands("ipfs p2p close -l /p2p/" + myPID);
                        }

                    } else {
                        LaunchListenLogger.debug("Initiator did not verify signatures");
                        sklaunch.close();
                        sslaunch.close();
                        // executeIPFSCommands("ipfs p2p close -l /p2p/" + myPID);
                    }
                }else {
                    LaunchListenLogger.debug("Initiator not authenticated");
                    output.println("Error");
                    sklaunch.close();
                    sslaunch.close();
                    // executeIPFSCommands("ipfs p2p close -l /p2p/" + myPID);
                }
            }
            else {
                LaunchListenLogger.debug("Update Checks failed");
                output.println("Error: Invalid Operation");
                sklaunch.close();
                sslaunch.close();
            }
            } catch (JSONException | IOException | InterruptedException e) {
                LaunchListenLogger.error("IO/JSON Exception Occurred", e);
                try {
                    sklaunch.close();
                    sslaunch.close();
                } catch (IOException ioException) {
                    LaunchListenLogger.error("IO Exception Occurred", e);
                    ioException.printStackTrace();
                }
                e.printStackTrace();
            }
        }
    }
}
