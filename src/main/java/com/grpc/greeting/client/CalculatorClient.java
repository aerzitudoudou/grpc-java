package com.grpc.greeting.client;

import com.proto.calculator.*;
import com.proto.greet.GreetRequest;
import com.proto.greet.GreetResponse;
import com.proto.greet.GreetServiceGrpc;
import com.proto.greet.Greeting;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;

import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class CalculatorClient {
    public static void main(String[] args) {
        System.out.println("Hello I'm a grpc calculator client!");

        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50051)
                .usePlaintext()
                .build();
        CalculatorClient calculatorClient = new CalculatorClient();

//        calculatorClient.calculate(channel);
//        calculatorClient.computeAverage(channel);
//        calculatorClient.max(channel);
        calculatorClient.doErrorCall(channel);



        System.out.println("shutting down channel");
        channel.shutdown();
    }

    private void doErrorCall(ManagedChannel channel){
        CalculatorServiceGrpc.CalculatorServiceBlockingStub blockingStub = CalculatorServiceGrpc.newBlockingStub(channel);
        try{
            blockingStub.squareRoot(SquareRootRequest.newBuilder()
                    .setNumber(-4)
                    .build());
        }catch(StatusRuntimeException e){
            System.out.println("Got an exception for square root!");
            e.printStackTrace();
        }

    }

    private void max(ManagedChannel channel) {
        CalculatorServiceGrpc.CalculatorServiceStub asyncClient = CalculatorServiceGrpc.newStub(channel);
        CountDownLatch latch = new CountDownLatch(1);

        StreamObserver<MaxRequest> reqeustObserver = asyncClient.max(new StreamObserver<MaxResponse>() {
            @Override
            public void onNext(MaxResponse value) {
                System.out.println("response from server: " + value.getResult());

            }

            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onCompleted() {
                System.out.println("server is done sending data");
                latch.countDown();
            }
        });

//        for(int i = 1; i < 100; i++){
//            System.out.println("sending : " + i);
//            reqeustObserver.onNext(MaxRequest.newBuilder()
//                    .setNumber(i)
//                    .build());
//            try {
//                Thread.sleep(1000L);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//
//        }


        Arrays.asList(1, 100, 2, 3, 6, 101, 5, 6, 900).forEach(
                    number ->{
                        System.out.println("Sending : " + number);
                        reqeustObserver.
                                onNext(MaxRequest.newBuilder()
                                        .setNumber(number)
                                        .build());

                        try {
                            Thread.sleep(100L);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }


        );

        reqeustObserver.onCompleted();

        try {
            latch.await(3, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }

    private void computeAverage(ManagedChannel channel) {
        CalculatorServiceGrpc.CalculatorServiceStub asyncClient = CalculatorServiceGrpc.newStub(channel);

        CountDownLatch latch = new CountDownLatch(1);

        StreamObserver<AverageRequest> requestObserver = asyncClient.average(new StreamObserver<AverageResponse>() {
            @Override
            public void onNext(AverageResponse value) {
                System.out.println("receive the average from the server: ");
                System.out.println(value.getResult());
            }

            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onCompleted() {
                System.out.println("server complete send the average");
                latch.countDown();
            }
        });

        System.out.println("sending integer 1...");

        requestObserver.onNext(AverageRequest.newBuilder()
                .setNumber(1)
                .build()
        );

        System.out.println("sending integer 2...");

        requestObserver.onNext(AverageRequest.newBuilder()
                .setNumber(2)
                .build()
        );

        System.out.println("sending integer 3...");

        requestObserver.onNext(AverageRequest.newBuilder()
                .setNumber(3)
                .build()
        );

        System.out.println("sending integer 4...");

        requestObserver.onNext(AverageRequest.newBuilder()
                .setNumber(4)
                .build()
        );

        requestObserver.onCompleted();


        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void calculate(ManagedChannel channel){
        System.out.println("Creating stub");
        //old and dummy
        //DummyServiceGrpc.DummyServiceBlockingStub syncClient = DummyServiceGrpc.newBlockingStub(channel);

        //DummyServiceGrpc.DummyServiceFutureStub asyncClient = DummyServiceGrpc.newFutureStub(channel);
        //create a greet client (blocking - synchronous)
        CalculatorServiceGrpc.CalculatorServiceBlockingStub calculatorClient = CalculatorServiceGrpc.newBlockingStub(channel);

        //Create a proto buf greeting msg
        Integers numbers = Integers.newBuilder()
                .setFirstInteger(3)
                .setSecondInteger(10)
                .build();

        //do same for greetrequest
        CalculatorRequest calculatorRequest = CalculatorRequest.newBuilder()
                .setIntegers(numbers)
                .build();

        //call the rpc and get back a greetresponse(proto buf)
        CalculatorResponse calculatorResponse = calculatorClient.calculate(calculatorRequest);

        System.out.println(calculatorResponse.getResult());
    }
}
