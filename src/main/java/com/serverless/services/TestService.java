package com.serverless.services;

import com.google.common.collect.ImmutableMap;
import com.serverless.models.Test;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLQuery;

import java.util.Map;

public class TestService extends Service {
    private static Map<Integer, Test> TESTS = ImmutableMap.<Integer, Test>builder()
            .put(1, new Test("A", 1))
            .put(2, new Test("B", 2))
            .put(3, new Test("C", 3))
        .build();

    @GraphQLQuery(name = "test")
    public Test getById(@GraphQLArgument(name = "id") Integer id) {
      return TESTS.get(id);
    }
}
