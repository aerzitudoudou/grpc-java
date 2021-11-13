package com.grpc.greeting.client;

import com.proto.calculator.CalculatorRequest;
import com.proto.calculator.CalculatorResponse;
import com.proto.calculator.CalculatorServiceGrpc;
import com.proto.calculator.Integers;
import com.proto.greet.GreetRequest;
import com.proto.greet.GreetResponse;
import com.proto.greet.GreetServiceGrpc;
import com.proto.greet.Greeting;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class CalculatorClient {
    public static void main(String[] args) {
        System.out.println("Hello I'm a grpc calculator client!");

        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50051)
                .usePlaintext()
                .build();

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

        System.out.println("shutting down channel");
        channel.shutdown();
    }
}
