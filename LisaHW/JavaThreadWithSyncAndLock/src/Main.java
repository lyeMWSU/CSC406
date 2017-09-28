import java.io.File;
import java.io.PrintWriter;
import java.util.concurrent.*;
import java.util.concurrent.locks.*;

public class Main {
    private static final int threads = 9;

    public static void main(String[] args) throws Exception{
        PrintWriter output;
        output = new PrintWriter(new File("output1.txt"));

        //thread pool
        ExecutorService executor = Executors.newFixedThreadPool(9);

        //create the array
        executor.execute(new Job("PB", 1, 'D', 60000, output));
        executor.execute(new Job("PB", 3, 'P', 100000, output));
        executor.execute(new Job("PB", 2, 'D', 75000, output));
        executor.execute(new Job("FB", 1, 'P', 30000, output));
        executor.execute(new Job("FB", 2, 'D', 150000, output));
        executor.execute(new Job("FB", 3, 'P', 89000, output));
        executor.execute(new Job("MB", 1, 'P', 200000, output));
        executor.execute(new Job("MB", 2, 'D', 140000, output));
        executor.execute(new Job("MB", 3, 'P', 135000, output));

        Router myrouter = new Router(output);
        executor.execute(new Calculate(myrouter));

        //shutdown the executor
        executor.shutdown();

        while (!executor.isTerminated()) {}

    }
}

class Job implements Runnable{
    int n;
    String strings;
    char data;
    PrintWriter out;
    int number;

    //constructor
    public Job(String branch, int port, char d, int numOfChars,PrintWriter output){
        strings = branch;
        n = port;
        data = d;
        number = numOfChars;
        out = output;
    }

    //run method
    public void run(){
        System.out.println(strings+"\t"+n+"\t"+data+"\t"+number+ "\n");
        out.println(strings+"\t"+n+"\t"+data+"\t"+number+ "\n");
        System.out.flush();
        out.flush();
        Thread.yield();
    }//end of run
}//end of Job Class

class Router {
    private static Lock lock=new ReentrantLock(); //create a lock
    PrintWriter out1;
    int totald;
    int totalp;
    int totalc;
    int num;
    String branch;

    //constructor
    public Router(PrintWriter x) {
        out1 = x;
        totald = 0;
    }

    public void calc(char device) {

        lock.lock(); //acquire the lock for this add.

        if (branch == "PB"){
            if (device == 'D'){
                totald += num;
            }else {
                totalp += num;
            }
        }

        System.out.println("the device total for data is: " + totald);
        System.out.println("the device total for data is: " + totalp);
        System.out.flush();
        out1.flush();
        lock.unlock(); //this releases the lock avoiding the race conditions
    }
}//End of Router

class Calculate implements Runnable {
    //this is the Router to add the dollar
    Router myrouter;

    public Calculate(Router x) {
        //this is the constructor
        myrouter = x;
    }

    public void run() {
        myrouter.calc('D');
    }//add one dollar to the account
}

