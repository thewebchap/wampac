package com.rubix.WAMPAC.Anomaly.Communication;

import com.rubix.Resources.Functions;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;

import static com.rubix.Resources.Functions.pathSet;
import static com.rubix.Resources.Functions.readFile;
import static com.rubix.WAMPAC.NMS.Constants.PathConstants.nmsFolder;

public class Receiver implements Runnable {
    static int PORT = 15060;
    public static Logger Quorum = Logger.getLogger(Receiver.class);

    @Override
    public void run() {
        while (true){
            PropertyConfigurator.configure(Functions.LOGGER_PATH + "log4jWallet.properties");
            ServerSocket serverSocket = null;
            Socket socket = null;
            BufferedReader in = null;
            PrintStream out= null;
            JSONArray array = new JSONArray();


            try {
                Quorum.debug("Quorum Listening on " + PORT);
                serverSocket = new ServerSocket(PORT);
                socket = serverSocket.accept();
                System.out.println("Accepted");
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintStream(socket.getOutputStream());
                String json = in.readLine();
                JSONObject temp = new JSONObject(json);
                String did = temp.getString("did");
                temp.remove("did");
                File logFile = new File(nmsFolder+did);
                if(!logFile.exists()){
                    array = new JSONArray();
                    logFile.mkdirs();
                }else {
                    File anomalyFile = new File(logFile + "\\anomaly.json");
                    if(anomalyFile.exists()) {
                        array = new JSONArray(readFile(logFile + "\\anomaly.json"));
                    }else{
                        anomalyFile.createNewFile();
                    }
                }

                array.put(temp);

                Functions.writeToFile(logFile+"\\anomaly.json",array.toString(),false);

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


}
