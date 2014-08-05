import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SimpleCrawler {
	static List<String>urlPool = new ArrayList<String>();
	static List<String>content = new ArrayList<String>();	
	String [] catalog = {
			"http://www.nownews.com/cat/politic",
//			"http://www.nownews.com/cat/finance",
//			"http://www.nownews.com/cat/life",
//			"http://www.nownews.com/cat/taiwanhot",
//			"http://www.nownews.com/cat/society",
//			"http://www.nownews.com/cat/sport",
//			"http://www.nownews.com/cat/entertainment",
//			"http://www.nownews.com/cat/global",
//			"http://www.nownews.com/cat/china",
//			"http://www.nownews.com/cat/novelty",
//			"http://www.nownews.com/cat/fashion",
//			"http://www.nownews.com/cat/travel",
//			"http://www.nownews.com/cat/pets",
//			"http://www.nownews.com/cat/eworld"
	};	
	
	public SimpleCrawler() throws Exception{
		urlPool.add("http://www.nownews.com");
		for(int j = 0; j < catalog.length; j++){
			URL url = new URL(catalog[j]);
			InputStream is=url.openStream();
			FileOutputStream fos=new FileOutputStream("website/Source.txt");
			while(true){
				int i=is.read();
				if(i==-1)
					break;
				fos.write(i);					// changed indent one tab to the left
			}
			is.close();
			fos.close();
			newsReader(j);
		} // 大類項下的 Link 萃取
		newsWriter(); // 連結 urlPool 中的所有 Link 並將特定標籤內容取出	
	}  // 建構子結束
	
	
	@SuppressWarnings("unused")
	public static void main(String[] args){
		try {
			SimpleCrawler test = new SimpleCrawler();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void newsReader(int index) throws Exception{		
		StringBuffer sb = new StringBuffer();
		BufferedReader br = new BufferedReader(new FileReader("website/Source.txt"));
		String line="";
		while( (line=br.readLine()) != null) {		// changed lines 66 ~ 83 indent (one tab to the left)
				sb.append(line);
		} br.close();								// closed br

		Pattern patternLink = Pattern.compile("http://www.nownews.com/n/2014[\\w|\\?|\\.|/]+");
		Matcher mLink = patternLink.matcher(sb.toString());
		while(mLink.find()) {
			String str = mLink.group();
			System.out.println("====" + str);
			String result=check(str);
			if(result=="true"){
				urlPool.add(str);
				//System.out.println("====" + str);
			}else{
				System.out.println("skipped");
				//System.out.println(str); // 列出重複的 Link
			} // 檢查 urlPool 中的 Link 是否有重複			
		}		
	} // 萃取原始碼中的所有  Link
	
	private String check(String link) {
		String result="";
		for(int i=0; i<urlPool.size();i++){
			if(link.equals(urlPool.get(i))){
				result="faulse";
				break;
			}else{
				result="true";
				continue;
			}
		}		
		return result;
	} // 檢查 Link 是否重複的方法
	
	public void newsWriter() throws Exception{
		for(int j = 0; j < urlPool.size(); j++){
			URL url = new URL(urlPool.get(j));
			InputStream is=url.openStream();
			FileOutputStream fos=new FileOutputStream("website/Source.txt");
			while(true){
				int i=is.read();
				if(i==-1)
					break;
					fos.write(i);
			}
			is.close();
			fos.close();
//			newsReader(j);
			
			StringBuffer sb = new StringBuffer();
			BufferedReader br = new BufferedReader(new FileReader("website/Source.txt"));
			String line="";
				while( (line=br.readLine()) != null) {
						sb.append(line);
				}
				
			Pattern patternContent = Pattern.compile("<div class=\"story_content\".*?>.*?</div>");
			Matcher mContent = patternContent.matcher(sb.toString());
			while(mContent.find()) {
				String str = mContent.group();			
				content.add(str);
			}			
		}
		
		FileWriter fwContent=new FileWriter("website/NewsContent.txt", true);	
		for(int i=0;i<content.size();i++){
			fwContent.write(content.get(i)+"\r\n");
		}
		fwContent.flush();
		fwContent.close();		
	} // 萃取特定標籤下的內容方法
	
}
