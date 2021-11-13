package com.grpc.greeting.server;

import com.proto.calculator.CalculatorRequest;
import com.proto.calculator.CalculatorResponse;
import com.proto.calculator.CalculatorServiceGrpc;
import com.proto.calculator.Integers;
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
}
