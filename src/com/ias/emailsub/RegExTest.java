package com.ias.emailsub;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegExTest {
	private static Pattern urlList1 = Pattern.compile("^\\\\\"(.*?)\\\\\"");
	private static Pattern urlList2 = Pattern.compile("^(?:\\s*,\\s*\\\\\"(.*?)\\\\\")");	

	public static void main(String[] args){
		String t1 = "\\\"x\\\",\\\"y\\\",\\\"z\\\",\\\"z1\\\"";
		System.out.println(t1);
		for ( String x : parseUrlList(t1) ){
			System.out.println(x);
		}
		t1 = "\\\"x\\\"";
		System.out.println(t1);
		for ( String x : parseUrlList(t1) ){
			System.out.println(x);
		}
	}
	
	private static String[] parseUrlList(String t1){
		ArrayList<String> rst = new ArrayList<String>();
		Matcher m = urlList1.matcher(t1);
		if ( m.find()) {
			rst.add(m.group(1));
			String x = t1.substring(m.end());
			while ( x.length() > 0){
				m = urlList2.matcher(x);
				if ( m.find() ){
					rst.add(m.group(1));
					x = x.substring(m.end());
				} else {
					throw new RuntimeException("invalid line " + t1);
				}
			}
		} else {
			throw new RuntimeException("found invalid line " + t1);
		}
		return rst.toArray(new String[rst.size()]);
	}
}
