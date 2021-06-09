package com.rubix.WAMPAC.NMSCollection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import oshi.SystemInfo;
import java.time.LocalDateTime;

import static com.rubix.Resources.Functions.readFile;
import static com.rubix.Resources.Functions.writeToFile;
import static com.rubix.WAMPAC.NMSCollection.Paths.DiskFile;
import static com.rubix.WAMPAC.NMSCollection.Paths.DiskFolder;

public class DiskStorage {
    public static void DiskStorageMain() throws JSONException{
        String readFile = readFile(DiskFolder + DiskFile);
        JSONArray array = new JSONArray(readFile);
        array.put(collectDiskStorage());
        writeToFile(DiskFolder + DiskFile, array.toString(), false);

    }

    public static JSONObject collectDiskStorage() throws JSONException {
        JSONObject object = new JSONObject();
        SystemInfo si = new SystemInfo();
        object.put("time", LocalDateTime.now());
        if (si.getHardware().getDiskStores().get( 0 ).getSize() == 0)
            object.put( "message", "Disk storage is 0" );
        else
            object.put( "message", "" );
        object.put("diskName", si.getHardware().getDiskStores().get(0).getName());
        object.put("size", si.getHardware().getDiskStores().get(0).getSize());
        object.put("readBytes", si.getHardware().getDiskStores().get(0).getReadBytes());
        object.put("writeBytes", si.getHardware().getDiskStores().get(0).getWriteBytes());
        return object;
    }

}
