package unit;

import static org.junit.Assert.*;

import java.text.ParseException;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import server.GMTConversion;

public class GMTConversionTest {
	private Date date;
	private String gmtString;
	
	@Before
	public void setUp() {
		// 1485299450722L = Tue Jan 24 18:10:50 EST 2017
		date = new Date(1485299450722L);
		// GMT result from our function
		gmtString = "Tue, 24 Jan 2017 23:10:50 GMT";
	}
	
	@Test
	public void testToGMTString() {
		String actual = GMTConversion.toGMTString(date);
		assertEquals(gmtString, actual);
	}
	
	@Test(expected=ParseException.class)
	public void testFromGMTStringException() throws ParseException {
		String badGmtString = "Tue Jan 24 18:10:50 EST 2017";
		GMTConversion.fromGMTString(badGmtString);
	}
	
	@Test
	public void testFromGMTString() throws ParseException {
		Date gmtDate = GMTConversion.fromGMTString(gmtString);
		assertEquals(date.toString(), gmtDate.toString());
	}
}
