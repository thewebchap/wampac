package com.rubix.WAMPAC.NMSCollection;

/**
 * The code is run to check if its a Desktop or Battery powered system
 * Runs when system switches on
 * Pushes output to console
 * If a battery powered system, trigger SC to alert system is not DESKTOP
 */

import org.json.JSONException;
import org.json.JSONObject;
import oshi.SystemInfo;
import oshi.hardware.HardwareAbstractionLayer;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.LocalDateTime;

public class PowerInfo {

    public static DecimalFormat df2 = new DecimalFormat("#.##");
    private static JSONObject object = new JSONObject(  );


    public static JSONObject powerInfo() throws JSONException {
        SystemInfo si = new SystemInfo();
        HardwareAbstractionLayer hal = si.getHardware();
        df2.setRoundingMode( RoundingMode.UP );

        if((hal.getPowerSources().get( 0 ).isPowerOnLine()==false) && (hal.getPowerSources().get( 0 ).isCharging()==(false))) {
            //System is on battery and not charging for eg: laptop/tablet etc

            object.put( "time" , LocalDateTime.now() );
            object.put( "capacity", df2.format( hal.getPowerSources().get( 0 ).getRemainingCapacityPercent()) );
            object.put( "Message", "System is on battery power" );
            object.put( "condition", "Informational" );
           // resultArray.put( object );
        }
        else if((hal.getPowerSources().get( 0 ).isPowerOnLine()==true) && (hal.getPowerSources().get( 0 ).isCharging()==(false))){
            //System is connected to charger but 100 percent charged - All sorts of systems

            object.put( "time" , LocalDateTime.now() );
            object.put( "capacity", df2.format( hal.getPowerSources().get( 0 ).getRemainingCapacityPercent()) );
            object.put( "Message", "System is not charging" );
            object.put( "condition", "Informational" );
           // resultArray.put( object );
            }
            else if((hal.getPowerSources().get( 0 ).isPowerOnLine()==true) && (hal.getPowerSources().get( 0 ).isCharging()==(true))) {
            //System is connected to charger and charging - All sorts of systems

            object.put( "time" , LocalDateTime.now() );
            object.put( "capacity", df2.format( hal.getPowerSources().get( 0 ).getRemainingCapacityPercent()) );
            object.put( "Message", "System is charging" );
            object.put( "condition", "Informational" );
            //resultArray.put( object );

        }
        if(hal.getPowerSources().get( 0 ).getRemainingCapacityPercent()<=(0.10))
            {

                object.put( "time" , LocalDateTime.now() );
                object.put( "capacity", df2.format( hal.getPowerSources().get( 0 ).getRemainingCapacityPercent()) );
                object.put( "Message", "Battery power is less than 10 percent and needs attention" );
                object.put( "condition", "Critical" );
               // resultArray.put( object );
            }
            return object;
    }
}
