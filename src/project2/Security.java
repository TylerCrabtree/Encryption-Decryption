package project2;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Security {
	
	//Globals => Prep for modular math & remove magic numbers
	//Declare magic numbers and use BigInteger module 
	static String two= "2";
    static BigInteger G = new BigInteger(two);
    static BigInteger modular2 = new BigInteger(two);

	static String twelve= "12";
    static BigInteger modular12 = new BigInteger(twelve);
    
	static String five= "5";
    static BigInteger modular5 = new BigInteger(five);
    
	static String empty= "0";
	static String third= "3";
    // End Globals

	//Boolean function to determine if prime, used as flag later on.
    private static boolean prime(BigInteger n) {
        int equals = 1;							//Flag for comparison
        BigInteger three = new BigInteger(third);
        BigInteger modular2 = new BigInteger(two);
        BigInteger zero = new BigInteger(empty);   //Big int module for variables
        BigInteger x = three;					
        int compare = x.multiply(x).compareTo(n);
        while (equals != compare) {				// Check for comparisons
            if ((n.mod(x)).equals(zero)){		// Further check for comparisons
                return false;
            }
            x = x.add(modular2);			// update
            compare = x.multiply(x).compareTo(n);
        }
        return true;						// return 
    }

    
    // Key Gen
    static void keyGeneration (Scanner fileGrab){
    	System.out.println("Key generation\nPlease enter seed integer:"); //prompt
    	int max = 31;
        boolean check = fileGrab.hasNextInt();   // check if there is an int
        if (check == false){					// if not break
            System.out.println("Invalid seed");//prompt 
            return;
        }
        int seed = fileGrab.nextInt();      // grab seed
        Random random = new Random();		// grab random
        BigInteger Q = BigInteger.probablePrime(max,random);	//Big int module
        BigInteger P = Q.multiply(modular2).add(BigInteger.ONE);
        boolean flag = Q.mod(modular12).equals(modular5);  // set flags
        boolean flag2 = prime(P);					
        while((!(flag) || !flag2)) {	//compare
            Q = BigInteger.probablePrime(31,random);	 //Set Q
            P = Q.multiply(modular2).add(BigInteger.ONE);// Set P
            flag = Q.mod(modular12).equals(modular5);  // flags
            flag2 = prime(P);

        }
        Random grabRandom =new Random(seed);  
        int getInt = grabRandom.nextInt();
        BigInteger D = BigInteger.valueOf(getInt);
        int flag3 = D.compareTo(BigInteger.ZERO);
        int flag4 = D.compareTo(P);
        while ( ((flag3)!=1) && flag4 != -1){
        	getInt = random.nextInt();
            D =BigInteger.valueOf(getInt);
            flag3 =D.compareTo(BigInteger.ZERO);
            flag4 = D.compareTo(P);

        }
        BigInteger modFinalPow= G.modPow(D, P);
        String pubkey = P.toString()+" "+ G.toString()+" "+ modFinalPow.toString();
        String prikey = P.toString()+" "+ G.toString()+" "+ D.toString();
        writeText("prikey.txt", prikey);
        writeText("pubkey.txt", pubkey);
    }

    //Encryption
    static void encrypt ( ){
    	System.out.println("Encrypt.");     //prompt
        String buff = "0"+"0"+"0";
        Random grabRandom = new Random();	//grab random
        int zero =0;						// declare numbers
        int second = 2; 
        int fourth = 4;
        String code = new String();			//complete code string
        String pubkeys = new String((readIn("pubkey.txt")));	//grab pub key
        String blank = " ";
        String parts[]= pubkeys.split(blank);		//split on blanks
        BigInteger P = new BigInteger(parts[zero]);// big int module
        BigInteger encrypt = new BigInteger(parts[second]);
        long text = grabRandom.nextInt();	// get next int
        long pVal = P.longValue();		//flag
        while(pVal <= text ){		//check flag
            text=grabRandom.nextInt();		// set text
            pVal = P.longValue();			//update flag
        }
        BigInteger K = BigInteger.valueOf(text);
        String[] parse = readIn("ptext.txt").split(String.format("(?<=\\G.{%1$d})", fourth)); //reg ex found online
        for(String var: parse) {		// prep for encypt
            List<String> array = new ArrayList<>();
            int place = 0;
            while ( place < var.length()) {
                String position = Integer.toString(var.charAt(place));
                String string = buff.substring(position.length()) + position;
                array.add(string);
                place++;
            }
            List<String> textString = array;
            for (String val : textString) {  // start encrypt
                BigInteger mod1 = G.modPow(K, P);

                BigInteger message = new BigInteger(val);
                
                BigInteger mod2 = ((encrypt.modPow(K, P)).multiply(message.mod(P))).mod(P);
                K = BigInteger.valueOf(grabRandom.nextInt());
                String complete = mod1.toString() + " " + mod2.toString() + "\n";
                code += complete;
            }
            writeText("ctext.txt", code);
        }

    }
    
    //decrypt
    private static void decrypt (){
    	System.out.println("Decryption.");
        String text = new String();
        int zero = 0;
        int first = 1;
        int second = 2;
        String blank = " ";
        String keys = new String((readIn("prikey.txt")));
        String parse[] = keys.split(blank);
        BigInteger P = new BigInteger(parse[zero]);
        BigInteger D = new BigInteger(parse[second]);
        String read =  new String(readIn("ctext.txt"));
        String[] message = read.split("\n");
        long exp = P.longValue() - D.longValue() - first;
        BigInteger modded = new BigInteger(String.valueOf(exp));
        for(String val: message){
            String[] decryptionVal = val.split(blank);
            BigInteger valZero = new BigInteger(decryptionVal[zero]);
            BigInteger valOne = new BigInteger(decryptionVal[first]);
            BigInteger mod = ((valZero.modPow(modded,P)).multiply(valOne.mod(P))).mod(P); 
            String word = Character.toString((char)mod.intValue());
            text +=word;
        }

        writeText("dtext.txt", text);
    }
    
    // Read in files.
    private  static  String readIn(String file){
    	String badRead = "Cannot read file.";
        try{
        	Charset set = StandardCharsets.ISO_8859_1;
            String words = new String(Files.readAllBytes(Paths.get(file)), set);
            return words;
        }
        catch (IOException e) {
            System.out.println(e);
        }
        return badRead;
    }  

    // Write to files. 
    static void writeText(String name, String data){
        try {
            FileWriter text = new FileWriter(name);
            text.write(data);
            text.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

  
   
    // MAIN
    public static void main(String[] args) {
        System.out.println("Welcome to an encrypt /decryption program (case insensitive):");   // prompt     
        int flag = 1;  // for loop
        while (flag == 1) { //loop until flag is set off
            Scanner fileGrab = new Scanner(System.in);  
            System.out.println("Please, enter 'k' for key genration.\nPlease, enter 'e'"
            		+ " for encrypt.\nPlease, enter 'd' for decryption.\nPlease, enter 'exit' to exit program.\nOr 'read' to look at the readMe.txt");
            String n = fileGrab.next();
            switch(n){  // get case (implemented lower and upper cases)
            case "k": 	keyGeneration (fileGrab);
            			break;
            case "K": 	keyGeneration (fileGrab);
						break;
            case "d": 	decrypt ();
            			break;
            case "D":	decrypt ();
            			break;
            case "e":  	encrypt ();
            			break;
            case "E":  	encrypt ();
						break;
            case "exit":System.out.println("The program has ended");
            			fileGrab.close();
                		flag = 2;
                		return;
            case "EXIT":System.out.println("The program has ended");
						fileGrab.close();
						flag = 2;
						return; 
            case "read":
            			String text = readIn("readme.txt");
            			System.out.print(text); 
            			break;
            	
            case "READ": 
            			String text2 = readIn("readme.txt");
						System.out.print(text2);     
						break;
        
            default: 	System.out.println("Please enter a correct command:\n");
            }
                
        }
        return;
        
    }
}
