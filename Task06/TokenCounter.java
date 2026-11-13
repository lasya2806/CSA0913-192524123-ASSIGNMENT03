
public class TokenCounter {

    // PART A: Hostel Wallet Transfer

    static class Wallet {
        int id;
        int balance;

        Wallet(int id, int balance) {
            this.id = id;
            this.balance = balance;
        }
    }

    // Transfer with consistent lock ordering (deadlock prevention)
    static void transfer(Wallet a, Wallet b, int amount) {

        Wallet first = a.id < b.id ? a : b;
        Wallet second = a.id < b.id ? b : a;

        synchronized (first) {
            synchronized (second) {

                if (a.balance >= amount) {
                    a.balance -= amount;
                    b.balance += amount;

                    System.out.println(
                        "Transferred " + amount +
                        " from Wallet " + a.id +
                        " to Wallet " + b.id);
                }
            }
        }
    }

    // PART B: Producer-Consumer Problem

    private final int[] buffer = new int[5];

    private int count = 0;
    private int in = 0;
    private int out = 0;

    // Producer method
    public synchronized void put(int token)
            throws InterruptedException {

        while (count == buffer.length) {
            wait();
        }

        buffer[in] = token;
        in = (in + 1) % buffer.length;
        count++;

        System.out.println("Produced token: " + token);

        notifyAll();
    }

    // Consumer method
    public synchronized int take()
            throws InterruptedException {

        while (count == 0) {
            wait();
        }

        int token = buffer[out];
        out = (out + 1) % buffer.length;
        count--;

        System.out.println("Consumed token: " + token);

        notifyAll();

        return token;
    }

    public static void main(String[] args)
            throws InterruptedException {

        // Part A: Wallet transfer
        System.out.println("PART A: WALLET TRANSFER");

        Wallet w1 = new Wallet(1, 1000);
        Wallet w2 = new Wallet(2, 500);

        Thread t1 = new Thread(
            () -> transfer(w1, w2, 100));

        Thread t2 = new Thread(
            () -> transfer(w2, w1, 50));

        t1.start();
        t2.start();

        t1.join();
        t2.join();

        System.out.println("Wallet 1 Balance: " + w1.balance);
        System.out.println("Wallet 2 Balance: " + w2.balance);

        // Part B: Producer-Consumer
        System.out.println("\nPART B: PRODUCER CONSUMER");

        TokenCounter counter = new TokenCounter();

        int[] collected = new int[2];

        Thread cook = new Thread(() -> {
            try {
                for (int t = 1; t <= 20; t++) {
                    counter.put(t);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        Thread[] students = new Thread[2];

        for (int s = 0; s < 2; s++) {

            final int id = s;

            students[s] = new Thread(() -> {
                try {
                    for (int k = 0; k < 10; k++) {
                        collected[id] += counter.take();
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }

        cook.start();

        for (Thread t : students) {
            t.start();
        }

        cook.join();

        for (Thread t : students) {
            t.join();
        }

        System.out.println("\nSum of tokens collected: "
            + (collected[0] + collected[1])
            + " (expected 210)");
    }
}

