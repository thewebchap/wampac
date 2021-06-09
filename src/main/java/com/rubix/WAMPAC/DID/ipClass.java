package com.rubix.WAMPAC.DID;

import java.net.*;
import java.util.Collections;
import java.util.Enumeration;

public class ipClass {
    public static String  getIP() {
        String myIP = "";
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface iface = interfaces.nextElement();
                if (iface.isLoopback() || !iface.isUp() || iface.isVirtual() || iface.isPointToPoint())
                    continue;
                Enumeration<InetAddress> addresses = iface.getInetAddresses();
                while(addresses.hasMoreElements()) {
                    InetAddress address = addresses.nextElement();
                    final String ip = address.getHostAddress();
                    if(Inet4Address.class == address.getClass() && !ip.contains("127.0.0.1"))
                        myIP = ip;
                }
            }
        } catch (SocketException e) {
            myIP = "Error: " + e;
        }
        return  myIP;
    }
}

