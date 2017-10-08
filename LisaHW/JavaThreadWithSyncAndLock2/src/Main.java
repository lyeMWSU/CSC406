/*
Lisa Ye
Kent Pickett
CSC406
October 1, 2017

Program 02
This program is dedicated to solving calculations of printing and data characters and costs from various branches of the
network as it travels to the router. There are three main branches that consists of: Production, Finance, and Marketing,
ane each branch consists of three computers which makes up a total of 9 computers being used through the router. Each
branch also uses different device types such as: Data, and Printer ('D', 'P'). The router's purpose is to collect the
information passed through as a job. Each job will collect the information regarding to each branch's total Data count
and cost, and the Printer device's total character count and cost.
This program usage is used to similate the router's job.

*/


import java.io.File;
import java.io.PrintWriter;
import java.util.concurrent.*;
import java.util.concurrent.locks.*;

public class Main {

    public static void main(String[] args) throws Exception{

        //create the print writer to write to an output file
        PrintWriter output;
        output = new PrintWriter(new File("output1.txt"));

        //create the router to similate the jobs
        Router myrouter = new Router(output);

        //Here is the thread pool
        ExecutorService executor = Executors.newFixedThreadPool(9);

        //Here we create the threads for the pool...
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

        //this prints out the calculated data
        myrouter.printRouter();

    }
}

//the job class is a task...
class Job implements Runnable{
    //declared important variables...
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

        //print what's stored in the executors
        System.out.println(branch.branchName+"\t"+n+"\t"+data+"\t"+number+ "\n");
        output.println(branch.branchName+"\t"+n+"\t"+data+"\t"+number+ "\n");
        System.out.flush();

    }//end of run
}//end of Job Class

//Branch class to hold all three branches and to create variables for calculations
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
    //get the data and print costs...
    public double getpCost() {
        return pCost;
    }
    public double getdCost() {
        return dCost;
    }

    //get the amount of data and print characters
    public double getpChars() {
        return pChars;
    }
    public double getdChars() {
        return dChars;
    }

    //return the total number of Characters and the total Costs
    public double getTotChars() {
        return dChars + pChars;
    }
    public double getTotCost() {
        return dCost + pCost;
    }

    //calculate the characters and costs of printing and data usage
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
        }//end of for
        lock.unlock();
    }//end of calc
}//end of branch

//router class
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

    //Prints the data...
    public void printRouter(){
        //printing the production branch...
        System.out.println("-----------------------");
        System.out.printf("PB-Data: %d\n", Math.round(product.getdChars()));
        System.out.printf("PB-Data Cost: %d\n", Math.round(product.getdCost()));
        System.out.printf("PB-Print: %d\n", Math.round(product.getpChars()));
        System.out.printf("PB-Print Cost: %d\n",Math.round(product.getpCost()));
        System.out.printf("PB-TotalChars: %d\n", Math.round(product.getTotChars()));
        System.out.printf("PB-TotalCost: %d\n", Math.round(product.getTotCost()));

        //printing the finance branch...
        System.out.println("-----------------------");
        System.out.printf("FB-Data: %d\n", Math.round(finance.getdChars()));
        System.out.printf("FB-Data Cost: %d\n", Math.round(finance.getdCost()));
        System.out.printf("FB-Print: %d\n", Math.round(finance.getpChars()));
        System.out.printf("FB-Print Cost: %d\n",Math.round(finance.getpCost()));
        System.out.printf("FB-TotalChars: %d\n", Math.round(finance.getTotChars()));
        System.out.printf("FB-TotalCost: %d\n", Math.round(finance.getTotCost()));

        //printing the Marketing branch...
        System.out.println("-----------------------");
        System.out.printf("MB-Data: %d\n", Math.round(market.getdChars()));
        System.out.printf("MB-Data Cost: %d\n", Math.round(market.getdCost()));
        System.out.printf("MB-Print: %d\n", Math.round(market.getpChars()));
        System.out.printf("MB-Print Cost: %d\n",Math.round(market.getpCost()));
        System.out.printf("MB-TotalChars: %d\n", Math.round(market.getTotChars()));
        System.out.printf("MB-TotalCost: %d\n", Math.round(market.getTotCost()));
        System.out.println("-----------------------");

        //print all to the output file...
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
    }//end of printrouter method
}//end of router class...
