package com.rubix.WAMPAC.Controller;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.*;


import static com.rubix.Resources.Functions.*;
import static com.rubix.WAMPAC.NMS.Constants.PathConstants.backupFolder;
import static com.rubix.WAMPAC.NMS.Constants.PathConstants.nmsFolder;
import static com.rubix.WAMPAC.NMSCollection.Paths.MemoryFile;
import static com.rubix.WAMPAC.NMSCollection.Paths.MemoryFolder;

@RestController
public class NMS {

    @PostMapping(value = "/addBackup", produces = {"application/json", "application/xml"})
    public static String addBackup(@RequestParam("file") MultipartFile file) throws JSONException, IOException {
        System.out.println("Called /addBackup");
        pathSet();
        JSONObject result = new JSONObject();

        double fileSize = file.getSize() * 0.00000095367432;
        if (fileSize < 1) {
            byte[] bytes = file.getBytes();
            Path path = Paths.get(backupFolder + file.getOriginalFilename());
            Files.write(path, bytes);

            File filePath = new File(path.toString());
            if (filePath.exists())
                result.put("data", "Success");
            else
                result.put("data", "Failed to backup file. Try again later.");

        } else
            result.put("data", "Upload smaller file (2MB). Try again later.");
        result.put("message", "");
        result.put("status", "true");
        return result.toString();
    }

    @PostMapping(value = "/getNmsUser", produces = {"application/json", "application/xml"})
    public static String getNmsUser(@RequestParam("index") int index) throws JSONException {
        System.out.println("Called /getNmsUser");
        pathSet();
        JSONObject finalObject = new JSONObject();
        File logsFile = new File(nmsFolder + "log.json");
        if (logsFile.exists()) {
            String logFileContent = readFile(nmsFolder + "log.json");
            System.out.println(nmsFolder + "log.json");
            JSONArray logsArray = new JSONArray(logFileContent);
            JSONArray results = new JSONArray();
            HashMap<String, String> map = new HashMap<>();

            for (int i = logsArray.length() - 1 - (index * 50); map.size() < 10 && i >= 0; i--)
                map.put(logsArray.getJSONObject(i).getString("tid"), logsArray.getJSONObject(i).getString("status"));

            for (Map.Entry<String, String> entry : map.entrySet()) {
                JSONObject object = new JSONObject();
                object.put("tid", entry.getKey());
                object.put("status", entry.getValue());
                results.put(object);
            }
            System.out.println(results.length());
            finalObject.put("count", results.length());
            finalObject.put("payload", results);
        } else {
            finalObject.put("count", 0);
            finalObject.put("payload", new JSONArray());
        }
        JSONObject result = new JSONObject();
        result.put("data", finalObject);
        result.put("message", "");
        result.put("status", "true");
        return result.toString();
    }

    @PostMapping(value = "/getTxnDetails", produces = {"application/json", "application/xml"})
    public static String getTxnDetails(@RequestParam("tid") String tid) throws JSONException {
        System.out.println("Called /getTxnDetails");
        pathSet();
        JSONObject result = new JSONObject();
        File file = new File(nmsFolder + "log.json");
        if (file.exists()) {
            String logFileContent = readFile(nmsFolder + "log.json");
            JSONArray logsArray = new JSONArray(logFileContent);
            JSONArray signArray = new JSONArray();
            JSONObject resultObject = new JSONObject();
            for (int i = 0; i < logsArray.length(); i++) {
                if (logsArray.getJSONObject(i).getString("tid").equals(tid)) {
                    signArray.put(logsArray.getJSONObject(i).getString("sign"));
                    resultObject = logsArray.getJSONObject(i);
                }
            }

            resultObject.put("sign", signArray);
            result.put("data", resultObject);
        } else
            result.put("data", new JSONObject());
        result.put("message", "");
        result.put("status", "true");
        return result.toString();
    }

    @GetMapping(value = "/getBackupFiles", produces = {"application/json", "application/xml"})
    public static String getBackupFiles() throws JSONException {
        System.out.println("Called /getBackupFiles");
        pathSet();
        JSONObject result = new JSONObject();
        JSONArray backupList = new JSONArray();

        File folder = new File(backupFolder);
        File[] listOfFiles = folder.listFiles();

        for (int i = 0; i < listOfFiles.length; i++) {
            backupList.put(listOfFiles[i].getName());
        }
        result.put("data", backupList);
        result.put("message", "");
        result.put("status", "true");
        return result.toString();
    }

    @GetMapping(value = "/getStatusList", produces = {"application/json", "application/xml"})
    public static String getStatusList() throws JSONException {
        System.out.println("Called /getStatusList");
        pathSet();
        JSONObject result = new JSONObject();
        JSONArray resultArray = new JSONArray();
        JSONArray usersArrays = new JSONArray();
        File folder = new File(nmsFolder);
        File[] listOfFiles = folder.listFiles();
        boolean invalid = false;
        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isDirectory()) {
                if (!listOfFiles[i].getName().startsWith("Qm"))
                    invalid = true;
                usersArrays.put(listOfFiles[i].getName());
            }
        }
        System.out.println(invalid);
        if (!invalid) {
            for (int i = 0; i < usersArrays.length(); i++) {
                File file = new File(nmsFolder + usersArrays.getString(i) + "/log.json");
                if (file.exists()) {
                    String userDetails = readFile(nmsFolder + usersArrays.getString(i) + "/log.json");
                    JSONArray jsonArray = new JSONArray(userDetails);
                    JSONObject object = new JSONObject();
                    object.put("did", usersArrays.getString(i));
                    object.put("status", jsonArray.getJSONObject(jsonArray.length() - 1).getString("status"));

                    resultArray.put(object);
                }
            }
            result.put("data", resultArray);
        } else
            result.put("data", "Operation not allowed");


        result.put("message", "");
        result.put("status", "true");
        return result.toString();
    }

    @GetMapping(value = "/getPieData", produces = {"application/json", "application/xml"})
    public static String getPieData() throws JSONException {
        System.out.println("Called /getPieData");
        pathSet();
        JSONObject result = new JSONObject();
        JSONArray countObject = new JSONArray();
        JSONArray usersArrays = new JSONArray();
        File folder = new File(nmsFolder);
        File[] listOfFiles = folder.listFiles();
        boolean invalid = false;
        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isDirectory()) {
                if (!listOfFiles[i].getName().startsWith("Qm"))
                    invalid = true;
                usersArrays.put(listOfFiles[i].getName());
            }
        }
        int unsafeCount = 0;
        int safeCount = 0;
        if (!invalid) {

            for (int i = 0; i < usersArrays.length(); i++) {
                File file = new File(nmsFolder + usersArrays.getString(i) + "/log.json");
                if (file.exists()) {
                    String userDetails = readFile(nmsFolder + usersArrays.getString(i) + "/log.json");
                    System.out.println(userDetails);
                    JSONArray jsonArray = new JSONArray(userDetails);
                    if (jsonArray.getJSONObject(jsonArray.length() - 1).getString("status").contains("restore"))
                        unsafeCount++;
                    else
                        safeCount++;
                }

            }
            countObject.put(unsafeCount);
            countObject.put(safeCount);
        } else
            result.put("data", "Operation not allowed");

        result.put("data", countObject);
        result.put("message", "");
        result.put("status", "true");
        return result.toString();
    }

    @PostMapping(value = "/getDidTransactions", produces = {"application/json", "application/xml"})
    public static String getDidTransactions(@RequestParam("did") String did) throws JSONException {
        System.out.println("Called /getDidTransactions");
        pathSet();
        JSONObject result = new JSONObject();
        File logFileData = new File(nmsFolder + did + "/log.json");
        if (logFileData.exists()) {
            String logFile = readFile(nmsFolder + did + "/log.json");
            JSONArray userLogArray = new JSONArray(logFile);

            JSONArray usersArrays = new JSONArray();
            for (int i = 0; i < userLogArray.length(); i++) {
                JSONObject object = new JSONObject();
                object.put("tid", userLogArray.getJSONObject(i).getString("tid"));
                object.put("status", userLogArray.getJSONObject(i).getString("status"));

                usersArrays.put(object);
            }
            result.put("data", usersArrays);
        } else {
            result.put("data", new JSONArray());
        }
        result.put("message", "");
        result.put("status", "true");
        return result.toString();
    }

    @PostMapping(value = "/getDidTxnDetails", produces = {"application/json", "application/xml"})
    public static String getDidTxnDetails(@RequestParam("did") String did, @RequestParam("tid") String tid) throws JSONException {
        System.out.println("Called /getDidTxnDetails");
        pathSet();
        JSONObject result = new JSONObject();
        File logFileData = new File(nmsFolder + did + "/log.json");
        if (logFileData.exists()) {
            String logFile = readFile(nmsFolder + did + "/log.json");
            JSONArray userLogArray = new JSONArray(logFile);

            JSONArray usersArrays = new JSONArray();
            for (int i = 0; i < userLogArray.length(); i++) {
                if (userLogArray.getJSONObject(i).getString("tid").equals(tid)) {
                    JSONObject object = new JSONObject();
                    object.put("did", did);
                    object.put("tid", tid);
                    object.put("logHash", userLogArray.getJSONObject(i).getString("loghash"));
                    object.put("sign", userLogArray.getJSONObject(i).getString("sign"));
                    object.put("share", userLogArray.getJSONObject(i).getString("share"));

                    usersArrays.put(object);
                }
            }
            result.put("data", usersArrays);
        } else {
            result.put("data", new JSONArray());
        }
        result.put("message", "");
        result.put("status", "true");
        return result.toString();
    }

    @GetMapping(value = "/unsafeCount", produces = {"application/json", "application/xml"})
    public static String unsafeCount() throws JSONException, IOException {
        System.out.println("Called /unsafeCount");
        pathSet();
        JSONObject result = new JSONObject();
        int unsafeCount = 0;
        File unsafeFile = new File(nmsFolder + "unsafe.json");
        if (unsafeFile.exists()) {
            String userRecordsFile = readFile(nmsFolder + "unsafe.json");
            JSONArray recordArray = new JSONArray(userRecordsFile);

            for (int i = 0; i < recordArray.length(); i++) {
                Iterator<String> keys = recordArray.getJSONObject(i).keys();
                String did = keys.next();
                unsafeCount = unsafeCount + recordArray.getJSONObject(i).getInt(did);
            }
        }
        result.put("data", unsafeCount);
        result.put("message", "");
        result.put("status", "true");
        return result.toString();
    }

    @GetMapping(value = "/mostUnsafe", produces = {"application/json", "application/xml"})
    public static String mostUnsafe() throws JSONException, IOException {
        System.out.println("Called /mostUnsafe");
        pathSet();
        JSONObject result = new JSONObject();

        File unsafeFile = new File(nmsFolder + "unsafe.json");
        if (unsafeFile.exists()) {
            String userRecordsFile = readFile(nmsFolder + "unsafe.json");
            JSONArray recordArray = new JSONArray(userRecordsFile);

            JSONArray resultArray = new JSONArray();

            int index;
            while (recordArray.length() > 0) {
                int mostUnsafe = 0;
                index = 0;
                for (int i = 0; i < recordArray.length(); i++) {
                    JSONObject temp = recordArray.getJSONObject(i);
                    Iterator<String> keys = temp.keys();
                    String did = keys.next();
                    if (recordArray.getJSONObject(i).getInt(did) > mostUnsafe) {
                        mostUnsafe = recordArray.getJSONObject(i).getInt(did);
                        index = i;
                    }
                }

                resultArray.put(recordArray.getJSONObject(index));
                recordArray.remove(index);
            }
            result.put("data", resultArray);
        } else
            result.put("data", new JSONArray());
        result.put("message", "");
        result.put("status", "true");
        return result.toString();
    }

    @GetMapping(value = "/networkUsersCount", produces = {"application/json", "application/xml"})
    public static String networkUsersCount() throws JSONException {
        System.out.println("Called /networkUsersCount");
        pathSet();
        JSONObject result = new JSONObject();
        JSONArray usersArrays = new JSONArray();
        File folder = new File(nmsFolder);
        File[] listOfFiles = folder.listFiles();
        boolean invalid = false;
        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isDirectory()) {
                if (!listOfFiles[i].getName().startsWith("Qm"))
                    invalid = true;
                usersArrays.put(listOfFiles[i].getName());
            }
        }
        if (!invalid) {
            result.put("data", usersArrays.length());
        } else
            result.put("data", "Operation not allowed");


        result.put("message", "");
        result.put("status", "true");
        return result.toString();
    }

    @GetMapping(value = "/getAnomalyListUser", produces = {"application/json", "application/xml"})
    public static String getAnomalyListUser() throws JSONException {
        System.out.println("Called /getAnomalyListUser");
        pathSet();
        JSONObject result = new JSONObject();
        File anomalyFile = new File(nmsFolder + "anomaly.json");
        JSONArray anomalyArray;
        if (!anomalyFile.exists()) {
            anomalyArray = new JSONArray();
        } else {
            String anomalyData = readFile(nmsFolder + "anomaly.json");
            anomalyArray = new JSONArray(anomalyData);
        }

        result.put("data", anomalyArray);


        result.put("message", "");
        result.put("status", "true");
        return result.toString();
    }

    @GetMapping(value = "/getAnomalyListVerifier", produces = {"application/json", "application/xml"})
    public static String getAnomalyListVerifier() throws JSONException {
        System.out.println("Called /getAnomalyListVerifier");
        pathSet();
        JSONObject result = new JSONObject();
        JSONArray usersArrays = new JSONArray();
        File folder = new File(nmsFolder);
        File[] listOfFiles = folder.listFiles();
        boolean invalid = false;
        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isDirectory()) {
                if (!listOfFiles[i].getName().startsWith("Qm"))
                    invalid = true;
                usersArrays.put(listOfFiles[i].getName());
            }
        }
        JSONArray anomalyArray = new JSONArray();
        if (!invalid) {
            for (int i = 0; i < usersArrays.length(); i++) {
                File userAnomalyFile = new File(nmsFolder + usersArrays.getString(i) + "/anomaly.json");
                if (userAnomalyFile.exists()) {
                    String anomalyData = readFile(nmsFolder + usersArrays.getString(i) + "/anomaly.json");
                    JSONArray userAnomalyArray = new JSONArray(anomalyData);
                    for (int j = 0; j < userAnomalyArray.length(); j++) {
                        JSONObject dataObject = userAnomalyArray.getJSONObject(j);
                        dataObject.put("did", usersArrays.getString(i));
                        anomalyArray.put(dataObject);
                    }
                }

            }
            result.put("data", anomalyArray);
        } else
            result.put("data", "Operation not allowed");


        result.put("message", "");
        result.put("status", "true");
        return result.toString();
    }


    @GetMapping(value = "/getLogsCount", produces = {"application/json", "application/xml"})
    public static String getLogsCount() throws JSONException {
        System.out.println("Called /getLogsCount");
        pathSet();
        JSONArray logsArray = new JSONArray();
        File logsFile = new File(nmsFolder + "log.json");
        if (logsFile.exists()) {
            String logFileContent = readFile(nmsFolder + "log.json");
            System.out.println(nmsFolder + "log.json");
            logsArray = new JSONArray(logFileContent);
        }
        JSONObject result = new JSONObject();
        result.put("data", logsArray.length()/5);
        result.put("message", "");
        result.put("status", "true");
        return result.toString();
    }

    @GetMapping(value = "/latestSystemStatus", produces = {"application/json", "application/xml"})
    public static String latestSystemStatus() throws JSONException {
        System.out.println("Called /latestSystemStatus");
        pathSet();
        String status = "";
        File logsFile = new File(nmsFolder + "log.json");
        if (logsFile.exists()) {
            String logFileContent = readFile(nmsFolder + "log.json");
            System.out.println(nmsFolder + "log.json");
            JSONArray logsArray = new JSONArray(logFileContent);
            status = logsArray.getJSONObject(logsArray.length() - 1).getString("status");
        }
        JSONObject result = new JSONObject();
        result.put("data", status);
        result.put("message", "");
        result.put("status", "true");
        return result.toString();
    }

    @GetMapping(value = "/getLatestAnomalies", produces = {"application/json", "application/xml"})
    public static String getLatestAnomalies() throws JSONException {
        System.out.println("Called /getLatestAnomalies");
        pathSet();
        JSONObject result = new JSONObject();
        JSONArray finalArray = new JSONArray();
        File anomalyFile = new File(nmsFolder + "anomaly.json");
        JSONArray anomalyArray;
        if (anomalyFile.exists()) {
            String anomalyData = readFile(nmsFolder + "anomaly.json");
            anomalyArray = new JSONArray(anomalyData);
            if (anomalyArray.length() <= 3)
                finalArray = anomalyArray;
            else {
                int i = anomalyArray.length() - 1, j = 0;
                while (j < 3) {
                    finalArray.put(anomalyArray.getJSONObject(i));
                    i--;
                    j++;
                }
            }
        }

        result.put("data", finalArray);
        result.put("message", "");
        result.put("status", "true");
        return result.toString();
    }

    @GetMapping(value = "/anomalyBarChart", produces = {"application/json", "application/xml"})
    public static String anomalyBarChart() throws JSONException {
        System.out.println("Called /anomalyBarChart");
        pathSet();
        JSONObject contentObject = new JSONObject();

        JSONArray timeArray = new JSONArray();
        File file = new File(nmsFolder + "anomaly.json");
        if (file.exists()) {
            String anomalyFile = readFile(nmsFolder + "anomaly.json");
            JSONArray anomalyArray = new JSONArray(anomalyFile);
            int len = anomalyArray.length() - 1;
            int j = 0;
            while (j < anomalyArray.length()) {
                JSONObject object = anomalyArray.getJSONObject(len);
                timeArray.put(object.getString("timestamp"));
                j++;
                len--;
            }

            HashSet<String> hashSet = new HashSet();
            for (int i = 0; i < timeArray.length(); i++) {
                String output = timeArray.getString(i).substring(0, 10);
                hashSet.add(output);
            }

            JSONArray timeCountArray = new JSONArray();
            for (String language : hashSet) {
                timeCountArray.put(new JSONObject().put("time", language));
            }
            for(int i = 0; i < timeCountArray.length(); i++){
                int count = 0;
                for(int k =0; k < timeArray.length(); k++){
                    String time = anomalyArray.getJSONObject(k).getString("timestamp").substring(0, 10);
                    if(time.equals(timeCountArray.getJSONObject(i).getString("time")))
                        count++;
                }
                timeCountArray.getJSONObject(i).put("count", count);
            }

            contentObject.put("payload", timeCountArray);
        }else
            contentObject.put("payload", new JSONArray());

        JSONObject result = new JSONObject();
        result.put("data", contentObject);
        result.put("message", "");
        result.put("status", "true");
        return result.toString();
    }

    @GetMapping(value = "/getLastLogTime", produces = {"application/json", "application/xml"})
    public static String getLastLogTime() throws JSONException{
        System.out.println("Called /getLastLogTime");

        System.out.println(MemoryFolder + MemoryFile);
        JSONObject result = new JSONObject();
        File logFile = new File(MemoryFolder + MemoryFile);
        if (logFile.exists()) {
            String logsContent = readFile(MemoryFolder + MemoryFile);
            JSONArray logsArray = new JSONArray(logsContent);
            System.out.println(logsArray);
            System.out.println(logsArray.getJSONObject(logsArray.length()-1));
            System.out.println(logsArray.getJSONObject(logsArray.length()-1).getString("time"));
            result.put("data", logsArray.getJSONObject(logsArray.length()-1).getString("time"));
        }
        else
            result.put("data", "");
        result.put("message", "");
        result.put("status", "true");
        return result.toString();
    }

    @GetMapping(value = "/getTokenCount", produces = {"application/json", "application/xml"})
    public static String getTokenCount() throws JSONException{
        System.out.println("Called /getTokenCount");
        pathSet();
        JSONObject result = new JSONObject();
        File tokenFile = new File(WALLET_DATA_PATH + "tokenList.json");
        if (tokenFile.exists()) {
            String tokenContent = readFile(WALLET_DATA_PATH + "tokenList.json");
            JSONArray tokenArray = new JSONArray(tokenContent);
            result.put("data", tokenArray.length());
        }
        else
            result.put("data", 0);

        result.put("message", "");
        result.put("status", "true");
        return result.toString();
    }

}
