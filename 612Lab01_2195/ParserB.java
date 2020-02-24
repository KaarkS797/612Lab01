import java.io.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;
import java.util.*;
/**
 * ISTE-612 
 * Lab01
 * Kaarthik Sundaramoorthy
 */
public class ParserB {
   private String[] myDocs;
         
   public ParserB(String fileName) {
     
   }
   
   public ParserB() {
     
   }
   
   //Binary search to identify a stop word
   public int searchStopWord(String key) throws IOException {
      List<String> stopList = Files.readAllLines(Paths.get("stopwords.txt"), StandardCharsets.UTF_8); 
      String[] stopWords = stopList.toArray(new String[stopList.size()]);
      
      int lo = 0;
      int hi = stopWords.length-1;
      
      while(lo <= hi) {
         int mid = lo +(hi-lo)/2;
         int result = key.compareTo(stopWords[mid]);
         if(result < 0) hi = mid-1;
         else if(result > 0) lo = mid+1;
         else return mid;
      }
      return -1;
   }
   
   //Tokenization
   public ArrayList<String> parseB(File fileName) throws IOException {
      String[] tokens = null;
      ArrayList<String> pureTokens = new ArrayList<String>();
      ArrayList<String> stemms = new ArrayList<String>();
      
      Scanner scan = new Scanner(fileName);
      String allLines = new String();
      
      //Case folding 
      while(scan.hasNextLine()) {
         allLines += scan.nextLine().toLowerCase();
      }
      
      tokens = allLines.split("[ '.,?!:;$%+()\\-\\*]+");
      
      //remove stop words
      for(String token:tokens) {
         if(searchStopWord(token) == -1) {
            pureTokens.add(token);
         }
      }
      
      //stemming
      Stemmer st = new Stemmer();
      for(String token:pureTokens) {
         st.add(token.toCharArray(), token.length());
         st.stem();
         stemms.add(st.toString());
         st = new Stemmer();
      }
      return stemms;
   }
   
   public static void main(String[] args) throws IOException {
      ParserB p = new ParserB();
      
      //Stemming
      Stemmer st = new Stemmer();
      String stTest = "replacement";
      st.add(stTest.toCharArray(), stTest.length());
      st.stem();
      System.out.println("Stemmed: " + st.toString());    
      

   }
}