package com.rubix.WAMPAC.NMSCollection;
/**
 * The code is automation of USB detection and Removal
 * Runs in loop continuously
 * Pushes output to console
 */

import org.json.JSONArray;
import org.json.JSONObject;
import oshi.SystemInfo;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.hardware.UsbDevice;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public final class DetectUSB {

    private static List<String> devicesToDetect = new ArrayList<>();
    private static boolean anyDeviceFound = false;
    private static JSONArray resultArray = new JSONArray(  );

    public static JSONArray usbDetectMain () throws Throwable {
        devicesToDetect.clear();
        resultArray = new JSONArray(  );
        devicesToDetect.add("USB Mass Storage Device");
        SystemInfo si = new SystemInfo();
        HardwareAbstractionLayer hal = si.getHardware();
        List<UsbDevice> USBData;
        USBData = hal.getUsbDevices( true );
        anyDeviceFound = false;
        for( UsbDevice dev : USBData) {
            usbDetect( dev, false );
        }
        if (!anyDeviceFound) {
            JSONArray resultArray = new JSONArray(  );
            JSONObject object = new JSONObject(  );
            object.put( "time" , LocalDateTime.now() );
            object.put( "detected", "no" );
            object.put( "devicename", "" );
           resultArray.put( object );
        }
        return resultArray;
    }

    private static void usbDetect (UsbDevice usbDev, boolean devDetected) throws Throwable {

        boolean devDetectedLoc = false;
        if (devicesToDetect.contains( usbDev.getName() )) {
            devDetectedLoc = true;
        }
        if (devDetected) {
            anyDeviceFound = true;
           usbDisconnect( usbDev );
        } else {
            if (!usbDev.getConnectedDevices().toString().equals( "[]" )) {
                for (UsbDevice dev : usbDev.getConnectedDevices()) {
                    usbDetect( dev, devDetectedLoc );
                }
            }
        }
    }

    private static String usbDisconnect (UsbDevice usbDev) throws Throwable {

        JSONObject object = new JSONObject(  );
        object.put( "time" , LocalDateTime.now() );
        object.put( "detected", "yes" );
        object.put( "devicename", usbDev.getName() );
        resultArray.put( object );

        //outp.add( LocalDateTime.now() + " [Detected Device [" + usbDev.getName() + "]]");
//        System.out.println(LocalDateTime.now() + " [Disconnecting [" + usbDev.getName() + "]]");
//        //logic to disconnect
//        if(true) {
//            System.out.println( LocalDateTime.now() + " [Disconnected [" + usbDev.getName() + "]]" );
//        }
//        else {
//            System.out.println( LocalDateTime.now() + " [Could not disconnect [" + usbDev.getName() + "]]" );
//        }
        return resultArray.toString();
    }
}
