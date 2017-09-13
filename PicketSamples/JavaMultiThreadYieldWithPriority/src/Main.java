/*
This program is a multithreaded JAVA Program Example. The program creates three threads each printing characters.
Two threads print characters and a third prints integers. The threads are given priorities at the beginning of the run.
The number print thread has lowest priority. These threads do yield after certain number of prints
*/

import java.io.*;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.File;
import java.io.PrintWriter;

public class Main {

   public static void main(String[] args) throws Exception{
        // Create PrintWriter for the tasks.
        PrintWriter outf1;
        outf1 = new PrintWriter(new File("JavaMultiThreadOutPriority1.txt"));
        //First we must create tasks. We will pass these tasks to the threads for running.

        Runnable printA = new PrintChar('a', 100, outf1);
        Runnable printB = new PrintChar('b', 100, outf1);
        Runnable print100 = new PrintNum(100, outf1);
        // Runnable printbreak = new Pbreak();

        // Now Create the Threads

        Thread thread1 = new Thread(printA);
        Thread thread2 = new Thread(printB);
        Thread thread3 = new Thread(print100);

        //Now give them priorities
        thread1.setPriority(Thread.MAX_PRIORITY);
        thread2.setPriority(Thread.NORM_PRIORITY);
        thread3.setPriority(Thread.MIN_PRIORITY);

        //Thread thread4 = new Thread(prntbreak);

        //Now start the threads
        thread1.start();
        thread2.start();
        thread3.start();
        //thread4.start();

        outf1.flush();
    }

}


class PrintChar implements Runnable {
    private char charToPrint; //this is the character to print
    private int times; //this is the number of times to repeat the print
    private PrintWriter outf;
    //now for the constructor

    public PrintChar(char c, int t, PrintWriter outf1) {
        charToPrint = c;
        times = t;
        outf = outf1;
    }

        /*
        Every task object must have a run() method. This overrides system run() method and tells the system what task to
        perform.
        */

    public void run() {
        for (int i = 1; i <= times; i++) {
            System.out.print(charToPrint);
            outf.print(charToPrint);
            outf.flush();

            if (i % 10 == 0) {
                System.out.println();
                outf.println();
                outf.flush();
            }
        }
    }
}

class PrintNum implements Runnable {
    private int lastNum; //this is the last integer to print
    private PrintWriter outf; //this is the output text file from this thread

    //now for the constructor
    public PrintNum(int n, PrintWriter out1) {
        lastNum = n;
        ;
        outf = out1;
    }//end of constructor
    //now the task run method
    public void run(){
            /*
            NOTE: that System.out.print is a system function that is synced with the processing thread so no lfush is
            necessary to assure the output buffer is empty. BUT Printing to a text file from a PrintWriter is not synced.
            ALWAYS FLUSH THE PrintWriter BUFFER after each print to assure it gets on the text file before the thread loses
            control */
        for (int i=1; i<=lastNum; i++){
            System.out.print(" "+i);
            outf.print(" "+i);
            outf.flush();

            if (i%5==0){
                System.out.println();
                outf.println();
                outf.flush();
            }
        }
    }
}

