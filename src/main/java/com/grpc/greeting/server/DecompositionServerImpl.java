package com.grpc.greeting.server;

import com.proto.decomposition.DecompositionRequest;
import com.proto.decomposition.DecompositionServiceGrpc;
import com.proto.decomposition.DecompositionResponse;
import com.proto.decomposition.Number;
import io.grpc.stub.StreamObserver;

public class DecompositionServerImpl extends DecompositionServiceGrpc.DecompositionServiceImplBase {
    @Override
    public void decomposition(DecompositionRequest request, StreamObserver<DecompositionResponse> responseObserver) {
        //extract the number
        Number number = request.getNumber();
        int num = number.getNum();


        int k = 2;
        while(num > 1){
            //find the decomposition
            if(num % k == 0){
                //generate response
                DecompositionResponse response = DecompositionResponse.newBuilder()
                        .setResult(k)
                        .build();
                //send response to client
                responseObserver.onNext(response);
                num /= k;
            }else k++;
        }
        //complete the rpc call
        responseObserver.onCompleted();

    }
}
