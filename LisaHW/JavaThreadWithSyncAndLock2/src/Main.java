/*
Lisa Ye
Kent Pickett
CSC406
October 1, 2017
*/

import java.io.File;
import java.io.PrintWriter;
import java.util.concurrent.*;
import java.util.concurrent.locks.*;

public class Main {

    public static void main(String[] args) throws Exception{
        PrintWriter output;
        output = new PrintWriter(new File("output1.txt"));

        Router myrouter = new Router(output);

        //thread pool
        ExecutorService executor = Executors.newFixedThreadPool(9);

        //create the threads
        executor.execute(new Job(myrouter.product, 1, 'D', 60000, output));
        executor.execute(new Job(myrouter.product, 3, 'P', 100000, output));
        executor.execute(new Job(myrouter.product, 2, 'D', 75000, output));
        executor.execute(new Job(myrouter.finance, 1, 'P', 30000, output));
        executor.execute(new Job(myrouter.finance, 2, 'D', 150000, output));
        executor.execute(new Job(myrouter.finance, 3, 'P', 89000, output));
        executor.execute(new Job(myrouter.market, 1, 'P', 200000, output));
        executor.execute(new Job(myrouter.market, 2, 'D', 140000, output));
        executor.execute(new Job(myrouter.market, 3, 'P', 135000, output));

        //shutdown the executor
        executor.shutdown();

        while (!executor.isTerminated()) {}

        myrouter.printRouter();

    }
}

class Job implements Runnable{
    int n;
    Branch branch;
    char data;
    int number;
    PrintWriter output;

    //constructor
    public Job(Branch branch, int port, char device, int numOfChars, PrintWriter out) {
        this.branch = branch;
        n = port;
        data = device;
        number = numOfChars;
        output = out;
    }

    //run method
    public void run(){
        branch.calculate(n,data,number);

        //calcs
        System.out.println(branch.branchName+"\t"+n+"\t"+data+"\t"+number+ "\n");
        output.println(branch.branchName+"\t"+n+"\t"+data+"\t"+number+ "\n");
        System.out.flush();

    }//end of run
}//end of Job Class

//Branch class to hold all three branches
class Branch {
    static Lock lock = new ReentrantLock();

    //important variables
    double pCost;
    double dCost;
    double pChars;
    double dChars;
    String branchName;
    double dCostPer;
    double pCostPer;

    //constructor
    public Branch (String branchName, double pCostPer, double dCostPer){
        this.branchName = branchName;
        this.dCostPer = dCostPer;
        this.pCostPer = pCostPer;
    }

    //getters
    public double getpCost() {
        return pCost;
    }
    public double getdCost() {
        return dCost;
    }
    public double getpChars() {
        return pChars;
    }
    public double getdChars() {
        return dChars;
    }
    public double getTotChars() {
        return dChars + pChars;
    }
    public double getTotCost() {
        return dCost + pCost;
    }

    public void calculate(int port, char device, int numOfChars) {
        lock.lock();
        for (int i = 1; i <= numOfChars; i++) {
            if (port >= 1 && port <= 3) {
                if (device == 'P') {
                    pCost += pCostPer;
                    pChars++;
                } else {
                    dCost += dCostPer;
                    dChars++;
                }
            } else {
                System.out.println("Error");
            }
        }
        lock.unlock();
    }//end of calc
}//end of branch

class Router{
    Branch product;
    Branch finance;
    Branch market;

    PrintWriter output;

    //constructor
    public Router (PrintWriter out){
        product = new Branch("PB",0.007, 0.008);
        finance = new Branch("FB",0.009, 0.007);
        market = new Branch("MB",0.0095, 0.0082);
        output = out;
    }

    public void printRouter(){
        System.out.println("-----------------------");
        System.out.printf("PB-Data: %d\n", Math.round(product.getdChars()));
        System.out.printf("PB-Data Cost: %d\n", Math.round(product.getdCost()));
        System.out.printf("PB-Print: %d\n", Math.round(product.getpChars()));
        System.out.printf("PB-Print Cost: %d\n",Math.round(product.getpCost()));
        System.out.printf("PB-TotalChars: %d\n", Math.round(product.getTotChars()));
        System.out.printf("PB-TotalCost: %d\n", Math.round(product.getTotCost()));
        System.out.println("-----------------------");
        System.out.printf("FB-Data: %d\n", Math.round(finance.getdChars()));
        System.out.printf("FB-Data Cost: %d\n", Math.round(finance.getdCost()));
        System.out.printf("FB-Print: %d\n", Math.round(finance.getpChars()));
        System.out.printf("FB-Print Cost: %d\n",Math.round(finance.getpCost()));
        System.out.printf("FB-TotalChars: %d\n", Math.round(finance.getTotChars()));
        System.out.printf("FB-TotalCost: %d\n", Math.round(finance.getTotCost()));
        System.out.println("-----------------------");
        System.out.printf("MB-Data: %d\n", Math.round(market.getdChars()));
        System.out.printf("MB-Data Cost: %d\n", Math.round(market.getdCost()));
        System.out.printf("MB-Print: %d\n", Math.round(market.getpChars()));
        System.out.printf("MB-Print Cost: %d\n",Math.round(market.getpCost()));
        System.out.printf("MB-TotalChars: %d\n", Math.round(market.getTotChars()));
        System.out.printf("MB-TotalCost: %d\n", Math.round(market.getTotCost()));
        System.out.println("-----------------------");

        output.println("-----------------------");
        output.printf("PB-Data: %d\n", Math.round(product.getdChars()));
        output.printf("PB-Data Cost: %d\n", Math.round(product.getdCost()));
        output.printf("PB-Print: %d\n", Math.round(product.getpChars()));
        output.printf("PB-Print Cost: %d\n",Math.round(product.getpCost()));
        output.printf("PB-TotalChars: %d\n", Math.round(product.getTotChars()));
        output.printf("PB-TotalCost: %d\n", Math.round(product.getTotCost()));
        output.println("-----------------------");
        output.printf("FB-Data: %d\n", Math.round(finance.getdChars()));
        output.printf("FB-Data Cost: %d\n", Math.round(finance.getdCost()));
        output.printf("FB-Print: %d\n", Math.round(finance.getpChars()));
        output.printf("FB-Print Cost: %d\n",Math.round(finance.getpCost()));
        output.printf("FB-TotalChars: %d\n", Math.round(finance.getTotChars()));
        output.printf("FB-TotalCost: %d\n", Math.round(finance.getTotCost()));
        output.println("-----------------------");
        output.printf("MB-Data: %d\n", Math.round(market.getdChars()));
        output.printf("MB-Data Cost: %d\n", Math.round(market.getdCost()));
        output.printf("MB-Print: %d\n", Math.round(market.getpChars()));
        output.printf("MB-Print Cost: %d\n",Math.round(market.getpCost()));
        output.printf("MB-TotalChars: %d\n", Math.round(market.getTotChars()));
        output.printf("MB-TotalCost: %d\n", Math.round(market.getTotCost()));
        output.println("-----------------------");
        System.out.flush();
        output.flush();
    }
}
