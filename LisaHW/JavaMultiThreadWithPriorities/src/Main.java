import java.io.File;
import java.io.PrintWriter;

public class Main {

    public static void main(String[] args) throws Exception{
        PrintWriter output;
        output = new PrintWriter(new File("output1.txt"));

        //create tasks

        Runnable Gui = new GuiOut("Gui Poll\n\n", 600, output);
        Runnable WP = new WPOut('A', 50*10*2, output);
        Runnable DS = new DSOut(2500, output);
        Runnable Printer = new PrintOut(3600, output);

        //create threads

        Thread thread1 = new Thread(Gui);
        Thread thread2 = new Thread(WP);
        Thread thread3 = new Thread(DS);
        Thread thread4 = new Thread(Printer);

        //priorities

        thread1.setPriority(Thread.MAX_PRIORITY);
        thread2.setPriority(Thread.MIN_PRIORITY);
        thread3.setPriority(Thread.NORM_PRIORITY);
        thread4.setPriority(Thread.NORM_PRIORITY);

        //Start Threads

        thread1.start();
        thread2.start();
        thread3.start();
        thread4.start();

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
            Thread.yield();

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
        for (int i = 1; i <= n; i++){
            if (i%10==0){
                System.out.print("WPx\n");
                out.println("WPx\n");
                out.flush();
                Thread.yield();
            }
        }
    }
}

class DSOut implements Runnable{
    int n;
    PrintWriter out;


    //constructor
    public DSOut(int t, PrintWriter output){
        n = t;
        out = output;
    }

    //run method

    public void run(){
        for (int i = 0; i <= n; i += 20){
            System.out.println("DS" + i);
            out.print("DS" + i);
            if (i%60==0){
                out.flush();
                Thread.yield();
            }
        }
    }
}

class PrintOut implements Runnable{
    int n;
    PrintWriter out;


    //constructor
    public PrintOut(int t, PrintWriter output){
        n = t;
        out = output;
    }

    //run method

    public void run(){
        for (int i = 1; i <= n; i++){
            System.out.print("Print Line " + i + ", characters \n" );
            out.print("Print Line " + i + ", characters \n" );
            if (i%60==0){

            }


            if (i%6*60==0){
                out.flush();
                Thread.yield();
            }

        }
    }
}
