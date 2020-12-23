package com.serverless;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.amazonaws.HttpMethod;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import com.serverless.services.Services;
import com.serverless.services.TestService;
import graphql.*;
import graphql.language.SourceLocation;
import graphql.schema.GraphQLSchema;
import io.leangen.graphql.GraphQLSchemaGenerator;
import org.apache.log4j.Logger;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;

public class Handler implements RequestHandler<APIGatewayProxyRequestEvent, ApiGatewayResponse>
{
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final Logger LOG = Logger.getLogger(Handler.class);
    private static final Map<String, String> HEADERS = ImmutableMap.<String, String>builder()
            .put("X-Powered-By", "AWS Lambda & Serverless")
            .put("Content-Type", "application/json")
            .build();

    private final GraphQL graphQL;

    public Handler() {
        TestService userService = new TestService(); //instantiate the service (or inject by Spring or another framework)
        GraphQLSchemaGenerator schema = new GraphQLSchemaGenerator()
                .withBasePackages("io.leangen"); //not mandatory but strongly recommended to set your "root" packages

        Services.provideServices(schema::withOperationsFromSingleton);

        graphQL = new GraphQL.Builder(schema.generate())
                .build();
    }

    @Override
    public ApiGatewayResponse handleRequest(APIGatewayProxyRequestEvent input, Context context) {
        LOG.info("received: " + input);

        final ApiGatewayResponse.Builder responseBuilder = ApiGatewayResponse.builder()
                .setStatusCode(200)
                .setHeaders(HEADERS);

        if (input.getHttpMethod().equals("OPTIONS")) {
            return responseBuilder.build();
        }

        final HttpMethod requestMethod = HttpMethod.valueOf(input.getHttpMethod());
        final Map<String, Object> requestParams = _getRequestParams(input, requestMethod);

        try {
            final ExecutionResult result = graphQL.execute(ExecutionInput.newExecutionInput()
                    .query((String) requestParams.get("query"))
                    .operationName((String) requestParams.getOrDefault("operationName", ""))
                    .variables(_getQueryVariables(requestParams.get("variables")))
                    .context(context)
                    .build());

            return responseBuilder.setObjectBody(result.toSpecification()).build();
        } catch (Exception e) {
            return responseBuilder.setObjectBody(
                    ExecutionResultImpl.newExecutionResult()
                        .addError(_fromException(e))
                ).build();
        }
    }

    private static Map<String, Object> _getRequestParams(final APIGatewayProxyRequestEvent input,
                                                         final HttpMethod httpMethod) {
        if (httpMethod == HttpMethod.GET) {
            return input.getQueryStringParameters().entrySet().stream()
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        } else {
            try {
                return MAPPER.readValue(input.getBody(), new TypeReference<Map<String, Object>>() {});
            } catch (JsonProcessingException e) {
                LOG.error("Unable to deserialize request body", e);
                return Collections.emptyMap();
            }
        }
    }

    private static Map<String, Object> _getQueryVariables(final Object variables) {
        if (variables == null) {
            return Collections.emptyMap();
        }

        return MAPPER.convertValue(variables, new TypeReference<Map<String, Object>>() {});
    }

    private static GraphQLError _fromException(final Exception e) {
        return new GraphQLError() {
            @Override
            public String getMessage() {
                return e.getMessage();
            }

            @Override
            public List<SourceLocation> getLocations() {
                return null;
            }

            @Override
            public ErrorClassification getErrorType() {
                return null;
            }
        };
    }
}