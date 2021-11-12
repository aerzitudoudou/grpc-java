package com.grpc.greeting.client;

import com.proto.dummy.DummyServiceGrpc;
import com.proto.greet.GreetRequest;
import com.proto.greet.GreetResponse;
import com.proto.greet.GreetServiceGrpc;
import com.proto.greet.Greeting;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class GreetingClient {
    public static void main(String[] args) {
        System.out.println("Hello I'm a grpc client!");

        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50051)
                .usePlaintext()
                .build();

        System.out.println("Creating stub");
        //old and dummy
        //DummyServiceGrpc.DummyServiceBlockingStub syncClient = DummyServiceGrpc.newBlockingStub(channel);

        //DummyServiceGrpc.DummyServiceFutureStub asyncClient = DummyServiceGrpc.newFutureStub(channel);
        //create a greet client (blocking - synchronous)
        GreetServiceGrpc.GreetServiceBlockingStub greetClient = GreetServiceGrpc.newBlockingStub(channel);

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

        System.out.println("shutting down channel");
        channel.shutdown();


    }
}
