package edu.handong.csee.isel.network;

import com.google.protobuf.Any;
import com.google.rpc.Code;
import com.google.rpc.ErrorInfo;

import io.grpc.protobuf.StatusProto;
import io.grpc.stub.StreamObserver;

public class ErrorHandler {
    /**
     * Handle error.
     * 
     * @param message status message
     * @param code status code
     * @param reason reason of the error
     * @param domain domain of the error
     * @param responseObserver response observer that receives the error
     */
    public void handle(String message, Code code, 
                       String reason, String domain,
                       StreamObserver<?> responseObserver) {
        Any detail = Any.pack(ErrorInfo.newBuilder()
                                       .setReason(reason)
                                       .setDomain(domain)
                                       .build());
        responseObserver.onError(
                StatusProto.toStatusRuntimeException(
                        com.google.rpc.Status.newBuilder()
                                             .setMessage(message)     
                                             .setCode(code.getNumber())
                                             .setDetails(0, detail)
                                             .build()));
    }
}
