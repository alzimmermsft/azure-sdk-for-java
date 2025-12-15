// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.cosmos.kafka.connect;

import com.azure.cosmos.implementation.Strings;
import com.azure.cosmos.implementation.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public class KafkaCosmosTestConfigurations {
    private static final Logger logger = LoggerFactory.getLogger(KafkaCosmosTestConfigurations.class);
    private static Properties properties = loadProperties();

    private static final String COSMOS_EMULATOR_KEY = "C2y6yDjf5/R+ob0N8A7Cgv30VRDJIWEHLM+4QDU5DE2nQ9nDuVTqobD4b8mGGyPMbIZnqyMsEcaGQy67XIw/Jw==";
    private static final String COSMOS_EMULATOR_HOST = "https://localhost:8081/";
    public static final String DEFAULT_CONFLUENT_VERSION = "7.6.0"; //https://docs.confluent.io/platform/current/installation/versions-interoperability.html
    public static final String DEFAULT_CONNECT_GROUP_ID = "1";
    public static final String DEFAULT_CONNECT_CONFIG_STORAGE_TOPIC = "docker-connect-configs";
    public static final String DEFAULT_CONNECT_CONFIG_STORAGE_REPLICATION_FACTOR = "1";
    public static final String DEFAULT_CONNECT_OFFSET_STORAGE_TOPIC = "docker-connect-offsets";
    public static final String DEFAULT_CONNECT_OFFSET_STORAGE_REPLICATION_FACTOR = "1";
    public static final String DEFAULT_CONNECT_STATUS_STORAGE_TOPIC = "docker-connect-status";
    public static final String DEFAULT_CONNECT_STATUS_STORAGE_REPLICATION_FACTOR = "1";
    public static final String DEFAULT_CONNECT_KEY_CONVERTER = "org.apache.kafka.connect.storage.StringConverter";
    public static final String DEFAULT_CONNECT_VALUE_CONVERTER = "org.apache.kafka.connect.json.JsonConverter";
    public static final String DEFAULT_CONNECT_PLUGIN_PATH = "/kafka/connect/cosmos-connector";
    public static final String DEFAULT_CONNECT_REST_ADVERTISED_HOST_NAME = "connect";
    public static final String DEFAULT_ACR_NAME = "confluentinc";

    public final static String ACR_NAME = getConfig("COSMOS_ACR_NAME", DEFAULT_ACR_NAME);
    public final static String MASTER_KEY = getConfig("ACCOUNT_KEY", COSMOS_EMULATOR_KEY);
    public final static String SECONDARY_MASTER_KEY = getConfig("SECONDARY_ACCOUNT_KEY", COSMOS_EMULATOR_KEY);
    public final static String HOST = getConfig("ACCOUNT_HOST", COSMOS_EMULATOR_HOST);
    public final static String ACCOUNT_TENANT_ID = getConfig("ACCOUNT_TENANT_ID", Strings.EMPTY);
    public final static String ACCOUNT_AAD_CLIENT_ID = getConfig("ACCOUNT_AAD_CLIENT_ID", Strings.EMPTY);
    public final static String ACCOUNT_AAD_CLIENT_SECRET = getConfig("ACCOUNT_AAD_CLIENT_SECRET", Strings.EMPTY);
    public final static String KAFKA_CLUSTER_KEY = getConfig("KAFKA_CLUSTER_KEY", Strings.EMPTY);
    public final static String KAFKA_CLUSTER_SECRET = getConfig("KAFKA_CLUSTER_SECRET", Strings.EMPTY);
    public final static String SCHEMA_REGISTRY_KEY = getConfig("SCHEMA_REGISTRY_KEY", Strings.EMPTY);
    public final static String SCHEMA_REGISTRY_SECRET = getConfig("SCHEMA_REGISTRY_SECRET", Strings.EMPTY);

    public final static String SCHEMA_REGISTRY_BASIC_AUTH_USER_INFO = SCHEMA_REGISTRY_KEY + ":" + SCHEMA_REGISTRY_SECRET;

    public final static String SCHEMA_REGISTRY_URL = getConfig("SCHEMA_REGISTRY_URL", Strings.EMPTY);
    public final static String BOOTSTRAP_SERVER = getConfig("BOOTSTRAP_SERVER", Strings.EMPTY);
    public final static String SASL_JAAS = getConfig("SASL_JAAS", Strings.EMPTY);
    public final static String CONFLUENT_VERSION = getConfig("CONFLUENT_VERSION", DEFAULT_CONFLUENT_VERSION);
    public final static String CONNECT_GROUP_ID = getConfig("CONNECT_GROUP_ID", DEFAULT_CONNECT_GROUP_ID);
    public final static String CONNECT_CONFIG_STORAGE_TOPIC = getConfig("CONNECT_CONFIG_STORAGE_TOPIC", DEFAULT_CONNECT_CONFIG_STORAGE_TOPIC);
    public final static String CONNECT_CONFIG_STORAGE_REPLICATION_FACTOR = getConfig("CONNECT_CONFIG_STORAGE_REPLICATION_FACTOR", DEFAULT_CONNECT_CONFIG_STORAGE_REPLICATION_FACTOR);
    public final static String CONNECT_OFFSET_STORAGE_TOPIC = getConfig("CONNECT_OFFSET_STORAGE_TOPIC", DEFAULT_CONNECT_OFFSET_STORAGE_TOPIC);
    public final static String CONNECT_OFFSET_STORAGE_REPLICATION_FACTOR = getConfig("CONNECT_OFFSET_STORAGE_REPLICATION_FACTOR", DEFAULT_CONNECT_OFFSET_STORAGE_REPLICATION_FACTOR);
    public final static String CONNECT_STATUS_STORAGE_TOPIC = getConfig("CONNECT_STATUS_STORAGE_TOPIC", DEFAULT_CONNECT_STATUS_STORAGE_TOPIC);
    public final static String CONNECT_STATUS_STORAGE_REPLICATION_FACTOR = getConfig("CONNECT_STATUS_STORAGE_REPLICATION_FACTOR", DEFAULT_CONNECT_STATUS_STORAGE_REPLICATION_FACTOR);
    public final static String CONNECT_KEY_CONVERTER = getConfig("CONNECT_KEY_CONVERTER", DEFAULT_CONNECT_KEY_CONVERTER);
    public final static String CONNECT_VALUE_CONVERTER = getConfig("CONNECT_VALUE_CONVERTER", DEFAULT_CONNECT_VALUE_CONVERTER);
    public final static String CONNECT_PLUGIN_PATH = getConfig("CONNECT_PLUGIN_PATH", DEFAULT_CONNECT_PLUGIN_PATH);
    public final static String CONNECT_REST_ADVERTISED_HOST_NAME = getConfig("CONNECT_REST_ADVERTISED_HOST_NAME", DEFAULT_CONNECT_REST_ADVERTISED_HOST_NAME);

    private static String getConfig(String name, String def) {
        return properties.getProperty(name, Utils.defaultIfNull(Strings.trimToNull(System.getenv().get(name)), def));
    }

    private static Properties loadProperties() {
        Path root = FileSystems.getDefault().getPath("").toAbsolutePath();
        Path propertiesInProject = Paths.get(root.toString(),"../kafka-cosmos-local.properties");

        Properties props = loadFromPathIfExists(propertiesInProject);
        if (props != null) {
            return props;
        }

        Path propertiesInUserHome = Paths.get(System.getProperty("user.home"), "kafka-cosmos-local.properties");
        props = loadFromPathIfExists(propertiesInUserHome);
        if (props != null) {
            return props;
        }

        return System.getProperties();
    }

    private static Properties loadFromPathIfExists(Path propertiesFilePath) {
        if (Files.exists(propertiesFilePath)) {
            try (InputStream in = Files.newInputStream(propertiesFilePath)) {
                Properties props = new Properties();
                props.load(in);
                logger.info("properties loaded from {}", propertiesFilePath);
                return props;
            } catch (Exception e) {
                logger.error("Loading properties {} failed", propertiesFilePath, e);
            }
        }
        return null;
    }
}
