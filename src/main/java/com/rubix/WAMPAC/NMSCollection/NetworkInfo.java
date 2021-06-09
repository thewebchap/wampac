package com.rubix.WAMPAC.NMSCollection;

import org.json.JSONException;
import org.json.JSONObject;
import oshi.hardware.platform.windows.WindowsNetworkIF;

import java.net.*;
import java.time.LocalDateTime;
import java.util.Arrays;

public class NetworkInfo {

    private static JSONObject object = new JSONObject(  );

    public static JSONObject networkInfo() throws UnknownHostException, SocketException, JSONException {
        InetAddress ip = Inet4Address.getLocalHost();
        NetworkInterface nifByIp = NetworkInterface.getByInetAddress( ip );
        WindowsNetworkIF windowsNetworkIF = new WindowsNetworkIF( nifByIp );

        object.put("time", LocalDateTime.now() );
        object.put("interface", nifByIp );
        object.put("macAddress", windowsNetworkIF.getMacaddr());
        object.put("ipAddress", ip);
        object.put("displayName", windowsNetworkIF.getDisplayName());
        object.put("packetsReceived", windowsNetworkIF.getPacketsRecv());
        object.put("bytesReceived", windowsNetworkIF.getBytesRecv());
        object.put("packetsSent", windowsNetworkIF.getPacketsSent());
        object.put("bytesSent", windowsNetworkIF.getBytesSent());
        object.put("inputDrops", windowsNetworkIF.getInDrops());
        object.put("inputErrors", windowsNetworkIF.getInErrors());
        object.put("outErrors", windowsNetworkIF.getOutErrors() );
        object.put("speed", windowsNetworkIF.getSpeed());
        object.put("mtu", windowsNetworkIF.getMTU());

        String ipv4Addr = Arrays.toString( windowsNetworkIF.getIPv4addr());
        //triggering SC if any of the below conditions are not met
        if(windowsNetworkIF.queryNetworkInterface() == null)
            object.put( "message", "Interface queried cannot be null" );
        else
            object.put( "message", "" );
        if(ipv4Addr.contains( "127.0.0.1" ))
            object.put( "message", "Interface is localhost, please check network connectivity" );
        else
            object.put( "message", "" );
        if(windowsNetworkIF.getInErrors() >= 5){
            object.put( "message", "More than 5 input errors present" );
            object.put( "value", windowsNetworkIF.getInErrors() );
            //trigger SC to check
        }
        else
            object.put( "message", "" );
        if(windowsNetworkIF.getOutErrors() >= 5)
        {
            object.put( "message", "More than 5 output errors present" );
            object.put( "value", windowsNetworkIF.getOutErrors() );
            //Trigger SC to check
        }
        else
            object.put( "message", "" );
        return object;
    }
}
