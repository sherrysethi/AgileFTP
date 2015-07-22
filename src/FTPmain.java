import java.util.Scanner;


public class FTPmain {

	private static String Username = "test";
	private static String Password = "test";

	/**
	 * @param args
	 */
	public static void main(String[] args) {
	    System.out.println("Enter Username: ");
	    Scanner uName = new Scanner(System.in);
	    String UN = uName.nextLine();

	    System.out.println("Enter Password: ");
	    Scanner password = new Scanner(System.in);
	    String PW = password.nextLine();

	    if (UN.equals(Username) && PW.equals(Password))
	    {
	        System.out.println("Login Successful !");
	    }
	    else {
	        System.out.println("Please try again.");
	    }
	    
	    System.out.println("Enter server address: ");
	    Scanner serverAdd = new Scanner(System.in);
	    String serverAddStr = serverAdd.nextLine();
	    
	}

}
