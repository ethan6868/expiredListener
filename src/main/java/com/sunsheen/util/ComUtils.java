package com.sunsheen.util;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
/**
 * 通用操作工具类
 * 
 * @author zhaohailong
 *
 */
public class ComUtils {

	/**
	 * 获取服务器IP
	 * 
	 * @return
	 */
	public static String getServerIp() {
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface
					.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf
						.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress()
							&& (inetAddress instanceof Inet4Address)) {
						return inetAddress.getHostAddress().toString();
					}
				}
			}
		} catch (SocketException ex) {
			ex.printStackTrace();
		}
		return null;
	}

	/**
	 * 日期转换成 Timestamp
	 * 
	 * @param date
	 * @return Timestamp 日期格式
	 */
	public static Timestamp DateToTimestamp(Date date) {

		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Long time = new Long(date.getTime());
		String data_time = format.format(time);

		Timestamp retdate = null;
		try {
			retdate = new Timestamp(format.parse(data_time).getTime());
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return retdate;
	}

	/**
	 * 字符串转换成日期
	 * 
	 * @param str
	 * @return date
	 */
	public static Date StrToSqlDate(String str) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = null;
		try {
			java.util.Date ud = format.parse(str);
			date = new java.sql.Date(ud.getTime());
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}

	/**
	 * 日期转换成字符串
	 * 
	 * @param date
	 * @return str
	 */
	public static String DateToStr(Date date) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String str = format.format(date);
		return str;
	}

	/**
	 * 将连接在一起的日期字符串转换成日期格式的字符串
	 * 
	 * @param conversionStr
	 * @return
	 */
	public static String strConversionStrData(String conversionStr) {
		String ret = "";
		if (conversionStr != null) {
			String year = "";
			String mouth = "";
			String day = "";
			String hour = "";
			String mi = "";
			String ss = "";
			
			if (conversionStr.length() <= 4) {
				return conversionStr;
			}
			
			if (conversionStr.length() >= 4) {  // 年
				year = conversionStr.substring(0, 4);
				ret = year;
			}
			
			if (conversionStr.length() >= 6) { // 月
				mouth = conversionStr.substring(4, 6);
				ret += "-" + mouth;
			}
			
			if (conversionStr.length() >= 8) { // 日
				day = conversionStr.substring(6, 8);
				ret += "-" + day;
			}
			
			if (conversionStr.length() >= 10) { // 时
				hour = conversionStr.substring(8, 10);
				ret += " " + hour;
			}
			
			if (conversionStr.length() >= 12) { // 分
				mi += conversionStr.substring(10, 12);
				ret += ":" + mi;
			}
			
			if (conversionStr.length() >= 14) { // 秒
				ss = conversionStr.substring(12, 14);
				ret += ":" + ss;
			}
		}
		return ret;
	}
	
	
	/**
	 * 判断一个字符串是否都为数字  
	 * @param strNum
	 * @return
	 */
	public static boolean isStrDigit(String strNum) {  
	    Pattern pattern = Pattern.compile("[0-9]{1,}");  
	    Matcher matcher = pattern.matcher((CharSequence) strNum);  
	    return matcher.matches();  
	}
	

	/**
	 * 
	 * @param date
	 * @return
	 */
	public static String DateToString(String pFormat, Date date) {
		SimpleDateFormat format = null;
		if (null != pFormat) {
			format = new SimpleDateFormat(pFormat);
		} else {
			format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		}
		String str = format.format(date);
		return str;
	}

	/**
	 * timestamp转成string
	 * 
	 * @param timestamp
	 * @return
	 */
	public static String TimestampToString(Timestamp timestamp) {
		String tsStr = "";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			tsStr = sdf.format(timestamp);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return tsStr;
	}

	/**
	 * 判断是否为null
	 * 
	 * @param string
	 * @return
	 */
	public static boolean isNull(String string) {
		return StringUtils.isEmpty(string);
	}

	/**
	 * 计算时间差
	 * 
	 * @param startDate
	 * @param endDate
	 * @param type
	 *            标识 day天 h小时 min分 sec秒
	 * @return
	 */
	public static long timeConversion(String startTime, String endTime,
			String str) {

		if (isNull(startTime) || isNull(endTime) || isNull(str)) {
			return 0;
		}

		// 按照传入的格式生成一个simpledateformate对象
		SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		long nd = 1000 * 24 * 60 * 60;// 一天的毫秒数
		long nh = 1000 * 60 * 60;// 一小时的毫秒数
		long nm = 1000 * 60;// 一分钟的毫秒数
		long ns = 1000;// 一秒钟的毫秒数
		long diff;
		long day = 0;
		long hour = 0;
		long min = 0;
		long sec = 0;
		// 获得两个时间的毫秒时间差异
		try {
			diff = sd.parse(endTime).getTime() - sd.parse(startTime).getTime();
			day = diff / nd;// 计算差多少天
			hour = diff % nd / nh + day * 24;// 计算差多少小时
			min = diff % nd % nh / nm + day * 24 * 60;// 计算差多少分钟
			sec = diff % nd % nh % nm / ns;// 计算差多少秒
			// 输出结果
			System.out.println("时间相差：" + day + "天" + (hour - day * 24) + "小时"
					+ (min - day * 24 * 60) + "分钟" + sec + "秒。");
		} catch (ParseException e) {
			e.printStackTrace();
		}

		if (str.equalsIgnoreCase("h")) {
			return hour;
		} else if (str.equalsIgnoreCase("day")) {
			return day;
		} else if (str.equalsIgnoreCase("sec")) {
			return sec;
		} else if (str.equalsIgnoreCase("min")) {
			return min;
		} else {
			return 0;
		}
	}

}
