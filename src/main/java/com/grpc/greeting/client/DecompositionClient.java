package com.grpc.greeting.client;

import com.proto.decomposition.DecompositionRequest;
import com.proto.decomposition.DecompositionResponse;
import com.proto.decomposition.DecompositionServiceGrpc;
import com.proto.decomposition.Number;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class DecompositionClient {
    public static void main(String[] args) {
        System.out.println("I'm a decomposition client!");

        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50051)
                .usePlaintext()
                .build();

        System.out.println("creating stub...");
        //create a decomposition client
        DecompositionServiceGrpc.DecompositionServiceBlockingStub decompositionClient = DecompositionServiceGrpc.newBlockingStub(channel);

        DecompositionRequest decompositionRequest = DecompositionRequest
                .newBuilder()
                .setNumber(Number.newBuilder().setNum(120).build())
                .build();

        decompositionClient.decomposition(decompositionRequest)
                .forEachRemaining(decompositionResponse -> {
                    System.out.println(decompositionResponse.getResult());
                });

        channel.shutdown();

    }
}
