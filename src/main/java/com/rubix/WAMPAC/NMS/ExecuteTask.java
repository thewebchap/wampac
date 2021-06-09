package com.rubix.WAMPAC.NMS;

import com.rubix.WAMPAC.NMS.LogsConsensus.LogInitConsensus;
import com.rubix.WAMPAC.NMS.NLSS.Interact;
import io.ipfs.api.IPFS;
import io.ipfs.api.MerkleNode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import static com.rubix.Resources.Functions.*;
import static com.rubix.WAMPAC.NMS.Constants.PathConstants.*;
import static com.rubix.WAMPAC.NMS.Resources.Functions.*;

public class ExecuteTask implements Runnable{

    @Override
    public void run() {
        pathSet();
        Timer timer = new Timer();
        File recovered = new File(RecoveryFolder);
        if (!recovered.exists())
            recovered.mkdirs();
        File nms = new File(nmsFolder);
        if (!nms.exists())
            nms.mkdirs();
        File logs = new File(logsFolder);
        if (!logs.exists())
            logs.mkdirs();
        File backup = new File(backupFolder);
        if (!backup.exists())
            backup.mkdirs();

        TimerTask loggerTask = new TimerTask() {
            @Override
            public void run() {
                populateLogs();
                List<MerkleNode> hashList = addFolder(logsFolder);
                assert hashList != null;
                String ipfsHashLog = (hashList.get(hashList.size() - 1).toString().substring(0, 46));
                String peerID = getPeerID(DATA_PATH + "DID.json");
                String did = getValues(DATA_PATH + "DID.json", "didHash", "peerid", peerID);
                JSONObject logObject = new JSONObject();
                try {
                    logObject.put("logHash", ipfsHashLog);
                    logObject.put("did", did);
                    String signature = sign(logObject.toString());


                    JSONArray allShares = new JSONArray();
                    JSONObject q1 = new JSONObject();
                    JSONObject q2 = new JSONObject();
                    JSONObject q3 = new JSONObject();
                    JSONObject q4 = new JSONObject();
                    JSONObject q5 = new JSONObject();
//                    q1.put("index",1);
//                    q2.put("index",2);
//                    q3.put("index",3);
//                    q4.put("index",4);
//                    q5.put("index",5);

                    File logsfolder = new File(logsFolder);
                    File[] logsFileNames = logsfolder.listFiles();
                    File backupfolder = new File(backupFolder);
                    File[] backupFileNames = backupfolder.listFiles();

                    Boolean flag = false;
                    for (int i = 0; i < Objects.requireNonNull(backupFileNames).length; i++) {
                        String data = readFile(backupFileNames[i].getAbsolutePath());
                        if (data.isEmpty())
                            break;
                        flag = true;
                        String temp = convertToBinary(data);
                        Interact interact = new Interact(temp);
                        interact.sharecreate();
                        if(interact.checkshare()){
                            q1.put(backupFileNames[i].getName(),interact.secretstring);
                            q2.put(backupFileNames[i].getName(),interact.y1string);
                            q3.put(backupFileNames[i].getName(),interact.y2string);
                            q4.put(backupFileNames[i].getName(),interact.y3string);
                            q5.put(backupFileNames[i].getName(),interact.y4string);
//                            System.out.println("recover: "+Interact.getback(interact.secretstring,interact.y1string));
//                            System.out.println("recover: "+Interact.getback(interact.secretstring,interact.y2string));
//                            System.out.println("recover: "+Interact.getback(interact.secretstring,interact.y3string));
//                            System.out.println("recover: "+Interact.getback(interact.secretstring,interact.y4string));
                            System.out.println("Secretshare  "+interact.secretstring);
                        }else
                            flag = false;
//                        Split.split(temp);
//                        int[][] shares = Split.get135Shares();
//                        StringBuilder tempshare = new StringBuilder();
//
//                        for (int j = 0; j < shares[0].length; j++)
//                            tempshare.append(shares[0][j]);
//                        q1.put(backupFileNames[i].getName(),tempshare.toString());
//
//                        tempshare = new StringBuilder();
//                        for (int j = 0; j < shares[1].length; j++)
//                            tempshare.append(shares[1][j]);
//                        q2.put(backupFileNames[i].getName(),tempshare.toString());
//
//                        tempshare = new StringBuilder();
//                        for (int j = 0; j < shares[2].length; j++)
//                            tempshare.append(shares[2][j]);
//                        q3.put(backupFileNames[i].getName(),tempshare.toString());
//
//                        tempshare = new StringBuilder();
//                        for (int j = 0; j < shares[3].length; j++)
//                            tempshare.append(shares[3][j]);
//                        q4.put(backupFileNames[i].getName(),tempshare.toString());
//
//                        tempshare = new StringBuilder();
//                        for (int j = 0; j < shares[4].length; j++)
//                            tempshare.append(shares[4][j]);
//                        q5.put(backupFileNames[i].getName(),tempshare.toString());

                    }

                    allShares.put(q1);
                    allShares.put(q2);
                    allShares.put(q3);
                    allShares.put(q4);
                    allShares.put(q5);

                    JSONArray logfiles = new JSONArray();
                    for (int i = 0; i < Objects.requireNonNull(logsFileNames).length; i++) {
                        String data = readFile(logsFileNames[i].getAbsolutePath());

                        JSONObject temp = new JSONObject();
                        temp.put(logsFileNames[i].getName(),data);
                        logfiles.put(temp);
                    }

                    JSONObject payload = new JSONObject();
                    payload.put("Hash", logObject.toString());
                    payload.put("sign", signature);
                    payload.put("shares", allShares);
                    payload.put("logfiles",logfiles);

                    if (flag) {
                        IPFS ipfs = new IPFS("/ip4/127.0.0.1/tcp/" + IPFS_PORT);
                        LogInitConsensus.start(payload.toString(), ipfs, 15040);
                    }
                } catch (JSONException | IOException e) {
                    e.printStackTrace();
                }

            }
        };

        timer.schedule(loggerTask, 0L, 1000 * 60 * 60 * 6);
    }
}
