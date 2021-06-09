package com.rubix.WAMPAC.NMS.NLSS;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class Functions {

    public static String converttobinary(String strs){
        strs=strs.replaceAll("\\s+","");
        byte[] bytes = strs.getBytes();
        StringBuilder binary = new StringBuilder();
        for (byte b : bytes){
            int val = b;
            for (int i = 0; i < 8; i++){
                binary.append((val & 128) == 0 ? 0 : 1);
                val <<= 1;
            }
            binary.append(' ');
        }
        return binary.toString();
    }

    public static String readFile(String filePath) throws IOException {
        FileReader fileReader = new FileReader(filePath);
        StringBuilder fileContent = new StringBuilder();

        int i;
        while((i = fileReader.read()) != -1) {
            fileContent.append((char)i);
        }

        fileReader.close();
        return fileContent.toString();
    }

    public static void writeToFile(String filePath, String data, Boolean appendStatus) throws IOException {
        File writeFile = new File(filePath);
        FileWriter fw = new FileWriter(writeFile, appendStatus);
        fw.write(data);
        fw.close();
    }


    public static int factor(int x){
        ArrayList arrayList = new ArrayList<Integer>();
        int i,mid;
        for (i = 1; i < x/2; i++) {
            if(x%i==0)
                arrayList.add(i);
        }

        mid = (int) arrayList.get((arrayList.size()/2));
        return mid;
    }

    public static String intToBinary(int a) {
        String temp = Integer.toBinaryString(a);
        while(temp.length() !=8){
            temp = "0"+temp;
        }
        return temp;
    }
    public static String binarytoDec(String bin)
    {
        System.out.println(bin.length());
        StringBuilder result = new StringBuilder();
        int val;
        for(int i = 0; i < bin.length(); i +=8) {
            val = Integer.parseInt(bin.substring(i, i+8), 2);
            result.append(val);
            result.append(' ');
        }
        return result.toString();
    }

}
