import java.io.File;
import java.io.PrintWriter;
import java.util.concurrent.*;
import java.util.concurrent.locks.*;

public class Main {

    public static void main(String[] args) throws Exception{
        PrintWriter output;
        output = new PrintWriter(new File("output1.txt"));

        Production production = new Production();
        Financial finance = new Financial();
        Market market = new Market();


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

        System.out.printf("PB-Data: %d\n", Math.round(production.getDataChars()));
        System.out.printf("PB-Data Cost: %d\n", Math.round(production.getDataCost()));
        System.out.printf("PB-Print: %d\n", Math.round(production.getPrinterChars()));
        System.out.printf("PB-Print Cost: %d\n",Math.round(production.getPrinterCost()));
        System.out.printf("PB-TotalChars: %d\n", Math.round(production.getTotalChars()));
        System.out.printf("PB-TotalCost: %d\n", Math.round(production.getTotalCost()));

        System.out.println("-----------------------");

        System.out.printf("FB-Data: %d\n", Math.round(finance.getDataChars()));
        System.out.printf("FB-Data Cost: %d\n", Math.round(finance.getDataCost()));
        System.out.printf("FB-Print: %d\n", Math.round(finance.getPrinterChars()));
        System.out.printf("FB-Print Cost: %d\n",Math.round(finance.getPrinterCost()));
        System.out.printf("FB-TotalChars: %d\n", Math.round(finance.getTotalChars()));
        System.out.printf("FB-TotalCost: %d\n", Math.round(finance.getTotalCost()));

        System.out.println("-----------------------");

        System.out.printf("MB-Data: %d\n", Math.round(market.getDataChars()));
        System.out.printf("MB-Data Cost: %d\n", Math.round(market.getDataCost()));
        System.out.printf("MB-Print: %d\n", Math.round(market.getPrinterChars()));
        System.out.printf("MB-Print Cost: %d\n",Math.round(market.getPrinterCost()));
        System.out.printf("MB-TotalChars: %d\n", Math.round(market.getTotalChars()));
        System.out.printf("MB-TotalCost: %d\n", Math.round(market.getTotalCost()));


    }
}

class Job implements Runnable{
    Production product = new Production();
    Financial finance = new Financial();
    Market market = new Market();

    int n;
    String strings;
    char data;
    PrintWriter out;
    int number;
    double amt;

    //constructor
    public Job(String branch, int port, char device, int numOfChars,PrintWriter output) {
        strings = branch;
        n = port;
        data = device;
        number = numOfChars;
        out = output;
    }

    //run method
    public void run(){
        System.out.println(strings+"\t"+n+"\t"+data+"\t"+number+ "\n");
        out.println(strings+"\t"+n+"\t"+data+"\t"+number+ "\n");

        if (strings == "PB"){
            product.productionBranch(n,data, number);
        } else if (strings == "FB"){
            finance.financialBranch(n,data,number);
        }else if (strings == "MB"){
            market.marketBranch(n,data,number);
        }

        //Thread.yield();*/

    }//end of run
}//end of Job Class

class Production {

    static double pCost = 0.0;
    static double dCost = 0.0;
    static double pChars = 0.0;
    static double dChars = 0.0;

    private static Lock lock = new ReentrantLock();

    public void productionBranch(int port, char device, int numOfChars) {
        lock.lock();

        for (int i = 1; i <= numOfChars; i++) {

            if (port >= 1 && port <= 3) {
                if (device == 'P') {
                    pCost += 0.007;
                    pChars++;
                } else {
                    dCost += 0.008;
                    dChars++;
                }
            } else {
                System.out.println("Error");
            }
        }
        lock.unlock();
    }

    public double getPrinterCost() {
        return pCost;
    }

    public double getPrinterChars() {
        return pChars;
    }

    public double getDataCost() {
        return dCost;
    }

    public double getDataChars() {
        return dChars;
    }

    public double getTotalChars(){
        return pChars + dChars;
    }

    public double getTotalCost(){
        return dCost + pCost;
    }

}

class Financial{

    static double pCost = 0.0;
    static double dCost = 0.0;
    static double pChars = 0.0;
    static double dChars = 0.0;

    private static Lock lock = new ReentrantLock();

    public void financialBranch(int port, char device, int numOfChars) {
        lock.lock();

        for (int i = 1; i <= numOfChars; i++) {

            if (port >= 1 && port <= 3) {
                if (device == 'P') {
                    pCost += 0.009;
                    pChars++;
                } else {
                    dCost += 0.007;
                    dChars++;
                }
            } else {
                System.out.println("Error");
            }
        }
        lock.unlock();
    }

    public double getPrinterCost() {
        return pCost;
    }

    public double getPrinterChars() {
        return pChars;
    }

    public double getDataCost() {
        return dCost;
    }

    public double getDataChars() {
        return dChars;
    }

    public double getTotalChars(){
        return pChars + dChars;
    }

    public double getTotalCost(){
        return dCost + pCost;
    }

}

class Market{

    static double pCost = 0.0;
    static double dCost = 0.0;
    static double pChars = 0.0;
    static double dChars = 0.0;

    private static Lock lock = new ReentrantLock();

    public void marketBranch(int port, char device, int numOfChars) {
        lock.lock();

        for (int i = 1; i <= numOfChars; i++) {

            if (port >= 1 && port <= 3) {
                if (device == 'P') {
                    pCost += 0.0095;
                    pChars++;
                } else {
                    dCost += 0.0082;
                    dChars++;
                }
            } else {
                System.out.println("Error");
            }
        }
        lock.unlock();
    }

    public double getPrinterCost() {
        return pCost;
    }

    public double getPrinterChars() {
        return pChars;
    }

    public double getDataCost() {
        return dCost;
    }

    public double getDataChars() {
        return dChars;
    }

    public double getTotalChars(){
        return pChars + dChars;
    }

    public double getTotalCost(){
        return dCost + pCost;
    }

}



