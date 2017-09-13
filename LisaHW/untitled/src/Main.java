import java.io.File;
import java.io.PrintWriter;

public class Main {

    public static void main(String[] args) throws Exception{
        PrintWriter output;
        output = new PrintWriter(new File("output1.txt"));

        //create tasks

        Runnable Gui = new GuiOut("Gui Poll", 600, output);
        Runnable WP = new WPOut('A', 50*10*2, output);
//        Runnable DS = new DSOut();
//        Runnable Printer = new PrintOut();

        //create threads

        Thread thread1 = new Thread(Gui);
        Thread thread2 = new Thread(WP);
//        Thread thread3 = new Thread(DS);
//        Thread thread4 = new Thread(Printer);

        //priorities

        thread1.setPriority(Thread.MIN_PRIORITY);
        thread2.setPriority(Thread.MAX_PRIORITY);
//        thread3.setPriority(Thread.NORM_PRIORITY);
//        thread4.setPriority(Thread.NORM_PRIORITY);

        //Start Threads

        thread1.start();
        thread2.start();
//        thread3.start();
//        thread4.start();

        output.flush();
    }
}

//classes

class GuiOut implements Runnable{
    double seconds;
    String strings;
    PrintWriter out;

    //constructor
    public GuiOut(String ss, double s, PrintWriter output){
        strings = ss;
        seconds = s;
        out = output;
    }

    //run method

    public void run(){
        for (int i=1; i <= seconds; i++){
            System.out.print(strings);
            out.print(strings);
            out.flush();

            if (i % 2 == 0) {
                System.out.println();
                Thread.yield();
            }
        }
    }
}

class WPOut implements Runnable{
    char chars;
    int n;
    PrintWriter out;


    //constructor
    public WPOut(char c, int t, PrintWriter output){
        chars = c;
        n = t;
        out = output;
    }

    //run method

    public void run(){
       for (int i = 1; i <= n; i+=10){
           System.out.println("WPx");
           Thread.yield();
        }
    }
}

//class DSOut implements Runnable{
//    char chars;
//    int n;
//    PrintWriter out;
//
//
//    //constructor
//    public DSOut(char c, int t, PrintWriter output){
//        chars = c;
//        n = t;
//        out = output;
//    }
//
//    //run method
//
//    public void run(){
//        for (int i = 1; i <= n; i+=10){
//            System.out.println("WPx");
//            Thread.yield();
//
//        }
//    }
//
//
//}
