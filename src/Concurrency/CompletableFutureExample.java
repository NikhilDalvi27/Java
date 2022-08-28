package Concurrency;

import java.util.Random;
import java.util.concurrent.*;

public class CompletableFutureExample {

    /**
     * Need for Completable Future
     */

    public static void main(String[] args) {
//        NeedForCompletableFuture1();
        NeedForCompletableFuture2();
    }

    private static void NeedForCompletableFuture2() {
        Executor service = Executors.newFixedThreadPool(10);
        try {
            Future<Integer> future =  ((ExecutorService) service).submit(getOrderTask());
            Integer res = future.get();

            ((ExecutorService) service).submit(getOrderTaskDependent(res));

        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    private static void NeedForCompletableFuture1() {
        Executor service = Executors.newFixedThreadPool(10);

        /** We use future (as a placeholder) to get any returned value from the asynchronous task **/
        Future<Integer> future = ((ExecutorService) service).submit(new Task());

        try{
            // todo Note this is blocking if we call the get() operation
            //  before the result is available in the future variable
            // blocks the main thread, Problem will amplify if this get() operation is done in a for loop
            Integer result = future.get();
            System.out.println("Result from the task: "+ result);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static Callable<Integer> getOrderTask(){
        return new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                int res = new Random().nextInt();
                System.out.println("Expected value "+ res);
                return res;
            }
        };
    }

    public static Callable<Integer> getOrderTaskDependent(int prevVal){
        return new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                System.out.println("Received value "+ prevVal);
                return prevVal;
            }
        };
    }


    static class Task implements Callable<Integer> {

        @Override
        public Integer call() throws Exception {
            return new Random().nextInt();
        }
    }


}
