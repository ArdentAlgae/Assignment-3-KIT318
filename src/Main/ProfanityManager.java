package Main;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.io.FileWriter;
import java.util.Locale;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;


public class ProfanityManager {
    
    /*
    *Input: String - checks string for pattern, replaces matches with ***
    *Output: String - fixed string
    */
    public static String ManageString(String str, Request request) throws InterruptedException {
        
        String[] profanityList = {"badwords", "badwordt", "badwordy"};  //List of banned words that will get filtered out. TESTING
	
        String profanityCheckedMessage = str;
        String[] splitStr = profanityCheckedMessage.split(" "); //split input string into array
        
       
        request.setProgress(0);
        float currentProgress = 0;
        float splitProgress = 100/splitStr.length;
       
        if (request.getType().equals(Request.Type.STRING))
        {
        	request.setProgress(currentProgress);
        }
        
        /*
        *Loop through input string array
        *Loop through profanity list
        *Check if any profanity list entry matches input - if so then replace with **
        */
        for (int j = 0; j < splitStr.length; j++ ) 
        {
            for (int i = 0; i < profanityList.length; i++) 
            {
            	if (!request.getStatus().equals(Request.Status.CANCELLED))
            	{
            		if(splitStr[j].toLowerCase(Locale.ROOT).contains(profanityList[i])) {
                        splitStr[j] = splitStr[j].replaceAll("(?i)" + profanityList[i], "*****");
            	}

                }
            }
            
            if (request.getType().equals(Request.Type.STRING))
            {
            	currentProgress = currentProgress + splitProgress;
                request.setProgress(currentProgress);
                //System.out.print(splitStr.toString()+" Progress: "+currentProgress);
                //Use for testing
                //TimeUnit.SECONDS.sleep(5);
            }
        }
        
        if (request.getType().equals(Request.Type.STRING) && !request.getStatus().equals(Request.Status.CANCELLED))
        {
        	request.setProgress(100);
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
    public static void ManageFiles(String inputDir, String outputDir, Request request) throws InterruptedException {

        try {
            FileWriter myWriter = null;
            File inputdir = new File(inputDir); //specify input directory path
            File outputdir = null; //will hold output directory path
            File[] directoryListing = inputdir.listFiles();
            Scanner readFile = null;
            
            Integer currentFile = 0;
            request.setNumFiles(directoryListing.length);
            

            /*
            * check if input directory contains files + is a directory
            * go through each file in input directory, read line by line then run through
            * ManageString and write to outputdirectory path (create files when needed)
            */
            if (directoryListing != null && inputdir.isDirectory()) {
                for (File child : directoryListing) {
                	
                	
                	currentFile++;
                	request.setCurrentFile(currentFile);
                	//TESTING
                	//TimeUnit.SECONDS.sleep(5);
                	
                	
                    //System.out.println("file name:" + child.getName());
                    readFile = new Scanner(child);
                    String input; 
                    //StringBuffer sb = new StringBuffer(); //testing
                    
                   File outputDirectory = new File(outputDir);
                    
                    if (!outputDirectory.exists())
                    {
                    	outputDirectory.mkdir();
                    }
                    
                    outputdir = new File(outputDir + "\\" + child.getName());
                    myWriter = new FileWriter(outputdir);
                    
                    /*
                    * if file does not exist - create file
                    */
                    if(!outputdir.exists()){
                        outputdir.createNewFile();
                        outputdir.canWrite();
                        outputdir.canRead();
                    }
                    
                    
                    while (readFile.hasNextLine()) {
                        input = readFile.nextLine();
                        input = ManageString(input, request);
                        //inputToBytes = input.getBytes();
                        //sb.append(input + " "); //testing
                        myWriter.write(input);
                        myWriter.write("\n");
                      
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
    public static void ManageSingleFile(String inputFile, String outputDir, Request r) throws InterruptedException {
    	
    	FileWriter myWriter = null;
        File inputfile = new File(r.getInputFilePath()+"\\"+inputFile); //specify input directory path"
        
        File outputdir = new File(r.getOutputFilePath()); //will hold output directory path
        
        Scanner readFile = null;
        
        //TESTING
        //TimeUnit.SECONDS.sleep(5);
        
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
                	//System.out.println("Directory does not exist");
                }
                
                while (readFile.hasNextLine()) {
                    input = readFile.nextLine();
                    input = ManageString(input, r);
                    //inputToBytes = input.getBytes();
                    //sb.append(input + " "); //testing
                    myWriter.write(input);
                    myWriter.write("\n");
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