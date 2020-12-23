package com.serverless;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableMap;
import com.google.common.io.ByteSource;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public class Playground implements RequestHandler<APIGatewayProxyRequestEvent, ApiGatewayResponse> {
    private static final Map<String, String> HEADERS = ImmutableMap.<String, String>builder()
            .put("X-Powered-By", "AWS Lambda & Serverless")
            .put("Content-Type", "text/html")
            .build();

    @Override
    public ApiGatewayResponse handleRequest(APIGatewayProxyRequestEvent input, Context context) {
        final ApiGatewayResponse.Builder responseBuilder = ApiGatewayResponse.builder()
                .setHeaders(HEADERS)
                .setStatusCode(200);

        try {
            final InputStream inputStream = getClass().getClassLoader().getResourceAsStream("static/index.html");
            ByteSource byteSource = new ByteSource() {
                @Override
                public InputStream openStream() throws IOException {
                    return inputStream;
                }
            };

            String text = byteSource.asCharSource(Charsets.UTF_8).read();
            return responseBuilder.setStatusCode(200).setRawBody(text).build();
        } catch (Exception e) {
            return responseBuilder.setStatusCode(500).build();
        }
    }
}
