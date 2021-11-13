package com.grpc.greeting.client;

import com.proto.dummy.DummyServiceGrpc;
import com.proto.greet.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class GreetingClient {
    public static void main(String[] args) {
        System.out.println("Hello I'm a grpc greeting client!");

        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50051)
                .usePlaintext()
                .build();

        System.out.println("Creating stub");
        //old and dummy
        //DummyServiceGrpc.DummyServiceBlockingStub syncClient = DummyServiceGrpc.newBlockingStub(channel);

        //DummyServiceGrpc.DummyServiceFutureStub asyncClient = DummyServiceGrpc.newFutureStub(channel);
        //create a greet client (blocking - synchronous)
        GreetServiceGrpc.GreetServiceBlockingStub greetClient = GreetServiceGrpc.newBlockingStub(channel);
        //unary
//        //Create a proto buf greeting msg
//        Greeting greeting = Greeting.newBuilder()
//                        .setFirstName("Jone").setLastName("Smith").build();
//
//        //do same for greetrequest
//        GreetRequest greetRequest = GreetRequest.newBuilder()
//                        .setGreeting(greeting)
//                        .build();
//
//        //call the rpc and get back a greetresponse(proto buf)
//        GreetResponse greetResponse = greetClient.greet(greetRequest);
//
//        System.out.println(greetResponse.getResult());
//
//        System.out.println("shutting down channel");

        GreetManyTimesRequest greetManyTimesRequest =
                GreetManyTimesRequest.newBuilder()
                                .setGreeting(Greeting.newBuilder().setFirstName("Mary"))
                                        .build();

        greetClient.greetManyTimes(greetManyTimesRequest)
                        .forEachRemaining(greetManyTimesResponse -> {
                            System.out.println(greetManyTimesResponse.getResult());
                        });

        channel.shutdown();


    }
}
