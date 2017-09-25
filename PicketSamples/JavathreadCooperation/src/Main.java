/*This program is an example of creating threads that are both synced and coordinated.
The system has two threads that simulate a system tries to store information in a limited buffer space.
Specifically one thread presents integers for storage in a buffer(an array of limited size) and the
other thread takes characters from the buffer. The buffer of size 10 is initialized to all -9s
Here are the rules;
When Adding values you cannot add when there is not enough room for your number of values. When Deleting
values. There must always be at least one values to delete or you must wait. If there are not enough to delete, you
may delete to empty and then quit.
 */

import java.io.*;
import java.io.IOException;
import javax.swing.*;
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
                System.out.println("now there is room amt " + amt + " value" + value + " fill" + fill);
                out1.println("in storedata amt " + amt + "value " + value + " fill " + fill);
                System.out.flush();
                out1.flush();
                //now the store the data
                for(int i=fill; i <=fill+amt-1;i++)storage[i] = value;
                fill = fill+amt; //fill always points to the next available spot
                System.out.println("Now the array looks like");
                for (int i=0; i<=fill-1;i++)System.out.println("x["+i+"]="+storage[i]);
                System.out.println("new fill is" +fill);
                System.out.flush();
                //now that the buffer has a value tell conditional to all
            }catch (InterruptedException ex){
                System.out.println("trouble in catch in storedata");
                ex.printStackTrace();
            }
            finally { //release the lock signal all and release
                infostorage.signalAll();
                lockstrclr.unlock();
            }
        }//end of storedata
        public void cleardata(int amt){
            //first acquire the lock
            lockstrclr.lock();
            try {
                System.out.println("in cleardata amt " + amt + "fill " + fill);
                System.out.flush();
                //trying to clear data. There must be at least one to clear
                while (fill < 1)infostorage.await();//must be at least one value to clear
                //now clear this amt of data or less
                if (fill-amt-1<0){
                    //the buffer is completely cleared.
                    int ifmt = fill-1;
                    System.out.println("M1 Clearing from " +ifmt+"to 0");
                    System.out.flush();
                    for (int i=fill-1;i>=0;i--){
                        //clearing data from the fill-1 down
                        System.out.println("c" + i);
                        System.out.flush();
                        System.out.println("clearing x[" + "]" + storage[i]);
                        storage[i]=0;
                        System.out.flush();
                    }//end of for
                    fill=0;
                } else{
                    //we can clear the amt and still have data
                    int ifmt=fill-amt;
                    int ifmt2=fill-1;
                    System.out.println("M2 Clearing from "+ifmt2 + " to" + ifmt);
                    System.out.flush();
                    for (int i = fill-1; i>=fill-amt;i--){
                        //clearing data from the fill-1 down
                        System.out.println(i);
                        System.out.flush();
                        System.out.println("clearing x["+"]" + storage[i]);
                        storage[i]=0;
                        System.out.flush();
                    }//end of for
                    fill=fill-amt;
                    System.out.println("Now the Fill is"+fill);
                    System.out.flush();
                }
                System.out.println("leaving cleardata fill is "+fill);
                System.out.flush();
            }//end of try
            catch(InterruptedException ex){
                System.out.println("Trouble in catch in cleardata");
                ex.printStackTrace();
            }//end of catch
            finally{
                //now that the buffer is cleared tell conditional to all
                infostorage.signalAll();
                //release the lock
                lockstrclr.unlock();
            }//end of finally
        }//end of cleardata
    }//end of buffer class

    //Now let us create a class for the add integers to the buffer
    public static class Add implements Runnable{
        //this is the buffer to add integers
        Buffer Bufx;
        public Add(Buffer x){
            //this is the contructor
            Bufx=x;
        }
        public void run(){
            //add integers to the buffer
            int[] addn={5, 8, 3, 6};//this is the number to add
            int[] nadd={-3,2,-5,-12};//these are the integers to add
            for(int i=0;i<=3;i++){
                Bufx.storedata(addn[i],nadd[i]);
                System.out.println("just added" + addn[i]+" to the buffer int "+nadd[i]);
                System.out.println("buffer fill is "+Bufx.getfill());
                System.out.flush();
                Thread.yield();
            }//end of for
        }//end of run
    }//end of add

    public static class Delete implements Runnable{
        //this is the buffer to add integers
        Buffer Bufx;
        public Delete(Buffer x){
            //this is the contructor
            Bufx=x;
        }
        public void run(){
            //add integers to the buffer
            int[] subn={4,2,2,2,4}; //this is the number to subtract from the buffer
            for(int i=0;i<=4;i++){
                Bufx.cleardata(subn[i]);
                System.out.println("just cleared" +subn[i]+" from the buffer int");
                System.out.println("buffer fill is " + Bufx.getfill());
                System.out.flush();
                Thread.yield();
            }//end of for
        }//end of run
    }//end of delete
}
