
class ScannerStation extends Thread {

    private int scripts;

    ScannerStation(String name, int scripts) {
        super(name);
        this.scripts = scripts;
    }

    public void run() {
        try {
            System.out.println(getName() + " started scanning.");

            Thread.sleep(1000);

            System.out.println(
                getName() + " scanned " + scripts + " scripts.");

        } catch (InterruptedException e) {
            System.out.println("Thread interrupted.");
            Thread.currentThread().interrupt();
        }
    }
}

class RunnableStation implements Runnable {

    private int scripts;
    private String stationName;

    RunnableStation(String name, int scripts) {
        this.stationName = name;
        this.scripts = scripts;
    }

    public void run() {
        try {
            System.out.println(stationName + " started scanning.");

            Thread.sleep(1500);

            System.out.println(
                stationName + " scanned " + scripts + " scripts.");

        } catch (InterruptedException e) {
            System.out.println("Thread interrupted.");
            Thread.currentThread().interrupt();
        }
    }
}

public class ThreadDemo {

    public static void main(String[] args)
            throws InterruptedException {

        System.out.println("Examination Scanner Stations");

        // Creating threads by extending Thread
        ScannerStation s1 =
            new ScannerStation("Station 1", 10);

        ScannerStation s2 =
            new ScannerStation("Station 2", 15);

        // Creating threads using Runnable
        Thread s3 = new Thread(
            new RunnableStation("Station 3", 20));

        Thread s4 = new Thread(
            new RunnableStation("Station 4", 25));

        // Starting all threads
        s1.start();
        s2.start();
        s3.start();
        s4.start();

        System.out.println("Station 1 alive: " + s1.isAlive());

        // Waiting for all threads to finish
        s1.join();
        s2.join();
        s3.join();
        s4.join();

        System.out.println("All scanner stations completed.");
        System.out.println("Station 1 alive: " + s1.isAlive());

        System.out.println("Total scripts scanned: 70");
    }
}

