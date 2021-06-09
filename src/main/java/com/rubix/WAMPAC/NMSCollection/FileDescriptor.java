package com.rubix.WAMPAC.NMSCollection;
/**
 * The code is automation File Descriptor Info in the System
 * Runs every 6 minutes
 * Pushes output to console
 */

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import oshi.SystemInfo;
import java.io.IOException;
import java.time.LocalDateTime;

import static com.rubix.Resources.Functions.readFile;
import static com.rubix.Resources.Functions.writeToFile;
import static com.rubix.WAMPAC.NMSCollection.Paths.DescriptorFile;
import static com.rubix.WAMPAC.NMSCollection.Paths.DescriptorFolder;

public class FileDescriptor {
    private static JSONObject object = new JSONObject(  );

    public static void DescriptorMain() throws JSONException, IOException {
        String readFile = readFile(DescriptorFolder + DescriptorFile);
        JSONArray array = new JSONArray(readFile);
        array.put(fileDescriptor());
        writeToFile(DescriptorFolder + DescriptorFile, array.toString(), false);

    }

    public static JSONObject fileDescriptor() throws JSONException {

        SystemInfo si = new SystemInfo();
        object.put( "time" , LocalDateTime.now() );
        object.put( "openDescriptors" , si.getOperatingSystem().getFileSystem().getOpenFileDescriptors()  );
        object.put("MaxDescriptors", si.getOperatingSystem().getFileSystem().getMaxFileDescriptors());
        if(si.getOperatingSystem().getFileSystem().getOpenFileDescriptors()==(si.getOperatingSystem().getFileSystem().getMaxFileDescriptors()))
            object.put( "message", "Maximum and open file descriptors are same" );
        else
            object.put( "message", "" );
        return object;
    }
}
