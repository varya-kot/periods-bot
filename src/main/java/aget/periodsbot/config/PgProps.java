package aget.periodsbot.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PgProps {
    public String connectionUrl() {
        try (
            InputStream inputStream =
                PgProps.class.getClassLoader().getResourceAsStream(System.getenv("profile"))

        ) {
            Properties properties = new Properties();
            properties.load(inputStream);

            return properties.getProperty("postgres.connection.url");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
