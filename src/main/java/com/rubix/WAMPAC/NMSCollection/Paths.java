package com.rubix.WAMPAC.NMSCollection;

import org.json.JSONArray;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;

import static com.rubix.Resources.Functions.*;

public class Paths {

    public static String CollectionFolder = getCollectionFolderPath();

    private static String getCollectionFolderPath() {
        if(getOsName().contains("Linux"))
            return "/home/" + getSystemUser() + "/Rubix/Collection/";
        else if(getOsName().contains("Mac"))
            return "/Applications/Rubix/Collection/";
        else if(getOsName().contains("Windows"))
            return "C:\\Rubix\\Collection\\";
        return null;
    }

    public static String MemoryFolder = getMemoryFolderPath();

    private static String getMemoryFolderPath() {
        if(getOsName().contains("Linux"))
            return "/home/" + getSystemUser() + "/Rubix/Collection/Memory/";
        else if(getOsName().contains("Mac"))
            return "/Applications/Rubix/Collection/Memory/";
        else if(getOsName().contains("Windows"))
            return "C:\\Rubix\\Collection\\Memory\\";
        return null;
    }

    public static String DiskFolder = getDiskFolderPath();

    private static String getDiskFolderPath() {
        if(getOsName().contains("Linux"))
            return "/home/" + getSystemUser() + "/Rubix/Collection/DiskStorage/";
        else if(getOsName().contains("Mac"))
            return "/Applications/Rubix/Collection/DiskStorage/";
        else if(getOsName().contains("Windows"))
            return "C:\\Rubix\\Collection\\DiskStorage\\";
        return null;
    }

    public static String DescriptorFolder = getDescriptorFolderPath();

    private static String getDescriptorFolderPath() {
        if(getOsName().contains("Linux"))
            return "/home/" + getSystemUser() + "/Rubix/Collection/Descriptors/";
        else if(getOsName().contains("Mac"))
            return "/Applications/Rubix/Collection/Descriptors/";
        else if(getOsName().contains("Windows"))
            return "C:\\Rubix\\Collection\\Descriptors\\";
        return null;
    }

    public static String FileSystemFolder = getFileSystemFolderPath();

    private static String getFileSystemFolderPath() {
        if(getOsName().contains("Linux"))
            return "/home/" + getSystemUser() + "/Rubix/Collection/FileSystem/";
        else if(getOsName().contains("Mac"))
            return "/Applications/Rubix/Collection/FileSystem/";
        else if(getOsName().contains("Windows"))
            return "C:\\Rubix\\Collection\\FileSystem\\";
        return null;
    }

    public static String MemoryFile = getMemoryFilePath();

    private static String getMemoryFilePath() {
        String day, month, year;

        day = String.format("%2d", LocalDateTime.now().getDayOfMonth()).replace(' ', '0');
        month = String.format("%2d", LocalDateTime.now().getMonthValue()).replace(' ', '0');
        year = String.valueOf(LocalDateTime.now().getYear());
       return day.concat(month).concat(year).concat("_").concat("Memory").concat(".json");
    }

    public static String DiskFile = getDiskFilePath();

    private static String getDiskFilePath() {
        String day, month, year;

        day = String.format("%2d", LocalDateTime.now().getDayOfMonth()).replace(' ', '0');
        month = String.format("%2d", LocalDateTime.now().getMonthValue()).replace(' ', '0');
        year = String.valueOf(LocalDateTime.now().getYear());
        return day.concat(month).concat(year).concat("_").concat("DiskStorage").concat(".json");
    }

    public static String DescriptorFile = getDescriptorFilePath();

    private static String getDescriptorFilePath() {
        String day, month, year;

        day = String.format("%2d", LocalDateTime.now().getDayOfMonth()).replace(' ', '0');
        month = String.format("%2d", LocalDateTime.now().getMonthValue()).replace(' ', '0');
        year = String.valueOf(LocalDateTime.now().getYear());
        return day.concat(month).concat(year).concat("_").concat("Descriptor").concat(".json");
    }

    public static String FileSystemFile = getFileSystemFilePath();

    private static String getFileSystemFilePath() {
        String day, month, year;

        day = String.format("%2d", LocalDateTime.now().getDayOfMonth()).replace(' ', '0');
        month = String.format("%2d", LocalDateTime.now().getMonthValue()).replace(' ', '0');
        year = String.valueOf(LocalDateTime.now().getYear());
        return day.concat(month).concat(year).concat("_").concat("FileSystem").concat(".json");
    }

    public static void createCollectionFolder() throws IOException {
        File collectionFolder = new File(CollectionFolder);
        if(!collectionFolder.exists())
            collectionFolder.mkdir();


        File memoryFolder = new File(MemoryFolder);
        if(!memoryFolder.exists())
            memoryFolder.mkdir();

        File diskFolder = new File(DiskFolder);
        if(!diskFolder.exists())
            diskFolder.mkdir();

        File descriptorFolder = new File(DescriptorFolder);
        if(!descriptorFolder.exists())
            descriptorFolder.mkdir();

        File fileSystemFolder = new File(FileSystemFolder);
        if(!fileSystemFolder.exists())
            fileSystemFolder.mkdir();

        File memoryFile = new File(MemoryFolder + MemoryFile);
        if(!memoryFile.exists()) {
            memoryFile.createNewFile();
            writeToFile(MemoryFolder + MemoryFile, "[]", false);
        }

        File diskFile = new File(DiskFolder + DiskFile);
        if(!diskFile.exists()) {
            diskFile.createNewFile();
            writeToFile(DiskFolder + DiskFile, "[]", false);
        }

        File descriptorFile = new File(DescriptorFolder + DescriptorFile);
        if(!descriptorFile.exists()) {
            descriptorFile.createNewFile();
            writeToFile(DescriptorFolder + DescriptorFile, "[]", false);
        }

        File fileSystemFile = new File(FileSystemFolder + FileSystemFile);
        if(!fileSystemFile.exists()) {
            fileSystemFile.createNewFile();
            writeToFile(FileSystemFolder + FileSystemFile, "[]", false);
        }




    }
}
