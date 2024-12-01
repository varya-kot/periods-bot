/*
 * MIT License
 *
 * Copyright (c) 2024 Artem Getmanskii
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package aget.periodsbot.domain;

import org.jdbi.v3.core.Handle;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class PgPeriods implements Periods {
    private final Handle dataSource;
    private final UUID userId;

    public PgPeriods(Handle dataSource, UUID userId) {
        this.dataSource = dataSource;
        this.userId = userId;
    }

    @Override
    public void add(LocalDate start) {
        this.dataSource
            .useTransaction(
                handle ->
                    handle.createUpdate("""
                            INSERT INTO public.periods (id, user_id, start_date)
                            VALUES (:id, :user_id, :start_date)
                            """)
                        .bind("id", UUID.randomUUID())
                        .bind("user_id", this.userId)
                        .bind("start_date", start)
                        .execute()
            );
    }

    @Override
    public List<Period> last(Integer amount) {
        return this.dataSource.registerRowMapper(
                Period.class,
                (rs, ctx) -> new EaPeriod(
                    rs.getDate("start_date").toLocalDate(),
                    rs.getDate("end_date").toLocalDate())
            ).select(
                """
                    SELECT start_date
                    , CASE WHEN end_date IS NULL THEN now() ELSE end_date END
                    FROM (
                        SELECT start_date,
                        lag(start_date) OVER(
                            ORDER BY start_date DESC,
                            start_date rows between current row and unbounded following
                        ) as end_date
                        FROM public.periods
                        WHERE user_id = ?
                        LIMIT ?
                    ) x""",
                this.userId, amount
            ).mapTo(Period.class)
            .collectIntoList();
    }

    @Override
    public void remove(LocalDate start) {
        this.dataSource.useTransaction(
            handle ->
                handle.execute("""
                        DELETE FROM public.periods
                        WHERE user_id = ?
                        AND  start_date = ?""",
                    this.userId, start
                )
        );
    }
}
