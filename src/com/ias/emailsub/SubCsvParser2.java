package com.ias.emailsub;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * checks report patterns
 * input: sub_1.csv
 * <id>,<count>,<url>
 * output: report_type, count
 *
 */
public class SubCsvParser2 {
	static Map<String, Integer> index = new HashMap<String, Integer>();
	public static void main(String[] args) throws IOException {
		File f = new File("/Users/ffan/email_sub/sub_1.csv");
		BufferedReader br = new BufferedReader(new FileReader(f));
		String line;
		while ( ( line = br.readLine() ) != null){
			processLine(line);
		}
		br.close();
		for (String k : index.keySet()){
			System.out.println(k + ": " + index.get(k));
		}
	}

	private static void processLine(String line) throws IOException{
		line = line.trim();
		if ( line.length() == 0 ){
			return;
		}
		String[] x = line.split("\\s*,\\s*", 3);
		String url = x[2].trim();
		String key = extractReportType(url, line);
		Integer i = index.get(key);
		if ( i == null ){
			i = 0;
		} else {
			i = i+1;
		}
		index.put(key, i);
		
	}
	
	static Pattern p = Pattern.compile("^/reportingservice/api/teams/\\d+/([^/]+)(?:[^?]*)/(.*?)(?:\\?|$)");
	private static String extractReportType(String url, String line){
		Matcher m = p.matcher(url);
		if ( m.find() && m.groupCount() == 2 ){
			return m.group(1)+ "/" + m.group(2);
		} else {
			throw new RuntimeException("Can't parse url: " + line);
		}
	}
}
