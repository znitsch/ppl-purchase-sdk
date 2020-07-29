package com.paysafe.paylater;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Properties;

import com.paysafe.paylater.communication.Communicator;
import com.paysafe.paylater.communication.CommunicatorBuilder;
import com.paysafe.paylater.communication.CommunicatorConfiguration;
import com.paysafe.paylater.communication.Connection;
import com.paysafe.paylater.communication.standard.JsonMarshaller;
import com.paysafe.paylater.communication.standard.PaysafeConnection;

/**
 * Paysafe Pay Later platform factory for several SDK components.
 */
public final class Factory {

    private Factory() { }

    /**
     * Creates a {@link CommunicatorConfiguration} based on the configuration
     * values in {@code configurationFileUri}.
     */
    public static CommunicatorConfiguration createConfiguration(URI configurationFileUri) {
        try {
            Properties properties = new Properties();
            try (InputStream configurationFileInputStream = configurationFileUri.toURL().openStream()) {
                properties.load(configurationFileInputStream);
            }
            return new CommunicatorConfiguration(properties);
        } catch (IOException e) {
            throw new RuntimeException("Unable to load properties", e);
        }
    }

    /**
     * Creates a {@link CommunicatorBuilder} based on the configuration
     * values in {@code configurationFileUri}.
     */
    public static CommunicatorBuilder createCommunicatorBuilder(URI configurationFileUri) {
        CommunicatorConfiguration configuration = createConfiguration(configurationFileUri);
        return createCommunicatorBuilder(configuration);
    }

    /**
     * Creates a {@link CommunicatorBuilder} based on the passed configuration.
     */
    public static CommunicatorBuilder createCommunicatorBuilder(CommunicatorConfiguration configuration) {

        return new CommunicatorBuilder()
                .withAPIEndpoint(configuration.getApiEndpoint())
                .withConnection(new PaysafeConnection(
                        configuration.getConnectTimeout(),
                        configuration.getReadTimeout(),
                        configuration.getMaxConnections(),
                        configuration.getHttpsProtocols()
                ))
                .withMarshaller(JsonMarshaller.INSTANCE);
    }

    /**
     * Creates a {@link Communicator} based on the configuration
     * values in {@code configurationFileUri}.
     */
    public static Communicator createCommunicator(URI configurationFileUri) {
        CommunicatorConfiguration configuration = createConfiguration(configurationFileUri);
        return createCommunicator(configuration);
    }

    /**
     * Creates a {@link Communicator} based on the passed configuration.
     */
    public static Communicator createCommunicator(CommunicatorConfiguration configuration) {
        return createCommunicatorBuilder(configuration)
                .build();
    }

    /**
     * Creates a {@link Communicator} based on the passed API Endpoint.
     */
    public static Communicator createCommunicatorWithEndpoint(URI apiEndpoint) {
        PaysafeConnection connection = new PaysafeConnection(CommunicatorConfiguration.DEFAULT_CONNECT_TIMEOUT, CommunicatorConfiguration.DEFAULT_READ_TIMEOUT);
        return createCommunicator(apiEndpoint, connection);
    }

    /**
     * Creates a {@link Communicator} based on the passed API Endpoint and connection.
     */
    public static Communicator createCommunicator(URI apiEndpoint, Connection connection) {
        return new CommunicatorBuilder()
                .withAPIEndpoint(apiEndpoint)
                .withConnection(connection)
                .withMarshaller(JsonMarshaller.INSTANCE)
                .build();
    }
}
