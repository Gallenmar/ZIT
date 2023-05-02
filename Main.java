// Grupas nosaukums: ZIT

// Grupas dalībnieki:
// Ņina Aļjanaki 12. gr. 221RDB018
// Konstantīns Siņica 12. gr. 151RMC125
// Vasīlijs Dvils-Dmitrijevs 12. gr. 221RDB021
// Kirils Bedins 18. gr. 221RDC018
// Vladislavs Jacina 12. gr. 221RDB038
// Ņikita Plotņikovs 12. gr. 221RDB021 (Grupas vadītājs)

import java.io.*;
import java.util.*;

class Data {
  static ArrayList<Byte> theData = new ArrayList<>();
  static ArrayList<Byte> theDataHuffman = new ArrayList<>();
}

class Huffman {
  public static class Node {
    int frequency;
  }

  public static class Branch extends Node {
    Node left;
    Node right;
  }

  public static class Leaf extends Node {
    byte character;
  }

  public static void encoder(FileOutputStream out) {
    Node tree = treeMaker(freqTable());
    fileToBinary((Branch) tree, out, freqTable());

  }

  public static int bitBuffer[] = new int[8];
  public static int bitBufferLength = 0;
  public static int bitBuffer2 = 0;
  public static byte bitBuffer1 = 0;
  public static boolean BufferFileEnd = false;

  public static void decoder(FileInputStream input, ArrayList<Byte> theDataHuffman) {
    ArrayList<Node> leafs = new ArrayList<>();
        try {
            int treesize = (byte)input.read()& 0xFF;
            int freq ;
            byte c;
        for(int i=0;i<treesize;i++){
            c = (byte)input.read();
            freq = ((byte)input.read() << 24) | ((byte)input.read() << 16) | ((byte)input.read() << 8) | (byte)input.read()& 0xFF;
            Leaf leaf = new Leaf();
            leaf.character = c;
            leaf.frequency = freq;
            leafs.add(leaf); 
           // System.out.println("char: "+c+" freq: "+freq);

        } 

        } catch (Exception e) {
            System.out.println(e);
            // TODO: handle exception
        }
       

        Node tree = treeMaker(leafs);
    bitBuffer2 = 0;
    bitBufferLength = 0;
    //
    byte c = 0;
    int debug = 0;
    while ((!BufferFileEnd || bitBufferLength - 1 > 0)) {
      c = FindCharacterInTree((Branch) tree, input);
      //System.out.print(c);
      theDataHuffman.add(c);
    }
  }

  public static ArrayList<Node> freqTable() {
    ArrayList<Node> leafs = new ArrayList<>();
    HashMap<Byte, Integer> frequencyMap = new HashMap<>();
    for (byte c : Data.theData) {
      frequencyMap.put(c, frequencyMap.getOrDefault(c, 0) + 1);
    }
    for (byte c : frequencyMap.keySet()) {
      Leaf leaf = new Leaf();
      leaf.character = c;
      leaf.frequency = frequencyMap.get(c);
      leafs.add(leaf);
    }
    return leafs;
  }

  public static Node treeMaker(ArrayList<Node> leafs) {
    // making a tree
    while (leafs.size() != 1) {
      // finding 2 smallest frequency nodes
      int smallest = Integer.MAX_VALUE;
      int secondSmallest = Integer.MAX_VALUE;
      int indexOfSmallest = 0;
      int indexOfSecondSmallest = 0;

      for (int i = leafs.size() - 1; i >= 0; i--) {
        int current = leafs.get(i).frequency;
        if (current < smallest) {
          secondSmallest = smallest;
          smallest = current;
          indexOfSecondSmallest = indexOfSmallest;
          indexOfSmallest = i;
        } else if (current < secondSmallest) {
          secondSmallest = current;
          indexOfSecondSmallest = i;
        }
      }
      // creating a new branch holding those 2 branches
      Branch root = new Branch();
      root.left = leafs.get(indexOfSmallest);
      root.right = leafs.get(indexOfSecondSmallest);
      root.frequency = leafs.get(indexOfSmallest).frequency + leafs.get(indexOfSecondSmallest).frequency;

      // deleting 2 elements in the right order
      if (indexOfSmallest > indexOfSecondSmallest) {
        leafs.remove(indexOfSmallest);
        leafs.remove(indexOfSecondSmallest);
      } else {
        leafs.remove(indexOfSecondSmallest);
        leafs.remove(indexOfSmallest);
      }

      // adding newly created branch at the end
      leafs.add(leafs.size(), root);
    }
    return leafs.get(0);

  }

  public static Node fileToBinary(Branch tree, FileOutputStream out, ArrayList<Node> leafs) {
    leafs.size();
    int freqency = 0;
    try {// try tree
      out.write((char) leafs.size());// size of tree
      for (int i = 0; i < leafs.size(); i++) {
        freqency = leafs.get(i).frequency;
        out.write((char) ((Leaf) leafs.get(i)).character);
        out.write(freqency >> 24);
        out.write(freqency >> 16);
        out.write(freqency >> 8);
        out.write(freqency);

      }
    } catch (Exception e) {

    }

    bitBuffer1 = 0;
    bitBufferLength = 0;
    try {
      for (int i = 0; i < Data.theData.size(); i++) {

        writeBits(FindCharacter(tree, Data.theData.get(i)), out);

      }

      // System.out.println(bitBuffer1&0xFF);
      int totalbits = bitBufferLength;
      out.write((char) (bitBuffer1 << 8 - bitBufferLength));
      out.write((char) bitBufferLength);
      out.close();
    } catch (Exception e) {
      System.out.println(e);
    }
    return tree;
  }

  public static long FindCharacter(Branch tree, byte c) {
    long C_Founded = 0;
    if (tree.left instanceof Leaf) {
      if (((Leaf) tree.left).character == c)
        return 1;
    } else {
      C_Founded = FindCharacter((Branch) tree.left, c);
      if (C_Founded != -1)
        return C_Founded * 10 + 1;
    }

    if (tree.right instanceof Leaf) {
      if (((Leaf) tree.right).character == c)
        return 2;
    } else {
      C_Founded = FindCharacter((Branch) tree.right, c);
      if (C_Founded != -1)
        return C_Founded * 10 + 2;
    }
    return -1;
  }

  public static byte FindCharacterInTree(Branch tree, FileInputStream Input) {
    int bit;
    while (true) {
      bit = BitFromBuffer(Input);
      if (bit == 0) {
        if (tree.left instanceof Leaf) {
          return ((Leaf) tree.left).character;
        } else {
          tree = (Branch) tree.left;
        }
      } else if (bit == 1) {
        if (tree.right instanceof Leaf) {
          return ((Leaf) tree.right).character;
        } else {
          tree = (Branch) tree.right;
        }
      } else {
        System.out.println("End of the file");
        return ' ';
      }
    }

  }

  public static int BitFromBuffer(FileInputStream input) {
    int bit = 0;
    int newBits = 0;
    try {
      if (bitBufferLength <= 24 && !BufferFileEnd) {
        while (bitBufferLength <= 24) {

          newBits = input.read();

          if (newBits == -1) {
            bitBufferLength = bitBufferLength + ((byte) (bitBuffer2 >> 8 & 0xFF)) - 16;
            BufferFileEnd = true;
            break;
          }

          bitBuffer2 |= newBits;
          if (bitBufferLength < 24)
            bitBuffer2 <<= 8;
          bitBufferLength += 8;
        }
      }
      bitBufferLength--;
      bit = (bitBuffer2 >> 31) & 1;
      bitBuffer2 <<= 1;
      return bit;
    } catch (Exception e) {
      System.out.println(e);
      BufferFileEnd = true;
      return -1;
    }
  }

  public static int debug = 0;

  public static void writeBits(long bits, FileOutputStream out) throws IOException {// there 2 is 1, and 1 is 0;
                                                                                   // inverse order, Used with
                                                                                   // FindCharacter which give inversed
                                                                                   // order
    while (bits > 0) {
      // if(debug==44)
      // System.out.println("debug");
      int lastBit = (int)(bits % 10) - 1; // extract the last digit and-1, 2=1, 1=0
      bits /= 10; // remove the last digit from the number
      bitBuffer1 <<= 1;
      bitBuffer1 |= lastBit;
      if (++bitBufferLength == 8) { // if the buffer is full write to file
        out.write(bitBuffer1);
        debug++;
        // ystem.out.println(debug);
        bitBufferLength = 0; // reset the buffer Lenght
        bitBuffer1 = -1;// reset the buffer
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

public class Main {

  public static void main(String[] args) {
    Scanner sc = new Scanner(System.in);
    String choiseStr;
    String sourceFile, resultFile, firstFile, secondFile;

    int terminationCounter = 0;
    loop: while (true) {

      choiseStr = sc.next();

      switch (choiseStr) {
        case "comp":
          terminationCounter = 0;
          System.out.print("source file name: ");
          sourceFile = sc.next();
          System.out.print("archive name: ");
          resultFile = sc.next();
          comp(sourceFile, resultFile);
          break;
        case "decomp":
          terminationCounter = 0;
          System.out.print("archive name: ");
          sourceFile = sc.next();
          System.out.print("file name: ");
          resultFile = sc.next();
          decomp(sourceFile, resultFile);
          break;
        case "size":
          terminationCounter = 0;
          System.out.print("file name: ");
          sourceFile = sc.next();
          size(sourceFile);
          break;
        case "equal":
          terminationCounter = 0;
          System.out.print("first file name: ");
          firstFile = sc.next();
          System.out.print("second file name: ");
          secondFile = sc.next();
          System.out.println(equal(firstFile, secondFile));
          break;
        case "about":
          terminationCounter = 0;
          about();
          break;
        case "exit":
          break loop;
        case "q":
          break loop;
        default:
          terminationCounter += 1;
          switch (terminationCounter) {
            case 6:
              System.out.println("This command doesn't exist. 1 more attempt or the program will exit.");
            case 7:
              break loop;
            default:
              System.out.println("This command doesn't exist. Try again.");
          }
      }
    }

    sc.close();
  }

  public static void readFile(String sourceFile) {
    try {
      FileInputStream reader = new FileInputStream(sourceFile);
      int currentByte;
      while ((currentByte = reader.read()) != -1) {
        byte currentChar = (byte) currentByte;
        Data.theData.add(currentChar);
      }
      reader.close();
    } catch (IOException e) {
      System.out.println("Error: " + e.getMessage());
    }
  }

  public static void writeFile(String outputFilePath) {
    try {
      FileOutputStream writer = new FileOutputStream(outputFilePath);
      for (byte c : Data.theDataHuffman) {
        writer.write(c);
      }
      writer.close();
    } catch (IOException e) {
      System.out.println("Error: " + e.getMessage());
    }
  }

  public static void printData() {
    for (byte c : Data.theData) {
      System.out.print(c);
    }
    System.out.println();
  }

  public static void comp(String sourceFile, String resultFile) {
    File file = new File(sourceFile);
    if (file.exists()) {
      readFile(sourceFile);
    } else {
      System.out.println("File doesn't exist");
    }
    // LZ77.encoder();
    try {
      FileOutputStream writer = new FileOutputStream(resultFile);
      Huffman.encoder(writer);
      writer.close();
    } catch (Exception e) {
      // System.out.println(e);
    }

    // Data.theData.clear();
    // Data.theDataHuffman.clear();
  }

  public static void decomp(String sourceFile, String resultFile) {

    try {
      FileInputStream reader = new FileInputStream(sourceFile);

      Huffman.decoder(reader, Data.theDataHuffman);
      reader.close();
    } catch (Exception e) {
      // System.out.println(e);
    }

    // LZ77.decoder();

    writeFile(resultFile);
    // Data.theData.clear();
    // Data.theDataHuffman.clear();
  }

  public static void size(String sourceFile) {
    try {
      FileInputStream f = new FileInputStream(sourceFile);
      System.out.println("size: " + f.available());
      f.close();
    } catch (IOException ex) {
      // System.out.println(ex.getMessage());
    }

  }

  public static boolean equal(String firstFile, String secondFile) {
    try {
      FileInputStream f1 = new FileInputStream(firstFile);
      FileInputStream f2 = new FileInputStream(secondFile);
      int k1, k2;
      byte[] buf1 = new byte[1000];
      byte[] buf2 = new byte[1000];
      do {
        k1 = f1.read(buf1);
        k2 = f2.read(buf2);
        if (k1 != k2) {
          f1.close();
          f2.close();
          return false;
        }
        for (int i = 0; i < k1; i++) {
          if (buf1[i] != buf2[i]) {
            f1.close();
            f2.close();
            return false;
          }

        }
      } while (k1 == 0 && k2 == 0);
      f1.close();
      f2.close();
      return true;
    } catch (IOException ex) {
      // System.out.println(ex.getMessage());
      return false;
    }
  }

  public static void about() {
    System.out.println("Grupas nosaukums: ZIT");
    System.out.println("");
    System.out.println("Grupas dalībnieki:");
    System.out.println("Ņina Aļjanaki 12. gr. 221RDB018");
    System.out.println("Konstantīns Siņica 12. gr. 151RMC125");
    System.out.println("Vasīlijs Dvils-Dmitrijevs 12. gr. 221RDB021");
    System.out.println("Kirils Bedins 18. gr. 221RDC018");
    System.out.println("Vladislavs Jacina 12. gr. 221RDB038");
    System.out.println("Ņikita Plotņikovs 12. gr. 221RDB021 (Grupas vadītājs)");
  }
}
