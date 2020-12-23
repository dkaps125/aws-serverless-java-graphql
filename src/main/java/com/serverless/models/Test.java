package com.serverless.models;

import io.leangen.graphql.annotations.GraphQLQuery;

public class Test {
    private String name;
    private int id;

    public Test(String name, int id) {
        this.name = name;
        this.id = id;
    }

    @GraphQLQuery(name = "name", description = "A test name")
    public String getName() {
        return name;
    }

    @GraphQLQuery
    public Integer getId() {
        return id;
    }
}
