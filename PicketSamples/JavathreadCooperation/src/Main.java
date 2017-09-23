/*This program is an example of creating threads that are both synced and coordinated.
The system has two threads that simulate a system tries to store information in a limited buffer space.
Specifically one thread presents integers for storage in a buffer(an array of limited size) and the
other thread takes characters from the buffer. The buffer of size 10 is initialized to all -9s
Here are the rules;
When Adding values you cannot add when there is not enough room for your number of values. When Deleting
values. There must always be at least one values to delete or you must wait. If there are not enough to delete, you
may delete to empty and then quit.
 */

import org.graalvm.compiler.core.common.type.ArithmeticOpTable;

import java.io.*;
import java.io.IOException;
import javax.swing.*;
import java.nio.Buffer;
import java.util.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.lang.IllegalStateException;
import java.util.NoSuchElementException;
import java.lang.*; //this allows the threads to be created as objects from the Thread class
//import java.lang.Thread; //this allows the threads to be run
import java.util.concurrent.*; // This allows the the creation of a thread pool thjat can all be launched
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
//one executor Executor

public class Main {

    public static void main(String[] args) throws Exception{
        //Create print writer for the tasks
        PrintWriter outf1;
        outf1 = new PrintWriter(new File("ThreadsCooperating.txt"));
        //create the thread pool
        ExecutorService executor = Executors.newFixedThreadPool(2);
        //Now create and launch 2 add and delete threads
        //create buffer
        Buffer Buf1=new Buffer(outf1);
        //create the add thread and give it priority
        Add tadd = new Add(Buf1);
        Delete tdelete = new Delete (Buf1);

        executor.execute(tadd);
        executor.execute(tdelete);
        System.out.println("Thread add and delete created and launched");
        System.out.flush();
        outf1.println("Thread add and delete created and launched");
        outf1.flush();

        //Now shut down the executor.
        executor.shutdown();
        //now let all threads shut down and wait till all tasks are finished

        while(!executor.isTerminated());
        System.out.println("What is in the Buffer?");
        Buf1.printdata();
        System.out.flush();

    }//end of main

    public static class Buffer{
        private int [] storage;
        private int fill;
        PrintWriter out1;
        private int bsize;
        private static Lock lockstrclr = new ReentrantLock(); // Create a lock for objects in this clas
        private static Condition infostorage = lockstrclr.newCondition(); // create a condition for the lock
        public Buffer (PrintWriter x){
            //this is the constructor
            out1 = x;
            fill = 0;
            //set the buffer storage to 10 integers
            storage = new int[10];
            //now initialize all integers to 0
            for (int i=0; i <= 9; i++)storage[i]=0;
        }
        public int getfill(){
            return fill;
        }
        public void printdata(){
            //list the storage array
            for (int i=0;i<=9;i++)
                System.out.println("x["+i+"]="+storage[i]);
                System.out.flush();
        }
        public void storedata (int amt, int value){
            //first acquire the lock
            lockstrclr.lock();
            //now try to store the data
            try{
                System.out.println("in storedata amt " + amt + "value " + value + " fill " + fill);
                while((fill+amt-1)>9)infostorage.await();//wait till there is room for the data
                System.out.println("now there is room amt " + amt + " value")
            }
        }
    }
}
