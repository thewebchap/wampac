package com.rubix.WAMPAC.Controller;


import com.rubix.WAMPAC.NMSCollection.DescriptorThread;
import com.rubix.WAMPAC.NMSCollection.DiskThread;
import com.rubix.WAMPAC.NMSCollection.FileSystemThread;
import com.rubix.WAMPAC.NMSCollection.MemoryThread;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;

import static com.rubix.Resources.Functions.pathSet;
import static com.rubix.Resources.Functions.readFile;
import static com.rubix.WAMPAC.NMSCollection.DetectUSB.usbDetectMain;
import static com.rubix.WAMPAC.NMSCollection.NetworkInfo.networkInfo;
import static com.rubix.WAMPAC.NMSCollection.Paths.*;
import static com.rubix.WAMPAC.NMSCollection.PowerInfo.powerInfo;
import static com.rubix.WAMPAC.NMSCollection.ProcessInfo.processInfo;
import static com.rubix.WAMPAC.NMSCollection.Sessions.sessionInfo;
import static com.rubix.WAMPAC.NMSCollection.TCPandUDPStats.tcpudpStats;
import static com.rubix.WAMPAC.NMSCollection.Functions.*;


@CrossOrigin(origins = "http://localhost:1898")
@RestController
public class NMSCollection {
    public static boolean nmsStart = false;
    @RequestMapping(value = "/start", method = RequestMethod.GET,
            produces = {"application/json", "application/xml"})
    public static String start() throws JSONException {

            nmsStart = true;
            pathSet();
            DiskThread diskThread = new DiskThread();
            Thread disk = new Thread(diskThread);
            disk.start();

            MemoryThread memoryThread = new MemoryThread();
            Thread memory = new Thread(memoryThread);
            memory.start();

            DescriptorThread descriptorThread = new DescriptorThread();
            Thread descriptor = new Thread(descriptorThread);
            descriptor.start();

            FileSystemThread fileSystemThread = new FileSystemThread();
            Thread fileSystem = new Thread(fileSystemThread);
            fileSystem.start();

            JSONObject result = new JSONObject();
            JSONObject contentObject = new JSONObject();
            contentObject.put("message", "Setup Complete");
            result.put("data", contentObject);
            result.put("message", "");
            result.put("status", "true");
            return result.toString();

    }

    @RequestMapping(value = "/USBLogging", method = RequestMethod.GET,
            produces = {"application/json", "application/xml"})
    public static String USBLogging() throws Throwable {
        JSONObject result = new JSONObject();
        JSONObject contentObject = new JSONObject();
        contentObject.put("payload", usbDetectMain());
        result.put("data", contentObject);
        result.put("message", "");
        result.put("status", "true");
        return result.toString();
    }


    @RequestMapping(value = "/Memory", method = RequestMethod.GET,
            produces = {"application/json", "application/xml"})
    public static String Memory() throws JSONException {
        JSONObject result = new JSONObject();
        JSONObject contentObject = new JSONObject();
        File file = new File(MemoryFolder + MemoryFile);
        if(file.exists()) {
            String memoryFile = readFile(MemoryFolder + MemoryFile);
            JSONArray diskArray = new JSONArray(memoryFile);

            JSONObject resultObject = new JSONObject();
            JSONArray availableMemoryArray = new JSONArray();
            JSONArray timeArray = new JSONArray();
            JSONArray usedVirtualMemoryArray = new JSONArray();
            int len = diskArray.length() - 1;
            System.out.println(len);
            if (len < 30) {
                int j = 0;
                while (j < len) {
                    JSONObject object = diskArray.getJSONObject(len - j);
                    availableMemoryArray.put(object.getLong("availableMem"));
                    timeArray.put(object.getString("time"));
                    usedVirtualMemoryArray.put(object.getLong("usedVirtualMem"));
                    j++;
                }
            } else {
                int i = 0;
                while (i < 30) {
                    JSONObject object = diskArray.getJSONObject(len - i);
                    availableMemoryArray.put(object.getLong("availableMem"));
                    timeArray.put(object.getString("time"));
                    usedVirtualMemoryArray.put(object.getLong("usedVirtualMem"));
                    i++;
                }
            }
            resultObject.put("totalMemory", diskArray.getJSONObject(len).getLong("totalMemory"));
            resultObject.put("totalVirtualMemory", diskArray.getJSONObject(len).getLong("totalVirtualMemory"));
            resultObject.put("availableMem", availableMemoryArray);
            resultObject.put("time", timeArray);
            resultObject.put("usedVirtualMem", usedVirtualMemoryArray);

            contentObject.put("payload", resultObject);
        }

        else contentObject.put("payload", new JSONObject());


        result.put("data", contentObject);
        result.put("message", "");
        result.put("status", "true");
        return result.toString();


    }

    @RequestMapping(value = "/PowerInfo", method = RequestMethod.GET,
            produces = {"application/json", "application/xml"})
    public static String PowerInfo() throws JSONException {
        JSONObject result = new JSONObject();
        JSONObject contentObject = new JSONObject();
        contentObject.put("payload", powerInfo());
        result.put("data", contentObject);
        result.put("message", "");
        result.put("status", "true");
        return result.toString();
    }

    @RequestMapping(value = "/SessionsInfo", method = RequestMethod.GET,
            produces = {"application/json", "application/xml"})
    public static String SessionsInfo() throws JSONException {
        JSONObject result = new JSONObject();
        JSONObject contentObject = new JSONObject();
        contentObject.put("payload", sessionInfo());
        result.put("data", contentObject);
        result.put("message", "");
        result.put("status", "true");
        return result.toString();
    }

    @RequestMapping(value = "/FileDescriptor", method = RequestMethod.GET,
            produces = {"application/json", "application/xml"})
    public static String FileDescriptor() throws JSONException, IOException, InterruptedException {

        JSONObject result = new JSONObject();
        JSONObject contentObject = new JSONObject();
        File file = new File(DescriptorFolder + DescriptorFile);
        if(file.exists()) {
            String descriptorFile = readFile(DescriptorFolder + DescriptorFile);
            JSONArray diskArray = new JSONArray(descriptorFile);

            JSONObject resultObject = new JSONObject();
            JSONArray openDescriptorsArray = new JSONArray();
            JSONArray timeArray = new JSONArray();
            JSONArray MaxDescriptorsArray = new JSONArray();
            int len = diskArray.length() - 1;
            System.out.println(len);
            if (len < 30) {
                int j = 0;
                while (j < len) {
                    JSONObject object = diskArray.getJSONObject(len - j);
                    openDescriptorsArray.put(object.getLong("openDescriptors"));
                    timeArray.put(object.getString("time"));
                    MaxDescriptorsArray.put(object.getLong("MaxDescriptors"));
                    j++;
                }
            } else {
                int i = 0;
                while (i < 30) {
                    JSONObject object = diskArray.getJSONObject(len - i);
                    openDescriptorsArray.put(object.getLong("openDescriptors"));
                    timeArray.put(object.getString("time"));
                    MaxDescriptorsArray.put(object.getLong("MaxDescriptors"));
                    i++;
                }
            }
            resultObject.put("openDescriptors", openDescriptorsArray);
            resultObject.put("time", timeArray);
            resultObject.put("MaxDescriptors", MaxDescriptorsArray);

            contentObject.put("payload", resultObject);
        }else
            contentObject.put("payload", new JSONObject());
        result.put("data", contentObject);
        result.put("message", "");
        result.put("status", "true");
        return result.toString();

    }

    @RequestMapping(value = "/FileSystem", method = RequestMethod.GET,
            produces = {"application/json", "application/xml"})
    public static String FileSystem() throws JSONException, IOException, InterruptedException {

        JSONObject result = new JSONObject();
        JSONObject contentObject = new JSONObject();
        File file = new File(FileSystemFolder + FileSystemFile);
        if(file.exists()) {
            String fileSystemFile = readFile(FileSystemFolder + FileSystemFile);
            JSONArray diskArray = new JSONArray(fileSystemFile);

            JSONObject resultObject = new JSONObject();
            JSONArray freeSpaceArray = new JSONArray();
            JSONArray usableSpaceArray = new JSONArray();
            JSONArray totalSpaceArray = new JSONArray();
            int len = diskArray.length() - 1;
            if (len < 30) {
                int j = 0;
                while (j < len) {
                    JSONObject object = diskArray.getJSONObject(len - j);
                    freeSpaceArray.put(object.getLong("freeSpace"));
                    usableSpaceArray.put(object.getLong("usableSpace"));
                    totalSpaceArray.put(object.getLong("totalSpace"));
                    j++;
                }
            } else {
                int i = 0;
                while (i < 30) {
                    JSONObject object = diskArray.getJSONObject(len - i);
                    freeSpaceArray.put(object.getLong("freeSpace"));
                    usableSpaceArray.put(object.getLong("usableSpace"));
                    totalSpaceArray.put(object.getLong("totalSpace"));
                    i++;
                }
            }
            resultObject.put("fileStoreName", diskArray.getJSONObject(len).getString("fileStoreName"));
            resultObject.put("freeSpace", freeSpaceArray);
            resultObject.put("usableSpace", usableSpaceArray);
            resultObject.put("totalSpace", totalSpaceArray);


            contentObject.put("payload", resultObject);
        }else
            contentObject.put("payload", new JSONObject());
        result.put("data", contentObject);
        result.put("message", "");
        result.put("status", "true");
        return result.toString();
    }

    @RequestMapping(value = "/DiskStorage", method = RequestMethod.GET,
            produces = {"application/json", "application/xml"})
    public static String DiskStorage() throws JSONException, IOException, InterruptedException {

        JSONObject result = new JSONObject();
        JSONObject contentObject = new JSONObject();
        File file = new File(DiskFolder + DiskFile);
        if(file.exists()) {
            String diskFile = readFile(DiskFolder + DiskFile);
            JSONArray diskArray = new JSONArray(diskFile);

            JSONObject resultObject = new JSONObject();
            JSONArray writeBytesArray = new JSONArray();
            JSONArray readBytesArray = new JSONArray();
            JSONArray timeArray = new JSONArray();
            int len = diskArray.length() - 1;
            if (len < 30) {
                int j = 0;
                while (j < len) {
                    JSONObject object = diskArray.getJSONObject(len - j);
                    writeBytesArray.put(object.getLong("writeBytes"));
                    readBytesArray.put(object.getLong("readBytes"));
                    timeArray.put(object.getString("time"));
                    j++;
                }
            } else {
                int i = 0;
                while (i < 30) {
                    JSONObject object = diskArray.getJSONObject(len - i);
                    writeBytesArray.put(object.getLong("writeBytes"));
                    readBytesArray.put(object.getLong("readBytes"));
                    timeArray.put(object.getString("time"));
                    i++;
                }
            }
            resultObject.put("diskName", diskArray.getJSONObject(len).getString("diskName"));
            resultObject.put("size", diskArray.getJSONObject(len).getLong("size"));
            resultObject.put("writeBytes", writeBytesArray);
            resultObject.put("readBytes", readBytesArray);
            resultObject.put("time", timeArray);

            contentObject.put("payload", resultObject);
        }
        else
            contentObject.put("payload", new JSONObject());
        result.put("data", contentObject);
        result.put("message", "");
        result.put("status", "true");
        return result.toString();

    }

    @RequestMapping(value = "/TCPUDPStats", method = RequestMethod.GET,
            produces = {"application/json", "application/xml"})
    public static String TCPUDPStats() throws JSONException {

        JSONObject result = new JSONObject();
        JSONObject contentObject = new JSONObject();
        contentObject.put("payload", tcpudpStats());
        result.put("data", contentObject);
        result.put("message", "");
        result.put("status", "true");
        return result.toString();
    }

    @RequestMapping(value = "/NetworkInfo", method = RequestMethod.GET,
            produces = {"application/json", "application/xml"})
    public static String NetworkInfo() throws SocketException, UnknownHostException, JSONException {
        JSONObject result = new JSONObject();
        JSONObject contentObject = new JSONObject();
        contentObject.put("payload", networkInfo());
        result.put("data", contentObject);
        result.put("message", "");
        result.put("status", "true");
        return result.toString();
    }

    @RequestMapping(value = "/ProcessInfo", method = RequestMethod.GET,
            produces = {"application/json", "application/xml"})
    public static String ProcessInfo() throws JSONException {

        JSONObject result = new JSONObject();
        JSONObject contentObject = new JSONObject();
        contentObject.put("payload", processInfo());
        result.put("data", contentObject);
        result.put("message", "");
        result.put("status", "true");
        return result.toString();
    }

    @RequestMapping(value = "/InstalledApps", method = RequestMethod.GET,
            produces = {"application/json", "application/xml"})
    public static String InstalledApps() throws JSONException, IOException {

        File file = new File(CollectionFolder + "InstalledSoft.csv");
        if(!file.exists())
            Runtime.getRuntime().exec( "cmd /c start cmd.exe /c \"wmic product get name,vendor /format:csv >"+CollectionFolder+"InstalledSoft.csv\" " );

        JSONObject result = new JSONObject();
        JSONObject contentObject = new JSONObject();
        contentObject.put("payload", readDataFromCSV(CollectionFolder + "InstalledSoft.csv"));
        result.put("data", contentObject);
        result.put("message", "");
        result.put("status", "true");
        return result.toString();
    }


}
