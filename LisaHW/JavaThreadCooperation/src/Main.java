/*
Lisa Ye
Kent Pickett
CSC406
October 1, 2017
*/

import java.io.*;
import java.lang.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Main {

    public static void main(String[] args) throws Exception{

        PrintWriter output;
        output = new PrintWriter(new File("output.txt"));

        Router myrouter = new Router(output);

        //create the thread pool
        ExecutorService executor = Executors.newFixedThreadPool(2);

        StorageTask storageTask = new StorageTask(myrouter);
        DeleteTask deleteTask = new DeleteTask(myrouter);

        //execute
        executor.execute(storageTask);
        executor.execute(deleteTask);



        //shutdown
        executor.shutdown();

        while (!executor.isTerminated()){}


    }

    public static class StorageTask implements Runnable{
        Router myrouter;

        public StorageTask(Router myrouter){
            this.myrouter = myrouter;
        }

        public void run(){
            myrouter.store("PB1",5,-3);
            myrouter.store("FB2",6,78);
            myrouter.store("PB1",8,13);
            myrouter.store("MB3",10,22);
            myrouter.store("FB4",6,75);

        }
    }

    public static class DeleteTask implements Runnable{
        Router myrouter;

        public DeleteTask(Router myrouter){
            this.myrouter = myrouter;
        }

        public void run(){
            myrouter.delete("FB2", 2);
            myrouter.delete("PB1", 2);
            myrouter.delete("MB3", 4);
            myrouter.delete("PB1", 3);
        }
    }

    private static class Router {
        //create the lock
        private static Lock lock = new ReentrantLock();

        //create condition
        private static Condition newData = lock.newCondition();


        int Dstore[];
        String Cstore[];
        PrintWriter out;
        int whereIsItAt;
        String branch;

        //the constructor
        public Router(PrintWriter out) {
            this.out = out;

            //set to store 30
            Dstore = new int [30];
            Cstore = new String [30];

            //set the array to 0
            whereIsItAt = 0;
            for (int i = 0; i <= 29; i++){
                Dstore[i] = 0;
                Cstore[i] = "";
            }
        }

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

                //Print it
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


                for (int i = 0; i <=29; i++){
                    System.out.println("CS["+i+"] = "+ Cstore[i] + "\t \tDS["+i+"] = "+Dstore[i]);
                    out.println("CS["+i+"] = "+ Cstore[i] + "\t \tDS["+i+"] = "+Dstore[i]);
                    System.out.flush();
                    out.flush();
                }
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
                newData.signalAll();
                lock.unlock();
            }
        }



        public void delete (String branch, int amt) {

            //acquire the lock
            lock.lock();

            //try to delete
            try {
                System.out.println();
                System.out.println("Time to Delete!");
                System.out.println("Branch: "+ branch + " amt: " + amt + " fill: " + whereIsItAt);
                System.out.flush();

                out.println();
                out.println("Time to Delete!");
                out.println("Branch: "+ branch + " amt: " + amt + " fill: " + whereIsItAt);
                out.flush();

                //trying to clear data. There must be at least one to clear
                while (whereIsItAt < 1)newData.await();

                //Now Delete!
                if (whereIsItAt - amt - 1 < 0 ){
                    int ifamt = whereIsItAt-1;
                    System.out.println("Clearing from " +ifamt+" to 0");
                    out.println("Clearing from " +ifamt+" to 0");
                    System.out.flush();
                    out.flush();

                    for (int i=whereIsItAt-1;i>=0;i--){
                        //clearing data from the fill-1 down
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
                    whereIsItAt=whereIsItAt-amt;

                    //print it
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
                System.out.println("Trouble in catch in cleardata");
                ex.printStackTrace();
            }
            finally{
                newData.signalAll();
                lock.unlock();
            }
        }
    }
}
