/*
Lisa Ye
Kent Pickett
CSC406
October 5, 2017

Program 03
This program usage is used to similate the router's memory bank that has been allocated to a fixed size of 30 integer
storage positions. This is a set of parallel arrays of characters. Contrarily, any computer from any branch can delete
the same amount of data that had been stored. There are two array structured as an integer Dstore[30] and a String
Cstore[30]. The Dstore stores only integer data from certain branches that flow into the router. The Cstore stores a
string that consists of only three characters that identifies the branch of that computer. However, the router flows
through an interface software that separates these data into their respective space.
*/

import java.io.*;
import java.lang.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Main {

    public static void main(String[] args) throws Exception{

        //create the print writer to write to an output file
        PrintWriter output;
        output = new PrintWriter(new File("output.txt"));

        Router myrouter = new Router(output);

        //create the thread pool
        ExecutorService executor = Executors.newFixedThreadPool(2);

        //Create these tasks
        StorageTask storageTask = new StorageTask(myrouter);
        DeleteTask deleteTask = new DeleteTask(myrouter);

        //execute these tasks
        executor.execute(storageTask);
        executor.execute(deleteTask);

        //shutdown
        executor.shutdown();

        while (!executor.isTerminated()){}

    }

    //storage function that will store the branch, amount, and value
    public static class StorageTask implements Runnable{
        Router myrouter;

        //the constructor for the StorageTask
        public StorageTask(Router myrouter){
            this.myrouter = myrouter;
        }

        //the run method
        public void run(){
            myrouter.store("PB1",5,-3);
            myrouter.store("FB2",6,78);
            myrouter.store("PB1",8,13);
            myrouter.store("MB3",10,22);
            myrouter.store("FB4",6,75);

        }//end of run
    }//end of StorageTask

    //The delete task that will delete the amount of each branch
    public static class DeleteTask implements Runnable{
        Router myrouter;

        //the constructor for the DeleteTAsk
        public DeleteTask(Router myrouter){
            this.myrouter = myrouter;
        }

        public void run(){
            myrouter.delete("FB2", 2);
            myrouter.delete("PB1", 2);
            myrouter.delete("MB3", 4);
            myrouter.delete("PB1", 3);
        }//end of run
    }//end of DeleteTask

    //the router class that
    private static class Router {
        //create the lock
        private static Lock lock = new ReentrantLock();

        //create condition for the lock
        private static Condition newData = lock.newCondition();

        //variables to use
        int Dstore[];
        String Cstore[];
        PrintWriter out;
        int whereIsItAt;

        //the constructor
        public Router(PrintWriter out) {
            this.out = out;

            //set to store 30
            Dstore = new int [30];
            Cstore = new String [30];

            //set everything to 0
            whereIsItAt = 0;
            for (int i = 0; i <= 29; i++){
                Dstore[i] = 0;
                Cstore[i] = "";
            }
        }

        //where the storing action happens
        public void store (String branch, int amt, int value){
            //acquire the lock
            lock.lock();

            //try to store
            try{
                //store the data
                for(int i = whereIsItAt; i <= whereIsItAt +amt-1; i++){
                    Dstore[i] = value;
                    Cstore[i] = branch;
                }

                //the next available spot
                whereIsItAt = whereIsItAt +amt;

                //Print where the array is
                System.out.println();
                System.out.println("---------------------------");
                System.out.println("Will store  amt: " + amt + ", value: " + value);
                System.out.println("----------Storing----------");
                out.flush();

                out.println();
                out.println("---------------------------");
                out.println("Will store  amt: " + amt + ", value: " + value);
                out.println("----------Storing----------");
                out.flush();

                //start storing
                for (int i = 0; i <=29; i++){
                    System.out.println("CS["+i+"] = "+ Cstore[i] + "\t \tDS["+i+"] = "+Dstore[i]);
                    out.println("CS["+i+"] = "+ Cstore[i] + "\t \tDS["+i+"] = "+Dstore[i]);
                    System.out.flush();
                    out.flush();
                }
                //print where the array is
                System.out.println("---------------------------");
                System.out.println("Where is it at? " + whereIsItAt);
                System.out.println("---------------------------");
                System.out.flush();

                out.println("---------------------------");
                out.println("Where is it at? " + whereIsItAt);
                out.println("---------------------------");
                out.flush();

                //wait till there is room for storage
                while((whereIsItAt+amt-1)>29)newData.await();

            }catch (InterruptedException ex ){
                System.out.println("Error in Store");
                ex.printStackTrace();
            }finally {
                //finally signal the condition to continue
                newData.signalAll();
                //releases the lock
                lock.unlock();
            }
        }

        //start deleting
        public void delete (String branch, int amt) {

            //acquire the lock
            lock.lock();

            //try to delete
            try {
                System.out.println();
                System.out.println("Time to Delete!");
                System.out.flush();

                out.println();
                out.println("Time to Delete!");
                out.flush();

                //trying to clear data. There must be at least one to clear, otherwise wait.
                while (whereIsItAt < 1)newData.await();

                //Now Delete!
                if (whereIsItAt - amt - 1 < 0 ){
                    int ifamt = whereIsItAt-1;
                    //where is it clearing from
                    System.out.println("Clearing from " +ifamt+" to 0");
                    out.println("Clearing from " +ifamt+" to 0");
                    System.out.flush();
                    out.flush();

                    //the delete process starts here
                    for (int i=whereIsItAt-1;i>=0;i--){
                        System.out.println("clearing: CS[" + i + "] = " + Cstore[i] + "\t \tDS[" + i + "] = " + Dstore[i]);
                        out.println("clearing: CS[" + i + "] = " + Cstore[i] + "\t \tDS[" + i + "] = " + Dstore[i]);
                        Dstore[i]=0;
                        Cstore[i]="";
                        System.out.flush();
                        out.flush();
                    }

                    whereIsItAt=0;

                } else{
                    //we can delete the amt and still have data
                    int ifamt=whereIsItAt-amt;
                    int ifamt2=whereIsItAt-1;
                    //where is it clearing from
                    System.out.println("Clearing from "+ifamt2 + " to " + ifamt);
                    out.println("Clearing from "+ifamt2 + " to " + ifamt);
                    System.out.flush();
                    out.flush();

                    for (int i = whereIsItAt-1; i>=whereIsItAt-amt;i--){
                        System.out.println("clearing: CS[" + i + "] = " + Cstore[i] + "\t \tDS[" + i + "] = " + Dstore[i]);
                        out.println("clearing: CS[" + i + "] = " + Cstore[i] + "\t \tDS[" + i + "] = " + Dstore[i]);
                        Dstore[i]=0;
                        Cstore[i]="";
                        System.out.flush();
                        out.flush();
                    }
                    //set it to the next empty slot
                    whereIsItAt=whereIsItAt-amt;

                    //print the updates
                    System.out.println();
                    System.out.println("----------Deleted----------");
                    System.out.flush();

                    out.println();
                    out.println("----------Deleted----------");
                    out.flush();

                    for (int i = 0; i <=29; i++) {
                        System.out.println("CS[" + i + "] = " + Cstore[i] + "\t \tDS[" + i + "] = " + Dstore[i]);
                        out.println("CS[" + i + "] = " + Cstore[i] + "\t \tDS[" + i + "] = " + Dstore[i]);
                        System.out.flush();
                        out.flush();
                    }

                    //print where the array is
                    System.out.println("---------------------------");
                    System.out.println("Where is it at? " + whereIsItAt);
                    System.out.println("---------------------------");
                    System.out.flush();

                    out.println("---------------------------");
                    out.println("Where is it at? " + whereIsItAt);
                    out.println("---------------------------");
                    out.flush();
                }
            }//end of try
            catch(InterruptedException ex){
                System.out.println("Trouble in catch in Delete");
                ex.printStackTrace();
            }
            finally{
                //finally signal the condition to continue
                newData.signalAll();
                //releases the lock
                lock.unlock();
            }
        }
    }
}
