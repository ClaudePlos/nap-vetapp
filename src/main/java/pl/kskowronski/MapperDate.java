package pl.kskowronski;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class MapperDate {

    public SimpleDateFormat dtYYYYMMDDHHMMSS = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public SimpleDateFormat dtYYYYMMDD = new SimpleDateFormat("yyyy-MM-dd");
    public SimpleDateFormat dtYYYY = new SimpleDateFormat("yyyy");
    public SimpleDateFormat dtYYYYMM = new SimpleDateFormat("yyyy-MM");
    public SimpleDateFormat dtDD = new SimpleDateFormat("dd");

    public DateTimeFormatter localDateYYYYMMDD = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public DateTimeFormatter localDateMMYYYY = DateTimeFormatter.ofPattern("MM-yyyy");
    public DateTimeFormatter formatterYYYYMMDDHHMMSS = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public String getCurrentlyYear(){
        Date today = new Date();
        return dtYYYY.format(today);
    }

}
