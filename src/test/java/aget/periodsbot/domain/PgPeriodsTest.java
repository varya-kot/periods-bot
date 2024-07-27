package aget.periodsbot.domain;

import aget.periodsbot.context.PgUsersContext;
import aget.periodsbot.context.UsersContext;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.statement.UnableToExecuteStatementException;
import org.jdbi.v3.testing.junit5.JdbiExtension;
import org.jdbi.v3.testing.junit5.tc.JdbiTestcontainersExtension;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.MountableFile;

import java.time.LocalDate;
import java.util.UUID;

@Testcontainers
public class PgPeriodsTest {
    @Container
    public static final JdbcDatabaseContainer<?> dbContainer =
        new PostgreSQLContainer<>("postgres:16-alpine")
            .withReuse(false)
            .withDatabaseName("periods_bot")
            .withCopyFileToContainer(
                MountableFile.forClasspathResource("db/init.sql"),
                "/docker-entrypoint-initdb.d/"
            );

    @RegisterExtension
    static JdbiExtension extension = JdbiTestcontainersExtension.instance(dbContainer);

    @Test
    void add_userIsPresent_addsNew(Jdbi jdbi) {
        UsersContext context = new PgUsersContext(jdbi);
        context.consume(users -> users.add(1L, "test"));

        Assertions.assertDoesNotThrow(() ->
            context.callback(users -> users.user(1L))
        );
    }

    @Test
    void add_userIsNotPresent_throws(Jdbi jdbi) {
        Assertions.assertThrows(
            UnableToExecuteStatementException.class,
            () -> jdbi.useTransaction(
                handle -> new PgPeriods(
                    handle,
                    UUID.randomUUID()
                ).add(LocalDate.now())
            )
        );
    }

    @Test
    void last_periodIsPresent_returnsLast(Jdbi jdbi) {
        UsersContext context = new PgUsersContext(jdbi);
        context.consume(users -> users.add(2L, "test"));

        LocalDate current = LocalDate.now();

        context.consume(users -> users.user(2L).periods().add(current));

        Assertions.assertEquals(
            current,
            context.callback(users ->
                users.user(2L)
                    .periods()
                    .last(10)
            ).get(0).start()
        );
    }

    @Test
    void remove_periodIsPresent_removes(Jdbi jdbi) {
        UsersContext context = new PgUsersContext(jdbi);
        context.consume(users -> users.add(3L, "test"));

        LocalDate current = LocalDate.now();

        context.consume(users -> users.user(3L).periods().add(current));

        Assertions.assertDoesNotThrow(
            () -> context.consume(
                users -> users.user(3L).periods().remove(current)
            )
        );
    }
}
