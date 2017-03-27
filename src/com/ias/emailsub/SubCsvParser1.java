package com.ias.emailsub;

import java.io.*;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * extracts urls from url list
 * input, sub.csv:
 * <id>,<url_wrapper>
 * output, sub_1.csv:
 * <id>,<count>,<url>
 *
 */
public class SubCsvParser1 {
	public static void main(String[] args) throws IOException {
		File output = new File("/Users/ffan/email_sub/sub_1.csv");
		PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(output)));
		File f = new File("/Users/ffan/email_sub/sub.csv");
		BufferedReader br = new BufferedReader(new FileReader(f));
		String line;
		while ( ( line = br.readLine() ) != null){
			processLine(line, pw);
		}
		pw.close();
		br.close();
		
	}
	private static Pattern urlList1 = Pattern.compile("^\\\\\"(.*?)\\\\\"");
	private static Pattern urlList2 = Pattern.compile("^(?:\\s*,\\s*\\\\\"(.*?)\\\\\")");
	
	private static void processLine(String line, PrintWriter pw) throws IOException{
		line = line.trim();
		if ( line.length() == 0 ){
			return;
		}
		String[] x = line.split("\\s*,\\s*", 2);
		String id = x[0].trim();
		String url_wrapper = x[1].trim();
		if ( ! url_wrapper.startsWith("\"[") ){
			throw new RuntimeException("invalid url prefix, " + line);
		}
		url_wrapper = url_wrapper.substring(2);
		if ( ! url_wrapper.endsWith("]\"") ){
			throw new RuntimeException("invalid url suffix, " + line);
		}
		String url = url_wrapper.substring(0, url_wrapper.length() - 2);
		String[] extracted_urls = parseUrlList(url);
		if ( extracted_urls.length == 0 ){
			throw new RuntimeException("invalid url list, " + line);
		}
		int count = 0;
		for ( String s : extracted_urls){
			pw.println(id + "," + count + "," + s);
			count++;
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
