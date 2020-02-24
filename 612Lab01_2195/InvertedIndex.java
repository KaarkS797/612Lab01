import java.util.*;
import java.io.*;
import java.io.File; 
import java.io.FileWriter; 
import java.io.BufferedWriter; 
import java.io.PrintWriter; 
import java.io.IOException; 
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
/**
 * ISTE612
 * 612Lab01
 * Kaarthik Sundaramoorthy 
 */
 
public class InvertedIndex { //query in function so declaring it globally
   /**
    *attributes
    */
   private String[] myDocs;               //input docs
   private ArrayList<String> termList;    //dictionary
   private ArrayList<ArrayList<Integer>> documentLists;
   private ArrayList<Integer> documentList; 
   private String[] order = new String[3]; 
   
   File doc1 = new File("/Users/kaart/Documents/612Lab01_2195/612Lab01_2195/Lab1_Data/cv000_29416.txt");
   File doc2 = new File("/Users/kaart/Documents/612Lab01_2195/612Lab01_2195/Lab1_Data/cv001_19502.txt");
   File doc3 = new File("/Users/kaart/Documents/612Lab01_2195/612Lab01_2195/Lab1_Data/cv002_17424.txt");
   File doc4 = new File("/Users/kaart/Documents/612Lab01_2195/612Lab01_2195/Lab1_Data/cv003_12683.txt");
   File doc5 = new File("/Users/kaart/Documents/612Lab01_2195/612Lab01_2195/Lab1_Data/cv004_12641.txt"); 
   File[] docs = {doc1, doc2, doc3, doc4, doc5};
   /**
    * Constructor
    */
   public InvertedIndex(String[] Docs) {
      myDocs = Docs;                         
      termList = new ArrayList<String>();
      documentLists = new ArrayList<ArrayList<Integer>>();  // postings lists
      documentList = new ArrayList<Integer>();  // postings list
      /**
       * splitting every word and inserting it in termList
       */
      for(int i=0; i < myDocs.length; i++) { 
         String[] words = myDocs[i].split(" ");
         for(String word : words) {                                 
            if(!termList.contains(word)) {   //termList is the dictionary
               termList.add(word);
               documentList = new ArrayList<Integer>();
               documentList.add(i);  //adding the words not in the dictionary to posting list
               documentLists.add(documentList); 
            }
            else {
               int index = termList.indexOf(word);    // finding where the word is in the dictionary
               documentList = documentLists.get(index);  
                 if(!documentList.contains(i)) {
                  documentList.add(i);
                  documentLists.set(index,documentList);
               }
            }
         }
      }    
   }
      public void input() throws IOException {
             
            String Query1 ="require";
            String Query2 ="Entertaining";
            System.out.println("Single Query Test Case");
            System.out.println("Enter Query: " + Query1);
            System.out.println("Enter Query: " + Query2);
            String parse1 = parseB(Query1).toString();        
            String parse2 = parseB(Query2).toString();
            ArrayList<Integer> Rresult1 = search(parse1);      
            ArrayList<Integer> Rresult2 = search(parse2);
            System.out.println("Documents which have the word: " + Query1 + " are: ");
              for(Integer i : Rresult1) {
            System.out.println(i + "." + docs[i].getName());   
         }
            System.out.println("Documents which have the word: " + Query2 + " are: ");
              for(Integer i : Rresult2) {
            System.out.println(i + "." + docs[i].getName());
         }     
            /**
             * Two Keyword Search with AND
             */
            String Query3 = "Product Year";
            String Query4 = "William Time";
            
            ArrayList<Integer> Rresult3 = procAnd(Query3);  //calling procAnd
            ArrayList<Integer> Rresult4 = procAnd(Query4);
       	
       	   System.out.println("Test case for the AND Query");
            System.out.println("Documents which have the words(AND): " + Query3 + " are: ");
            for(Integer i : Rresult3) {
            System.out.println(i + "." + docs[i].getName());
         }

            System.out.println("Documents which have the words(AND): " + Query4 + " are: ");
            for(Integer i : Rresult4) {
            System.out.println(i + "."+docs[i].getName());
         }      
            /**
             * Two Keyword Search with OR
             */
            String Query5 ="Robot Act";
            String Query6 = "fact target";
            ArrayList<Integer> Rresult5 = procOr(Query5);
       	   System.out.println("Test case for the OR Query");
            System.out.println("Documents which have the words(OR): " + Query5 + " are: ");
            for(Integer i : Rresult5) {
            System.out.println(i + "." + docs[i].getName());
         }          
            ArrayList<Integer> Rresult6 =  procOr(Query6);
            System.out.println("Documents which have the words(OR): " + Query6 + " are: ");
            for(Integer i : Rresult6) {
            System.out.println(i + "." + docs[i].getName());
         } 
            /**
             * Multiple Keyword Search
             */
            String Query7 = "man save cast";
            String Query8 = "late television 1960";
            ArrayList<Integer> Rresult7 =  procMultiAnd(Query7);
            procOrder(Query7); 
    
            ArrayList<Integer> Rresult8 =  procMultiAnd(Query8);
            procOrder(Query8);
            
            System.out.println("Test case for the multi keyword word AND");
            System.out.println("Documents which have the words(MultiAnd): " + Query7 + " are: ");
            for(Integer i : Rresult7) {
            System.out.println(i + "." + docs[i].getName());
         }
            
            System.out.println("Documents which have the words(MultiAnd): " + Query8 + " are: ");
            for(Integer i : Rresult8) {
            System.out.println(i + "." + docs[i].getName());

         }
            for(String i : order) { //stores what order the words are stored in
            System.out.println(i);
            }           
         } 

         /**
          * Binary search
          */
      public int searchStopWord(String key) throws IOException {   
	   List<String> list = Files.readAllLines(Paths.get("stopwords.txt"), StandardCharsets.UTF_8); //reads the stopwords file and stores in array
	   String[] stopWords = list.toArray(new String [list.size()]); 

      int low = 0;
      int hi = stopWords.length-1;
      
      while(low <= hi) {      //check if the key has a stopword
         int mid = low +(hi-low)/2; 
         int Rresult = key.compareTo(stopWords [mid]);
         if(Rresult < 0) hi = mid-1;
         else if(Rresult > 0) low = mid+1;
         else return mid;
      }
      return -1;
   }   /**
        * Parsing Query
        */
      public String parseB(String Query) throws IOException {  
	      String[] tokens = null;
	      ArrayList<String> pureTokens = new ArrayList<String>();           //removes stopwords in the token
	      ArrayList<String> stemms = new ArrayList<String>();             //stems the stopwords
	      String res = null;
	      String words = Query.toLowerCase();
	      /**
          * splitting into tokens 
          */	      
	      tokens = words.split("[\" '.,?!:;$%+()\\-\\*]+");  
         	     
	      /**
          * remove stop words
	       */
         for(String token : tokens) {
	         if(searchStopWord(token) == -1) {
	            pureTokens.add(token);
	         }
	      }	     
	      /**
          * stemming - transform word to root form
	       */
          Stemmer st = new Stemmer();
	      for(String token:pureTokens) {
	         st.add(token.toCharArray(), token.length());
	         st.stem();
	         stemms.add(st.toString());
	         res = String.join(" ", stemms);
	         st = new Stemmer();
	      }
	      return res;
	   } 
   /**
    * finding which doc has the term
    */
   public ArrayList<Integer> search(String query) {   
      int index = termList.indexOf(query);
      if(index >= 0) {
         return documentLists.get(index);
      }
      else return null;
   }
   /**
    * string representation
    */
   public String toString() {  
      String outputString = new String();
      for(int i=0; i < termList.size() ; i++) {
         outputString += String.format("%-15s", termList.get(i));
         documentList = documentLists.get(i);
         for(int j=0; j < documentList.size() ; j++) {
            outputString += documentList.get(j) + "\t";
         outputString += "\n";
      }
      
   }
   return outputString;
   }
    public ArrayList<Integer> procOr(String Query) throws IOException { 
	   	   
	   String[] words = Query.split(" ");
	   
	      String term1 = parseB(words[0]);
		   String term2 = parseB(words[1]);
	   
	    String[] terms = {term1,term2};
     /**
      * converting words into ArrayList and finding where they are
      */ 	   
	    ArrayList<Integer> terms1 = search(terms[0]); 
       ArrayList<Integer> terms2 = search(terms[1]);
       /*
        * merge the words which are found
        */
       ArrayList<Integer> merge = MergeOr(terms1,terms2);   
       return merge;
   }
    /**
     * @param1 ArrayList term1 of Integer
     * @param2 ArrayList term2 of Integer
     * @return ArrayList merged of Integer
     */
    /**
     * Method to Merge OR Query
     */
   public ArrayList<Integer> MergeOr(ArrayList<Integer>terms1, ArrayList<Integer>terms2) {
	  int i=0;
     int j=0;
	  ArrayList<Integer> merged = new ArrayList<Integer>();
     /**
      * finding size of words
      */
	  while(i < terms1.size() && j < terms2.size())    
	  {
		  if(terms1.get(i).intValue() > terms2.get(j).intValue()) {
			  merged.add(terms2.get(j));
			  j++;			  
		  }
		  if(terms1.get(i).intValue() < terms2.get(j).intValue()) {
			  merged.add(terms1.get(i));
			  i++;			  
		  }
		  if(terms1.get(i).intValue() == terms2.get(j).intValue()) {
			  merged.add(terms1.get(i));  
			  i++;
			  j++;			  
		  }		  
	  }	  
          while(i < terms1.size()) {  // remaining documents
              merged.add(terms1.get(i));
              i++;
        	  }     
          while(j < terms2.size()) {       	  
              merged.add(terms2.get(j));
              j++;
        	  }          
      return merged;	   
   }
    /**
     * @param Multiple word Query of String 
     * @return ArrayList merged of Integer 
     */
    /**
     * Method to Process AND Query
     */
   public ArrayList<Integer> procAnd(String Query) throws IOException {		   
	   String[] words = Query.split(" ");
	   
	      String term1 = parseB(words[0]);
		   String term2 = parseB(words[1]);
	   
	   String[] terms = {
      term1,term2    
      };
	    /**
        * converting words into ArrayList and finding where they are
        */  
	    ArrayList<Integer> terms1 = search(terms[0]);  
       ArrayList<Integer> terms2 = search(terms[1]);
       /**
        * merge the found words
        */
       ArrayList<Integer> merge = MergeAnd(terms1,terms2);  
       return merge;
   }
    /**
     * @param Multiple word Query of String 
     * @return ArrayList merged of Integer
     */
    /**
     * Method to Merge AND Query
     */
   public ArrayList<Integer> MergeAnd(ArrayList<Integer>terms1,ArrayList<Integer>terms2 ) {
	  int i=0; int j=0;
	  ArrayList<Integer> merged = new ArrayList<Integer>();
	  while(i < terms1.size() && j < terms2.size())
	  {
		  if(terms1.get(i).intValue() > terms2.get(j).intValue()) {
			  j++;
			  
		  }
		  if(terms1.get(i).intValue() < terms2.get(j).intValue())
		  {
			  i++;
			  
		  }
		  if(terms1.get(i).intValue() == terms2.get(j).intValue()) {
			  merged.add(terms1.get(i));			  
			  i++;
			  j++;
			  
		  }			  
	  }
	return merged;
	   }
    /**
     * @param1 ArrayList term1 of Integer
     * @param2 ArrayList term2 of Integer
     * @return ArrayList merged of Integer
     */
   /**
    * Method to Process MultiAND Query
    */
   public ArrayList<Integer> procMultiAnd(String Query) throws IOException
   {
	   String[] words = Query.split(" ");  
	   
	   String term1 = parseB(words[0]);
	   String term2 = parseB(words[1]);
	   String term3 = parseB(words[2]);
   
	  String[] terms0 = {
     term1,term2,term3
     };
	 
	   int size1;
	   int size2;
	   int size3;
	   
	 ArrayList<Integer> terms1 = search(terms0[0]);
    ArrayList<Integer> terms2 = search(terms0[1]);
    ArrayList<Integer> terms3 = search(terms0[2]);
    
    size1 = terms1.size();
    size2 = terms2.size();
    size3 = terms3.size();
    /**
     * sort by frequency
     */
    ArrayList<Integer> merge = new ArrayList<Integer>();
    if(Math.min(size1,size2) == size1 & (Math.min(size1,size3) == size1)) { 
    	      order[0] = term1;
    		if(Math.min(size2,size3) == size2) { 
    			ArrayList<Integer> merge1 = MergeAnd(terms1,terms2);
    		    merge = MergeAnd(merge1,terms3);   
    		     order[1] = term2;
              order[2] = term3;
    		}
    		else if(Math.min(size2,size3) == size3) { 
    			ArrayList<Integer> merge1 = MergeAnd(terms1,terms3);
    		    merge = MergeAnd(merge1,terms2);  
             order[1] = term3;
             order[2] = term2;
    	}
    }
    if(Math.min(size2,size1) == size2) {
            
    	if(Math.min(size2,size3) == size2) {
         order[0] = term2;
    		if(Math.min(size1,size3) == size1) {         
    			ArrayList<Integer> merge1 = MergeAnd(terms2,terms1);
    		    merge = MergeAnd(merge1,terms3);
             order[1] = term1;
             order[2] = term3;
    		}
         
    		 if (Math.min(size1,size3) == size3) {       
    			ArrayList<Integer> merge1 = MergeAnd(terms2,terms3);
    		    merge = MergeAnd(merge1,terms1); 
             order[1] = term3;
             order[2] = term1;
    		}
    	}
    }
    if(Math.min(size3,size1) == size3) {

    	if(Math.min(size2,size3) == size3) {
             order[0] = term3;      
    		if(Math.min(size1,size2) == size1) {
             order[1] = term1;
             order[2] = term2;
    			ArrayList<Integer> merge1 = MergeAnd(terms3,terms1);
    		    merge = MergeAnd(merge1,terms2);
    		}
    		else if(Math.min(size1,size2) == size2) {
             order[1] = term2;
             order[2] = term1;
    			ArrayList<Integer> merge1 = MergeAnd(terms3,terms2);
    		    merge = MergeAnd(merge1,terms1);   		       		    
    		}
    	}
    }
    if(size1 == size3) {
    
    	if(size1 == size2) {     
    		ArrayList<Integer> merge1 = MergeAnd(terms3,terms2);
		    merge = MergeAnd(merge1,terms1);
          order[0] = term3;
          order[1] = term2;		    
    	}
    		}
      return merge; 
   }
    public ArrayList<Integer> procOrder(String Query) throws IOException 
   {
	   String[] words = Query.split(" ");
	   
	   String term1 = parseB(words[0]);
	   String term2 = parseB(words[1]);
	   String term3 = parseB(words[2]);
   
	  String[] terms0 = {
     term1,term2,term3
     };
	 
	   int size1;
	   int size2;
	   int size3;
	   
	 ArrayList<Integer> terms1 = search(terms0[0]); 
    ArrayList<Integer> terms2 = search(terms0[1]);
    ArrayList<Integer> terms3 = search(terms0[2]);
    
    size1 = terms1.size();
    size2 = terms2.size();
    size3 = terms3.size();
    /**
     * sort by frequency - how many times a term occurs in a document 
     */
    ArrayList<Integer> merge = new ArrayList<Integer>();
    if(Math.min(size1,size2) == size1 & (Math.min(size1,size3) == size1)) { 
    	      order[0] = term1;
    		if(Math.min(size2,size3) == size2) {    
    		     order[1] = term2;
              order[2] = term3;
    		}
    		else if(Math.min(size2,size3) == size3) { 
             order[1] = term3;
             order[2] = term2;
    	}
    }
    if(Math.min(size2,size1) == size2) {
            
    	if(Math.min(size2,size3) == size2) {
         order[0] = term2;
    		if(Math.min(size1,size3) == size1) {         
    			
            order[1] = term1;
             order[2] = term3;
    		}
         
    		 if (Math.min(size1,size3) == size3) {       
    			
             order[1] = term3;
             order[2] = term1;
    		}
    	}
    }
    if(Math.min(size3,size1) == size3) {

    	if(Math.min(size2,size3) == size3) {
             order[0] = term3;      
    		if(Math.min(size1,size2) == size1) {
             order[1] = term1;
             order[2] = term2;

    		}
    		else if(Math.min(size1,size2) == size2) {
             order[1] = term2;
             order[2] = term1;
    			  		       		    
    		}
    	}
    }
    if(size1 == size3) {
    
    	if(size1 == size2) {     
    		
          order[0] = term3; //
          order[1] = term2;		    
    	}
    		}
      return merge; 
   }
    
   public static void main(String[] args) throws IOException { //passing file and creating inv index 
  
   ParserB p = new ParserB();
   File doc1 = new File("/Users/kaart/Documents/612Lab01_2195/612Lab01_2195/Lab1_Data/cv000_29416.txt");
   File doc2 = new File("/Users/kaart/Documents/612Lab01_2195/612Lab01_2195/Lab1_Data/cv001_19502.txt");
   File doc3 = new File("/Users/kaart/Documents/612Lab01_2195/612Lab01_2195/Lab1_Data/cv002_17424.txt");
   File doc4 = new File("/Users/kaart/Documents/612Lab01_2195/612Lab01_2195/Lab1_Data/cv003_12683.txt");
   File doc5 = new File("/Users/kaart/Documents/612Lab01_2195/612Lab01_2195/Lab1_Data/cv004_12641.txt"); 
   File[] docs = {doc1, doc2, doc3, doc4, doc5};

         ArrayList<String> stemmed1 = p.parseB(doc1);
         ArrayList<String> stemmed2 = p.parseB(doc2);
         ArrayList<String> stemmed3 = p.parseB(doc3);
         ArrayList<String> stemmed4 = p.parseB(doc4);
         ArrayList<String> stemmed5 = p.parseB(doc5);
         String File1 = String.join(" ", stemmed1);
         String File2 = String.join(" ", stemmed2);
         String File3 = String.join(" ", stemmed3);
         String File4 = String.join(" ", stemmed4);
         String File5 = String.join(" ", stemmed5);
         String[] docArray = new String[] {File1,File2,File3,File4,File5};
         
      InvertedIndex matrix = new InvertedIndex(docArray);
         matrix.input();
      System.out.println(matrix);
         }
      
      }         
   


