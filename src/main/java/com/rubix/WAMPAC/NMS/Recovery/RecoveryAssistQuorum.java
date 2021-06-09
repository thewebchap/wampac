package com.rubix.WAMPAC.NMS.Recovery;

import com.rubix.Resources.Functions;
import com.rubix.Resources.IPFSNetwork;
import io.ipfs.api.IPFS;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;


import static com.rubix.Resources.Functions.*;
import static com.rubix.WAMPAC.NMS.Constants.PathConstants.nmsFolder;

public class RecoveryAssistQuorum implements Runnable {
    static int PORT = 15050;
    public static Logger RecoveryAssistQuorum = Logger.getLogger(RecoveryAssistQuorum.class);

    @Override
    public void run() {
        while (true){
            PropertyConfigurator.configure(Functions.LOGGER_PATH + "log4jWallet.properties");
            String shareIPFS = null;
            ServerSocket serverSocket = null;
            Socket socket = null;
            BufferedReader in = null;
            PrintStream out= null;
            Boolean flag = false;



            try {
                RecoveryAssistQuorum.debug("Quorum Listening on " + PORT);
                serverSocket = new ServerSocket(PORT);
                socket = serverSocket.accept();
                System.out.println("Accepted");
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintStream(socket.getOutputStream());
                String json = in.readLine();
                JSONObject temp = new JSONObject(json);
                String tid = temp.getString("tid");
                String log = readFile(nmsFolder + temp.getString("did") + "\\log.json");
                JSONArray records = new JSONArray(log);
                for (int i = (records.length()-1); i >= 0; i--) {
                    JSONObject record = records.getJSONObject(i);
                    if(record.getString("tid").equals(tid)){
                        shareIPFS = record.getString("share");
                        flag = true;
                        break;
                    }
                }
                if(flag){
                    IPFS ipfs = new IPFS("/ip4/127.0.0.1/tcp/" + IPFS_PORT);
                    String share = IPFSNetwork.get(shareIPFS,ipfs);
                    out.println(share);
                }else
                    out.println("Not_Found");

                in.close();
                out.close();
                socket.close();
                serverSocket.close();

            }
            catch (BindException e)
            {
                try {
                    in.close();
                    out.close();
                    socket.close();
                    serverSocket.close();
                    e.printStackTrace();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
            catch (IOException e) {
                try {
                    in.close();
                    out.close();
                    socket.close();
                    serverSocket.close();
                    e.printStackTrace();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }

            } catch (JSONException e) {
                try {
                    in.close();
                    out.close();
                    socket.close();
                    serverSocket.close();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
                e.printStackTrace();
            }

        }
    }

//    public static void main(String[] args) {
//        pathSet();
//        RecoveryAssistQuorum q1 = new RecoveryAssistQuorum();
//        q1.run();
//    }
}
