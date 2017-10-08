/*
Lisa Ye
Kent Pickett
CSC406

Exam 01
This is a simluation of eBay's bidding system. An oil painting called "The Dutch Masters Oil Painting", is currently on
the bidding system. Japan and Saudi Arabia are currently bidding for it. However, with the 10min Latency from foreign
countries, things get a little tricky. Some bids are not processed fast enough. Below lists the bidder's time and the
criteria that only one bid can be made on the object. With the ForSale class, I have created an object that is for
sale (The oil painting). With the MyBid class, each bidder is notified or flagged (with reasons) whether their bid is
acceptable.

*/

import java.io.File;
import java.io.PrintWriter;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Main {

    public static void main(String[] args) throws Exception{

        //create the print writer to write to an output file
        PrintWriter output;
        output = new PrintWriter(new File("output.txt"));

        //create the oilpainting
        ForSale OilPainting = (new ForSale ("Dutch Masters Oil Painting", 5000.00, 60000.00, 0, 18490, 17050, 4827, 0));

        //create the thread pool
        ExecutorService executor = Executors.newFixedThreadPool(6);

        //create the threads for the pool
        executor.execute(new MyBid(5283, 4827, 4000, 18481, output));
        executor.execute(new MyBid(4681, 4827, 15000, 18482, output));
        executor.execute(new MyBid(5283, 4827, 14500, 18483, output));
        executor.execute(new MyBid(5283, 4827, 17500, 18485, output));
        executor.execute(new MyBid(4681, 4827, 25000, 18489, output));
        executor.execute(new MyBid(5283, 4827, 32000, 18495, output));

        //shutdown the executor
        executor.shutdown();

        while (!executor.isTerminated()){}
    }

}

class ForSale {
    //usable variables
    static String ItemDes;
    static double MinBid;
    static double BuyItNow;
    static double CurrentBid;
    static int TimeDone;
    static int Ctime;
    static int ItemNum;
    static int sold;

    //here is the constructor
    public ForSale(String itemDes, double minBid, double buyItNow, double currentBid, int timeDone, int ctime, int itemNum, int sold) {
        ItemDes = itemDes;
        MinBid = minBid;
        BuyItNow = buyItNow;
        CurrentBid = currentBid;
        TimeDone = timeDone;
        Ctime = ctime;
        ItemNum = itemNum;
        this.sold = sold;
    }//end of constructor

}//end of ForSale

//the MyBid function
class MyBid implements Runnable{

    //Declare the lock...
    static Lock lock = new ReentrantLock();

    //usable variables
    int ItemNum;
    int BidNo;
    int BidAmt;
    int ctime;
    PrintWriter output;

    //Constructor for MyBid
    public MyBid(int bidNo,int itemNum, int bidAmt, int ctime, PrintWriter output) {
        ItemNum = itemNum;
        BidNo = bidNo;
        BidAmt = bidAmt;
        this.ctime = ctime;
        this.output = output;
    }//end constructor

    public void run() {
        //Acquire the lock
        lock.lock();

        //print the Bidders
        System.out.println(BidNo + "\t\t" + ItemNum + "\t\t" + BidAmt + "\t\t" + ctime);
        output.println(BidNo + "\t\t" + ItemNum + "\t\t" + BidAmt + "\t\t" + ctime);
        System.out.flush();
        output.flush();


        //characteristic #1: if the bid is less than the minimum bid, bidders will get error message
        if (BidAmt < ForSale.MinBid){
            System.out.printf("Sorry, %d, the minimum bid is too low for %d, %s, for %d\n", BidNo, ItemNum,
                    ForSale.ItemDes,BidAmt);
            output.printf("Sorry, %d, the minimum bid is too low for %d, %s, for %d\n", BidNo, ItemNum, ForSale.ItemDes,
                    BidAmt);

            System.out.println();
            output.println();

            System.out.flush();
            output.flush();

        }

        //characteristic #2 & 3: the if statement below indicates when the amount is equal to the Buy it now price,
        // the Bidder has bought the item, else if the bidding amount is less than the current bid,
        // an error message appears.
        if (BidAmt == ForSale.BuyItNow) {
            //it is marked sold;
            ForSale.sold = 1;

            //the bidder is notified
            System.out.printf("Great Job, %d, you just bought %d, %s for %d amount.\n", BidNo, ItemNum, ForSale.ItemDes,
                    BidAmt);
            output.printf("Great Job, %d, you just bought %d, %s for %d amount.\n", BidNo, ItemNum, ForSale.ItemDes,
                    BidAmt);

            System.out.println();
            output.println();

            System.out.flush();
            output.flush();

        } else if (BidAmt < ForSale.CurrentBid) {
            //the error message
            System.out.printf("Sorry, %d, this is not enough for %d, %s, for %d\n", BidNo, ItemNum, ForSale.ItemDes,
                    BidAmt);
            output.printf("Sorry, %d, this is not enough for %d, %s, for %d\n", BidNo, ItemNum, ForSale.ItemDes,
                    BidAmt);

            System.out.println();
            output.println();

            System.out.flush();
            output.flush();
        }//end if statement

        //characteristic #4 & 5: when the bid amount is greater than the current bid, the the bid amount becomes
        // the current bid and the bidder is notified else if the current time is greater than the end time, the user
        // is flagged with an error message.
        if (BidAmt > ForSale.CurrentBid  && BidAmt > ForSale.MinBid && ctime < ForSale.TimeDone) {
            //the current bid is updated...
            ForSale.CurrentBid = BidAmt;

            //the bidder is notified
            System.out.printf("Congratulations %d! You are currently the highest bidder for %d, %s, but keep watching" +
                    " for the good stuff. Things goes fast on eBay.\n", BidNo, ItemNum, ForSale.ItemDes, BidAmt);

            output.printf("Congratulations %d! You are currently the highest bidder for %d, %s, but keep watching" +
                    " for the good stuff. Things goes fast on eBay.\n", BidNo, ItemNum, ForSale.ItemDes, BidAmt);

            System.out.println();
            output.println();

            System.out.flush();
            output.flush();

        }else if (ctime > ForSale.TimeDone && BidAmt > ForSale.CurrentBid  && BidAmt > ForSale.MinBid){
            System.out.println("Sorry, " + BidNo + ", you ran out of time! Time Currently: " + ctime + " End Time: " +
                    ForSale.TimeDone);
            output.println("Sorry, " + BidNo + ", you ran out of time! Time Currently: " + ctime + " End Time: " +
                    ForSale.TimeDone);

            System.out.println();
            output.println();

            System.out.flush();
            output.flush();
        }
        //unlock
        lock.unlock();
    }//end of run
}//end of MyBid

