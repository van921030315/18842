package messagePasser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

public class URLreader {
	
	String path = "http://www.andrew.cmu.edu/user/yilanx2/files/";
			//"https://dl.dropboxusercontent.com/u/5458762/";
				//"http://www.andrew.cmu.edu/user/yilanx2/files/";
	File config;
	String filename;
	
    public URLreader(String filename){
    	this.filename = filename;
    	config = new File(filename);
        URL url;
		try {
			url = new URL(path +filename);
			BufferedReader in = new BufferedReader(
			        new InputStreamReader(url.openStream()));
			        config.createNewFile();
			        FileWriter writer = new FileWriter(config);
			        String inputLine;
			        while ((inputLine = in.readLine()) != null){
			            //System.out.println(inputLine);
			            writer.write(inputLine + '\n');
			        }
			        writer.flush();
			        writer.close();
			        in.close();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
    }
    
    public File getFile(){
    	
    	return config;
    }
}
