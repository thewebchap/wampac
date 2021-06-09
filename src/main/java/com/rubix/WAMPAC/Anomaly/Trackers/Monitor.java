package com.rubix.WAMPAC.Anomaly.Trackers;


import static com.rubix.WAMPAC.Anomaly.Resources.Functions.executeShell;

public class Monitor {
    public static void checkICMP(){
        System.out.println("Monitoring checkICMP");
        Boolean response = true;
        while(response)
            response = (executeShell("Get-NetFirewallRule -DisplayName \"ICMP Allow incoming V4 echo request\" -erroraction 'silentlycontinue' | Select-Object Enabled").contains("True"))?true:false;
        System.out.println("Triggered checkICMP");
    }

    public static void checkWIFI(){
        System.out.println("Monitoring checkWIFI");
        Boolean response = true;
        while(response)
            response = (executeShell("Get-NetAdapter -Name \"Wi-Fi\" -erroraction 'silentlycontinue' | Select Status").contains("Up"))?false:true;
        System.out.println("Triggered checkWIFI");
    }

    public static void checkUSB(){
        System.out.println("Monitoring checkUSB");
        Boolean response = true;
        while(response)
            response = (executeShell("Get-ItemProperty  \"HKLM:\\SYSTEM\\CurrentControlSet\\services\\USBSTOR\" -name start -erroraction 'silentlycontinue' | Select-Object Start").contains("3"))?false:true;
        System.out.println("Triggered checkUSB");
    }

    public static void checkLocalAccount(){
        System.out.println("Monitoring checkLocalAccount");
        Boolean response = true;
        while(response)
            response = (executeShell("net localgroup administrators | select -skip 6 -erroraction 'silentlycontinue' | ? {$_ -and $_ -notmatch 'successfully|^administrator|^msp.localadmin|^xyz.devadmin$'} ").isEmpty())?true:false;
        System.out.println("Triggered checkLocalAccount");
    }

    public static void checkExeCert(){
        System.out.println("Monitoring checkExeCert");
        Boolean response = true;
        while(response)
            response = (executeShell("Get-ChildItem -Path D:\\ -Filter \"*.exe\" ").isEmpty())?true:false;
        System.out.println("Triggered checkExeCert");
    }

}
