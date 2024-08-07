package aget.periodsbot;

import aget.periodsbot.bot.PeriodsBot;
import aget.periodsbot.bot.command.GreetCmd;
import aget.periodsbot.bot.command.NewPeriodCmd;
import aget.periodsbot.bot.command.RemovePeriodCmd;
import aget.periodsbot.bot.convert.DateConvert;
import aget.periodsbot.config.BotProps;
import aget.periodsbot.config.PgProps;
import aget.periodsbot.context.PgUsersContext;
import aget.periodsbot.context.UsersContext;
import com.github.artemget.teleroute.match.MatchRegex;
import com.github.artemget.teleroute.route.RouteDfs;
import com.github.artemget.teleroute.route.RouteFork;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class PeriodsbotApplication {
    public static void main(String[] args) throws TelegramApiException {
        UsersContext context = new PgUsersContext(new PgProps().connectionUrl());
        DateConvert convert = new DateConvert("dd-MM-yyyy", "\\d{2}-\\d{2}-\\d{4}");

        new PeriodsBot(
            new BotProps(),
            new RouteDfs<>(
                new RouteFork<>(
                    new MatchRegex<>("/start"),
                    new GreetCmd(context)
                ),
                new RouteFork<>(
                    new MatchRegex<>("[Дд]обавить .+"),
                    new NewPeriodCmd(context, convert)
                ),
                new RouteFork<>(
                    new MatchRegex<>("[Уу]далить .+"),
                    new RemovePeriodCmd(context, convert)
                )
            )
        ).start();
    }
}
