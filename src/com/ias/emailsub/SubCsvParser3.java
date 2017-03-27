package com.ias.emailsub;

import java.io.*;

/**
 * checks if there is ',' in the url list -- there is one but that's fine
 * input: sub_1.csv
 * <id>,<count>,<url>
 *
 */
public class SubCsvParser3 {
	static int count = 0;
	public static void main(String[] args) throws IOException {
		File f = new File("/Users/ffan/email_sub/sub_1.csv");
		BufferedReader br = new BufferedReader(new FileReader(f));
		String line;
		while ( ( line = br.readLine() ) != null){
			processLine(line);
		}
		br.close();
		System.out.println("total lines: " + count);
	}

	private static void processLine(String line) throws IOException{
		line = line.trim();
		if ( line.length() == 0 ){
			return;
		}
		String[] x = line.split("\\s*,\\s*", 3);
		String id = x[0].trim();
		String url = x[2].trim();
		if ( url.indexOf(',') != -1 ){
			System.out.println(line);
			count++;
		}
		
	}
}
