import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;

public class AVL {

	public static void main(String[] args) throws IOException {
		
		//Default required inputs:
		//args[0] = <tree filename>
		//args[1] = <"print" / "findI" / "findS" / "write" / "edit">
		
		//Printing tree
		//args[0] = <treeFileName>
		//args[1] = print
		//Example: customTree print
		
		//Return int from key
		//args[0] = <treeFileName>
		//args[1] = findI
		//args[2] = key
		//Example: customTree findI 100
		
		//Return string from key
		//args[0] = <treeFileName>
		//args[1] = findS
		//args[2] = key
		//Example: customTree findS 100
		
		//Create tree file with .txt data
		//args[0] = <treeFileName>
		//args[1] = write
		//args[2] = <dataFile.txt>
		//args[3] = <# of string fields>
		//args[4] = <max length of all strings>
		//args[5] = <# of int fields>
		//args[6] = print (optional)
		//Example: customTree write in.txt 2 30 3 print
		
		//Edit existing tree file with .txt data
		//args[0] = <treeFileName>
		//args[1] = write
		//args[2] = <dataFile.txt>
		//args[6] = print (optional)
		//Example: customTree edit CustomIn.txt print

		AVLTree tree = null;
		
		
		switch (args[1]) {
			case "print":
				System.out.println("Tree Data:");
				tree.print();
				break;
			
			case "findI":
				try {
					tree = new AVLTree(args[0]);
				} catch (Exception e){
					System.out.println("Missing tree file");
					return;
				}
				LinkedList<Integer> findI = tree.intFind(Integer.parseInt(args[2]));
				System.out.println(args[2] + ": " + findI);
				break;
				
			case "findS":
				try {
					tree = new AVLTree(args[0]);
				} catch (Exception e){
					System.out.println("Missing tree file");
					return;
				}
				LinkedList<Integer> findS = tree.intFind(Integer.parseInt(args[2]));
				System.out.println(args[2] + ": " + findS);
				break;
				
			case "write":
				//Read input .txt file
				BufferedReader file = new BufferedReader(new FileReader(args[2]));

				//Parse Input String array size
				int sFieldNum = Integer.parseInt(args[3]);
				int sFieldMax = Integer.parseInt(args[4]);
				int sFieldLens[] = new int[sFieldNum];
				for (int i = 0; i < sFieldNum; i++) {
					sFieldLens[i] = sFieldMax;
				}
				
				//Parse input int array size
				int iFieldsLen = Integer.parseInt(args[5]);
				
				//Create tree
				tree = new AVLTree(args[0], sFieldLens, iFieldsLen);
				
				//Write Data
				writeData(file, tree, sFieldNum, iFieldsLen, sFieldMax);

				//Print out tree if requested
				if(args.length == 7) {
					if (args[6].equals("print")) {
						System.out.println("Tree Data:");
						tree.print();
					}
				}
				break;
				
			case "edit":
				try {
					tree = new AVLTree(args[0]);
				} catch (Exception e){
					System.out.println("Missing tree file");
					return;
				}
				
				//Read input .txt file
				BufferedReader dataAdd = new BufferedReader(new FileReader(args[2]));

				//Parse Input String array size
				int sFieldNum2 = tree.numStringFields; 
				int sFieldMax2 = tree.fieldLengths[0]; 
				int sFieldLens2[] = new int[sFieldNum2];
				for (int i = 0; i < sFieldNum2; i++) {
					sFieldLens2[i] = sFieldMax2;
				}
				
				//Parse input int array size
				int iFieldsLen2 = tree.numIntFields;
				
				//Write Data
				writeData(dataAdd, tree, sFieldNum2, iFieldsLen2, sFieldMax2);
				
				//Print out tree if requested
				if(args.length == 4) {
					if (args[3].equals("print")) {
						System.out.println("Tree Data:");
						tree.print();
					}
				}
				break;
				
				
				
			default:
				System.out.println("Please call one of the following commands <print / findI / findS / write / edit>");
				return;
		}
		
		
		
		
		
		
		
		
		//Close tree
		tree.close();
		System.out.println("Finished");
	}
	

	//Write data
	private static void writeData(BufferedReader file, AVLTree tree, int sFieldNum, int iFieldsLen, int sFieldMax ) throws IOException {
		
		//Initialize
		String line;
		String fields[];
		int keys[] = new int[100];
		int numKeys = 0;
		char sFields[][] = new char[sFieldNum][];
		int iFields[] = new int[iFieldsLen];
		
		//Read input file
		line = file.readLine();
		while (line != null) {
			fields = line.split(" ");
			if (fields[0].equals("#")) {
				tree.remove(new Integer(fields[1]));
			} else {
				//Read Key
				keys[numKeys] = new Integer(fields[0]);
				
				//Read Strings
				for (int i = 0; i < sFieldNum; i++) {
					sFields[i] = Arrays.copyOf(fields[i+1].toCharArray(), sFieldMax);
				}
				
				//Read ints
				for (int i = 0; i < iFieldsLen; i++) {
					//System.out.println(fields[i + sFieldNum + 1]);
					iFields[i] = Integer.parseInt(fields[i + sFieldNum + 1]);
				}
				
				//Insert
				tree.insert(keys[numKeys], sFields, iFields);
				numKeys++;
			}
			line = file.readLine();
		}
	}


}
