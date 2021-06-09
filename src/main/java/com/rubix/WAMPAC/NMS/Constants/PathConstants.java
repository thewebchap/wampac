package com.rubix.WAMPAC.NMS.Constants;

import static com.rubix.Resources.Functions.getOsName;
import static com.rubix.Resources.Functions.getSystemUser;

public class PathConstants {

    public static String RecoveryFolder = getRecoverPath();

    private static String getRecoverPath() {
        if(getOsName().contains("Linux"))
            return "/home/" + getSystemUser() + "/Rubix/NMS/Recovered/";
        else if(getOsName().contains("Mac"))
            return "/Applications/Rubix/NMS/Recovered/";
        else if(getOsName().contains("Windows"))
            return "C:\\Rubix\\NMS\\Recovered\\";

        return null;
    }

    public static String backupFolder = getBackupPath();

    private static String getBackupPath() {
        if(getOsName().contains("Linux"))
            return "/home/" + getSystemUser() + "/Rubix/NMS/Backup/";
        else if(getOsName().contains("Mac"))
            return "/Applications/Rubix/NMS/Backup/";
        else if(getOsName().contains("Windows"))
            return "C:\\Rubix\\NMS\\Backup\\";

        return null;
    }

    public static String logsFolder = getLogsPath();

    private static String getLogsPath() {
        if(getOsName().contains("Linux"))
            return "/home/" + getSystemUser() + "/Rubix/NMS/Logs/";
        else if(getOsName().contains("Mac"))
            return "/Applications/Rubix/NMS/Logs/";
        else if(getOsName().contains("Windows"))
            return "C:\\Rubix\\NMS\\Logs\\";

        return null;
    }


    public static String nmsFolder = getNMSPath();

    private static String getNMSPath() {
        if(getOsName().contains("Linux"))
            return "/home/" + getSystemUser() + "/Rubix/NMS/";
        else if(getOsName().contains("Mac"))
            return "/Applications/Rubix/NMS/";
        else if(getOsName().contains("Windows"))
            return "C:\\Rubix\\NMS\\";

        return null;
    }

}
