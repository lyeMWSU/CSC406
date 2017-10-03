import java.io.*;
import java.io.IOException;
import javax.swing.*;
import java.nio.Buffer;
import java.util.*;
import java.io.File;
import java.lang.*;
//import java.lang.Thread; //this allows the threads to be run
import java.util.concurrent.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
public class Main {

    public static void main(String[] args) throws Exception{
        //Create print writer for the tasks
        PrintWriter output;
        output = new PrintWriter(new File("output1.txt"));
        //create the thread pool
        ExecutorService executor = Executors.newFixedThreadPool(2);

        //create interface
        InterfaceSoftware IS = new InterfaceSoftware(output);

        //create the add thread and give it priority
        Storage store = new Storage(IS);
        Delete erase = new Delete (IS);

        //execute
        executor.execute(store);
        executor.execute(erase);

        System.out.println("Thread store and erase created and launched");
        System.out.flush();
        output.println("Thread store and erase created and launched");
        output.flush();
    }

    public static class InterfaceSoftware{
        private int [] storage;
        private int fill;
        PrintWriter out;
        static Lock lock = new ReentrantLock(); //create lock
        static Condition condition = lock.newCondition(); // conditional lock

        public InterfaceSoftware(PrintWriter out){
            this.out = out;
            storage = new int[30];

            //initalize
            for (int i = 0; i <= 29; i++){
                storage[i] = 0;
            }
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

        public void storing (String branch, int amt, int value){
            //first acquire the lock
            lock.lock();

            //now try to store the data
            try{
                System.out.println("in storedata amt " + amt + "value " + value + " fill " + fill);
                while((fill+amt-1)>9)condition.await();//wait till there is room for the data
                System.out.println("now there is room amt " + amt + " value" + value + " fill " + fill);
                out.println("in storedata amt " + amt + "value " + value + " fill " + fill);
                System.out.flush();
                out.flush();

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
            }finally { //release the lock signal all and release
                condition.signalAll();
                lock.unlock();
            }
        }//end of storing

        public void erase(String branch, int amt){
            //first acquire the lock
            lock.lock();
            try {
                System.out.println("in cleardata amt " + amt + "fill " + fill);
                System.out.flush();
                //trying to clear data. There must be at least one to clear
                while (fill < 1)condition.await();//must be at least one value to clear

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
                condition.signalAll();
                //release the lock
                lock.unlock();
            }//end of finally
        }//end of cleardata
    }//end of InterFace


    public static class Storage implements Runnable{
        //this is the buffer to add integers
        InterfaceSoftware software;

        //constructor
        public Storage(InterfaceSoftware x){

            software = x;
        }

        public void run(){
            //add integers to the buffer
            int[] addn={5, 8, 3, 6};//this is the number to add
            int[] nadd={-3,2,-5,-12};//these are the integers to add
            for(int i=0;i<=3;i++){
                software.storing("PB1", addn[i],nadd[i]);
                System.out.println("just added" + addn[i]+" to the buffer int "+nadd[i]);
                System.out.println("buffer fill is "+software.getfill());
                System.out.flush();
                Thread.yield();
            }//end of for
        }//end of run
    }//end of storage

    public static class Delete implements Runnable{
        //this is the buffer to delete integers
        InterfaceSoftware software;

        //constructor
        public Delete(InterfaceSoftware x){
            software = x;
        }

        public void run(){
            //add integers to the buffer
            int[] subn={4,2,2,2,4}; //this is the number to subtract from the buffer
            for(int i=0;i<=4;i++){
                software.erase("FB2", subn[i]);
                System.out.println("just cleared" +subn[i]+" from the buffer int");
                System.out.println("buffer fill is " + software.getfill());
                System.out.flush();
                Thread.yield();
            }//end of for
        }//end of run
    }//end of delete
}

class Branch {

    //important variables
    String branchName;

    //constructor
    public Branch (String branchName) {
        this.branchName = branchName;
    }

}//end of branch

