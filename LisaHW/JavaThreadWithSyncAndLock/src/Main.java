import java.io.File;
import java.io.PrintWriter;
import java.util.concurrent.*;
import java.util.concurrent.locks.*;

public class Main {

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


        //shutdown the executor
        executor.shutdown();


        while (!executor.isTerminated()) {}

    }
}

class Job implements Runnable{
    private static Lock lock=new ReentrantLock();
    int n;
    String strings;
    char data;
    PrintWriter out;
    int number;
    double amt;
    int totald;
    int totalp;
    int pb1;
    int pb2;
    int pb3;
    int fb1;
    int fb2;
    int fb3;
    int mb1;
    int mb2;
    int mb3;
    int pb1c;
    int pb2c;
    int pb3c;

    //constructor
    public Job(String branch, int port, char device, int numOfChars,PrintWriter output){
        strings = branch;
        n = port;
        data = device;
        number = numOfChars;
        out = output;
    }

    //run method
    public void run(){
//        System.out.println(strings+"\t"+n+"\t"+data+"\t"+number+ "\n");
//        out.println(strings+"\t"+n+"\t"+data+"\t"+number+ "\n");
        lock.lock(); //acquire the lock for this add.

        if (strings == "PB" && data == 'D' && n == 1) {
            amt = 0.008;
            pb1 = (int) (amt * number);
            System.out.println("PB: " + pb1);
        }else if (strings == "PB" && data == 'D' && n == 2) {
            amt = 0.008;
            pb2 = (int) (amt * number);
            System.out.println("PB: " + pb2);
        }else if (strings == "PB" && data == 'P' && n == 3){
            amt = 0.007;
            pb3 = (int)(amt * number);
            System.out.println("PB: " + pb3);
        }else if (strings == "FB" && data == 'D' && n == 2) {
            amt = 0.007;
            fb2 = (int) (amt * number);
            System.out.println("FB: " + fb2);
        }else if (strings == "FB" && data == 'P' && n == 3) {
            amt = 0.007;
            fb3 = (int) (amt * number);
            System.out.println("FB: " + fb3);
        }else if (strings == "FB" && data == 'P' && n == 1){
            amt = 0.009;
            fb1 = (int)(amt * number);
            System.out.println("FB: " + fb1);
        }else if (strings == "MB" && data == 'D' && n == 2) {
            amt = 0.0082;
            mb2 = (int) (amt * number);
            System.out.println("MB: " + mb2);
        }else if (strings == "MB" && data == 'P' && n == 1){
            amt = 0.0095;
            mb1 = (int)(amt * number);
            System.out.println("MB: " + mb1);
        }else if (strings == "MB" && data == 'P' && n == 3){
            amt = 0.0095;
            mb3 = (int)(amt * number);
            System.out.println("MB: " + mb3);
        }else{

        }

        //calculate total d and p

        totald = (pb1 + pb2);

        System.out.println("The total should be: " + totald);





        //get number of characters
//        if (strings == "PB" && n == 1) {
//            pb1c = number;
//            //System.out.println(pb1c);
//        }else if (strings == "PB" && n == 2){
//            pb2c = number;
//            //System.out.println(pb2c);
//        }else if (strings == "PB" && n == 3){
//            pb3c = number;
//            //System.out.println(pb3c);
//        }

//        totald = pb1c + pb2c;
//        totalp = pb3c;
//        System.out.println(totald);
//        System.out.println(totalp);




        lock.unlock();
        System.out.flush();
        out.flush();
        Thread.yield();

    }//end of run
}//end of Job Class

//class Router{
//    //create a lock
//    PrintWriter out;
//    int totald;
//    int totalp;
//    int totalc;
//
//    public Router(PrintWriter output) {
//        out = output;
//    }
//
//    public void calc(String branch, char device){
//
//
//
//        System.out.println("the device total for data is: " + totald);
//        System.out.println("the device total for printer is: " + totalp);
//        out.println("the device total for data is: " + totald);
//        out.println("the device total for printer is: " + totalp);
//        System.out.flush();
//        out.flush();
//         //this releases the lock avoiding the race conditions
//    }//end of calc
//}//End of Router

//class Calculate implements Runnable {
//    //this is the Router to add the dollar
//    Router myrouter;
//
//    public Calculate(Router x) {
//        //this is the constructor
//        myrouter = x;
//    }
//
//    public void run() {
//        myrouter.calc("PB", 'D');
//        myrouter.calc("PB", 'P');
//    }//add one dollar to the account
//}

