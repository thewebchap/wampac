package com.rubix.WAMPAC.NMSCollection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import oshi.SystemInfo;
import oshi.software.os.OSFileStore;
import java.io.IOException;

import static com.rubix.Resources.Functions.readFile;
import static com.rubix.Resources.Functions.writeToFile;
import static com.rubix.WAMPAC.NMSCollection.Paths.FileSystemFile;
import static com.rubix.WAMPAC.NMSCollection.Paths.FileSystemFolder;


public class FileSystem {
    public static void FileSystemMain() throws JSONException, IOException {

        String readFile = readFile(FileSystemFolder + FileSystemFile);
        JSONArray array = new JSONArray(readFile);
        array.put(fileSystem());
        writeToFile(FileSystemFolder + FileSystemFile, array.toString(), false);
    }

    public static JSONObject fileSystem() throws JSONException {
        SystemInfo si = new SystemInfo();
        JSONObject object = new JSONObject();
        oshi.software.os.FileSystem fileSys = si.getOperatingSystem().getFileSystem();
        for (OSFileStore store : fileSys.getFileStores()) {
            object.put("fileStoreName", store.getName());
            object.put("freeSpace", store.getFreeSpace());
            object.put("usableSpace", store.getUsableSpace());
            object.put("totalSpace", store.getTotalSpace());
            if ((store.getFreeSpace() < store.getUsableSpace()) || (store.getFreeSpace() > store.getTotalSpace()) || (store.getUsableSpace() > store.getTotalSpace())) {
                object.put("message", "Error occurred for drive storage");
            }
            else
                object.put("message", "");
        }
        return object;
    }

}
