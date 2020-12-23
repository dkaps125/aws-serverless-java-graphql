package com.serverless.services;

import com.google.common.collect.ImmutableList;

import java.util.List;
import java.util.function.Consumer;

public class Services {
    private static final List<Service> SERVICES = new ImmutableList.Builder<Service>()
            .add(new TestService())
            .build();

    public static void provideServices(Consumer<Service> serviceConsumer)
    {
        SERVICES.forEach(serviceConsumer);
    }
}
