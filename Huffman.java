import java.util.ArrayList;
import java.util.HashMap;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

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
    

    public static void encoder(FileWriter out){
        Node tree = treeMaker(freqTable());
        fileToBinary((Branch)tree,out); // return binary file
        
    }
       public static void encoder(FileWriter out){
        Node tree = treeMaker(freqTable());
        fileToBinary((Branch)tree,out); // return binary file
        
    }
    //used variables in many places
    public static int bitBuffer[]= new int[8];
    public static int bitBufferLength=0;
    public static byte bitBuffer1=0;
    public static boolean BufferFileEnd=false;
    public static int totalChcount=31;//:TODO Insert Count of characters here!!

    public static void decoder(FileReader input , ArrayList<Character> theDataHuffman){
        //My idea is that I will insert in the output all chars probabilites
        // and then I will use the probabilites to create a tree, when this code will be maded
        // and then I will use the tree to decode the input
        //test tree
        Node tree = treeMaker(freqTable());
        //flush the data, if it is not empty from encoder
        bitBuffer1=0;
        bitBufferLength=0;
        //
        char c=0;
        int debug=0;
        while(!BufferFileEnd && (totalChcount-- >0)){
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
    public static Node fileToBinary(Branch tree,FileWriter out){
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
        bitBuffer1=0;
        bitBufferLength=0;
        String test = "AAABBBBNNBBAABNBABBNBNBNBNAAAAA";
        try {
        for(int i=0;i<test.length();i++){
            writeBits(FindCharacter(tree, test.charAt(i)),out);  
            //FindCharacter as "11212" and insert these bits
            //in bitBuffer1, If bitbuffer ==8 it will write it to the file
            // if string ended and bitbuffer1 is not empty, it will write it to the file as "111" <<5 11100000
            //:TODO: I need to count totalChcount before writing to file, I need to know how many bits to write
            //I think to make it in freqency table

            }
            if (bitBuffer1!=-1){
                System.out.println(bitBuffer1&0xFF);
                int totalbits = bitBufferLength;
                out.write((char)(bitBuffer1<< 8-bitBufferLength));
        }
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
    
public static char FindCharacterInTree(Branch tree,FileReader Input){
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
public static int BitFromBuffer(FileReader Input) {
    int bit=0;
    //read next byte if bitBufferLength ==0,
    // and get first bit from bitBuffer1
    // and shift bitBuffer1 to the left
    //"00011" <<"00110" << "01100" << "11000"
    try {
        if(bitBufferLength==0){
            bitBuffer1=(byte)Input.read();
            bitBufferLength=8;
        }
        bitBufferLength--;
        bit=(bitBuffer1 >> 7) & 1;
        bitBuffer1<<=1;
            return bit;
        
    } catch (Exception e) {
        System.out.println(e);
        BufferFileEnd=true;
        return -1;
    }
}
public static int debug=0;

public static void writeBits(int bits,FileWriter out) throws IOException 
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
