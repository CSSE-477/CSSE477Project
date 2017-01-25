package server;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class GMTConversion {
	public static final String GMT_FORMAT = "EEE, d MMM yyyy HH:mm:ss z";
	
	public static String toGMTString(Date date) {
		Calendar calendar = new GregorianCalendar();
		calendar.setTimeZone(TimeZone.getTimeZone("GMT"));

		DateFormat gmtFormat = new SimpleDateFormat(GMT_FORMAT);
		gmtFormat.setCalendar(calendar);
		
		return gmtFormat.format(date);
	}
	
	public static Date fromGMTString(String dateString) throws ParseException {
		Calendar calendar = new GregorianCalendar();
		calendar.setTimeZone(TimeZone.getTimeZone("GMT"));

		DateFormat gmtFormat = new SimpleDateFormat(GMT_FORMAT);
		gmtFormat.setCalendar(calendar);
	
		return gmtFormat.parse(dateString);
	}
}
