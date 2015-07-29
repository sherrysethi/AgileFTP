import java.io.IOException;
import java.util.Scanner;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

public class FTPmain //main class for FTP Client
{

	private static String Username = "test";
	private static String Password = "test";
	public static FTPClient client; //client is an object of type FTPClient
	public static String displaystring = "ftp>>";
	public static void main(String[] args) 
	{
		client = new FTPClient(); //initialize client
	    //call function which will implement command line interface
	    FTPmain.ftplooper();
	}
	
	public static void ftplooper()
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
	    	
	    	else if(inputCommand.equals("exit")) //log out from client
	    	{
	    		break;
	    	}
	    	else if(inputCommand.equals("login"))
	    	{
	    		if(FTPmain.ftplogin()== false)
	    		{
	    			System.out.println("Error logging in. Please check entered details and try again.");
	    		}
	    	}
	    	else if(inputCommand.equals("logout"))
	    	{
	    		FTPmain.ftplogout();
	    	}
	    	else if(inputCommand.equals(""))
	    	{
	    	}
	    	else
	    	{
	    		System.out.println("Bad Command");
	    	}
	    }
	}
	
	public static boolean ftplogout()
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
	public static boolean ftplogin()
	{
		//get username from user
	    System.out.println("Enter Username: ");
	    Scanner uName = new Scanner(System.in);
	    String UN = uName.nextLine();

	    //get password from user
	    System.out.println("Enter Password: ");
	    Scanner password = new Scanner(System.in);
	    String PW = password.nextLine();
	    
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
				displaystring = "ftp "+UN+"@"+serverAddStr+">>";
				return(true);
			}
		}
		catch(IOException e)
		{
			return false;
		}
		return false;
	}
	
	public static void ftpdisplayhelp()
	{
		//Display help messages
		System.out.println("Welcome to the Dexters FTP Server!");
		System.out.println("The following commands are available:");
		System.out.println("1. ls: List all directories and files. Directories are marked with ~ and files are marked with *");
		System.out.println("2. exit: Log out of the current session and exit the program.");
		System.out.println("3. help: Display this help message.");
		System.out.println("4. login: Log in to remote server.");
		System.out.println("End of help text");
	}
	
	public static void ftplistfiles()
	{
		try
		{
			FTPFile[] files = client.listFiles(); //get list of files from server and store it in array of type FTPFile
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
}
