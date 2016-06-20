import java.util.*;
import java.net.*;


public class WebCrawlerRunner 
{
	public static void main(String args[])
	{	
		Set<URL> store = null;
		Scanner input = new Scanner(System.in);
		String inputURL = input.next();	// Start URL.
		int maxPages = input.nextInt();	// Number of links to be fetched - MAX 1000.

		WebCrawler crawler = new WebCrawler(inputURL,maxPages);
		int retCode = crawler.preProcess();
		if (retCode != -1) 
			store = crawler.run();
		for(URL url:store){
			System.out.println(url.toString());	// Prints the repository of links fetched.	
		}
	}
}
