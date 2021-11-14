package com.grpc.greeting.client;

import com.proto.dummy.DummyServiceGrpc;
import com.proto.greet.*;
import io.grpc.*;
import io.grpc.stub.StreamObserver;

import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class GreetingClient {
    public static void main(String[] args) {
        System.out.println("Hello I'm a grpc greeting client!");

        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50051)
                .usePlaintext()
                .build();

        System.out.println("Creating stub");
        GreetingClient greetingClient = new GreetingClient();
//        greetingClient.doUnaryCall(channel);
//        greetingClient.doServerStreamingCall(channel);
//        greetingClient.doClientStreamingCall(channel);
//        greetingClient.doBiDiStreamingCall(channel);
        greetingClient.doUnaryCallWithDdl(channel);




        channel.shutdown();


    }

    private void doUnaryCall(ManagedChannel channel){
        GreetServiceGrpc.GreetServiceBlockingStub greetClient = GreetServiceGrpc.newBlockingStub(channel);
        //unary
        //Create a proto buf greeting msg
        Greeting greeting = Greeting.newBuilder()
                .setFirstName("Jone").setLastName("Smith").build();

        //do same for greetrequest
        GreetRequest greetRequest = GreetRequest.newBuilder()
                .setGreeting(greeting)
                .build();

        //call the rpc and get back a greetresponse(proto buf)
        GreetResponse greetResponse = greetClient.greet(greetRequest);

        System.out.println(greetResponse.getResult());

    }


    private void doServerStreamingCall(ManagedChannel channel){
        GreetServiceGrpc.GreetServiceBlockingStub greetClient = GreetServiceGrpc.newBlockingStub(channel);

        GreetManyTimesRequest greetManyTimesRequest =
                GreetManyTimesRequest.newBuilder()
                        .setGreeting(Greeting.newBuilder().setFirstName("Mary"))
                        .build();

        greetClient.greetManyTimes(greetManyTimesRequest)
                .forEachRemaining(greetManyTimesResponse -> {
                    System.out.println(greetManyTimesResponse.getResult());
                });
    }

    private void doClientStreamingCall(ManagedChannel channel){
        //create an async client
        GreetServiceGrpc.GreetServiceStub asyncClient = GreetServiceGrpc.newStub(channel);

        //this latch will be used by the responseObserver when we are done
        CountDownLatch latch = new CountDownLatch(1);

        //what client will do when get a response from the server
        StreamObserver<LongGreetRequest> requestObserver = asyncClient.longGreet(new StreamObserver<LongGreetResponse>() {
            @Override
            public void onNext(LongGreetResponse value) {
                //we get a response from the server
                //on next will be called only once
                System.out.println("received a response from the server:");
                System.out.println(value.getResult());
            }

            @Override
            public void onError(Throwable t) {
                //we get an error from the server
            }

            @Override
            public void onCompleted() {
                //server is done sending data
                //this will be called right after onNext()
                System.out.println("Server has completed sending us something");
                latch.countDown();
            }
        });

        System.out.println("Sending msg 1...");

        requestObserver.onNext(LongGreetRequest.newBuilder()
                        .setGreeting(Greeting.newBuilder()
                                .setFirstName("Alice")
                                .build())
                .build());

        System.out.println("Sending msg 2...");
        requestObserver.onNext(LongGreetRequest.newBuilder()
                .setGreeting(Greeting.newBuilder()
                        .setFirstName("Mary")
                        .build())
                .build());

        System.out.println("Sending msg 3...");
        requestObserver.onNext(LongGreetRequest.newBuilder()
                .setGreeting(Greeting.newBuilder()
                        .setFirstName("Tom")
                        .build())
                .build());


        //tell the server that the client is done sending data
        requestObserver.onCompleted();

        try {
            //whenever latch is 0 then await is completed
            latch.await(3, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    private void doBiDiStreamingCall(ManagedChannel channel){
        GreetServiceGrpc.GreetServiceStub asyncClient = GreetServiceGrpc.newStub(channel);
        CountDownLatch latch = new CountDownLatch(1);

        StreamObserver<GreetEveryoneRequest> requestObserver = asyncClient.greetEveryone(new StreamObserver<GreetEveryoneResponse>() {
            @Override
            public void onNext(GreetEveryoneResponse value) {
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

        Arrays.asList("Mary", "Alice", "Marc", "Patricia").forEach(

                name -> {
                    System.out.println("Sending: " + name);
                    requestObserver.onNext(GreetEveryoneRequest.newBuilder()
                            .setGreeting(Greeting.newBuilder()
                                    .setFirstName(name))
                            .build());
                    try {
                        Thread.sleep(20L);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
        );

        requestObserver.onCompleted();
        try {
            latch.await(3, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void doUnaryCallWithDdl(ManagedChannel channel){
        GreetServiceGrpc.GreetServiceBlockingStub blockingStub = GreetServiceGrpc.newBlockingStub(channel);


        //first call(3000ms ddl)

        try{
            System.out.println("Sending a request with a ddl of 500 ms");
            GreetWithDdlResponse response = blockingStub.withDeadline(Deadline.after(3000, TimeUnit.MILLISECONDS))
                    .greetWithDdl(
                            GreetWithDdlRequest.newBuilder().setGreeting(
                                            Greeting.newBuilder()
                                                    .setFirstName("Mary")
                                                    .build()
                                    )
                                    .build()
                    );
            System.out.println(response.getResult());
        }catch(StatusRuntimeException e){
            if(e.getStatus().getCode().equals(Status.DEADLINE_EXCEEDED.getCode())){
                System.out.println("Ddl has been exceeded, we don't want response");
            }else{
                e.printStackTrace();
            }
        }



        //first call(100ms ddl)

        try{
            System.out.println("Sending a request with a ddl of 100 ms");
            GreetWithDdlResponse response = blockingStub.withDeadline(Deadline.after(100, TimeUnit.MILLISECONDS))
                    .greetWithDdl(
                            GreetWithDdlRequest.newBuilder().setGreeting(
                                            Greeting.newBuilder()
                                                    .setFirstName("Mary")
                                                    .build()
                                    )
                                    .build()
                    );
            System.out.println(response.getResult());
        }catch(StatusRuntimeException e){
            if(e.getStatus().getCode().equals(Status.DEADLINE_EXCEEDED.getCode())){
                System.out.println("Ddl has been exceeded, we don't want response");
            }else{
                e.printStackTrace();
            }
        }


    }



}
