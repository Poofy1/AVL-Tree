# Java AVL Tree

## Description
This program can create, read, edit, and find data within an AVL tree. Appropriate rotations are applied automatically when needed. The tree format supports a set number and sized string fields and int fields. 

## Executing program
The "ALV" driver file accepts 2 arguments by default:
* args[0] = < treeFileName >
* args[1] = <"print" / "findI" / "findS" / "write" / "edit">

Here is are the input structures for each command

Printing the tree:
* args[0] = < treeFileName >
* args[1] = "print"

Return int from key:
* args[0] = < treeFileName >
* args[1] = "findI"
* args[2] = < key >

Return string from key:
* args[0] = < treeFileName >
* args[1] = "findS"
* args[2] = < key >

Create tree file with .txt data:
* args[0] = < treeFileName >
* args[1] = "write"
* args[2] = < dataFile.txt >
* args[3] = < # of string fields >
* args[4] = < max length of all strings >
* args[5] = < # of int fields >
* args[6] = "print" (optional)

Edit existing tree file with .txt data:
* args[0] = < treeFileName >
* args[1] = "write"
* args[2] = < dataFile.txt >
* args[6] = "print" (optional)

Examples:
* java AVL customTree print
* java AVL customTree findI 100
* java AVL customTree findS 100
* java AVL customTree write in.txt 2 30 3 print (this will define 2 string fields with a length of 30 as well as 3 int fields)
* java AVL customTree edit CustomIn.txt print

## Input Data

The input data must be stored in a .txt file with the following format. <br />
< KeyValue Strings ints > <br />
< KeyValue Strings ints > <br />
< KeyValue Strings ints > <br />
...

* Data must be seperated by spaces
* To remove data, write < # KeyValue > within the data .txt file
Here is an example input with data sizes (2, 30, 3):
```
100 Mary Shelley 8 30 1791
50 Anton Chekhov 1 29 1860
5 Virginia Woolf 1 25 1882
75 Jane Austen 12 16 1775
2 James Joyce 2 2 1882
80 Oscar Wilde 10 16 1854
12 Iris Murdoch 7 15 1919
# 50
# 5
90 Leo Tolstoy 9 9 1828
200 Fyodor Dostoyevsky 11 11 1821
```

## License
This project is licensed under the MIT License - see the LICENSE.md file for details
