import java.io.*;
import java.lang.*;
//import java.lang.Thread; //this allows the threads to be run
import java.util.concurrent.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
public class Main {

    private static PrintWriter output;
    private static Router myrouter = new Router(output);

    public static void main(String[] args) throws Exception{

        //create the thread pool
        ExecutorService executor = Executors.newFixedThreadPool(2);

        //execute
        executor.execute(new StorageTask());
        executor.execute(new DeleteTask());

        //shutdown
        executor.shutdown();


    }

    public static class StorageTask implements Runnable{

        public void run(){
            myrouter.store("PB1",5,-3);
            myrouter.store("FB2",6,78);
//            myrouter.store("PB1",8,13);
//            myrouter.store("MB3",10,22);
//            myrouter.store("FB4",6,75);

        }
    }

    public static class DeleteTask implements Runnable{

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

        public int getWhereIsItAt() {
            return whereIsItAt;
        }

        public void store (String branch, int amt, int value){
            //acquire the lock
            lock.lock();

            //store the data
            for(int i = whereIsItAt; i <= whereIsItAt +amt-1; i++){
                Dstore[i] = value;
                Cstore[i] = branch;
            }

            whereIsItAt = whereIsItAt +amt; //fill always points to the next available spot

            System.out.println();
            System.out.println("----------Storing----------");
            for (int i = 0; i <=29; i++){
                System.out.println("CS["+i+"] = "+ Cstore[i] + "\t \tDS["+i+"] = "+Dstore[i]);
                System.out.flush();
            }

            lock.unlock();



        }

        public void delete (String branch, int amt){
            //acquire the lock
            lock.lock();

            while(whereIsItAt < 1){
                try {
                    newData.await();
                } catch (InterruptedException e) {
                    System.out.println("Something wrong with Delete");
                    e.printStackTrace();
                }
            }


        }
    }

}
