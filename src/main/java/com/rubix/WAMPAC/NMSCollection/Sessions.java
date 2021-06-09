package com.rubix.WAMPAC.NMSCollection;
/**
 * The code is run to check if its a Desktop or Battery powered system
 * Runs when system switches on
 * Pushes output to console
 * If a battery powered system, trigger SC to alert system is not DESKTOP
 */

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import oshi.SystemInfo;
import oshi.software.os.OSSession;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class Sessions {

    public static DecimalFormat df2 = new DecimalFormat( "#.##" );
    private static JSONArray resultArray = new JSONArray(  );
    private static final DateTimeFormatter LOGIN_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");


    public static JSONArray sessionInfo() throws JSONException {

        resultArray = new JSONArray(  );
        SystemInfo si = new SystemInfo();
        df2.setRoundingMode( RoundingMode.UP );
        for (OSSession sess : si.getOperatingSystem().getSessions()) {
            Date date = new Date(sess.getLoginTime());
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            JSONObject object = new JSONObject(  );
            object.put( "userName", sess.getUserName() );
            object.put( "hostname", sess.getHost() );

        if(sess.getLoginTime()!=0)
        {
            object.put("login", dateFormat.format( date ) );
            object.put("category", "1");
        }else{
            object.put( "login", "No Login" );
            object.put( "category", "0" );
        }

        resultArray.put( object );

        }
        return resultArray;
    }


}
