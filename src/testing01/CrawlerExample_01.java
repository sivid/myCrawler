// http://comet.tw/comet/2014/04/1-java-crawler-httpclient-example/?doing_wp_cron=1406971045.1507360935211181640625
// I changed the url to ncu.edu.tw, and commented out the line that calls POST
// other than the above, everything should not change.

package testing01;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;

public class CrawlerExample_01 {

	HttpClient client;

	public static void main(String[] args) {
		CrawlerExample_01 cra = new CrawlerExample_01();
		// create HttpClient
		cra.client = HttpClientBuilder.create().build();
		// run GET Method
		cra.httpGetMethod();
		// run POST Method
		//cra.httpPostMethod();
	}

	@SuppressWarnings("resource")
	private void httpGetMethod() {
		// use GET method
		HttpGet get = new HttpGet("http://www.ncu.edu.tw");
		try {
			// send request
			HttpResponse response = client.execute(get);
			if (response.getStatusLine().getStatusCode() == 200) {
				// get response
				byte[] bufferByte = new Scanner(response.getEntity().getContent()).useDelimiter("\\A").next().getBytes();
				String result = new String(
						new String(bufferByte, "UTF-8").getBytes(), "UTF-8");
				// print it
				try{
					PrintWriter out = new PrintWriter(new FileWriter("ncu01.html"));
					out.print(result);
					out.close();
				}catch(IOException e){e.printStackTrace();}
				//System.out.println(result);
				// 
			} else {
				System.out.println("something wrong");
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void httpPostMethod() {		
		// use POST method
		HttpPost post = new HttpPost("https://selfsolve.apple.com/wcResults.do");
		// set parameters
		List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
		urlParameters.add(new BasicNameValuePair("sn", "C02G8416DRJM"));
		urlParameters.add(new BasicNameValuePair("cn", ""));
		urlParameters.add(new BasicNameValuePair("locale", ""));
		urlParameters.add(new BasicNameValuePair("caller", ""));
		urlParameters.add(new BasicNameValuePair("num", "12345"));
	 
		try {
			post.setEntity(new UrlEncodedFormEntity(urlParameters));
			HttpResponse response = client.execute(post);
			System.out.println("Response Code : " 
		                + response.getStatusLine().getStatusCode());
			// use BufferedReader
			BufferedReader rd = new BufferedReader(
			        new InputStreamReader(response.getEntity().getContent()));
		 
			StringBuffer result = new StringBuffer();
			String line = "";
			while ((line = rd.readLine()) != null) {
				result.append(line);
			}
			// print it
			System.out.println(result.toString());
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}