import java.util.ArrayList;
import java.util.HashMap;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class Huffman {
    public static class Node {
        int frequency;
    }
    public static class Branch extends Node{
        Node left;
        Node right;
    }
    public static class Leaf extends Node{
        char character;
    }
    

       public static void encoder(FileOutputStream out){
        Node tree = treeMaker(freqTable());
        fileToBinary((Branch)tree,out,freqTable()); // return binary file
        
    }
    //used variables in many places
    public static int bitBuffer[]= new int[8];
    public static int bitBufferLength=0;
    public static int bitBuffer2=0;
    public static byte bitBuffer1=0;
    public static boolean BufferFileEnd=false;
    //public static int totalChcount=31;//:TODO Insert Count of characters here!!

    public static void decoder(FileInputStream input , ArrayList<Character> theDataHuffman){
        //My idea is that I will insert in the output all chars probabilites
        // and then I will use the probabilites to create a tree, when this code will be maded
        // and then I will use the tree to decode the input
        //test tree
        //for (char c : frequencyMap.keySet()) { 
        //    Leaf leaf = new Leaf(); 
        //    leaf.character = c; 
        //    leaf.frequency = frequencyMap.get(c); 
        //    leafs.add(leaf); 
        //} 
        ArrayList<Node> leafs = new ArrayList<>();
        try {
            int treesize = (byte)input.read();
            int freq ;
            char c;
        for(int i=0;i<treesize;i++){
            c = (char)input.read();
            freq = ((byte)input.read() << 24) | ((byte)input.read() << 16) | ((byte)input.read() << 8) | (byte)input.read();
            Leaf leaf = new Leaf();
            leaf.character = c; ;
            leaf.frequency = freq;
            leafs.add(leaf); 
            System.out.println("char: "+c+" freq: "+freq);

        } 

        } catch (Exception e) {
            // TODO: handle exception
        }
       

        Node tree = treeMaker(leafs);
        //flush the data, if it is not empty from encoder
        bitBuffer2=0;
        bitBufferLength=0;
        //
        char c=0;
        int debug=0;
        while((!BufferFileEnd || bitBufferLength-1 >0)){
            c=FindCharacterInTree((Branch)tree, input);
            //if(debug++>115)
            //System.out.println("debug");
            System.out.print(c);
                theDataHuffman.add(c);
                //System.out.println(c);
            }
        // prototype code
        System.out.println();
        System.out.println("Decoder empty......");
    }


    public static ArrayList<Node> freqTable(){ 
        ArrayList<Node> leafs = new ArrayList<>(); 
        HashMap<Character, Integer> frequencyMap = new HashMap<>(); 
        for (char c : Data.theData){ 
            frequencyMap.put(c, frequencyMap.getOrDefault(c, 0) + 1); 
        } 
        for (char c : frequencyMap.keySet()) { 
            Leaf leaf = new Leaf(); 
            leaf.character = c; 
            leaf.frequency = frequencyMap.get(c); 
            leafs.add(leaf); 
        } 
        return leafs; 
    }

    public static Node treeMaker(ArrayList<Node> leafs){
        // making a tree
        while(leafs.size()!=1){
            // finding 2 smallest frequency nodes
            int smallest = Integer.MAX_VALUE;
            int secondSmallest = Integer.MAX_VALUE;
            int indexOfSmallest = 0;
            int indexOfSecondSmallest = 0;
        
            for (int i = leafs.size() -1; i >= 0; i--){
                int current = leafs.get(i).frequency;
                if (current<smallest){
                    secondSmallest = smallest;
                    smallest = current;
                    indexOfSecondSmallest = indexOfSmallest;
                    indexOfSmallest = i;
                } else if (current < secondSmallest){
                    secondSmallest = current;
                    indexOfSecondSmallest = i;
                }
            }
            // creating a new branch holding those 2 branches
            Branch root = new Branch();
            root.left = leafs.get(indexOfSmallest);
            root.right = leafs.get(indexOfSecondSmallest);
            root.frequency = leafs.get(indexOfSmallest).frequency + leafs.get(indexOfSecondSmallest).frequency;
        
            //System.out.print(indexOfSmallest+" "+indexOfSecondSmallest+" freq: "+ root.frequency + " size before: "+ leafs.size());

            // deleting 2 elements in the right order
            if (indexOfSmallest>indexOfSecondSmallest){
                leafs.remove(indexOfSmallest);
                leafs.remove(indexOfSecondSmallest);
            } else {
                leafs.remove(indexOfSecondSmallest);
                leafs.remove(indexOfSmallest);
            }

            // adding newly created branch at the end
            leafs.add(leafs.size(), root);
        
            //System.out.print(" new index:"+leafs.size());
            //System.out.println();
        }
        return leafs.get(0);

    }
    public static Node fileToBinary(Branch tree,FileOutputStream out,ArrayList<Node> leafs){

        // Notes on what to actually do:
        // u get a tree and u have to either:
        // create a new data object
        // or overwrite a existing Data
        // Idk what is better honestly, but think what makes more sense and do that
        
        // and then insert compressed data in the Data

        // u can use:
        // parseInt("001001", 2) | parseInt(x, base)
        // for writing bit by bit
        
        // dont forget to insert the tree itself not just the compressed data

        // dont hesitate to ask questions or make remarks or ask for help
        leafs.size();
        int freqency=0;
        try {//try tree
            out.write((char)leafs.size());//size of tree
            for(int i=0;i<leafs.size();i++){
                freqency=leafs.get(i).frequency;
                out.write((char)((Leaf)leafs.get(i)).character);
                out.write(freqency >> 24);
                out.write(freqency >> 16);
                out.write(freqency >> 8);
                out.write(freqency);
                
            }
        } catch (Exception e) {
            // TODO: handle exception
        }
        


        bitBuffer1=0;
        bitBufferLength=0;
        String test = "ABFBBBNNBBAFFF"+ (char)248+"OOORRRRABNBABBNBNBNBNAAAAARR" + (char)248+"ABFBBBNNBBOAFFF"+ (char)248+"OOORRRRABNBABBNBNBNBNAAAAARR" + (char)248;
        try {
        for(int i=0;i<test.length();i++){
            writeBits(FindCharacter(tree, test.charAt(i)),out);  
            //FindCharacter as "11212" and insert these bits
            //in bitBuffer1, If bitbuffer ==8 it will write it to the file
            // if string ended and bitbuffer1 is not empty, it will write it to the file as "111" <<5 11100000
            //:TODO: I need to count totalChcount before writing to file, I need to know how many bits to write
            //I think to make it in freqency table

            }
            
                System.out.println(bitBuffer1&0xFF);
                int totalbits = bitBufferLength;
                out.write((char)(bitBuffer1<< 8-bitBufferLength));
                out.write((char)bitBufferLength);
        out.close();
        } catch (Exception e) {
            System.out.println(e);
        }
        System.out.println("fileToBinary not erroring out");
        return tree;//IDK what to return there
    }
    
    public static int FindCharacter(Branch tree, char c){
        int C_Founded=0;
        if (tree.left instanceof Leaf){
            if(((Leaf) tree.left).character==c)
                return 1;
        }
        else{
            C_Founded = FindCharacter((Branch) tree.left, c);
            if(C_Founded!=-1)
                return C_Founded*10+1;
        }
    
        if (tree.right instanceof Leaf){
            if(((Leaf) tree.right).character==c)
                return 2;
        }
        else {
            C_Founded = FindCharacter((Branch) tree.right, c);
            if(C_Founded!=-1)
                return C_Founded*10+2;
        }
        return -1;
    }
    
public static char FindCharacterInTree(Branch tree,FileInputStream Input){
    int bit;
    while(true){//IF everything is working correctly, this will not loop forever
        bit = BitFromBuffer(Input);
        if (bit ==0){
            if (tree.left instanceof Leaf){
                return (char)((Leaf) tree.left).character;
            }
            else{
                tree = (Branch) tree.left;
            }
        }
        else if (bit ==1){
            if (tree.right instanceof Leaf){
                return (char)((Leaf) tree.right).character;
            }
            else{
                tree = (Branch) tree.right;
            }
        }
        else{//if bit is -1
            System.out.println("End of the file");
            return ' ';
        }
    }


}
public static int BitFromBuffer(FileInputStream input) {
    int bit = 0;
    int newBits = 0;
    // read next byte if bitBufferLength == 0,
    // and get first bit from bitBuffer1
    // and shift bitBuffer1 to the left
    // "00011" << "00110" << "01100" << "11000"
    try {
        if (bitBufferLength <= 24 && !BufferFileEnd) {
            while (bitBufferLength <= 24) {
                
                newBits = input.read();

                if (newBits == -1) {
                    bitBufferLength = bitBufferLength + ((byte)(bitBuffer2>>8 & 0xFF))-16;// Last byte is count of used bits 
                    // ((byte)(bitBuffer2>>8 & 0xFF)) is amount of used bits
                    //(used bits count + used bits byte = 16 bits)
                    //16bits-amount of used bits is << amount of bits to shift
                    BufferFileEnd = true;
                    if(bitBufferLength<24)
                    bitBuffer2 <<= 8;
                    break;
                }
                
                bitBuffer2 |= newBits;
                if(bitBufferLength<24)
                bitBuffer2 <<= 8;
                bitBufferLength += 8;
            }
        }
        bitBufferLength--;
        bit = (bitBuffer2 >> 31) & 1;
        bitBuffer2 <<= 1; // changed from (bitBuffer1 >> 31) & 1;
        return bit;
    } catch (Exception e) {
        System.out.println(e);
        BufferFileEnd = true;
        return -1;
    }
}
public static int debug=0;

public static void writeBits(int bits,FileOutputStream out) throws IOException 
{//there 2 is 1, and 1 is 0;
    // inverse order, Used with FindCharacter which give inversed order
    while(bits>0){
        //if(debug==44)
        //System.out.println("debug");
    int lastBit = (bits % 10)-1; // extract the last digit and-1, 2=1, 1=0
    bits /= 10;               // remove the last digit from the number
    bitBuffer1<<=1;
    bitBuffer1|=lastBit;
    if (++bitBufferLength == 8) {  // if the buffer is full write to file
        out.write(bitBuffer1);
        debug++;
        //ystem.out.println(debug);
        bitBufferLength = 0;  // reset the buffer Lenght
        bitBuffer1=-1;// reset the buffer
        }
        
    }
}

    // function that takes node, "" as default indent, 0 as default level
    // and outputs a somewhat readable tree
    // Made with chatGPT and doesn't need to go into production
    public static void traverse(Node node, String indent, int level) { // (node,"",0)
        if (node instanceof Leaf) {
            Leaf leaf = (Leaf) node;
            System.out.println(indent + "Leaf: " + leaf.character + ", Freq: " + leaf.frequency + ", Level: " + level);
        } else if (node instanceof Branch) {
            Branch branch = (Branch) node;
            System.out.println(indent + "Branch: Freq: " + branch.frequency + ", Level: " + level);
            traverse(branch.left, indent + "    ", level + 1);
            traverse(branch.right, indent + "    ", level + 1);
        }
    }
    


}
