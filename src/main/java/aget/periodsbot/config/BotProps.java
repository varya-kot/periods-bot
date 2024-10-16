package aget.periodsbot.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class BotProps {

    public String botName() {
        return this.property("bot.name");
    }

    public String botToken() {
        return this.property("bot.secret");
    }

    private String property(String name) {
        try (
            InputStream inputStream =
                PgProps.class.getClassLoader().getResourceAsStream(System.getenv("profile"))
        ) {
            Properties properties = new Properties();
            properties.load(inputStream);

            return properties.getProperty(name);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
