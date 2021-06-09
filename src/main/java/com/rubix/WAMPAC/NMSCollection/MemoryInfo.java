package com.rubix.WAMPAC.NMSCollection;
/**
 * The code is automation Memory Info in the System
 * Runs every 6 minutes
 * Pushes output to console
 */

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import oshi.SystemInfo;
import oshi.hardware.HardwareAbstractionLayer;

import java.io.IOException;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.LocalDateTime;

import static com.rubix.Resources.Functions.readFile;
import static com.rubix.Resources.Functions.writeToFile;
import static com.rubix.WAMPAC.NMSCollection.Paths.MemoryFile;
import static com.rubix.WAMPAC.NMSCollection.Paths.MemoryFolder;


public class MemoryInfo {
    private static final DecimalFormat df2 = new DecimalFormat("#.##");


    public static void MemoryMain() throws JSONException, IOException {
        String readFile = readFile(MemoryFolder + MemoryFile);
        JSONArray array = new JSONArray(readFile);
        array.put(memoryTest());
        writeToFile(MemoryFolder + MemoryFile, array.toString(), false);
    }

    public static JSONObject memoryTest() throws JSONException {
        JSONObject object = new JSONObject();
        SystemInfo si = new SystemInfo();
        HardwareAbstractionLayer hal = si.getHardware();
        df2.setRoundingMode(RoundingMode.UP);
        long totalMem = hal.getMemory().getTotal();
        long maxvm = hal.getMemory().getVirtualMemory().getVirtualMax();
        //threshold for Memory and Virtual Memory
        double twentyPTotal = 0.10 * totalMem;
        double twentypvm = 0.10 * maxvm;
        object.put("time", LocalDateTime.now());
        long availableMem = hal.getMemory().getAvailable();
        object.put("availableMem", availableMem);
        object.put("totalMemory", si.getHardware().getMemory().getTotal());
        long availableVM = hal.getMemory().getVirtualMemory().getVirtualInUse();
        object.put("usedVirtualMem", maxvm - availableVM);
        object.put("totalVirtualMemory", si.getHardware().getMemory().getVirtualMemory().getVirtualMax());
        //checking if Memory and Virtual memory is less
        if (availableMem < twentyPTotal) {
            object.put("Message", "Available memory is less than 10%");
        }
        if (availableVM < twentypvm) {
            object.put("Message", "Available virtual memory is less than 10%");
        }
        return object;
    }
}

