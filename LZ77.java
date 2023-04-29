import java.util.Arrays;

//import java.util.ArrayList;

public class LZ77 {
    public static class Triplet{
        byte first=0;
        byte second=0;
        char next='A';
    }

    public static void encoder(){
        // Read Data.theData and either:
        // create a new data object
        // or overwrite a existing Data
        // Idk what is better honestly, but think what makes more sense and do that

        // and then insert compressed data in the Data

        // u can use:
        // parseInt("001001", 2) | parseInt(x, base)
        // for writing bit by bit

        // dont hesitate to ask questions or make remarks or ask for help




        // prototype code
        // System.out.println("LZ77 not working");





        // shell for window
        char[] searchBuf = {'B','A','N'};
        char[] lookAhead = {'A','N','A', 'F'};
        Triplet tmp = new Triplet();
        tmp = window(searchBuf,lookAhead);
        System.out.println("first byte is: "+tmp.first+tmp.second+tmp.next);
        
    }

    public static void decoder(){

        // prototype code
        System.out.println("LZ77 decoding......");
    }

    public static Triplet subWindow(char[] searchBuf,char[] lookAhead){
        Triplet trip = new Triplet();
        byte maxLength = 0;
        byte maxOffset = 0;
        char nextChar = lookAhead[0];
        byte len = 0;
        byte i=0;
        while (i + len < searchBuf.length && len < lookAhead.length && searchBuf[i + len] == lookAhead[len]) {
            len++;
        }
        if (len > maxLength) {
            maxLength = len;
            maxOffset = i;
            if (i + len < searchBuf.length) {
                nextChar = searchBuf[i + len];
            } else {
                nextChar = lookAhead[len];
            }
        }
        maxOffset = (byte)(searchBuf.length-maxOffset);
        if (maxLength == 0) {
            trip.first = 0;
            trip.second = 0;
            trip.next = lookAhead[0];
        } else {
            trip.first = maxOffset;
            trip.second = maxLength;
            trip.next = nextChar;
            searchBuf = Arrays.copyOfRange(searchBuf, searchBuf.length- maxOffset, searchBuf.length);
            lookAhead = Arrays.copyOfRange(lookAhead, 0, maxLength);
            subWindow(searchBuf, lookAhead);
        }
        
        return trip;
    }

    public static Triplet window(char[] searchBuf,char[] lookAhead){
        Triplet trip = new Triplet();
        byte maxLength = 0;
        byte maxOffset = 0;
        char nextChar = lookAhead[0];
        for (byte i = 0; i < searchBuf.length; i++) {
            byte len = 0;
            while (i + len < searchBuf.length && len < lookAhead.length && searchBuf[i + len] == lookAhead[len]) {
                len++;
            }
            if (len > maxLength) {
                maxLength = len;
                maxOffset = i;
                nextChar = lookAhead[len];
            }
        }
        maxOffset = (byte)(searchBuf.length-maxOffset);
        if (maxLength == 0) {
            trip.first = 0;
            trip.second = 0;
            trip.next = lookAhead[0];
        } else {
            trip.first = maxOffset;
            trip.second = maxLength;
            trip.next = nextChar;
            Triplet tmpTrip = new Triplet();

            searchBuf = Arrays.copyOfRange(searchBuf, searchBuf.length- maxOffset, searchBuf.length);
            lookAhead = Arrays.copyOfRange(lookAhead, maxLength, lookAhead.length);
            tmpTrip = window(searchBuf, lookAhead);
            if (tmpTrip.first == maxOffset){
                trip.second += tmpTrip.second;
                trip.next = tmpTrip.next;
            }

        }
        
        return trip;

        
    }
}
