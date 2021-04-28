package com.atguigu.gulimall.search.thread;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadTest {


    public static ExecutorService executor = Executors.newFixedThreadPool(10);

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        System.out.println("main.....start....");

//        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
//			System.out.println("当前线程" + Thread.currentThread().getId());
//			int i = 10 / 2;
//			System.out.println("运行结束" + i);
//        }, executor);

        /**
         * 方法完成后的感知
         */
//        CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> {
//            System.out.println("当前线程" + Thread.currentThread().getId());
//            int i = 10 / 2;
//            System.out.println("运行结束" + i);
//            return i;
//        }, executor).whenComplete((result,exception)->{
//            //虽然能得到异常信息，但没法修改返回数据
//            System.out.println("异步任务成功完成了.....结果是:"+ result + "，异常是:"+exception);
//        }).exceptionally(throwable -> {
//            //可以感知异常。同时返回默认值
//            return 10;
//        });
//        System.out.println("main.....end...." + future.get());

        /**
         * 方法执行完成后的处理
         */
//        CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> {
//            System.out.println("当前线程" + Thread.currentThread().getId());
//            int i = 10 / 2;
//            System.out.println("运行结束" + i);
//            return i;
//        }, executor).handle((result, err)->{
//            if(result != null){
//                return result * 2;
//            }
//            if(err != null){
//                return 0;
//            }
//            return 0;
//        });
//        System.out.println("main.....end...." + future.get());

        /**
         * 线程串行化
         * 1)丶 thenRunAsync: 不能获取到上一步的执行结果
         *
         * 2)丶thenAcceptAsync: 使用上一步的结果 但是没有返回结果
         *
         * 3)丶thenApplyAsync: 能接受上一步的结果 还有返回值
         */
//        CompletableFuture.supplyAsync(() -> {
//            System.out.println("当前线程" + Thread.currentThread().getId());
//            int i = 10 / 2;
//            System.out.println("运行结束" + i);
//            return i;
//        }, executor).thenRunAsync(()->{
//            System.out.println("任务二启动了");
//        },executor);
//        System.out.println("main.....end....");

        /**
         * 使用上一步的结果 但是没有返回结果
         */
//		CompletableFuture.supplyAsync(() -> {
//			System.out.println("当前线程" + Thread.currentThread().getId());
//			int i = 10 / 2;
//			System.out.println("运行结束" + i);
//			return i;
//		}, executor).thenAcceptAsync(res -> System.out.println("thenAcceptAsync获取上一步执行结果：" + res));

        /**
         * 能接受上一步的结果 还有返回值
         */
//		CompletableFuture<String> async = CompletableFuture.supplyAsync(() -> {
//			System.out.println("当前线程" + Thread.currentThread().getId());
//			int i = 10 / 2;
//			System.out.println("运行结束" + i);
//			return i;
//		}, executor).thenApplyAsync(res -> {
//			System.out.println("任务2启动了...");
//			try {
//				Thread.sleep(2000);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
//			return "thenApplyAsync" + res;
//		});
//		System.out.println("thenApplyAsync获取结果:" + async.get());

        /**
         * 两个都要完成
         */
//        CompletableFuture<Object> future1 = CompletableFuture.supplyAsync(() -> {
//            System.out.println("任务1线程:" + Thread.currentThread().getId());
//            int i = 10 / 2;
//            System.out.println("任务1结束.");
//            return i;
//        }, executor);
//
//        CompletableFuture<Object> future2 = CompletableFuture.supplyAsync(() -> {
//            System.out.println("任务2线程:" + Thread.currentThread().getId());
//            try {
//                Thread.sleep(3000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            System.out.println("任务2结束.");
//            return "hello";
//        }, executor);

        //合并上面两个任务 这个不能感知结果
//        future1.runAfterBothAsync(future2, ()->{
//            System.out.println("任务3开始");
//        }, executor);


        // 合并上面两个任务 可以感知前面任务的结果
//        future1.thenAcceptBothAsync(future2,(f1, f2)->{
//            System.out.println("任务3开始.....之前任务结果:"+f1+"---->"+f2);
//        }, executor);

        /**
         * 合并两个任何 还可以返回结果
         */
////		CompletableFuture<String> future3 = future1.thenCombineAsync(future2, (res1, res2) -> res1 + ":" + res2 + "-> fire", executor);
////		System.out.println("自定义返回结果：" + future3.get());


        /**
         * 合并两个任务 其中任何一个完成了 就执行这个
         * runAfterEitherAsync: 不感知结果，自己没有返回值
         * acceptEitherAsync: 感知结果，自己没有返回值
         * applyToEitherAsync: 感知结果，自己有返回值
         */
//        future1.runAfterEitherAsync(future2,()->{
//            System.out.println("任务3开始。。。。");
//        },executor);

//        future1.acceptEitherAsync(future2, (res)->{
//            System.out.println("任务3开始。。。。" + res);
//        },executor);

//        CompletableFuture<String> future = future1.applyToEitherAsync(future2, res -> {
//
//            System.out.println("任务3开始之前的结果...." + res);
//            return res.toString() + "->哈哈";
//        }, executor);
//        System.out.println("main.....end...." +future.get());


        CompletableFuture<String> futureImg = CompletableFuture.supplyAsync(() -> {
            System.out.println("查询商品图片信息");
            return "1.jpg";
        },executor);

        CompletableFuture<String> futureAttr = CompletableFuture.supplyAsync(() -> {
            System.out.println("查询商品的属性");
            return "麒麟990 5G  钛空银";
        },executor);

        CompletableFuture<String> futureDesc = CompletableFuture.supplyAsync(() -> {

            try {
                Thread.sleep(3000);
                System.out.println("查询商品介绍");

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "华为";
        },executor);

        /**
         * 等这三个都做完
         */
//        CompletableFuture<Void> allOf = CompletableFuture.allOf(futureImg, futureAttr, futureDesc);
//        allOf.get(); //等待所有结果完成
//        System.out.println("main.....end...." + futureImg.get() +"=>" + futureAttr.get() + "==>" + futureDesc.get());

        CompletableFuture<Object> anyOf = CompletableFuture.anyOf(futureImg, futureAttr, futureDesc);
        anyOf.get();

        System.out.println("main....end" + anyOf.get());
        executor.shutdown();

    }



}
