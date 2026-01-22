package com.wangindustries.badmintondbBackend;

import com.amazonaws.serverless.exceptions.ContainerInitializationException;
import com.amazonaws.serverless.proxy.model.AwsProxyRequest;
import com.amazonaws.serverless.proxy.model.AwsProxyResponse;
import com.amazonaws.serverless.proxy.spring.SpringBootLambdaContainerHandler;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class LambdaHandler implements RequestHandler<AwsProxyRequest, AwsProxyResponse> {
    private static final Logger logger = LoggerFactory.getLogger(LambdaHandler.class);
    private static SpringBootLambdaContainerHandler<AwsProxyRequest, AwsProxyResponse> handler;

    static {
        try {
            // SpringBootLambdaContainer must be initialized with the springbootapplication entrypoint class...
            handler = SpringBootLambdaContainerHandler.getAwsProxyHandler(BadmintondbBackendApplication.class); }
        catch (ContainerInitializationException ex){
            throw new RuntimeException("Unable to load spring boot application",ex); }
    }

    @Override
    public AwsProxyResponse handleRequest(AwsProxyRequest input, Context context) {
        logger.info("Got to handle request method with input http method & path {}::{}", input.getHttpMethod(), input.getPath());
        return handler.proxy(input, context);
    }
}
