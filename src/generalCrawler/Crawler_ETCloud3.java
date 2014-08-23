// Great Idea #5 : use something else to get Document, then Jsoup to parse/grab
// doesn't work.  Jsoup has its own Document.

package generalCrawler;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.SimpleXmlSerializer;
import org.htmlcleaner.TagNode;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Crawler_ETCloud3 {
	List<String>urlPool = new ArrayList<String>();		// news links in a news list page

	public Crawler_ETCloud3() {
	}

	public void URLReader() {
//		URL testNewItem;
//		urlPool.add("http://www.ettoday.net/news/20130205/161762.htm");
		HtmlCleaner cleaner = new HtmlCleaner();
		CleanerProperties props = cleaner.getProperties();
		try {
			TagNode node = cleaner.clean(new URL("http://www.ettoday.net/news/20130205/161762.htm"));
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			new SimpleXmlSerializer(props).writeToStream(node, out);
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(new ByteArrayInputStream(out.toByteArray()));
			XPathFactory xpf = XPathFactory.newInstance();
			XPath xpath = xpf.newXPath();
			XPathExpression xpe = xpath.compile("/html/body/div[3]/div/div[7]/div/div/div[1]/div[2]/span[5]");
			NodeList list =  (NodeList) xpe.evaluate(doc, XPathConstants.NODESET);
			for (int i = 0; i < list.getLength(); i ++) {
			    Node n = list.item(i);
			    System.err.println(n.getNodeValue());
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
