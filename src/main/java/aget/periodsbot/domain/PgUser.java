package aget.periodsbot.domain;

import org.jdbi.v3.core.Handle;

import java.util.UUID;

public class PgUser implements User {
    private final Handle dataSource;
    private final PeriodsFactory periodsFactory;
    private final UUID userId;

    public PgUser(Handle dataSource,
                  PeriodsFactory periodsFactory,
                  UUID userId) {
        this.dataSource = dataSource;
        this.periodsFactory = periodsFactory;
        this.userId = userId;
    }

    @Override
    public String name() {
        return this.dataSource.inTransaction(
                handle ->
                    handle.select(
                        "SELECT name FROM public.users WHERE id = ?",
                        this.userId
                    ).mapTo(String.class)
            ).findFirst()
            .orElse("пользователь");
    }

    @Override
    public Periods periods() {
        return this.periodsFactory.periods(this.dataSource, this.userId);
    }
}
