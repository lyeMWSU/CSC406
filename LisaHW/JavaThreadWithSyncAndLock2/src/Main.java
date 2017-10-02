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

        //create the threads
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
        Router myrouter = new Router(output);
        myrouter.run();

    }
}

class Job implements Runnable{
    int n;
    String strings;
    char data;
    PrintWriter out;
    int number;

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
        System.out.flush();
        out.flush();

    }//end of run
}//end of Job Class

//Branch class to hold all three branches
class Branch {
    static Lock lock = new ReentrantLock();

    public static class Production{
        //important variables
        double pCost;
        double dCost;
        double pChars;
        double dChars;

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

        public void productionBranch(int port, char device, int numOfChars){
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
    }

    public static class Financial{
        //important variables
        double pCost;
        double dCost;
        double pChars;
        double dChars;

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

        public void financialBranch(int port, char device, int numOfChars){
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
    }//end of financial

    public static class Market{
        //important variables
        double pCost;
        double dCost;
        double pChars;
        double dChars;

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

        public void marketBranch(int port, char device, int numOfChars){
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
    }//end of market
}//end of branch

class Router implements Runnable{
    Branch.Production product = new Branch.Production();
    Branch.Financial finance = new Branch.Financial();
    Branch.Market market = new Branch.Market();
    PrintWriter myrouter;
    int n;
    String strings;
    char data;
    int number;

    public Router (PrintWriter out){
        myrouter = out;
    }

    public void run(){
        if (strings == "PB"){
            product.productionBranch(n,data, number);
        } else if (strings == "FB"){
            finance.financialBranch(n,data,number);
        }else if (strings == "MB"){
            market.marketBranch(n,data,number);
        }


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

        myrouter.println("-----------------------");
        myrouter.printf("PB-Data: %d\n", Math.round(product.getdChars()));
        myrouter.printf("PB-Data Cost: %d\n", Math.round(product.getdCost()));
        myrouter.printf("PB-Print: %d\n", Math.round(product.getpChars()));
        myrouter.printf("PB-Print Cost: %d\n",Math.round(product.getpCost()));
        myrouter.printf("PB-TotalChars: %d\n", Math.round(product.getTotChars()));
        myrouter.printf("PB-TotalCost: %d\n", Math.round(product.getTotCost()));
        myrouter.println("-----------------------");
        myrouter.printf("FB-Data: %d\n", Math.round(finance.getdChars()));
        myrouter.printf("FB-Data Cost: %d\n", Math.round(finance.getdCost()));
        myrouter.printf("FB-Print: %d\n", Math.round(finance.getpChars()));
        myrouter.printf("FB-Print Cost: %d\n",Math.round(finance.getpCost()));
        myrouter.printf("FB-TotalChars: %d\n", Math.round(finance.getTotChars()));
        myrouter.printf("FB-TotalCost: %d\n", Math.round(finance.getTotCost()));
        myrouter.println("-----------------------");
        myrouter.printf("MB-Data: %d\n", Math.round(market.getdChars()));
        myrouter.printf("MB-Data Cost: %d\n", Math.round(market.getdCost()));
        myrouter.printf("MB-Print: %d\n", Math.round(market.getpChars()));
        myrouter.printf("MB-Print Cost: %d\n",Math.round(market.getpCost()));
        myrouter.printf("MB-TotalChars: %d\n", Math.round(market.getTotChars()));
        myrouter.printf("MB-TotalCost: %d\n", Math.round(market.getTotCost()));
        myrouter.println("-----------------------");
        System.out.flush();
        myrouter.flush();
    }

}