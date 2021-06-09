package com.rubix.WAMPAC.Anomaly.Trackers;


import com.rubix.WAMPAC.Anomaly.Communication.Sender;
import org.json.JSONException;

import java.io.IOException;

import static com.rubix.WAMPAC.Anomaly.Resources.Functions.executeShell;
import static com.rubix.WAMPAC.Anomaly.Trackers.Monitor.checkICMP;

public class ICMP implements Runnable {

    @Override
    public void run() {
        try {
            executeShell("\n" +
                    "\n" +
                    "netsh advfirewall firewall add rule name=\"ICMP Allow incoming V4 echo request\" protocol=icmpv4:8,any dir=in action=allow");
            while (true) {
                checkICMP();
                executeShell("Enable-NetFirewallRule -DisplayName \"ICMP Allow incoming V4 echo request\"");
                Sender.start("ICMP", 15060);
            }
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
    }
}
