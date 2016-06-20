// Simple WebCrawler -- can be multiThreaded for more efficiency.
import java.util.*;
import java.net.*;
import java.io.*;

class WebCrawler {
    public static final int MAX_LIMIT = 1000;  // Max Links to be added to Repository.
    public static final boolean LOGGING = false;

    LinkedList<URL> queue;
    Set<URL> linkRepository;
    Scanner input;
    String inputURL;
    URL firstLink;
    int maxLinks;
    int count;

    public WebCrawler() {
    }

    public WebCrawler(String s,int n) {
        this.linkRepository = new HashSet<URL>();
        this.queue = new LinkedList<URL>();
        this.inputURL = s;
        this.maxLinks = n;
        this.count = 0;
    }


    public Set<URL> run()
    {
        String page;
        URL currentURL;
        while ( queue.size() != 0 ) {
            currentURL = queue.poll();
            page = downloadPage(currentURL);
            if (count < maxLinks && page.length() != 0) 
				process(currentURL,page);
            else 
				break;
        }
        System.out.println("Search complete.");
        return linkRepository;
    }


    public int preProcess() {
        URL url;
        if (maxLinks > MAX_LIMIT) 
		{
            if(LOGGING)
				System.out.println("MAX_LIMIT exceeded");
            return -1;
        }
        try 
		{
            url = new URL(inputURL);
        }
        catch (MalformedURLException e) {
            System.out.println("Input URL not valid" + inputURL);
            return -1;
        }
        linkRepository.add(url);
        queue.add(url);
		
        if (LOGGING) 
			System.out.println("Added Start URL");
        return 0;
    }

    // Files of type .htm or .html are added to repository

    public void addToRepository(URL oldURL, String newUrlString)
    {
        URL url;
        try
        {
            url = new URL(oldURL,newUrlString);
            if (!linkRepository.contains(url)) {
                String filename =  url.getFile();
                if ( filename.indexOf(".") == -1 ||
                     filename.endsWith("html") ||
                     filename.endsWith("htm") )
                {
                    linkRepository.add(url);
                    queue.add(url);
                    count++;
                    if (LOGGING) 
						System.out.println("Added URL "+ url);
                }
            }
        }
        catch (MalformedURLException e) {
            return;
        }
    }


	// Download contents of URL

    public String downloadPage(URL url)
    {
        try
        {
            // try opening the URL
            URLConnection conn = url.openConnection();
            conn.setAllowUserInteraction(false);

            InputStream stream = url.openStream();

            // read in the entire URL
            System.out.println(url.toString());
            byte byteArray[] = new byte[100];
            int value = stream.read(byteArray);
            String currPage="";
            String pgContent;

            while ((value != -1)) {
                pgContent = new String(byteArray, 0, value);
                currPage += pgContent;
                value = stream.read(byteArray);
            }
            return currPage;

        }
        catch (Exception e) {
            System.out.println("ERROR: couldn't open URL ");
            return "";
        }
    }

    // Extracts the href links upto hash symbol in each page
    // Adds it to repository

    public void process(URL url, String page)
    {
        int anchorStart=0;
        int anchorEnd, hrefInd, urlStart, urlEnd, hashInd, endInd;
        String newUrl;
        while (count < maxLinks && (anchorStart = page.indexOf("<a",anchorStart)) != -1 ||
                (anchorStart = page.indexOf("<A",anchorStart)) != -1 )
        {

            anchorEnd = page.indexOf(">",anchorStart);
            if ((hrefInd = page.indexOf("href",anchorStart)) != -1 ||
                    (hrefInd = page.indexOf("HREF",anchorStart)) != -1)
            {

                urlStart = page.indexOf("\"", hrefInd) + 1;
                if ((urlStart != -1) && (anchorEnd != -1) && (urlStart < anchorEnd))
                {

                    urlEnd = page.indexOf("\"",urlStart);
                    hashInd = page.indexOf("#", urlStart);
                    if ((urlEnd != -1) && (urlEnd < anchorEnd)) {
                        endInd = urlEnd;
                        if ((hashInd != -1) && (hashInd < urlEnd))
                            endInd = hashInd;
                        newUrl = page.substring(urlStart,endInd);
                        addToRepository(url, newUrl);
                    }
                }
            }
            anchorStart = anchorEnd;
        }
    }
}

