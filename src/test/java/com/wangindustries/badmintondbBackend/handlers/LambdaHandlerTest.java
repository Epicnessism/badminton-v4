package com.wangindustries.badmintondbBackend.handlers;

import com.amazonaws.serverless.proxy.model.AwsProxyRequest;
import com.amazonaws.serverless.proxy.model.AwsProxyResponse;
import com.amazonaws.services.lambda.runtime.Context;
import com.wangindustries.badmintondbBackend.LambdaHandler;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;

public class LambdaHandlerTest {

    /// todo this test fails because when lambdahandler is invoked, error saying the
    /// com.amazonaws.serverless.exceptions.InvalidRequestEventException: The incoming event is not a valid request from Amazon API Gateway or an Application Load Balancer
    @Test
    @Disabled
    void whenTheUsersPathIsInvokedViaLambda_thenShouldReturnAList() throws IOException {
        LambdaHandler lambdaHandler = new LambdaHandler();
        Context lambdaContext = Mockito.mock(Context.class);

        AwsProxyRequest req = new AwsProxyRequest();
        req.setHttpMethod("GET");
        req.setPath("/user/tony1234");
        AwsProxyResponse resp = lambdaHandler.handleRequest(req, lambdaContext);
        Assertions.assertNotNull(resp.getBody());
        Assertions.assertEquals(200, resp.getStatusCode());
    }
}
