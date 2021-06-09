package com.rubix.WAMPAC.Controller;

import com.rubix.Resources.Functions;
import com.rubix.Resources.IPFSNetwork;
import com.rubix.WAMPAC.Anomaly.Communication.Receiver;
import com.rubix.WAMPAC.Anomaly.NMS_main;
import com.rubix.WAMPAC.DID.RequestTokens;
import com.rubix.WAMPAC.DID.VerifierListen;
import com.rubix.WAMPAC.DID.VerifierUserListen;
import com.rubix.WAMPAC.DID.ipClass;
import com.rubix.WAMPAC.NMS.ExecuteTask;
import com.rubix.WAMPAC.NMS.LogsConsensus.LogQuorumConsensus;
import com.rubix.WAMPAC.NMS.Recovery.RecoveryAssistQuorum;
import com.rubix.WAMPAC.DID.User.*;
import com.rubix.WAMPAC.DID.Verifier.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static RubixDID.DIDCreation.DIDimage.createDID;
import static com.rubix.Resources.APIHandler.*;
import static com.rubix.Resources.APIHandler.onlinePeersCount;
import static com.rubix.Resources.Functions.*;
import static com.rubix.WAMPAC.DID.DIDFunctions.*;
import static com.rubix.WAMPAC.FileAccess.FileAccessFunctions.createRbxDrive;
import static com.rubix.WAMPAC.NMS.Constants.PathConstants.*;
import static com.rubix.WAMPAC.Controller.NMSCollection.nmsStart;
import static com.rubix.WAMPAC.NMSCollection.Paths.createCollectionFolder;

@CrossOrigin(origins = "http://localhost:1898")
@RestController
public class DID {
    private static final String USER_AGENT = "Mozilla/5.0";
    private static String role = "";
    public static boolean listenThreads = false;
    public static boolean userNMSinit = false;
    public static boolean verifierNmsListen = false;

    @RequestMapping(value = "/checkDidExists", method = RequestMethod.GET,
            produces = {"application/json", "application/xml"})
    public String checkDidExists() throws JSONException {
        System.out.println("Called /checkDidStatus");
        JSONObject result = new JSONObject();
        JSONObject contentObject = new JSONObject();
        setDir();
        Path path = Paths.get(dirPath);
        boolean isDir = Files.isDirectory(path);
        if (isDir) {
            setConfig();
            File config = new File(configPath);
            if (config.exists()) {
                pathSet();
                File didFile = new File(DATA_PATH + "DID.json");
                if (didFile.exists()) {
                    System.out.println("DID exists");
                    contentObject.put("payload", "Success");
                }
                else {
                    System.out.println("DID.json doesn't exists");
                    contentObject.put("payload", "Failure");
                }

            } else {
                System.out.println("config.json doesn't exists");
                contentObject.put("payload", "Failure");
            }
        } else {
            System.out.println("RUBIX folder doesn't exists");
            contentObject.put("payload", "Failure");
        }
        result.put("data", contentObject);
        result.put("message", "");
        result.put("status", "true");
        return result.toString();
    }

    @RequestMapping(value = "/checkDidVerified", method = RequestMethod.GET,
            produces = {"application/json", "application/xml"})
    public String checkDidVerified() throws JSONException {
        System.out.println("Called /checkDidVerified");
        JSONObject result = new JSONObject();
        JSONObject contentObject = new JSONObject();
        boolean verifiedStatus = false;

        pathSet();
        File vipFile = new File(DATA_PATH + "vip.json");
        if (!vipFile.exists())
            contentObject.put("payload", "Failure");

        else {
            String vipContent = readFile(DATA_PATH + "vip.json");
            JSONArray vipArray = new JSONArray(vipContent);

            String myIp = ipClass.getIP();
            System.out.println("/checkDidVerified IP Address: " + myIp);

            System.out.println(myIp);
            for (int i = 0; i < vipArray.length(); i++) {
                if (vipArray.getJSONObject(i).getString("ip").equals(myIp))
                    if (vipArray.getJSONObject(i).getString("status").equals("true"))
                        verifiedStatus = true;
            }
            if (verifiedStatus)
                contentObject.put("payload", "Success");

            else
                contentObject.put("payload", "Failure");

        }

        result.put("data", contentObject);
        result.put("message", "");
        result.put("status", "true");
        return result.toString();
    }

    @RequestMapping(value = "/checkRole", method = RequestMethod.GET,
            produces = {"application/json", "application/xml"})
    public String checkRole() throws JSONException {
        System.out.println("Called /checkRole");
        Functions.pathSet();
        JSONObject result = new JSONObject();
        JSONObject contentObject = new JSONObject();

        File file = new File(DATA_PATH + "vip.json");
        if(file.exists()) {
            System.out.println("Inside");
            String vipContent = readFile(DATA_PATH + "vip.json");
            JSONArray vipArray = new JSONArray(vipContent);
            String myIp = ipClass.getIP();
            System.out.println("/checkRole IP Address: " + myIp);
            for (int i = 0; i < vipArray.length(); i++) {
                if (vipArray.getJSONObject(i).getString("ip").equals(myIp))
                    role = vipArray.getJSONObject(i).getString("role");
            }
            contentObject.put("payload", role);
        }else
            System.out.println("Outside");
        result.put("data", contentObject);
        result.put("message", "");
        result.put("status", "true");
        return result.toString();
    }

    @RequestMapping(value = "/getIp", method = RequestMethod.GET,
            produces = {"application/json", "application/xml"})
    public String getIp() throws JSONException{
        System.out.println("Called /getIp");
        JSONObject result = new JSONObject();
        JSONObject contentObject = new JSONObject();
        String myIp = ipClass.getIP();
        System.out.println("/getIp IP Address: " + myIp);
        contentObject.put("payload", myIp);
        result.put("data", contentObject);
        result.put("message", "");
        result.put("status", "true");
        return result.toString();
    }

    @RequestMapping(value = "/getMac", method = RequestMethod.GET,
            produces = {"application/json", "application/xml"})
    public String getMac() throws JSONException {
        System.out.println("Called /getMac");
        JSONObject result = new JSONObject();
        JSONObject contentObject = new JSONObject();

        contentObject.put("payload", macAddress());
        result.put("data", contentObject);
        result.put("message", "");
        result.put("status", "true");
        return result.toString();
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST,
            produces = {"application/json", "application/xml"})
    public String Create(@RequestParam("image") MultipartFile imageFile, @RequestParam("data") String value) throws Exception {
        System.out.println("Called /create");

        JSONObject result = new JSONObject();
        JSONObject contentObject = new JSONObject();

        JSONObject didResult = createDID(value, imageFile.getInputStream());
        contentObject.put("payload", didResult);

        result.put("data", contentObject);
        result.put("message", "");
        result.put("status", "true");
        return result.toString();
    }

    @RequestMapping(value = "/getInfo", method = RequestMethod.GET,
            produces = {"application/json", "application/xml"})
    public String getInfo() throws JSONException {
        System.out.println("Called /getInfo");
        pathSet();
        JSONObject result = new JSONObject();
        JSONObject contentObject = new JSONObject();
        File file = new File(DATA_PATH + "DID.json");
        if(file.exists()) {
            String didFile = readFile(DATA_PATH + "DID.json");
            JSONArray didArray = new JSONArray(didFile);
            JSONObject didObject = didArray.getJSONObject(0);

            String myIp = ipClass.getIP();
            System.out.println("/getInfo IP Address: " + myIp);
            didObject.put("ip", myIp);

            contentObject.put("payload", didObject);
        }
        else
            contentObject.put("payload", new JSONObject());




        result.put("data", contentObject);
        result.put("message", "");
        result.put("status", "true");
        return result.toString();
    }

    @RequestMapping(value = "/getNetworkInfo", method = RequestMethod.GET,
            produces = {"application/json", "application/xml"})
    public String getNetworkInfo() throws JSONException {
        System.out.println("Called /getNetworkInfo");
        pathSet();
        JSONObject result = new JSONObject();
        JSONObject contentObject = new JSONObject();
        File file = new File(DATA_PATH + "DID.json");
        if(file.exists()) {
            String vipFile = readFile(DATA_PATH + "vip.json");
            JSONArray vipArray = new JSONArray(vipFile);
            JSONArray verifiersArray = new JSONArray();
            for (int i = 0; i < vipArray.length(); i++) {
                if (vipArray.getJSONObject(i).getString("role").equals("Verifier")) {
                    JSONObject verifierObject = vipArray.getJSONObject(i);
                    verifierObject.remove("role");
                    verifierObject.remove("status");
                    verifiersArray.put(verifierObject);
                }
            }

            contentObject.put("payload", verifiersArray);
        }
        else
            contentObject.put("payload", new JSONArray());


        result.put("data", contentObject);
        result.put("message", "");
        result.put("status", "true");
        return result.toString();
    }


    @RequestMapping(value = "/sync", method = RequestMethod.GET,
            produces = {"application/json", "application/xml"})
    public String sync() throws Exception {
        System.out.println("Called /sync");
        Functions.pathSet();
        IPFSNetwork.executeIPFSCommands("ipfs daemon");

        JSONObject result = new JSONObject();
        JSONObject contentObject = new JSONObject();

        String url = "http://172.17.128.102:9090/getrole?ip=";

        String myip = ipClass.getIP();
        System.out.println("/sync IP Address: " + myip);
        url = url + myip;
        URL obj = new URL(url);
        System.out.println(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        // Setting basic get request
        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", USER_AGENT);
        con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
        con.setRequestProperty("Accept", "application/json");
        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestProperty("Authorization", "null");

        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String response;
        StringBuffer stringBuffer = new StringBuffer();

        while ((response = in.readLine()) != null) {
            stringBuffer.append(response);
        }
        in.close();
        role = stringBuffer.toString();

        if (role.equals("Verifier")) {
            //Listen on all threads
            if(!listenThreads) {
                verifierVerifyListen();
                verifierUserListen();
                verifierSendData();
                listenThreads = true;
            }

            Functions.launch();
            if (vipSync()) {
                if(rulesSync()){
                    System.out.println("Successful - check Sync Status in the log");
                    contentObject.put("payload", "Success");
                }else{
                    System.out.println("Launch Failed Rules");
                    contentObject.put("payload", "Failed to sync. Try again.");
                }

            } else {
                System.out.println("Launch Failed VIP");
                contentObject.put("payload", "Failed to sync. Try again.");
            }
        } else if (role.equals("User")) {
            contentObject.put("payload", getDataFromVerifier.getData());
        }

        result.put("data", contentObject);
        result.put("message", "");
        result.put("status", "true");
        return result.toString();
    }

    @RequestMapping(value = "/enroll", method = RequestMethod.GET,
            produces = {"application/json", "application/xml"})
    public String enroll() throws Exception {
        System.out.println("Called /enroll");
        Functions.pathSet();
        IPFSNetwork.executeIPFSCommands("ipfs daemon");
        JSONObject result = new JSONObject();
        JSONObject contentObject = new JSONObject();

        String myip = ipClass.getIP();
        System.out.println("/enroll IP Address: " + myip);

        if (role.equals("Verifier")) {
            //verifierInitiate
            contentObject.put("payload", verifierDID(myip));
        } else if (role.equals("User")) {
            //userInitiate
            contentObject.put("payload", userDID(myip));

        }
        result.put("data", contentObject);
        result.put("message", "");
        result.put("status", "true");
        return result.toString();
    }

    public static void verifierVerifyListen() {
        VerifierListen verifierListen = new VerifierListen();
        Thread verThread = new Thread(verifierListen);
        verThread.start();

    }

    public static void verifierUserListen() {
        VerifierUserListen verifierUserListen = new VerifierUserListen();
        Thread verifierUserThread = new Thread(verifierUserListen);
        verifierUserThread.start();
    }

    public static void verifierSendData() {
        sendToUser sendToUser = new sendToUser();
        Thread send = new Thread(sendToUser);
        send.start();
    }

    public static void LogQuorumConsensusListen() {
        pathSet();
        LogQuorumConsensus q1 = new LogQuorumConsensus();
        Thread logQuorumConsensus = new Thread(q1);
        logQuorumConsensus.start();
    }

    public static void RecoveryAssistQuorumListen() {
        pathSet();
        RecoveryAssistQuorum q1 = new RecoveryAssistQuorum();
        Thread recoveryThread = new Thread(q1);
        recoveryThread.start();
    }

    public static void NMSLoggerInit() {
        pathSet();
        ExecuteTask q1 = new ExecuteTask();
        Thread executeThread = new Thread(q1);
        executeThread.start();
    }

    public static void AnomalyReceiver(){
        Receiver q1 = new Receiver();
        Thread executeThread = new Thread(q1);
        executeThread.start();
    }

    public static void NMSClient(){
        NMS_main q1 = new NMS_main();
        Thread executeThread = new Thread(q1);
        executeThread.start();
    }


    @RequestMapping(value = "/verifierGetUserVip", method = RequestMethod.POST,
            produces = {"application/json", "application/xml"})
    public String verifierGetUserVip(@RequestBody Details details) throws Exception {

        JSONObject vipDetails = new JSONObject();
        vipDetails.put("peerid", details.getPeerid());
        vipDetails.put("ip", details.getIp());
        vipDetails.put("role", details.getRole());
        vipDetails.put("status", details.getStatus());

        JSONObject result = new JSONObject();
        result.put("data", addUserVipListen.listen(vipDetails));
        result.put("message", "");
        result.put("status", "true");
        return result.toString();
    }

    @RequestMapping(value = "/verifierGetUserData", method = RequestMethod.POST,
            produces = {"application/json", "application/xml"})
    public String verifierGetUserData(@RequestBody Details details) throws Exception {
        JSONObject dataDetails = new JSONObject();
        dataDetails.put("peerid", details.getPeerid());
        dataDetails.put("didHash", details.getDidHash());
        dataDetails.put("walletHash", details.getWalletHash());
        System.out.println(dataDetails);

        JSONObject result = new JSONObject();
        result.put("data", addUserDataTableListen.listen(dataDetails));
        result.put("message", "");
        result.put("status", "true");
        return result.toString();
    }

    @RequestMapping(value = "/modifyIP", method = RequestMethod.GET,
            produces = {"application/json", "application/xml"})
    public String modifyIP() throws JSONException, IOException {
        Functions.pathSet();
        IPFSNetwork.executeIPFSCommands("ipfs daemon");

        JSONObject result = new JSONObject();

        String ip = ipClass.getIP();
        System.out.println("/modifyIp IP Address: " + ip);
        if (getRole(ip).equals("Verifier")) {
            //Call Contract - File Access
            JSONObject contentObject = new JSONObject();
            result.put("data", contentObject);
        } else {
            result.put("data", "Access Denied");
        }
        result.put("message", "");
        result.put("status", "true");
        return result.toString();
    }

    @RequestMapping(value = "/p2pclose", method = RequestMethod.GET,
            produces = {"application/json", "application/xml"})
    public String p2pclose() throws JSONException {
        IPFSNetwork.executeIPFSCommands("ipfs p2p close --all");
        System.out.println("P2P close Successful - check p2p ls for streams");

        JSONObject result = new JSONObject();
        JSONObject contentObject = new JSONObject();
        result.put("data", contentObject);
        result.put("message", "");
        result.put("status", "true");
        return result.toString();
    }

    @RequestMapping(value = "/getOnlinePeers", method = RequestMethod.GET,
            produces = {"application/json", "application/xml"})
    public String getOnlinePeers() throws IOException, JSONException, InterruptedException {
        Functions.pathSet();
        IPFSNetwork.executeIPFSCommands("ipfs daemon");
        JSONObject result = new JSONObject();
        JSONObject contentObject = new JSONObject();
        contentObject.put("payload", peersOnlineStatus());
        contentObject.put("count", peersOnlineStatus().length());
        result.put("data", contentObject);
        result.put("message", "");
        result.put("status", "true");
        return result.toString();
    }

    @RequestMapping(value = "/getContactsList", method = RequestMethod.GET,
            produces = {"application/json", "application/xml"})
    public String getContactsList() throws JSONException{
        Functions.pathSet();
        JSONObject result = new JSONObject();
        JSONObject contentObject = new JSONObject();
        contentObject.put("payload", contacts());
        contentObject.put("count", contacts().length());
        result.put("data", contentObject);
        result.put("message", "");
        result.put("status", "true");
        return result.toString();
    }

    @RequestMapping(value = "/requestTokens", method = RequestMethod.GET,
            produces = {"application/json", "application/xml"})
    public String requestTokens() throws JSONException{
        Functions.pathSet();
        JSONObject result = new JSONObject();
        JSONObject contentObject = new JSONObject();
        File tokenListFile = new File(WALLET_DATA_PATH + "tokenList.json");
        if(!tokenListFile.exists())
            contentObject.put("payload", RequestTokens.getTokens());
        else
            contentObject.put("payload", "Success");
        result.put("data", contentObject);
        result.put("message", "");
        result.put("status", "true");
        return result.toString();
    }

    @RequestMapping(value = "/getDashboard", method = RequestMethod.GET,
            produces = {"application/json", "application/xml"})
    public String getDashboard() throws JSONException, IOException, InterruptedException {
        pathSet();
        createCollectionFolder();
        createRbxDrive();

        IPFSNetwork.executeIPFSCommands("ipfs daemon");
        File nms = new File(nmsFolder);
        if (!nms.exists())
            nms.mkdirs();

        FileAccess.listen();

        String roleString = checkRole();
        JSONObject roleObject = new JSONObject(roleString);
        System.out.println("Role Object: " + roleString);
        String role = roleObject.getJSONObject("data").getString("payload");
        if (role.contains("Verifier")) {
            if (!listenThreads) {
                verifierVerifyListen();
                verifierUserListen();
                verifierSendData();
                listenThreads = true;
            }
            if(!verifierNmsListen){
                LogQuorumConsensusListen();
                RecoveryAssistQuorumListen();
                AnomalyReceiver();
                verifierNmsListen = true;
            }
        }
        else if(role.contains("User")){
            File recovered = new File(RecoveryFolder);
            if (!recovered.exists())
                recovered.mkdirs();
            File logs = new File(logsFolder);
            if (!logs.exists())
                logs.mkdirs();
            File backup = new File(backupFolder);
            if (!backup.exists())
                backup.mkdirs();
            if(!userNMSinit) {
                userNMSinit = true;
                NMSLoggerInit();
                NMSClient();
            }
        }

        if(!nmsStart)
            NMSCollection.start();

        JSONArray contactsObject = contacts();
        int contactsCount = contactsObject.length();

        JSONArray accountInfo = accountInformation();
        JSONObject accountObject = accountInfo.getJSONObject(0);

        JSONArray dateTxn = txnPerDay();
        JSONObject dateTxnObject = dateTxn.getJSONObject(0);

        accountObject.put("onlinePeers", onlinePeersCount());
        accountObject.put("contactsCount", contactsCount);
        accountObject.put("transactionsPerDay", dateTxnObject);


        JSONObject result = new JSONObject();
        JSONObject contentObject = new JSONObject();
        contentObject.put("payload", accountObject);
        result.put("data", contentObject);
        result.put("message", "");
        result.put("status", "true");
        return result.toString();
    }

}
