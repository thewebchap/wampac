//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.rubix.WAMPAC.Anomaly.Communication;

import com.rubix.Resources.Functions;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.Socket;
import java.sql.Timestamp;
import java.time.Instant;

import static com.rubix.Resources.Functions.*;
import static com.rubix.WAMPAC.Anomaly.Resources.Functions.getVerifierIPList;
import static com.rubix.WAMPAC.NMS.Constants.PathConstants.nmsFolder;

public class Sender {
    public static Logger Sender = Logger.getLogger(Sender.class);
    private static Socket[] qSocket;
    private static PrintStream[] qOut;
    private static BufferedReader[] qIn;
    private static String[] quorumID;


    public Sender() {
    }

    public static void start(String data, int PORT) throws JSONException, IOException {
        pathSet();
        PropertyConfigurator.configure(Functions.LOGGER_PATH + "log4jWallet.properties");

        JSONArray quorumPeersObject = getVerifierIPList();

        try {

            int i;
            JSONObject dataObject = new JSONObject();
            Timestamp instant= Timestamp.from(Instant.now());
            String senderPeerID = getPeerID(DATA_PATH + "DID.json");
            String did = getValues(DATA_PATH + "DID.json", "didHash", "peerid", senderPeerID);
            dataObject.put("anomaly",data);
            dataObject.put("did",did);
            dataObject.put("timestamp",instant);
            Sender.debug("Sending trigger "+data);

            for (int j = 0; j < quorumPeersObject.length(); ++j)
                quorumID[j] = quorumPeersObject.getString(j);


            Thread[] quorumThreads = new Thread[quorumPeersObject.length()];

            for (i = 0; i < quorumPeersObject.length(); ++i) {
                int j = i;
                quorumThreads[i] = new Thread(() -> {
                    try {
                        String var10001 = quorumID[j];
                        Sender.debug("Connected to " + var10001);
                        qSocket[j] = new Socket(quorumID[j], PORT);
                        qIn[j] = new BufferedReader(new InputStreamReader(qSocket[j].getInputStream()));
                        qOut[j] = new PrintStream(qSocket[j].getOutputStream());
                        qOut[j].println(dataObject);

                        qOut[j].close();
                        qIn[j].close();
                        qSocket[j].close();
                    } catch (IOException var13) {

                        try {
                            qOut[j].close();
                            qIn[j].close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Sender.error("IOException Occurred", var13);
                        var13.printStackTrace();
                    }

                });
                quorumThreads[i].start();
            }



            File logFile = new File(nmsFolder + "anomaly.json");
            JSONArray array;
            if (!logFile.exists()) {
                array = new JSONArray();
                logFile.createNewFile();
            }
            else
                array = new JSONArray(readFile( nmsFolder+ "anomaly.json"));

                array.put(dataObject);


            writeToFile(nmsFolder + "anomaly.json", array.toString(), false);

        } catch (JSONException var14) {
            Sender.error("JSON Exception Occurred", var14);
            var14.printStackTrace();
        }

    }

    static {
        qSocket = new Socket[Functions.QUORUM_COUNT];
        qOut = new PrintStream[Functions.QUORUM_COUNT];
        qIn = new BufferedReader[Functions.QUORUM_COUNT];
        quorumID = new String[Functions.QUORUM_COUNT];
    }
}
