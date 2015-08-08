//To execute from command line:
//javac -cp commons-net-3.3.jar FTPmain.java in both Windows and Linux
//java -cp .;commons-net-3.3.jar FTPmain in Windows
//java -cp .:commons-net-3.3.jar FTPmain in Linux

import java.io.IOException;
import java.util.Scanner;
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
	    //call function which will implement command line interface
	    FTPmain.ftplooper();
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
	    	else if(inputCommand.contains("cd"))//to change current remote directory
	    	{
	    		try
	    		{
	    			String[] dirName;
	    			dirName = inputCommand.split(" ");//split according to command and directory name
	    			boolean success = client.changeWorkingDirectory(dirName[1]);
	    			//showServerReply(client);
	    			if (success) 
	    			{
	    	                System.out.println("Successfully changed working directory.");
	    	            
	    			}
	    			else 
	    			{
	    	                System.out.println("Failed to change working directory.");
	    	        }
	    	 	}
	    		catch(Exception e)
	    		{
	    			System.out.println("Exception in changing working directory.");	    			
	    		}
	    	}
	    	else if(inputCommand.startsWith("put"))//to upload a file on the server
	    	{
	    		try
	    		{
	    			String[] commands;
	    			commands=inputCommand.split(" ");//split the command and filename
	    			FTPmain.ftpputfile(commands[1]);//invoke function with filename
	    		}
	    		catch(Exception e)
	    		{
	    			
	    		}
	    	}
	    	else
	    	{
	    		System.out.println("Bad Command");
	    	}
	    }
	}
	
	
	
	
	public static boolean ftplogout()//function for logging out of the server
	{
		try
		{
			if(client.logout()==true)
			{
				System.out.println("Logged out from server");
				displaystring="ftp>>";
				return true;
			}
			else
			{
				System.out.println("Error logging out of server. Could not exit.");
				
			}
		}
		catch(IOException e)
		{
			//e.printStackTrace();
			System.out.println("ERROR. Not logged in!");
		}
		return false;
	}
	public static boolean ftplogin()//function for logging in to the server
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
		System.out.println("7. ll: list local files");
		System.out.println("8. cd <<Directory name>>: Change current remote directory");
		System.out.println("9. put <<filename>>: Upload a file to the server. File must be present in the current local directory.");
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
}
