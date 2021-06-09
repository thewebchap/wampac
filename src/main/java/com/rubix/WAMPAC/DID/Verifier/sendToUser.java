package com.rubix.WAMPAC.DID.Verifier;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;

import static com.rubix.Resources.Functions.*;

public class sendToUser implements Runnable{
    public static Logger sendVipToUserLogger = Logger.getLogger(sendToUser.class);
    public static ServerSocket serverSocket = null;
    public static Socket socket = null;
    public static BufferedReader in;
    public static PrintStream out;


    @Override
    public void run() {
        while (true) {
            System.out.println("Listening for user data request ...");
            pathSet();
            String result = "";
            PropertyConfigurator.configure(LOGGER_PATH + "log4jWallet.properties");

            try {

                serverSocket = new ServerSocket(8787);
                socket = serverSocket.accept();
                sendVipToUserLogger.debug("Verifier Listening on " + 8787 + " for user's request of Vip list");

                 in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                 out = new PrintStream(socket.getOutputStream());

                String vipFile = readFile(DATA_PATH + "vip.json");
                JSONArray vipArray = new JSONArray(vipFile);

                String dataTableFile = readFile(DATA_PATH + "DataTable.json");
                JSONArray dataTableArray = new JSONArray(dataTableFile);

                JSONObject data = new JSONObject();
                data.put("vip", vipArray);
                data.put("datatable", dataTableArray);
                String incomingRequest;
                sendVipToUserLogger.debug("Waiting for Request from User");
                while ((incomingRequest = in.readLine()) == null) {
                }

                if (incomingRequest.contains("Request")) {
                    out.println(data.toString());

                    while ((incomingRequest = in.readLine()) == null) {
                    }

                    if (incomingRequest.contains("ACK")) {
                        result = "Data Sent";
                        System.out.println(result);
                        sendVipToUserLogger.debug("Details Acknowledged");
                    } else {
                        result = "Data Sent but user not ack";
                        System.out.println(result);
                        sendVipToUserLogger.debug("Details Not Acknowledged");
                    }
                }else {
                    result = "Bad Request";
                    System.out.println(result);
                    sendVipToUserLogger.debug("Details Not Acknowledged");
                }

                serverSocket.close();
                socket.close();
                in.close();
                out.close();

            } catch (IOException e) {
                result = "IOException Occurred" + e;
                System.out.println(result);
                sendVipToUserLogger.error("IOException Occurred", e);
                e.printStackTrace();
                sendVipToUserLogger.debug("Connection Closed");

            } catch (JSONException e) {
                result = "IOException Occurred" + e;
                System.out.println(result);
                sendVipToUserLogger.error("JSONException Occurred", e);
                e.printStackTrace();
                sendVipToUserLogger.debug("Connection Closed");


            } finally {
                try {
                    socket.close();
                    serverSocket.close();
                    sendVipToUserLogger.debug("Connection Closed");
                } catch (IOException e) {

                    result = "IOException Occurred" + e;
                    System.out.println(result);
                    sendVipToUserLogger.error("IOException Occurred", e);
                    e.printStackTrace();
                }
            }
            System.out.println(result);
        }
    }
}
