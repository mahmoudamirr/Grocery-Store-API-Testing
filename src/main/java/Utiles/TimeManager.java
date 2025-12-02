package Utiles;

public class TimeManager {

    //Screenshots - logs - report
    public static String getTimeStamp(){
        return new java.text.SimpleDateFormat( "yyyy-MM-dd-hh-mm-ss " ).format( new java.util.Date() );
    }
    public static String getSimpleTimeStamp(){
        return Long.toString(System.currentTimeMillis());
    }
}
