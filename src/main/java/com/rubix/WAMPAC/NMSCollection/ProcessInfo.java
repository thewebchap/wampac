package com.rubix.WAMPAC.NMSCollection;

import org.json.JSONException;
import org.json.JSONObject;
import oshi.SystemInfo;
import oshi.software.os.OSProcess;

public class ProcessInfo {

    private static JSONObject object = new JSONObject();

    public static JSONObject processInfo() throws JSONException {
        SystemInfo si = new SystemInfo();
        int countingprocessminor = 0;
        int countProc = 0;
        for (OSProcess proc : si.getOperatingSystem().getProcesses()) {
            if (proc.getUserID() == null) {
                object.put( "process", proc.getProcessID() );
                object.put( "user", proc.getUser() );
                //let the SC know this
            } else if (proc.getUser().equals( " " )) {
                object.put( "process", proc.getProcessID() );
                object.put( "user", proc.getUser() );
            } else if (proc.getMajorFaults() != 0) {
                object.put( "process", proc.getProcessID() );
                object.put( "fault", proc.getMajorFaults() );
                //let the SC know this
            } else if (proc.getMinorFaults() >= 100000) {
                countingprocessminor++;
                //let the SC know this
            } else if ((proc.getBitness() != (64)) && (proc.getBitness() != (32)) && (proc.getBitness() != (0))) {
                object.put( "process", proc.getProcessID() );
                object.put( "bitness", proc.getBitness() );
                //let the SC know this
            } else if (proc.getState().equals( "INVALID" )) {
                object.put( "process", proc.getProcessID() );
                object.put( "state", proc.getState() );
                //let the SC know this
            } else if (!(proc.getPriority() >= (-20) && proc.getPriority() <= 128)) {
                object.put( "process", proc.getProcessID() );
                object.put( "priority", proc.getPriority() );
                //let the SC know
            } else if (proc.getCurrentWorkingDirectory() == null) {
                countProc++;
            } else {  }

        }

        object.put( "processCount", si.getOperatingSystem().getProcessCount() );
        //below are the errors to be reported to SC for further analysis - cross check with the learning layer
        if (countingprocessminor >= 0) {
            object.put( "moinorErrors", countingprocessminor );
            object.put( "message", "Processes with minor faults greater than 100000" );
        } else {  }
        if (countProc >= 0) {
            object.put( "nullDirectory", countProc );
            object.put( "message", "Processes with current directory as null" );
        } else { }

        return object;
    }
}
