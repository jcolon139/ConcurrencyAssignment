import java.util.Random;
public class Concurrency extends Thread {

    int[] testArray;

    int low;
    int high;
    int partial;

    public Concurrency(int[] testArray, int low, int high) {
        this.testArray = testArray;
        this.low = low;
        this.high = Math.min(high, testArray.length);
    }

    public static int sum(int[] testArray, int low, int high) {
        int total = 0;

        for (int i = low; i < high; i++) {
            total += testArray[i];
        }
        return total;
    }

    public static int sum (int[] testArray) {
        return sum(testArray, 0, testArray.length);
    }

    public int getPartialSum()
    {
        return partial;
    }

    @Override
    public void run()
    {
        partial = sum(testArray, low, high);
    }

    public static int parallelSum(int[] arr)
    {
        return parallelSum(arr, Runtime.getRuntime().availableProcessors());
    }
    public static int parallelSum(int[] arr, int threads)
    {
        int size = (int) Math.ceil(arr.length * 1.0 / threads);

        Concurrency[] sums = new Concurrency[threads];

        for (int i = 0; i < threads; i++) {
            sums[i] = new Concurrency(arr, i * size, (i + 1) * size);
            sums[i].start();
        }

        try {
            for (Concurrency sum : sums) {
                sum.join();
            }
        } catch (InterruptedException ignored) { }

        int total = 0;

        for (Concurrency sum : sums) {
            total += sum.getPartialSum();
        }

        return total;
    }

    public static void main(String[] args) {

        Random random = new Random();

        int[] threadArray = new int [200000000];

        for(int i = 0; i < threadArray.length; i++) {
            threadArray[i] = random.nextInt(10) + 1;
        }

        long start = System.currentTimeMillis();

        System.out.println("Single Thread: Total Sum = " + (Concurrency.sum(threadArray)) + " in " + (System.currentTimeMillis() - start) + " milliseconds.");

        start = System.currentTimeMillis();

        System.out.println("Multiple Threads: Total Sum = " + (Concurrency.parallelSum(threadArray)) + " in " + (System.currentTimeMillis() - start) + " milliseconds.");

    }
}