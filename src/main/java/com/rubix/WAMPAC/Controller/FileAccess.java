package com.rubix.WAMPAC.Controller;


import com.rubix.Resources.Functions;
import com.rubix.Resources.IPFSNetwork;
import com.rubix.WAMPAC.DID.ipClass;
import com.rubix.WAMPAC.FileAccess.Listen;
import io.ipfs.api.IPFS;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.simple.parser.ParseException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import static com.rubix.Resources.Functions.*;
import static com.rubix.WAMPAC.FileAccess.FileAccessFunctions.*;
import static com.rubix.WAMPAC.FileAccess.UIFunctions.*;

@CrossOrigin(origins = "http://localhost:1898")
@RestController
public class FileAccess {
    public static boolean fileListen = false;

    @PostMapping(value = "/createContract", produces = {"application/json", "application/xml"})
    public static String createContract(@RequestParam("num") int num, @RequestParam("did") String did, @RequestParam("ip") String ip, @RequestParam("writeCount") int writeCount) throws JSONException, IOException, InterruptedException {
        System.out.println("Called /createContract");
        pathSet();
        createRbxDrive();
        JSONObject result = new JSONObject();
        JSONObject contentObject = new JSONObject();

        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("did", did);
        jsonObject.put("ip", ip);
        jsonObject.put("writeCount", writeCount);
        jsonArray.put(jsonObject);
        File file = new File(DRIVE_PATH + "contractData"+num+".json");
        if(!file.exists()) {
            file.createNewFile();
            writeToFile(file.toString(), jsonArray.toString(), false);
        } else {
            String fileData = readFile(file.toString());
            JSONArray jsonArray1 = new JSONArray(fileData);
            jsonArray1.put(jsonObject);
            writeToFile(file.toString(), jsonArray1.toString(), false);
        }
        contentObject.put("response", "Contract Data Updated");

        result.put("data", contentObject);
        result.put("message", "");
        result.put("status", "true");
        return result.toString();
    }

    @PostMapping(value = "/launch", produces = {"application/json", "application/xml"})
    public static String getLaunch(@RequestParam("file") MultipartFile file, @RequestParam("contractData") JSONArray contractData) throws JSONException, IOException, NoSuchAlgorithmException, ParseException, InterruptedException {
        System.out.println("Called /launch");
        System.out.println("Data contract data: " + contractData);
        Functions.pathSet();
        createRbxDrive();
        IPFSNetwork.executeIPFSCommands("ipfs daemon");

        JSONObject result = new JSONObject();
        JSONObject contentObject = new JSONObject();

        String didFile = readFile(DATA_PATH + "DID.json");
        JSONArray didArray = new JSONArray(didFile);
        JSONObject didObject = didArray.getJSONObject(0);
        String myDID = didObject.getString("didHash");
        System.out.println("DID: " + myDID);

        boolean didInList = false;
        for(int i = 0; i < contractData.length(); i++){
            if(contractData.getJSONObject(i).getString("did").equals(myDID))
                didInList = true;
        }

        if(didInList) {
            System.out.println("push file into IPFS – fileHash");
            IPFS ipfs = new IPFS("/ip4/127.0.0.1/tcp/" + IPFS_PORT);
            byte[] bytes = file.getBytes();
            Path path = Paths.get(DRIVE_PATH + file.getOriginalFilename());
            Files.write(path, bytes);
            String fileHash = IPFSNetwork.add(path.toString(), ipfs);
            System.out.println("IPFS FileHash : " + fileHash);
//
//        byte[] contractBytes = contractDataFile.getBytes();
//        Path contractPath = Paths.get(DRIVE_PATH + contractDataFile.getOriginalFilename());
//        Files.write(contractPath, contractBytes);
//
//        String contractDataString = readFile(DRIVE_PATH + contractDataFile.getOriginalFilename());
            //JSONArray contractData = new JSONArray(contractDataString);
            JSONObject hashObject = new JSONObject();
            String token = "";

            //JSONObject - fileHash, bytecodehash, contractdata, mydid
            hashObject.put("did", myDID);
            hashObject.put("fileHash", fileHash);
            hashObject.put("contractData", contractData);

            //Assign a token
            System.out.println("Assigning New Token to the file");
            File tokenFile = new File(WALLET_DATA_PATH + "tokenList.json");
            if(tokenFile.exists()) {

                String tokenList = readFile(WALLET_DATA_PATH + "tokenList.json");

                JSONArray tokensArray = new JSONArray(tokenList);
                if (!(tokensArray.length() == 0))
                    token = tokensArray.getJSONObject(0).getString("tokenHash");
                System.out.println("Assigned Token: " + token);

                System.out.println("adding token & tokenchain into IPFS");

                File tokenSingleFile = new File(TOKENS_PATH + token);
                if(tokenSingleFile.exists()) {
                    IPFSNetwork.add(TOKENS_PATH + token, ipfs);
                    IPFSNetwork.add(TOKENCHAIN_PATH + token + ".json", ipfs);

                    System.out.println("Calling Sign Init function for Signatures");
                    System.out.println("Data Passed as arguments: token:" + token + " " + "hashObject: " + hashObject);
                    if (signInitiator(token, hashObject, "launch", file.getOriginalFilename())) {
                        System.out.println("Signatures Received and Token Assigned");

                        //remove token from tokenList
                        tokensArray.remove(0);
                        System.out.println("Token Removed from tokenList.json" + token);
                        writeToFile(WALLET_DATA_PATH + "tokenList.json", tokensArray.toString(), false);

                        //write into fileTokenList
                        File tokenfile = new File(DRIVE_PATH + "fileTokenList.json");
                        if (!tokenfile.exists()) {
                            tokenfile.createNewFile();
                            writeToFile(tokenfile.getPath(), new JSONArray().toString(), false);
                        }

                        String newTokenData = readFile(tokenfile.getPath());
                        JSONArray newTokenArray = new JSONArray(newTokenData);
                        JSONObject newTokenObject = new JSONObject();
                        newTokenObject.put("tokenHash", token);
                        newTokenArray.put(newTokenObject);
                        writeToFile(tokenfile.getPath(), newTokenArray.toString(), false);

                        System.out.println("Token Added to fileTokenList.json");
                        contentObject.put("response", "Launch successful, token : " + token);
                        System.out.println("Launch successful");
                    } else {
                        contentObject.put("response", "Launch Failed");
                        System.out.println("Launch Failed");
                    }
                }else{
                    contentObject.put("response", "No tokens available");
                }
            }else{
                contentObject.put("response", "No tokens available");
            }
        }else{
            contentObject.put("response", "Select your DID for the contract");
        }
        result.put("data", contentObject);
        result.put("message", "");
        result.put("status", "true");
        return result.toString();
    }

    @GetMapping(value = "/listen", produces = {"application/json", "application/xml"})
    public static String listen() throws JSONException, IOException {

        Functions.pathSet();
        createRbxDrive();
        if(!fileListen){
            Listen listen = new Listen();
            Thread thread = new Thread(listen);
            thread.start();
            fileListen = true;
        }
        JSONObject result = new JSONObject();
        JSONObject contentObject = new JSONObject();
        contentObject.put("response", "Listening for Launch / update");
        result.put("data", contentObject);
        result.put("message", "");
        result.put("status", "true");
        return result.toString();
    }

    @PostMapping(value = "/update", produces = {"application/json", "application/xml"})
    public static String getUpdate(@RequestParam("newfile") MultipartFile newFile, @RequestParam("token") String token) throws JSONException, IOException, InterruptedException {
        System.out.println("Called /update");
        System.out.println("Data token: " + token);

        Functions.pathSet();
        createRbxDrive();
        IPFS ipfs = new IPFS("/ip4/127.0.0.1/tcp/" + IPFS_PORT);
        JSONObject result = new JSONObject();
        JSONObject contentObject = new JSONObject();
        System.out.println("Adding new file into IPFS");
        //Push newFile
        byte[] newbytes = newFile.getBytes();
        Path newpath = Paths.get(DRIVE_PATH + newFile.getOriginalFilename());
        Files.write(newpath, newbytes);
        String newfileHash = IPFSNetwork.add(newpath.toString(), ipfs);

        System.out.println("Checking token validity and launch status");
        if (tokenCheck(token)) {
            System.out.println("Fetching the contract details, prev version of the file using token");
            //fetch oldfile, tokenChain, contractData corresponding to token from contracts.json
            String contractFile = readFile(DRIVE_PATH + "Contracts.json");
            JSONArray contractDataFile = new JSONArray(contractFile);
            String oldfileHash = "";
            JSONArray contractData = new JSONArray();
            for (int i = 0; i < contractDataFile.length(); i++) {
                JSONObject innerObject = contractDataFile.getJSONObject(i);
                if ((innerObject.getString("token")).equals(token)) {
                    oldfileHash = innerObject.getString("fileHash");
                    contractData = innerObject.getJSONArray("contractData");
                }
            }
            System.out.println("prev file hash : " + oldfileHash + " " + "contractdata: " + contractData);
            //JSONObject - InitDID, newfileipfshash, prevFilehash
            String didFile = readFile(DATA_PATH + "DID.json");
            JSONArray didArray = new JSONArray(didFile);
            JSONObject didObject = didArray.getJSONObject(0);
            String myDID = didObject.getString("didHash");

            JSONObject details = new JSONObject();
            details.put("did", myDID);
            details.put("oldHash", oldfileHash);
            details.put("fileHash", newfileHash);
            details.put("contractData", contractData);
//            InetAddress myIP = InetAddress.getLocalHost();
//            String ip = myIP.getHostAddress();
//            System.out.println("IP Check" + ip);

            String myIp = ipClass.getIP();
            System.out.println("/update IP Address: " + myIp);
            details.put("ip",myIp);

            //Check verifyWriteCount(InitDID) from contractdata
            System.out.println("Verifying write count");
            if (verifyWriteCount(myDID, contractData, myIp)) {
                System.out.println("Access Permissions granted");
                System.out.println("calling sign init for signature creation");
                System.out.println("Arguments passed: " + "token: " + token + " " + "details for sign: " + details);
                //SignInitiator(Token, JSONObject, TC)
                if (signInitiator(token, details, "update", "")) {
                    System.out.println("Signatures obtained");

                    //adding new file to datapath
                    File oldfile = new File(DRIVE_PATH + oldfileHash);
                    if (oldfile.exists())
                        oldfile.delete();

                    System.out.println("Adding file shared to data path");
                    String newData = IPFSNetwork.get(newfileHash, ipfs);
                    writeToFile(DRIVE_PATH + newfileHash, newData, false);

                    System.out.println("Updating contract with new data");
                    updateContract(myDID, newfileHash, token);
                    System.out.println("Update Done");
                    contentObject.put("response", "update successful");
                } else {
                    System.out.println("Invalid Signatures");
                    contentObject.put("response", "Invalid Signatures");
                }
            } else {
                System.out.println("No permission to update");
                contentObject.put("response", "No permission to update");
            }
        } else {
            System.out.println("Invalid token / file not launched");
            contentObject.put("response", "Invalid token / file not launched");
        }
        result.put("data", contentObject);
        result.put("message", "");
        result.put("status", "true");
        return result.toString();
    }

    //======================================================================================================//
    @GetMapping(value = "/getSharedFilesCount", produces = {"application/json", "application/xml"})
    public static String getSharedFilesCount() throws JSONException {
        System.out.println("Called /getSharedFilesCount");
        Functions.pathSet();
        createRbxDrive();
        JSONObject result = new JSONObject();
        JSONObject contentObject = new JSONObject();
        int fileCount = sharedFilesCount();
        if(fileCount > 0)
            contentObject.put("response", fileCount);
        else
            contentObject.put("response", 0);
        result.put("data", contentObject);
        result.put("message", "");
        result.put("status", "true");
        return result.toString();
    }

    @GetMapping(value = "/getSharedFiles", produces = {"application/json", "application/xml"})
    public static String getSharedFiles() throws JSONException, IOException {
        System.out.println("Called /getSharedFiles");
        Functions.pathSet();
        createRbxDrive();
        JSONObject result = new JSONObject();
        JSONObject contentObject = new JSONObject();
        ArrayList<String> filesList = sharedFilesList();
        contentObject.put("response", filesList);
        result.put("data", contentObject);
        result.put("message", "");
        result.put("status", "true");
        return result.toString();
    }

    @PostMapping(value = "/getSharedFileDetails", produces = {"application/json", "application/xml"})
    public static String getSharedFileDetails(@RequestParam("fileName") String fileName) throws JSONException {
        System.out.println("Called /getSharedFileDetails");
        Functions.pathSet();
        createRbxDrive();
        JSONObject result = new JSONObject();
        JSONObject contentObject = new JSONObject();
        JSONObject data = sharedFileDetails(fileName);
        contentObject.put("response", data);
        result.put("data", contentObject);
        result.put("message", "");
        result.put("status", "true");
        return result.toString();
    }

    @PostMapping(value = "/getSharedFileMetaData", produces = {"application/json", "application/xml"})
    public static String getSharedFileMetaData(@RequestParam("fileName") String fileName) throws JSONException {
        System.out.println("Called /getSharedFileMetaData");
        Functions.pathSet();
        createRbxDrive();
        JSONObject result = new JSONObject();
        JSONObject contentObject = new JSONObject();
        JSONObject metaData = sharedFileMetaData(fileName);
        contentObject.put("response", metaData);
        result.put("data", contentObject);
        result.put("message", "");
        result.put("status", "true");
        return result.toString();
    }

    @GetMapping(value = "/getDidList", produces = {"application/json", "application/xml"})
    public static String getDidList() throws JSONException {
        System.out.println("Called /getDidList");
        Functions.pathSet();
        createRbxDrive();
        JSONObject result = new JSONObject();
        JSONObject contentObject = new JSONObject();

        String didFile = readFile(DATA_PATH + "DID.json");
        JSONArray myDidArray = new JSONArray(didFile);
        String myDid = myDidArray.getJSONObject(0).getString("didHash");

        String dataTableFile = readFile(DATA_PATH + "Datatable.json");
        JSONArray didArray = new JSONArray(dataTableFile);
        JSONArray didResultArray = new JSONArray();
        didResultArray.put(myDid);
        for(int i = 0; i < didArray.length(); i++){
            if(!didArray.getJSONObject(i).getString("didHash").equals(myDid))
                didResultArray.put(didArray.getJSONObject(i).getString("didHash"));
        }
        contentObject.put("response", didResultArray);
        result.put("data", contentObject);
        result.put("message", "");
        result.put("status", "true");
        return result.toString();
    }

    @PostMapping(value = "/getIpFromDid", produces = {"application/json", "application/xml"})
    public static String getIpFromDid(@RequestParam("did") String did) throws JSONException {
        System.out.println("Called /getIpFromDid");
        Functions.pathSet();
        createRbxDrive();
        JSONObject result = new JSONObject();
        JSONObject contentObject = new JSONObject();

        String ip = "", peerid = "";

        String dataTableFile = readFile(DATA_PATH + "Datatable.json");
        JSONArray didArray = new JSONArray(dataTableFile);
        for(int i = 0; i < didArray.length(); i++){
            JSONObject vipObject = didArray.getJSONObject(i);
            if(vipObject.getString("didHash").equals(did))
                peerid = vipObject.getString("peerid");
        }

        String vipFile = readFile(DATA_PATH + "vip.json");
        JSONArray vipArray = new JSONArray(vipFile);
        for(int i = 0; i < vipArray.length(); i++){
            JSONObject vipObject = vipArray.getJSONObject(i);
            if(vipObject.getString("peerid").equals(peerid))
                ip = vipObject.getString("ip");
        }

        if(ip.equals(""))
            contentObject.put("response", "Not a valid member");
        else
            contentObject.put("response", ip);
        result.put("data", contentObject);
        result.put("message", "");
        result.put("status", "true");
        return result.toString();
    }


}


