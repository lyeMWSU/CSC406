/*This program is an example of creating threads that are
synced with a Lock condition but in a "race condition". The
program creates 50 threads that add money to an account object.
The threads are synced through the addition of a Lock.
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
//import java.lang.Thread;//this allows the threads to be run
import java.util.concurrent.*; //this allows the creation of a thread pool that can all be launched by
//one executor Executor
import java.util.concurrent.locks.*; //this gives me the ability to lock a function.


public class Main {

    public static void main(String[] args) throws Exception {
        //Create print writer for the tasks
        PrintWriter outf1;
        outf1 = new PrintWriter(new File("JavaMultiThreadOutNonSync1.txt"));
        //create the thread pool
        ExecutorService executor = Executors.newFixedThreadPool(50);
        //Now create and launch 50 deposits threads
        //create an account
        Account savings = new Account(outf1);
        for (int i = 1; i <= 50; i++) {
            executor.execute(new AddADollarTask(savings));
            System.out.println("Thread " + i + "created and launched");
            System.out.flush();
            outf1.println("Thread " + i + "created and launched");
            outf1.flush();
        }
        //Now shut down the executor.
        executor.shutdown();
        //now let all threads shut down and wait till all tasks are finished
        while (!executor.isTerminated()) {
            System.out.println("What is the Balance?" + savings.getBalance());
            System.out.flush();
            outf1.println("What is the Balance?" + savings.getBalance());
            outf1.flush();
        }
    }

    public static class Account {
        private int balance;
        private static Lock lock=new ReentrantLock(); //create a lock
        PrintWriter out1;

        public Account(PrintWriter x) {
            out1 = x;
            balance = 0;
        }

        public int getBalance() {return balance;}

        public void deposite(int amt) {
            lock.lock(); //acquire the lcok for this add.

            try {
                int newBalance = balance+amt;
                //this delay is deliberate to magnify the data-corruption problem
                Thread.sleep(5);
                balance=newBalance;
            } catch (InterruptedException ex) {

            }
            System.out.println("this is the balance" + balance);
            System.out.flush();
            out1.println("this is the balance" + balance);
            out1.flush();
            lock.unlock(); //this releases the lock avoiding the race conditions
        }//end of deposite
    }//End of Account
    //Now let us create a class for the add a dollar Task Object

    public static class AddADollarTask implements Runnable {
        //this is the account to add the dollar
        Account myaccount;

        public AddADollarTask(Account x) {
            //this is the constructor
            myaccount = x;
        }

        public void run() {
            myaccount.deposite(1);
        }//add one dollar to the account
    }
}