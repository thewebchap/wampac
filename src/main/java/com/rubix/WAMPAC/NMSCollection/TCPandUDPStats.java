package com.rubix.WAMPAC.NMSCollection;

import org.json.JSONException;
import org.json.JSONObject;
import oshi.software.os.InternetProtocolStats;
import oshi.software.os.windows.WindowsInternetProtocolStats;

public class TCPandUDPStats {
    private static JSONObject object = new JSONObject(  );
    public static JSONObject tcpudpStats() throws JSONException {
        WindowsInternetProtocolStats winISP = new WindowsInternetProtocolStats();
        InternetProtocolStats.TcpStats Tcp = winISP.getTCPv4Stats();
        InternetProtocolStats.UdpStats Udp = winISP.getUDPv4Stats();
        object.put( "tcpInErrors", Tcp.getInErrors() );
        object.put("tcpOutResets", Tcp.getOutResets());
        object.put( "tcpConnectionFailures", Tcp.getConnectionFailures() );
        object.put( "tcpConnectionReset", Tcp.getConnectionsReset() );
        object.put( "tcpActiveConnection", Tcp.getConnectionsActive() );
        object.put( "tcpEstablishedConnection", Tcp.getConnectionsEstablished() );
        object.put( "tcpPassiveConnection", Tcp.getConnectionsPassive() );
        object.put( "tcpSegmentReceived", Tcp.getSegmentsReceived() );
        object.put( "tcpSegmentSent", Tcp.getSegmentsSent() );
        object.put( "tcpSegmentRetransmitted", Tcp.getSegmentsRetransmitted() );

        object.put( "udpDatagramReceived", Udp.getDatagramsReceived()  );
        object.put( "udpDatagramSent", Udp.getDatagramsSent() );
        object.put( "udpDatagramReceivedError", Udp.getDatagramsReceivedErrors() );
        object.put( "udpDatagramNoPort", Udp.getDatagramsNoPort() );

        return object;
    }
}
