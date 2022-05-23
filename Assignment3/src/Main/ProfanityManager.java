package Main;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.FileWriter;
import java.util.Locale;
import java.util.Scanner;


public class ProfanityManager {
    
    /*
    *Input: String - checks string for pattern, replaces matches with ***
    *Output: String - fixed string
    */
    public static String ManageString(String str) {
        
        String[] profanityList = {"badwords", "badwordt", "badwordy"};  //List of banned words that will get filtered out. TESTING
	
        String profanityCheckedMessage = str;
        String[] splitStr = profanityCheckedMessage.split(" "); //split input string into array
        
        /*
        *Loop through input string array
        *Loop through profanity list
        *Check if any profanity list entry matches input - if so then replace with **
        */
        for (int j = 0; j < splitStr.length; j++ ) {
            for (int i = 0; i < profanityList.length; i++) {
                if(splitStr[j].toLowerCase(Locale.ROOT).contains(profanityList[i])) {
                    splitStr[j] = splitStr[j].replaceAll("(?i)" + profanityList[i], "*****");
                }
            }
        }

        StringBuilder buildString = new StringBuilder();
        //convert input string array into string
        for (String part : splitStr) {
            buildString.append(part + " ");
        }

        profanityCheckedMessage = buildString.toString();

        
        return profanityCheckedMessage;
    }
    /*
    *Input: String inputDirectory path, String Output directory path 
    *Output: void (right now writes to output directory)
    */
    public static void ManageFiles(String inputDir, String outputDir) {

        try {
            FileWriter myWriter = null;
            File inputdir = new File(inputDir); //specify input directory path
            File outputdir = null; //will hold output directory path
            File[] directoryListing = inputdir.listFiles();
            Scanner readFile = null;

            /*
            * check if input directory contains files + is a directory
            * go through each file in input directory, read line by line then run through
            * ManageString and write to outputdirectory path (create files when needed)
            */
            if (directoryListing != null && inputdir.isDirectory()) {
                for (File child : directoryListing) {
                    System.out.println("file name:" + child.getName());
                    readFile = new Scanner(child);
                    String input; 
                    //StringBuffer sb = new StringBuffer(); //testing
                    
                    outputdir = new File(outputDir + "\\" + child.getName());
                    myWriter = new FileWriter(outputdir);
                    
                    /*
                    * if file does not exist - create file
                    */
                    if(!outputdir.exists()){
                        outputdir.createNewFile();
                        outputdir.canWrite();
                        outputdir.canRead();
                    } else {
                    	System.out.println("Directory does not exist");
                    }
                    
                    while (readFile.hasNextLine()) {
                        input = readFile.nextLine();
                        input = ManageString(input);
                        //inputToBytes = input.getBytes();
                        //sb.append(input + " "); //testing
                        myWriter.write(input);
                    }
                    myWriter.close();
                    ///System.out.println(sb.toString()); //testing read file
                }
            }


            
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

    }
    
    /*
     * Input: file (path/file.txt), output directory (dir/path)
     * Manages single file > filters for profanity then writes to output dir (creates file)
     * 
     * */
    public static void ManageSingleFile(String inputFile, String outputDir) {
    	
    	FileWriter myWriter = null;
        File inputfile = new File(inputFile); //specify input directory path
        File outputdir = null; //will hold output directory path
        
        Scanner readFile = null;
        
        if(inputfile.exists()) {
        	try {
				readFile = new Scanner(inputfile);
				outputdir = new File(outputDir + "\\" + inputfile.getName());
                myWriter = new FileWriter(outputdir);
                String input;
                
                //create new file in output dir
                if(!outputdir.exists()){
                    outputdir.createNewFile();
                    outputdir.canWrite();
                    outputdir.canRead();
                } else {
                	System.out.println("Directory does not exist");
                }
                
                while (readFile.hasNextLine()) {
                    input = readFile.nextLine();
                    input = ManageString(input);
                    //inputToBytes = input.getBytes();
                    //sb.append(input + " "); //testing
                    myWriter.write(input);
                }
                myWriter.close();
                
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.out.println("File does not exist");
				e.printStackTrace();
			}
        }
    }


}

