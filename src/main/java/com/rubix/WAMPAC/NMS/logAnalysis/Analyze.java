package com.rubix.WAMPAC.NMS.logAnalysis;

import com.opencsv.CSVReader;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static com.rubix.Resources.Functions.readFile;
import static com.rubix.WAMPAC.NMS.Constants.PathConstants.nmsFolder;

public class Analyze {
    static int error_count=0,warning_count=0,info_rules_count_login =0,info_rules_count_others=0;
    static boolean status=false;
    static Date dateparser(String dateval) throws ParseException {
        String[] values = dateval.split(" ");
        SimpleDateFormat format = new SimpleDateFormat("hh:mm:ss a");
        Date date = format.parse(values[1]+" "+values[2]);
        return date;
    }
    static boolean smackdownAnalyze(String[] column) throws ParseException, JSONException {
        JSONObject rules = new JSONObject(readFile(nmsFolder+"rules.json"));

        String start_date_string = rules.getString("start_date");
        String end_date_string = rules.getString("end_date");
        DateFormat formatter = new SimpleDateFormat("hh:mm:ss a");
        Date start_date = formatter.parse(start_date_string);
        Date end_date=formatter.parse(end_date_string);
        Date parse_date = null;
        switch (column[2]) {
            case "Critical":
                status=true;
                break;
            case "Error":
                if (column[1].equals("521"))
                    status=true;
                else
                error_count+=1;
                if(error_count>rules.getInt("error_threshold"))
                    status=true;
                break;
            case "Warning":
                warning_count+=1;
                if(warning_count>rules.getInt("warning_threshold"))
                    status=true;
                break;
            default:
               switch (column[1]) {
                   case "4624":
                   case "4625":
                   case "4648":
                   case "4720":
                   case "4722":
                   case "4725":
                   case "4726":
                   case "1102":
                           parse_date = dateparser(column[0]);
                       if (!(parse_date.after(start_date) && parse_date.before(end_date)))
                           status = true;
                       break;
                   case "4606":
                       info_rules_count_others+=1;
                       if(info_rules_count_others>rules.getInt("info_others_threshold"))
                           status=true;
                       break;
                   default: break;
               }
               break;
        }
        return status;
    }
    public static boolean rawAnalyze(String did) {
        File logfile = new File(nmsFolder+did+"\\log.json");
        if(!logfile.exists())
            return false;
        int i=0;
        String[] csv_files = {nmsFolder+did+"\\system.csv",nmsFolder+did+"\\security.csv", nmsFolder+did+"\\application.csv"};
        while ((i < csv_files.length && status==false))
        {
            try {
                Reader reader = new FileReader(csv_files[i]);
                List<String[]> rows = new CSVReader(reader).readAll();
                if(!rows.isEmpty())
                    rows.remove(0);
                for (String[] column : rows) {
                    if(smackdownAnalyze(column)) {
                        break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            i++;
        }
        System.out.println(status);
        return status;
    }
}
