//A simple command line FTP client
//CS 510 Agile Summer 2015
//Group Number 7
//Group Members: Deven B, Abhishek C, Sherry S, Nakul K, Maithily G

//To execute from command line:
//set path=C:\Program Files\Java\jdk1.7.0_02\bin
//javac -cp commons-net-3.3.jar FTPmain.java in both Windows and Linux
//java -cp .;commons-net-3.3.jar FTPmain in Windows
//java -cp .:commons-net-3.3.jar FTPmain in Linux

import java.io.IOException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.BufferedOutputStream;
import java.io.Console;
import java.io.FileOutputStream;
import java.io.File;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.FileInputStream;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

public class FTPmain //main class for FTP Client
{
	public static FTPClient client; //client is an object of type FTPClient
	public static String displaystring = "ftp>>";
	static FTPFile[] files;
	
	public static void main(String[] args) 
	{
		client = new FTPClient(); //initialize client
		System.out.println("Welcome to the Dexters FTP Client! For detailed help, type 'help' and press enter");
	    FTPmain.ftplooper();//call function which will implement command line interface
	}
	
	public static void ftplooper()//looper function for implementing command line interface
	{
		while(true)
	    {
	    	System.out.println(displaystring); //display the command line
	    	Scanner inputread = new Scanner(System.in); //read command from user
	    	String inputCommand = inputread.nextLine();
	    	
	    	if(inputCommand.equals("help")) //user asks for help, display documentation
	    	{
	    		FTPmain.ftpdisplayhelp();
	    	}
	    	
	    	else if(inputCommand.equals("ls")) //list all files and directories
	    	{
	    		FTPmain.ftplistfiles();
	    	}
	    	
	    	else if(inputCommand.equals("exit")) //exit the code. Automatically logs off client if logged in
	    	{
	    		break;
	    	}
	    	else if(inputCommand.equals("login"))
	    	{
	    		if(FTPmain.ftplogin()== false) //log into client
	    		{
	    			System.out.println("Error logging in. Please check entered details and try again.");
	    		}
	    	}
	    	else if(inputCommand.equals("logout"))
	    	{
	    		FTPmain.ftplogout(); //log out from client
	    	}
	    	else if(inputCommand.startsWith("get")) //for retrieving single file
	    	{
	    		try
	    		{
	    			String[] commands;
	    			commands=inputCommand.split(" "); //split the command and filename
	    			FTPmain.ftpgetfile(commands[1]); //invoke function with filename
	    		}
	    		catch(Exception e)
	    		{
	    			System.out.println("Usage: get <filename.filetype>"); //in case user doesn't enter filename
	    		}
	    	}
	    	else if(inputCommand.equals("ll")) //for getting local files
	    	{   
	    		File curDir = new File(".");
	    		try
	    		{
	    			File[] listOfFiles = getAllFiles(curDir);
	    			 for(File f : listOfFiles)
	    			 {
	    		        	if(f.isDirectory())
	    		                System.out.println(f.getName());
	    		            if(f.isFile())
	    		            {
	    		                System.out.println(f.getName());
	    		            }
	    		     }
	    		}
	    		catch(Exception e)
	    		{
	    			System.out.println("Usage: ll"); //to handle unexpected exceptions
	    		}
	    	}
	    	else if(inputCommand.startsWith("cd"))//to change current remote directory
	    	{
	    		try
	    		{
	    			String[] dirName;
	    			dirName = inputCommand.split(" ");//split according to command and directory name
	    			boolean success = client.changeWorkingDirectory(dirName[1]);
	    			if (success) 
	    			{
	    	                System.out.println("Successfully changed working directory.");
	    	            
	    			}
	    			else 
	    			{
	    	                System.out.println("Failed to change working directory. Check directory name and try again.");
	    	        }
	    	 	}
	    		catch(Exception e)
	    		{
	    			System.out.println("Error. Usage: cd <directoryname>");	    			
	    		}
	    	}
	    	else if(inputCommand.startsWith("put"))//to upload a file on the server
	    	{
	    		try
	    		{
	    			String[] commands;
	    			boolean fileupload;
	    			commands=inputCommand.split(" ");//split the command and filename
	    			fileupload=FTPmain.ftpputfile(commands[1]);//invoke function with filename
	    			if(fileupload)
	    			{
	    				System.out.println("File uploaded successfully");
	    			}
	    			else
	    			{
	    				System.out.println("Could not upload file.");
	    			}
	    		}
	    		catch(Exception e)
	    		{
	    			System.out.println("Error. Usage: put <filename>");
	    		}
	    	}
	    	else if(inputCommand.contains("mkdir" )) //Create a new directory
	    	{
	    		try
	    		{
	    			String[] dirName ;
	    			dirName = inputCommand.split(" ");//split command and directory name
	    			System.out.println("\n Are you sure you want to create a new directory: " + dirName[1] + "?\n Press Y or N") ;
	    			Scanner userInput = new Scanner(System.in);//Read confirmation from user
	    			String userIn = userInput.nextLine() ;
	    			if(userIn.toUpperCase().equals("Y"))
	    			{
	    				if(FTPmain.IsValidName(dirName[1]))//Check if input is valid directory name
	    				{
	    					if(!FTPmain.ftpDirectoryExists(dirName[1]))//Check if directory already exists 
	    					{
	    						if(!FTPmain.ftpCreateDirectory(dirName[1]))//Create the directory
	    						{
	    							System.out.println("\n ERROR: Directory cannot be created");
	    						}
	    					}
	    					else
	    					{	//Directory name already exists
	    						System.out.println("\n ERROR: Directory already exists") ;
	    					}
	    				}
	    				else
	    				{	//Some special characters appear in directory name
	    					System.out.println("\n ERROR: Filename contains special characters. Please try again.");
	    				}
	    			}
	    			else 
	    			{	//User enters N
	    				System.out.println("\n DIRECTORY CREATION ABORTED");
	    			}
	    		}
	    		catch(Exception e)
	    		{
	    			System.out.println("Error. Usage: mkdir <directoryname>");
	    		}
	    	}
	    	else if(inputCommand.equals("pwd"))//To print the current working directory
	    	{
	    		FTPmain.ftpPrintCurrentDir();
	    	}
	    	else if(inputCommand.startsWith("rmdir"))//To delete a directory on the server
	    	{
	    		try
	    		{
	    			String[] commands;
	    			boolean bCheck=false;
	    			commands=inputCommand.split(" ");//Split the command and filename
	    			bCheck=FTPmain.ftpdeletedirectory(commands[1]);//Invoke function with filename
	    			if(bCheck)
	    			{
	    				System.out.println("Removed directory");
	    			}
	    			else
	    			{
	    				System.out.println("Failed to remove directory. Check if directory exists.");
	    			}
	    		}
	    		catch(Exception e)
	    		{
	    			System.out.println("Error. Usage: rmdir <directoryname>");
	    		}
	    	}
	    	else if(inputCommand.startsWith("rm"))//To delete a file from the server
	    	{
	    		try 
	    		{
	    			String[] args;
	    			args = inputCommand.split(" ");//Split the command and filename
	    			String fileToDelete = args[1];
	    		    boolean deleted = client.deleteFile(fileToDelete);//Delete the file and handle response
	    		    if (deleted) 
	    		    {
	    		        System.out.println("The file was deleted successfully.");
	    		    } 
	    		    else 
	    		    {
	    		        System.out.println("Could not delete the file.");
	    		    }
	    		} 
	    		catch (Exception ex) 
	    		{
	    		    System.out.println("Error. Usage: rm <Filename>");
	    		}
	    	}
	    	else//The command is not recognized
	    	{
	    		System.out.println("Bad Command");
	    	}
	    }
	}
	
	
	
	
	public static boolean ftplogout()//function for logging out of the server
	{
			try
			{
				if(client.logout()==true)//Log out of the server
				{
					System.out.println("Logged out from server");
					displaystring="ftp>>";
					client.disconnect();
					return true;
				}
				else
				{
					System.out.println("Error logging out of server. Could not exit.");
				}
			}
			catch(IOException e)
			{
				System.out.println("ERROR. Not logged in!");
			}
		return false;
	}
	
	public static boolean ftplogin()//function for logging in to the server
	{
		if(client.isConnected()==false)
		{
			//get username from user
			System.out.println("Enter Username: ");
			Scanner uName = new Scanner(System.in);
			String UN = uName.nextLine();

			Console console = System.console(); //for securely reading password
			String PW;
			if(console==null)//if no console found then password input is displayed on screen
			{
				//get password from user
				System.out.println("WARNING. Console not found, password input is unsecured.");
				System.out.println("Enter Password: ");
				Scanner password = new Scanner(System.in);
				PW = password.nextLine();
			}
			else//else input is hidden from screen
			{
				char passwordArray[]=null;
				passwordArray = console.readPassword("Enter password: ");
				PW=new String(passwordArray);
			}
	    
			//get server address from user
			System.out.println("Enter server address: ");
			Scanner serverAdd = new Scanner(System.in);
			String serverAddStr = serverAdd.nextLine();
	    
			//check if login successful, handle response from server
			try
			{
				client.connect(serverAddStr, 21); //connect to the address provided by the user 
				if(client.login(UN, PW))
				{
					System.out.println("Successully logged in to " + serverAddStr);
					displaystring = "ftp "+UN+"@"+serverAddStr+">>"; //change display string
					return(true);
				}
			}
			catch(IOException e)
			{
				return false;
			}
		}
		else
		{
			System.out.println("Already logged in!");
		}
		return false;
	}
	
	public static void ftpdisplayhelp()//function for displaying help message
	{
		//Display help messages
		System.out.println("Welcome to the Dexters FTP Client!");
		System.out.println("The following commands are available:");
		System.out.println("1. ls: List all directories and files. Directories are marked with ~ and files are marked with *");
		System.out.println("2. exit: Exit the program.");
		System.out.println("3. help: Display this help message.");
		System.out.println("4. login: Log in to remote server.");
		System.out.println("5. logout: Log out of current session");
		System.out.println("6. get <<filename>>: Retrieve a file from the server. File must be present at the server.");
		System.out.println("7. ll: list local files. Files will be listed from current local working directory");
		System.out.println("8. cd <<Directory name>>: Change current remote directory");
		System.out.println("9. put <<filename>>: Upload a file to the server. File must be present in the current local directory.");
		System.out.println("10. mkdir <<Directory name>>: Create a directory on the remote server.");
		System.out.println("11. pwd: Print the current remote working directory");
		System.out.println("12. rm <<Filename>>: Delete the file from the server.");
		System.out.println("13. rmdir <<Directory name>>: Remove the directory from the server.");
		System.out.println("End of help text");
	}
	
	public static void ftplistfiles() //function for listing all files in the current server directory
	{
		try
		{
			files = client.listFiles(); //get list of files from server and store it in array of type FTPFile
			for(FTPFile file : files)//for each stored entry
			{
				if(file.isDirectory()) //check if the current entry is a directory
				{
					System.out.println("~ " + file.getName()); //display with ~
				}
				else
				{
					System.out.println("* " + file.getName()); //if file display with *
				}
			}
		}
		catch(IOException e)
		{
			//e.printStackTrace();
			System.out.println("ERROR. Not logged in!");
		}
		
	}
	
	public static boolean ftpgetfile(String filename) //function for retrieving single file from server
	{
		String currentDirectory;
		currentDirectory = System.getProperty("user.dir"); //get the current directory
		//System.out.println(currentDirectory);
		File downloadedfile = new File(currentDirectory + "\\" + filename); //append a backslash to current directory while creating a new file
		try
		{
			OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(downloadedfile)); //output stream for writing file
			boolean success = client.retrieveFile(filename, outputStream); //retrieve file from server
			outputStream.close(); //close the output stream
			if (success) //if downloaded successfully
			{
				System.out.println("File has been downloaded successfully to " + currentDirectory + "\\" + filename);
				return true;
			}
		}
		catch(IOException e1)
		{
			System.out.println("Error. File not found.");
		}
		return false;
	}
	
    private static  File[] getAllFiles(File curDir)//function to list local files
    {
        File[] filesList = curDir.listFiles();//list the files from current directory
        return filesList;
    }
    
    public static boolean ftpputfile(String filename)//function for uploading file on server
    {//the file must be present in the current working directory on client machine
    	try
    	{
    		boolean flag;
    		InputStream input = new FileInputStream(new File(filename));//create an input stream for reading file
    		flag = client.storeFile(filename, input);//put the file in current working directory on server
    		if(flag==true)//if upload successful
    		{
    			System.out.println("Upload successful!");
    			return true;
    		}
    		else
    		{
    			System.out.println("Could not transfer file. Please check file name or connection.");
    		}
    	}
    	catch(Exception e)//if file not found or any other exception
    	{
    		System.out.println("Error uploading file. Please check file name or connection");
    	}
    	return false;
    }
    
    private static boolean ftpCreateDirectory(String pathOfDir) // This method creates a new directory
	{
		boolean isCreated = false ;
		try 
		{
			boolean dirCreated = client.makeDirectory(pathOfDir) ;
			if(dirCreated) 
			{
				isCreated = true;
			}
			else
			{
				isCreated = false;
			}
		}
		catch(Exception exp)
		{
			System.out.println("\n PROBLEM CREATING OF DIRECTORY");
			isCreated = false ;
		}
		return isCreated ;
	}
    
    public static boolean ftpDirectoryExists(String checkDir) //This method checks if the dir we want to create already exists
	{
		boolean isExists = true;
		try 
		{
			boolean checkDirExists = client.changeWorkingDirectory(checkDir);
			if(!checkDirExists)
			{
				isExists = false ;
			}
			else
			{
				isExists = true ;
			}
		}
		catch(Exception Ex)
		{
			System.out.println("\n ERROR: We have some trouble with existing Directories and new directory creations");
			isExists = false;
		}
		return isExists;
	}
    
    public static boolean IsValidName(String dirName)//To check if entered directory name is valid
    {
		boolean isValid = true ;
		try
		{
			// Allow numerals in the dir name
			Pattern regex = Pattern.compile("[0-9]");
			Matcher matcher = regex.matcher(dirName);
			if(matcher.find())
			{
				isValid = true;
			}
			else
			{
				// If there are numerals in the dir name then check for special characters
				// if special characters found then dir name is inValid
				Pattern regex2 = Pattern.compile("[$&+,:;=?@#|'<>.-^*/()\"%!]");
				Matcher matcher2 = regex2.matcher(dirName);
				if(matcher2.find())
				{
					isValid = false;
				}
				else
				{
					// If there is no special character then dir name is valid
					isValid = true;
				}
			}	
		}
		catch(Exception ex)
		{
			System.out.println("\n ERROR: We have some trouble validating name of directory");
			isValid = true ;
		}
		return isValid ;
	}
    
    public static void ftpPrintCurrentDir()//Function to print current working directory
    {
    	try
    	{
    		System.out.println("Current working directory is: " + client.printWorkingDirectory());
    	}
    	catch(Exception e)
    	{
    		System.out.println("Error printing current directory.");
    	}
    }
    private static boolean ftpdeletedirectory(String commands) //function to list delete remote directory
	{
		boolean b = false;
		try 
		{
			b=client.removeDirectory(commands);
			if(b == true)
			{
				System.out.println("Directory Deleted !");
			}
			else
			{
				System.out.println("Directory doesn't exist !!!");
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		return b;
	}
}