package Concurrency;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

public class CompletableFutureExample {

    /**
     * Need for Completable Future
     */

    public static void main(String[] args) {
        needForCompletableFuture();
        usingCompletableFuture();
    }

    private static void usingCompletableFuture() {
        long start = System.currentTimeMillis();

        List<CompletableFuture> futureList = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            int tempI = i;
            futureList.add(CompletableFuture.supplyAsync(() -> getOrderTask())
                    .thenApplyAsync((randomValue) -> getOrderTask2(randomValue))
                    .thenAccept((randomValue) -> getOrderTaskDependent(tempI, randomValue)));
        }

        futureList.forEach(CompletableFuture::join);
        long end = System.currentTimeMillis();
        System.out.println("Time taken with Completable Future " + (end - start));
    }

    private static void needForCompletableFuture() {
        Executor service = Executors.newFixedThreadPool(10);

        /** We use future (as a placeholder) to get any returned value from the asynchronous task
         *  Here we get the placeholder (Future) IMMEDIATELY, Hence the below code line is NON BLOCKING
         * **/
        long start = System.currentTimeMillis();
        ArrayList<Future<Integer>> futures = new ArrayList<>();

        for (int i = 0; i < 20; i++) {
            try {
                // todo Note this if we call get() on future, it is a blocking operation
                //  till the time value is not available in the future variable
                //  it blocks the main thread, Problem will amplify if this get() operation is done in a for loop wherein
                Future<Integer> future = ((ExecutorService) service).submit(new Task());
                Integer result = future.get();

                // todo below operation is independent of the above result
                //  Note didn't find an alternative to run dependent task without CompletableFuture
                future = ((ExecutorService) service).submit(new Task1());
                result = future.get();
                System.out.println(i + " recieved Value " + result);

            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            }


        }

        long end = System.currentTimeMillis();
        System.out.println("Time taken without Completable Future " + (end - start));
    }

    public static int getOrderTask() {
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            throw new IllegalStateException(e);
        }
        return new Random().nextInt();

    }

    public static int getOrderTask2(int value) {
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            throw new IllegalStateException(e);
        }
        return value / 10;

    }

    public static void getOrderTaskDependent(int tempI, int randomValue) {
        System.out.println(tempI + " recieved Value " + randomValue);
    }


    static class Task implements Callable<Integer> {

        @Override
        public Integer call() throws Exception {
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                throw new IllegalStateException(e);
            }
            return new Random().nextInt();
        }
    }

    static class Task1 implements Callable<Integer> {

        @Override
        public Integer call() throws Exception {
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                throw new IllegalStateException(e);
            }
            return new Random().nextInt();
        }
    }


}
