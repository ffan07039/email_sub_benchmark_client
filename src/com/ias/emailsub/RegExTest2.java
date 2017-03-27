package com.ias.emailsub;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegExTest2 {
	//static Pattern p = Pattern.compile("^/reportingservice/api/teams/\\d+/([^/]+)(?:[^?]*)/(.*?)(\\?|$)");
	static Pattern p = Pattern.compile("^/reportingservice/api/teams/\\d+/([^/]+)(?:[^?]*)/(.*?)(?:\\?|$)");		

	public static void main(String[] args){
		String t1 = "/reportingservice/api/teams/171/fw/campaigns/1566/report.xls?&period=yesterday&cutoff=0&team=171&tabs=[country:sites:visibility:firewall]";
		System.out.println(t1);
		System.out.println(extractReportType(t1));
		t1 = "/reportingservice/api/teams/251/fw/teamsummary.xls";
		System.out.println(t1);
		System.out.println(extractReportType(t1));
	}
	private static String extractReportType(String url){
		Matcher m = p.matcher(url);
		if ( m.find() && m.groupCount() == 2 ){
			return m.group(1)+ "/" + m.group(2);
		} else {
			throw new RuntimeException("Can't parse url: " + url);
		}
	}
}
