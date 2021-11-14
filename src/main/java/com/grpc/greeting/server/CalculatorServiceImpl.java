package com.grpc.greeting.server;

import com.proto.calculator.*;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;

public class CalculatorServiceImpl extends CalculatorServiceGrpc.CalculatorServiceImplBase {
    @Override
    public void calculate(CalculatorRequest request, StreamObserver<CalculatorResponse> responseObserver) {
        //super.calculate(request, responseObserver);

        Integers integers = request.getIntegers();
        int firstNum = integers.getFirstInteger();
        int secondNum = integers.getSecondInteger();

        int result = firstNum + secondNum;

        CalculatorResponse response = CalculatorResponse.newBuilder()
                .setResult(result)
                .build();

        responseObserver.onNext(response);

        responseObserver.onCompleted();

    }


    @Override
    public StreamObserver<AverageRequest> average(StreamObserver<AverageResponse> responseObserver) {
        StreamObserver<AverageRequest> requestObserver = new StreamObserver<AverageRequest>() {
            double sum = 0;
            int counter = 0;
            @Override
            public void onNext(AverageRequest value) {
                counter++;
                sum+=value.getNumber();
            }

            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onCompleted() {
                responseObserver.onNext(
                        AverageResponse.newBuilder()
                                .setResult(sum/counter)
                                .build()
                );

                responseObserver.onCompleted();
            }
        };

        return requestObserver;
    }

    @Override
    public StreamObserver<MaxRequest> max(StreamObserver<MaxResponse> responseObserver) {
        StreamObserver<MaxRequest> requestObserver = new StreamObserver<MaxRequest>() {
            int curMax = Integer.MIN_VALUE;
            @Override
            public void onNext(MaxRequest value) {
                System.out.println("curMax is: " + curMax);
                int cur = value.getNumber();
                if(cur > curMax){
                    MaxResponse maxResponse = MaxResponse.newBuilder()
                            .setResult(cur)
                            .build();
                    responseObserver.onNext(maxResponse);
                    curMax = cur;

                }
            }

            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onCompleted() {
                responseObserver.onCompleted();
            }
        };

        return requestObserver;
    }

    @Override
    public void squareRoot(SquareRootRequest request, StreamObserver<SquareRootResponse> responseObserver) {
        Integer number = request.getNumber();
        if(number > 0){
            double numberRoot = Math.sqrt(number);
            responseObserver.onNext(
                    SquareRootResponse.newBuilder()
                            .setNumberRoot(numberRoot)
                            .build()
            );
            responseObserver.onCompleted();
        }else{
            //error handling
            responseObserver.onError(
                    Status.INVALID_ARGUMENT
                            .withDescription("The number is not valid")
                            .asRuntimeException()
            );
        }
    }
}
