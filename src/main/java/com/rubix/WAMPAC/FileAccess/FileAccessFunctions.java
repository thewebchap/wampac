package com.rubix.WAMPAC.FileAccess;

import com.rubix.Resources.Functions;
import com.rubix.WAMPAC.DID.ipClass;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;

import static com.rubix.AuthenticateNode.Authenticate.verifySignature;
import static com.rubix.Resources.Functions.*;

public class FileAccessFunctions {
    public static Logger FileAccessFunctionsLogger = Logger.getLogger(FileAccessFunctions.class);
    public static volatile JSONArray SignatureDetails = new JSONArray();
    public static volatile int noverify,selfverify, valid, error, signatureStatus, verifiedStatus, nonVerifiedStatus = 0;
    private static final Object signLock = new Object();
    public static volatile boolean verstatus, launchStatus= false;
    public static String DRIVE_PATH="";

    public static synchronized void signatureSync(JSONObject data){
        Functions.pathSet();
        PropertyConfigurator.configure(LOGGER_PATH + "log4jWallet.properties");
        synchronized (signLock) {
            SignatureDetails.put(data);
            FileAccessFunctionsLogger.debug("Added signature details in sync block. Length : " +SignatureDetails.length());
        }
    }

    public static boolean verifyWriteCount(String did, JSONArray contractData, String ip) throws JSONException {

        Functions.pathSet();
        PropertyConfigurator.configure(LOGGER_PATH + "log4jWallet.properties");

        FileAccessFunctionsLogger.debug("Verifying write count");
        if(contractData.length() == 0)
            return false;
        int oldval = 0;
        for (int i = 0; i < contractData.length(); i++) {
            String id = contractData.getJSONObject(i).getString("did");
            String ipfetch = contractData.getJSONObject(i).getString("ip");
            if (id.equals(did) && ipfetch.equals(ip)) {
                oldval = contractData.getJSONObject(i).getInt("writeCount");
            }
        }
        FileAccessFunctionsLogger.debug("Current writeCount: " + oldval);
        FileAccessFunctionsLogger.debug("DID: " + did);

        if (oldval > 0)
            return true;
        else
            return false;
    }

    public static void createRbxDrive() {
        pathSet();
        System.out.println(getOsName());
        if (getOsName().contains("Windows"))
            DRIVE_PATH = "C:\\Rubix\\Wallet\\RubixDrive\\";
        else if (getOsName().contains("Mac"))
            DRIVE_PATH = "/Applications/Rubix/Wallet/RubixDrive/";
        else
            DRIVE_PATH = "/home/" + getSystemUser() + "/Rubix/Wallet/RubixDrive/";
        System.out.println("Drive path" + DRIVE_PATH);
        File driveFolder = new File(DRIVE_PATH);
        if (!driveFolder.exists()) {
            driveFolder.mkdirs();
        }
    }

    public static void updateContract(String did, String newFileHash, String token) throws JSONException, IOException, InterruptedException {

        Functions.pathSet();
        createRbxDrive();
        PropertyConfigurator.configure(LOGGER_PATH + "log4jWallet.properties");
        FileAccessFunctionsLogger.debug("Updating contract File values");
        String contractFile = readFile(DRIVE_PATH + "Contracts.json");
        JSONArray contractArray = new JSONArray(contractFile);
        for (int i = 0; i < contractArray.length(); i++) {
            JSONObject innerObject = contractArray.getJSONObject(i);
            if (innerObject.getString("token").equals(token)) {
                JSONArray contractData = innerObject.getJSONArray("contractData");
                for(int j=0; j< contractData.length(); j++) {
                    if (contractData.getJSONObject(j).getString("did").equals(did)) {
                        int oldVal = contractData.getJSONObject(j).getInt("writeCount");
                        contractData.getJSONObject(j).put("writeCount", oldVal - 1);
                        innerObject.put("contractData", contractData);
                        innerObject.put("fileHash", newFileHash);
                        innerObject.put("status", true);
                    }
                }
                contractArray.remove(i);
                contractArray.put(innerObject);
                FileAccessFunctionsLogger.debug("Current contract data" + contractArray.toString());
                writeToFile(DRIVE_PATH + "Contracts.json", contractArray.toString(), false);
            }
        }
    }
    public static boolean signInitiator(String token, JSONObject hashObject, String operation, String filename) throws JSONException, IOException, InterruptedException {
        Functions.pathSet();
        createRbxDrive();
        PropertyConfigurator.configure(LOGGER_PATH + "log4jWallet.properties");

        signatureStatus = 0;
        verifiedStatus = 0;
        valid = 0;
        error = 0;
        selfverify = 0;
        noverify=0;
        SignatureDetails = new JSONArray();
        FileAccessFunctionsLogger.debug("Reset signatureStatus, verifiedStatus and SignatureDetails");
        //set signatureStatus = true
        //IPFS ipfs = new IPFS("/ip4/127.0.0.1/tcp/" + IPFS_PORT);
        //JSONObject- contractdata - get other verifiers except mydid – ContractMem
        JSONObject signObject = new JSONObject();
        signObject.put("did", hashObject.getString("did"));
        signObject.put("data", token + hashObject);
        String sign = Functions.sign(signObject.toString());
        FileAccessFunctionsLogger.debug("Initiator Sign on JSONObject: " +sign);

        String did = hashObject.getString("did");
        ArrayList<String> resultString = new ArrayList<>();
        JSONArray contractData = hashObject.getJSONArray("contractData");
        FileAccessFunctionsLogger.debug("Contract Data in SignInit Start:" + contractData);

//        InetAddress myIP = InetAddress.getLocalHost();
//        String ip = myIP.getHostAddress();
//        FileAccessFunctionsLogger.debug("my IP" + ip);

        String myIp = ipClass.getIP();
        System.out.println("/signInitiator IP Address: " + myIp);

        for (int i = 0; i < contractData.length(); i++) {
            String fetchIp = contractData.getJSONObject(i).getString("ip");
            if (!fetchIp.contains(myIp))
                resultString.add(fetchIp);
        }
        FileAccessFunctionsLogger.debug("Fetched contract Members to contact" + resultString.size());

        PrintStream[] cout = new PrintStream[resultString.size()];
        BufferedReader[] cin = new BufferedReader[resultString.size()];
        Socket[] senderSocket = new Socket[resultString.size()];

        JSONObject dataVerify = new JSONObject();
        dataVerify.put("signature", sign);
        dataVerify.put("data", hashObject);
        signatureSync(dataVerify);
        FileAccessFunctionsLogger.debug("Added Init signature details");
        dataVerify.put("token", token);

        //Forward to ContractMem
        Thread[] verifierThreads = new Thread[resultString.size()];
        for (int i = 0; i < resultString.size(); i++) {
            int j = i;
            verifierThreads[i] = new Thread(() -> {
                try {
//                    DID[j] = resultString.get(j);
//                    appname[j] = DID[j].concat("rec");
//                    PeerID[j] = getValues(DATA_PATH + "DataTable.json", "peerid", "didHash", DID[j]);
//                    IPFSNetwork.swarmConnect(PeerID[j], ipfs);
//                    IPFSNetwork.forward(appname[j], SEND_PORT + j, PeerID[j]);

                    senderSocket[j] = new Socket(resultString.get(j), RECEIVER_PORT);
                    cin[j] = new BufferedReader(new InputStreamReader(senderSocket[j].getInputStream()));
                    cout[j] = new PrintStream(senderSocket[j].getOutputStream());

                    //Sending operation
                    cout[j].println(operation);

                    if(operation.equals("launch")){
                        cout[j].println(filename);
                    }

                    //send Sign(Token+JSONObject+TC), Token,TC,JSONObject
                    cout[j].println(dataVerify);


                    FileAccessFunctionsLogger.debug("Sent Data to other members" + dataVerify);

                    String status = "";
                    while ((status = cin[j].readLine()) == null) {
                    }
                    FileAccessFunctionsLogger.debug("Received Status & signatures from other verifiers" + status);

                    if(status.contains("Error"))
                        error++;
                    else
                        valid++;

                    while(error + valid < resultString.size()){
                    }
                    //if(!error), then verify signatures
                    if (error == 0) {
                        String selfverifystatus = "";
                        while ((selfverifystatus = cin[j].readLine()) == null) {
                        }
                        if (!selfverifystatus.contains("Error"))
                            selfverify++;
                        else
                            noverify++;
                        while (selfverify + noverify < resultString.size()){}
                        if (selfverify == resultString.size()) {
                            String signaturestatus = "";
                            while ((signaturestatus = cin[j].readLine()) == null) {
                            }

                            FileAccessFunctionsLogger.debug("Status: " + status);
                            JSONObject details = new JSONObject(signaturestatus);
                            String verifierSign = details.getString("signature");
                            JSONObject verifierData = details.getJSONObject("data");

                            JSONObject reHashObject = new JSONObject();
                            reHashObject.put("did", verifierData.getString("did"));
                            reHashObject.put("data", token + verifierData);
                            String hash = calculateHash(reHashObject.toString(), "SHA3-256");

                            JSONObject dataToVerify = new JSONObject();
                            dataToVerify.put("did", verifierData.getString("did"));
                            dataToVerify.put("signature", verifierSign);
                            dataToVerify.put("hash", hash);

                            FileAccessFunctionsLogger.debug("Verifying the signatures");
                            if (verifySignature(dataToVerify.toString())) {
                                signatureSync(details);

                                FileAccessFunctionsLogger.debug("Waiting for all Signatures");
                                FileAccessFunctionsLogger.debug(SignatureDetails.length());
                                FileAccessFunctionsLogger.debug(resultString.size());
                                while (SignatureDetails.length() < resultString.size() + 1) {

                                }
                                FileAccessFunctionsLogger.debug("Sending Signatures to all members including initiator");
                                cout[j].println(SignatureDetails.toString());

                                String verificationStatus = "";
                                while ((verificationStatus = cin[j].readLine()) == null) {
                                }

                                FileAccessFunctionsLogger.debug("Verification Status of Signatures" + verificationStatus);
                                if (verificationStatus.equals("Verified"))
                                    verifiedStatus++;
                                else
                                    nonVerifiedStatus++;

                                FileAccessFunctionsLogger.debug("verified Status: " + verifiedStatus);
                                FileAccessFunctionsLogger.debug("contract mem length: " + resultString.size());
                                while (verifiedStatus + nonVerifiedStatus < resultString.size()) {
                                }
                                if (nonVerifiedStatus != 0) {
                                    FileAccessFunctionsLogger.debug("Few contract members verification failed");
                                    launchStatus = false;
                                    cin[j].close();
                                    cout[j].close();
                                    senderSocket[j].close();
                                }
                            } else {
                                FileAccessFunctionsLogger.debug("Few contract members verification failed");
                                nonVerifiedStatus++;
                                cout[j].println("error");
                                launchStatus = false;
                                cin[j].close();
                                cout[j].close();
                                senderSocket[j].close();

                            }
                            // executeIPFSCommands("ipfs p2p close -t /p2p/" + PeerID[j]);
                        } else {
                            FileAccessFunctionsLogger.debug("Error: Init data not Verified");
                            launchStatus = false;
                            nonVerifiedStatus++;
                            noverify++;
                            cin[j].close();
                            cout[j].close();
                            senderSocket[j].close();
                        }
                    } else {
                        FileAccessFunctionsLogger.debug("Error: Data not Verified/invalid operation");
                        launchStatus=false;
                        nonVerifiedStatus++;
                        cin[j].close();
                        cout[j].close();
                        senderSocket[j].close();
                    }
                    //executeIPFSCommands(" ipfs p2p close -t /p2p/" + PeerID[j]);
                    //System.exit(1);
                    cin[j].close();
                    cout[j].close();
                    senderSocket[j].close();
                } catch (JSONException | IOException e) {
                    e.printStackTrace();
                }
            });
            verifierThreads[i].start();
            FileAccessFunctionsLogger.debug("Thread " + i + "Started");
        }

        while (verifiedStatus + nonVerifiedStatus < resultString.size()) {
        }

        if(nonVerifiedStatus == 0) {
            launchStatus = true;
            FileAccessFunctionsLogger.debug("writing Initiator signature, did & fileHash to TokenChain");
            File tokenchainfile = new File(TOKENCHAIN_PATH + token + ".json");
            if (!tokenchainfile.exists()) {
                tokenchainfile.createNewFile();
                writeToFile(TOKENCHAIN_PATH + token + ".json", new JSONArray().toString(), false);
            }

            String tokenchainData = readFile(TOKENCHAIN_PATH + token + ".json");
            JSONArray tokenChainArray = new JSONArray(tokenchainData);
            JSONObject tokenChainSignData = new JSONObject();
            tokenChainSignData.put("did", did);
            tokenChainSignData.put("signature", sign);
            tokenChainSignData.put("fileHash", hashObject.getString("fileHash"));
            tokenChainArray.put(tokenChainSignData);
            writeToFile(TOKENCHAIN_PATH + token + ".json", tokenChainArray.toString(), false);

            if(operation.equals("launch"))
            {
                FileAccessFunctionsLogger.debug("Writing fileHash, contractData and Token to Contracts.json");
                File contract = new File(DRIVE_PATH + "Contracts.json");
                if (!contract.exists()) {
                    contract.createNewFile();
                    writeToFile(DRIVE_PATH + "Contracts.json", new JSONArray().toString(), false);
            }

            String contractFile = readFile(DRIVE_PATH + "Contracts.json");
            JSONArray contractArray = new JSONArray(contractFile);
            JSONObject innerObject = new JSONObject();
            innerObject.put("fileHash", hashObject.getString("fileHash"));
            innerObject.put("fileName", filename);
            innerObject.put("contractData", hashObject.getJSONArray("contractData"));
            innerObject.put("token", token);
            innerObject.put("status", true);
            contractArray.put(innerObject);
            writeToFile(DRIVE_PATH + "Contracts.json", contractArray.toString(), false);
            }
        }
        else
            return launchStatus;
        return launchStatus;
    }

    public static boolean tokenCheck(String token) throws JSONException {
        Functions.pathSet();
        createRbxDrive();
        PropertyConfigurator.configure(LOGGER_PATH + "log4jWallet.properties");
        boolean status = false;
        String contractFile = readFile(DRIVE_PATH + "Contracts.json");
        JSONArray contractDataFile = new JSONArray(contractFile);
        for (int i = 0; i < contractDataFile.length(); i++) {
            JSONObject innerObject = contractDataFile.getJSONObject(i);
            if ((innerObject.getString("token")).equals(token) && innerObject.get("status").equals(true)) {
                    status = true;
                    break;
            }
        }
        return status;
    }

}
