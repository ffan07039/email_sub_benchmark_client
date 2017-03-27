package com.ias.emailsub.client;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.*;

public class BenchMarkClient implements Runnable{
	public static int thread_count = 10;
	public static final String HOST_PORT = "http://s-etl02:19080";
	public static BlockingQueue<String> tasksQueue = new LinkedBlockingQueue<String>();
	public static BlockingQueue<String> resultsQueue = new LinkedBlockingQueue<String>();
	
	public static void main(String[]  args) throws Exception{
		for ( int i = 0; i < thread_count; i++){
			Thread t = new Thread(new BenchMarkClient());
			t.setDaemon(true);
			t.start();
		}
		int count = 0;
		
		File f = new File("/Users/ffan/email_sub/sub_1_1.csv");
		BufferedReader br = new BufferedReader(new FileReader(f));
		String line;
		while ( ( line = br.readLine() ) != null){
			if ( tasksQueue.add(line) ){
				count++;
			} else {
				throw new RuntimeException("Failed to insert entry, count: " + count + ", line: " + line);
			}
		}
		br.close();
		
		File output = new File("/Users/ffan/email_sub/sub_2_1.csv");
		PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(output)));
		while ( count > 0 ){
			String result_line = resultsQueue.take();
			count--;
			pw.println(result_line);
		}
		pw.close();
	}
	
	public void run() {
		try {
			while ( true ){
				String line = tasksQueue.take();
				String rst = processSubLine(line);
				if ( !resultsQueue.add(rst) ){
					System.err.println("Failed to insert into resultsQueue, stop application, " + rst);
					System.exit(-1);
				}
			}
		} catch(InterruptedException ie){
			System.err.println("Worker thread interrupted, stop application");
			System.exit(-1);
		}

	}

	private static String processSubLine(String line){
		String[] x = line.split("\\s*,\\s*", 3);
		Pair<Integer, Double> rst = handleReq(HOST_PORT+ x[2]);
		return x[0]+","+x[1]+","+rst+","+x[2];
	}
	
	private static Pair<Integer, Double> handleReq(String reqLine){
		CloseableHttpClient c = HttpClients.createDefault();
		HttpGet get = new HttpGet(reqLine);
		CloseableHttpResponse res = null;
		long start_nano = System.nanoTime();
		int status = -1;
		try {

			res = c.execute(get);
			status = res.getStatusLine().getStatusCode();
			if (status >= 200 && status < 300) {
				HttpEntity entity = res.getEntity();
				entity.writeTo(new NullOutputStream());
			}
		} catch (Exception ex){
			
		}
		finally {
			long end_nano = System.nanoTime();
			Pair<Integer, Double> rst =  new Pair<Integer, Double>(status,  (end_nano - start_nano)/(1000.0d * 1000 *1000));
			try{
				c.close();
				if ( res != null){
					res.close();
				}
			} catch(Exception e) {

			}
			return rst;
		}
	}
	
	public static class Pair<L, R>{
		private L l;
		private R r;
		Pair(L l, R r){
			this.l = l;
			this.r = r;
		}
		L getLeft(){
			return l;
		}
		R getRight(){
			return r;
		}
		
		public String toString(){
			return l + "," + r;
		}
	}
	
	public static class NullOutputStream extends OutputStream {

		@Override
		public void write(int b) throws IOException {
		}
		
	}
}
