import java.text.ParseException;
import java.text.SimpleDateFormat;

public class DateUtil {

	public static String transDateToMills(String date) {
		//2018-05-14 11:58:55
		SimpleDateFormat sf = new SimpleDateFormat("yyyy/MM/dd");
		try {
			return String.valueOf(sf.parse(date).getTime()/1000);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}
}
