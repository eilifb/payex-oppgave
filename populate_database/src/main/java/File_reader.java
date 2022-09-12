import java.io.File;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

public class File_reader {
    public ArrayList<String> show_titles;
    public ArrayList<Integer> test;

    public File_reader() {
        this.show_titles = new ArrayList<String>();
        this.test = new ArrayList<Integer>();
    }

    //NB! Presumes config file has a single show title on each new line.
    public void Read_file(String file_name) throws FileNotFoundException {
        File config_file = new File(file_name);
        Scanner file_reader = new Scanner(config_file);
        int i = 0;
        while(file_reader.hasNextLine()) {
            show_titles.add(file_reader.nextLine());
            test.add(i);
                    i++;
        }
    }

    public URL[] get_request_URLs() throws MalformedURLException {
        URL[] Api_urls = new URL[show_titles.size()];
        for(int i = 0; i < Api_urls.length; i++){
            Api_urls[i] = new URL("https://api.tvmaze.com/singlesearch/shows?q=" + show_titles.get(i));
        }
        return Api_urls;
    }

    public void add_show_title(String title){
        show_titles.add(title);
    }

    public void Clear_show_titles(){
        show_titles.clear();
    }


}
