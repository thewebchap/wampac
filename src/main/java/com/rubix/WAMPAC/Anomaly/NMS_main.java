package com.rubix.WAMPAC.Anomaly;

import com.rubix.WAMPAC.Anomaly.Communication.Sender;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.io.IOException;

import static com.rubix.Resources.Functions.pathSet;
import static com.rubix.WAMPAC.Anomaly.Resources.Functions.executeShell;

public class NMS_main implements Runnable{
    @Override
    public void run() {
        try {
            pathSet();
            executeShell("\n" +
                    "\n" +
                    "netsh advfirewall firewall add rule name=\"ICMP Allow incoming V4 echo request\" protocol=icmpv4:8,any dir=in action=allow");

            while (true) {
                Boolean response = (executeShell("Get-NetFirewallRule -DisplayName \"ICMP Allow incoming V4 echo request\" -erroraction 'silentlycontinue' | Select-Object Enabled").contains("True")) ? true : false;
                if (!response) {
                    System.out.println("Triggered checkICMP");
                    executeShell("Enable-NetFirewallRule -DisplayName \"ICMP Allow incoming V4 echo request\"");

                    Sender.start("ICMP", 15060);

                }
                response = (executeShell("Get-NetAdapter -Name \"Wi-Fi\" -erroraction 'silentlycontinue' | Select Status").contains("Up")) ? false : true;
                if (!response) {
                    System.out.println("Triggered checkWIFI");
                    executeShell("Disable-NetAdapter -Name \"Wi-Fi\" -Confirm:$false");
                    Sender.start("WIFI", 15060);
                }
                response = (executeShell("Get-ItemProperty  \"HKLM:\\SYSTEM\\CurrentControlSet\\services\\USBSTOR\" -name start -erroraction 'silentlycontinue' | Select-Object Start").contains("3")) ? false : true;
                if (!response) {
                    System.out.println("Triggered checkUSB");
                    executeShell("Set-ItemProperty -Path \"HKLM:\\SYSTEM\\CurrentControlSet\\Services\\USBSTOR\\\" -Name \"start\" -Value 4");
                    Sender.start("USB", 15060);
                }
                response = (executeShell("Get-ChildItem -Path D:\\ -Filter \"*.exe\" ").isEmpty()) ? true : false;
                if (!response) {
                    System.out.println("Triggered checkExeCert");
                    JSONArray records = new JSONArray(executeShell("Get-ChildItem -Path D:\\ -Filter \"*.exe\" -Recurse  | select Directory,Name | ConvertTo-Json"));
                    for (int i = 0; i < records.length(); i++) {
                        JSONObject record = records.getJSONObject(i);
                        JSONObject directory = record.getJSONObject("Directory");
                        String dir = directory.getString("FullName");
                        String filename = record.getString("Name");
                        executeShell("Remove-Item \"" + dir + "\\" + filename + "\"");
                    }

                    Sender.start("Uncertified EXE", 15060);
                }


            }
//        ICMP icmp = new ICMP();
//        WIFI wifi = new WIFI();
//        USB usb = new USB();
//        PrivilageDrop privilageDrop = new PrivilageDrop();
//        NonCertExePurge nonCertExePurge = new NonCertExePurge();
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
    }
}
