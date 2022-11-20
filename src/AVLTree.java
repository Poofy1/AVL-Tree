import java.io.*;
import java.util.*;

public class AVLTree {
	/*
	 * ALV tree of int keys and other data with fixed length
	 * character strings and ints. Data is stored in a random access file. Duplicates keys
	 * are not allowed. There must be at least 1 character string field
	 */

	private RandomAccessFile f;
	private long root; // the address of the root node in the file
	private long free; // the address in the file of the first node in the free list
	public int numStringFields; // the number of fixed length character fields
	public int fieldLengths[]; // the length of each character field
	public int numIntFields; // the number of integer fields

	private class Node {
		private int key;
		private char stringFields[][];
		private int intFields[];
		private long left;
		private long right;
		private int height;

		private Node(long l, int d, long r, char sFields[][], int iFields[]) {
			// constructor for a new node
			left = l;
			key = d;
			right = r;
			stringFields = sFields;
			intFields = iFields;
			height = 0;
		}

		private Node(long addr) throws IOException {
			// constructor for a node that exists and is stored in the file
			try {
				if(addr == -1) return;
				
				f.seek(addr);
				key = f.readInt();
				left = f.readLong();
				right = f.readLong();
				height = f.readInt();
				stringFields = new char[numStringFields][];

				for (int i = 0; i < numStringFields; i++) {
					stringFields[i] = new char[fieldLengths[i]];
					for (int j = 0; j < fieldLengths[i]; j++) {
						stringFields[i][j] = f.readChar();
					}
				}
				intFields = new int[numIntFields];
				for (int i = 0; i < numIntFields; i++) {
					intFields[i] = f.readInt();
				}
			} catch(Exception e) {
				System.out.println("Node not found");
			}
			
		}

		private void writeNode(long addr) throws IOException {
			// writes the node to the file at location addr
			f.seek(addr);
			f.writeInt(key);
			f.writeLong(left);
			f.writeLong(right);
			f.writeInt(height);
			for (int i = 0; i < numStringFields; i++) {
				for (int j = 0; j < fieldLengths[i]; j++) {
					try {
						f.writeChar(stringFields[i][j]);
					} catch(Exception e) {
						
					}
				}
			}
			for (int i = 0; i < numIntFields; i++) {
				f.writeInt(intFields[i]);
			}
		}
	}

	public AVLTree(String fname, int stringFieldLengths[], int numIntFields) throws IOException {
		// creates a new empty AVL tree stored in the file fname
		// the number of character string fields is stringFieldLengths.length
		// stringFieldLengths contains the length of each string field
		// create the file and write the header
		f = new RandomAccessFile(fname, "rw");
		root = 0;
		free = 0;
		this.numStringFields = stringFieldLengths.length;
		this.fieldLengths = stringFieldLengths;
		this.numIntFields = numIntFields;
		f.writeLong(root);
		f.writeLong(free);
		f.writeInt(numStringFields);
		for (int i = 0; i < numStringFields; i++) {
			f.writeInt(stringFieldLengths[i]);
		}
		f.writeInt(numIntFields);
	}

	public AVLTree(String fname) throws IOException {
		// reuse an existing tree stored in the file fname
		f = new RandomAccessFile(fname, "rw");
		root = f.readLong();
		free = f.readLong();
		numStringFields = f.readInt();
		fieldLengths = new int[numStringFields];
		for (int i = 0; i < numStringFields; i++) {
			fieldLengths[i] = f.readInt();
		}
		numIntFields = f.readInt();
	}

	private long GetFree() throws IOException {
		//Method that returns an available address space
		if (free == 0) {
			// the free list is empty, append the new node to the end of the file
			f.seek(f.length());
			return f.getFilePointer();
		} else {
			// the free list is not empty, reuse the first node in the free list
			long temp = free;
			Node m = new Node(free);
			free = m.left;
			return temp;
		}
	}
	
	public void insert(int k, char sFields[][], int iFields[]) throws IOException {
		// PRE: the number and lengths of the sFields and iFields match the expected
		// number and lengths
		// insert k and the fields into the tree
		// the string fields are null (‘\0’) padded
		root = insert(root, k, sFields, iFields);
	}

	private long insert(long rt, int k, char sFields[][], int iFields[]) throws IOException {
		// insert a new node recursively with key k and other data sFields and iFields
		// if k already exists, do nothing
		
		Node n;
		if (rt == 0) {
			// create a new node
			n = new Node(0, k, 0, sFields, iFields);
			long addr = GetFree();
			n.writeNode(addr);
			return addr;
			
		} else {
			n = new Node(rt);
			if (k < n.key) {
				//If key value is less than current node go left
				n.left = insert(n.left, k, sFields, iFields);
			} else if (k > n.key) {
				//If key value is less than current node go right
				n.right = insert(n.right, k, sFields, iFields);
			}
			n.height = 1 + Math.max(height(n.left), height(n.right));
			n.writeNode(rt);
			//Rotate the tree if needed so it remains balanced
			return balance(n, rt);
		}
	}
	
	
	
	private long balance(Node n, long addr) throws IOException {
		//Rebalance if node as a height difference of 2
		if (height(n.left) - height(n.right) == 2) {
			
			Node tempL = new Node(n.left);
			if (height(tempL.left) >= height(tempL.right)) {
				addr = rotateWithLeftChild(addr);
			} else {
				addr = doubleWithLeftChild(addr);
			}
		} else if (height(n.right) - height(n.left) == 2) {

			Node tempR = new Node(n.right);
			if (height(tempR.right) >= height(tempR.left)) {
				addr = rotateWithRightChild(addr);
			} else {
				addr = doubleWithRightChild(addr);
			}
		}
		
		return addr;
	}
	
	
	
	
	private int height(long addr) throws IOException {
		// return the height of the node at address addr
		if (addr == 0) {
			return -1;
		} else {
			Node n = new Node(addr);
			return n.height;
		}
	}

	private long rotateWithLeftChild(long rt) throws IOException {
		// rotate the subtree rooted at rt to the right
		// return the new root of the subtree
		Node n = new Node(rt);
		Node tempL = new Node(n.left);
		
		long pos = n.left;
		
		n.left = tempL.right;
		tempL.right = rt;
		
		n.height = 1 + Math.max(height(n.left), height(n.right));
		n.writeNode(rt);
		tempL.height = 1 + Math.max(height(tempL.left), height(tempL.right));
		tempL.writeNode(pos);
		
		return pos;
	}

	private long rotateWithRightChild(long rt) throws IOException {
		// rotate the subtree rooted at rt to the left
		// return the new root of the subtree
		Node n = new Node(rt);
		Node tempR = new Node(n.right);
		
		long pos = n.right;
	
		n.right = tempR.left;
		tempR.left = rt;
		
		n.height = 1 + Math.max(height(n.left), height(n.right));
		n.writeNode(rt);
		tempR.height = 1 + Math.max(height(tempR.left), height(tempR.right));
		tempR.writeNode(pos);
		
		return pos;
	}

	private long doubleWithLeftChild(long rt) throws IOException {
		// rotate the subtree rooted at rt’s left child to the right
		// then rotate the subtree rooted at rt to the right
		// return the new root of the subtree
		Node n = new Node(rt);
		n.left = rotateWithRightChild(n.left);
		n.writeNode(rt);
		return rotateWithLeftChild(rt);
	}

	private long doubleWithRightChild(long rt) throws IOException {
		// rotate the subtree rooted at rt's right child to the left
		// then rotate the subtree rooted at rt to the left
		// return the new root of the subtree
		Node n = new Node(rt);
		n.right = rotateWithLeftChild(n.right);
		n.writeNode(rt);
		return rotateWithRightChild(rt);
	}
	
	public void print() throws IOException {
		// Prints the contents of the nodes in the tree is ascending order of the key
		// print one node per line.
		// Includes the address of the node, the key, the character string fields
		// (without padding), the int fields, the height and the child addresses
		// inorder traversal
		print(root);
	}

	private void print(long rt) throws IOException {
		// recursive method to print the nodes in the tree rooted at rt
		if (rt == 0) return;
		
		Node x = new Node(rt);
		print(x.left);
		System.out.print("key:" + x.key + ", strings:");
		for (int i = 0; i < numStringFields; i++) {
			for (int j = 0; j < fieldLengths[i]; j++) {
				try {
					if (x.stringFields[i][j] != '\0')
						System.out.print(x.stringFields[i][j]);
				} catch(Exception e) {
					
				}
			}
			System.out.print(" ");
		}
		System.out.print(", ints:");
		
		for (int i = 0; i < numIntFields; i++) {
			try {
				System.out.print(x.intFields[i] + " ");
			} catch(Exception e) {
				
			}	
		}
		Node l = new Node(x.left);
		Node r = new Node(x.right);
		System.out.print(", height:" + x.height + ", left:" + l.key + ", right:" + r.key + "\n");
		
		print(x.right);
	}

	public void printFree() throws IOException {
		// Print one line containing a comma delimited list of the addresses of nodes in
		// the free list
		printFree(free);
	}

	private void printFree(long addr) throws IOException {
		if (addr == 0) {
			return;
		}
		f.seek(addr);
		long next = f.readLong();
		System.out.print(addr + ", ");
		printFree(next);
	}

	public LinkedList<String> stringFind(int k) throws IOException {
		// if k is in the tree return a linked list of the strings fields associated
		// with k
		// otherwise return null
		// The strings in the list must NOT include the padding (i.e the null chars)
		return stringFind(k, root);
	}

	private LinkedList<String> stringFind(int k, long addr) throws IOException {
		if (addr == 0) {
			return null;
		}
		Node n = new Node(addr);
		if (k == n.key) {
			LinkedList<String> list = new LinkedList<String>();
			for (int i = 0; i < numStringFields; i++) {
				String s = "";
				for (int j = 0; j < fieldLengths[i]; j++) {
					if (n.stringFields[i][j] != '\0')
						s += n.stringFields[i][j];
				}
				list.add(s);
			}
			return list;
		} else if (k < n.key) {
			return stringFind(k, n.left);
		} else {
			return stringFind(k, n.right);
		}
	}

	public LinkedList<Integer> intFind(int k) throws IOException {
		// if k is in the tree return a linked list of the integer fields associated
		// with k
		// otherwise return null
		return intFind(k, root);
	}

	private LinkedList<Integer> intFind(int k, long addr) throws IOException {
		if (addr == 0) {
			return null;
		}
		Node n = new Node(addr);
		if (k == n.key) {
			LinkedList<Integer> list = new LinkedList<Integer>();
			for (int i = 0; i < numIntFields; i++) {
				list.add(n.intFields[i]);
			}
			return list;
		} else if (k < n.key) {
			return intFind(k, n.left);
		} else {
			return intFind(k, n.right);
		}
	}

	public void remove(int k) throws IOException {
		// if k is in the tree remove the node with key k from the tree
		// otherwise do nothing
		root = remove(k, root);
	}

	private long remove(int k, long addr) throws IOException {
		if (addr == 0) {
			return 0;
		}
		Node n = new Node(addr);
		if (k < n.key) {
			n.left = remove(k, n.left);
			n.height = 1 + Math.max(height(n.left), height(n.right));
			n.writeNode(addr);
			return balance(n, addr);
		} else if (k > n.key) {
			n.right = remove(k, n.right);
			n.height = 1 + Math.max(height(n.left), height(n.right));
			n.writeNode(addr);
			return balance(n, addr);
		} else {
			if (n.left == 0 && n.right == 0) {
				// remove a leaf
				n.left = free;
				free = addr;
				return 0;
			} else if (n.left == 0) {
				// remove a node with only a right child
				long temp = n.right;
				n.left = free;
				free = addr;
				return temp;
			} else if (n.right == 0) {
				// remove a node with only a left child
				long temp = n.left;
				n.left = free;
				free = addr;
				return temp;
			} else {
				// remove a node with two children
				// find the inorder successor
				long temp = n.right;
				Node m = new Node(temp);
				while (m.left != 0) {
					temp = m.left;
					m = new Node(temp);
				}
				// copy the inorder successor to the node to be removed
				n.key = m.key;
				n.stringFields = m.stringFields;
				n.intFields = m.intFields;
				n.writeNode(addr);
				// remove the inorder successor
				n.right = remove(m.key, n.right);
				n.height = 1 + Math.max(height(n.left), height(n.right));
				n.writeNode(addr);
				return balance(n, addr);
			}
		}
	}

	public void close() throws IOException {
		// update everything in the file
		// close the random access file without losing any data
		f.seek(0);
		f.writeLong(root);
		f.writeLong(free);
		f.writeInt(numStringFields);
		for (int i = 0; i < numStringFields; i++) {
			f.writeInt(fieldLengths[i]);
		}
		f.writeInt(numIntFields);
		f.close();
	}
}
