package aget.periodsbot.bot.convert;

import aget.periodsbot.dto.PeriodAddDto;
import aget.periodsbot.dto.UserTIdDto;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Date;

public class PeriodAddConvert implements Convert<Update, PeriodAddDto> {
    private final Convert<String, Date> dateConvert;

    public PeriodAddConvert(Convert<String, Date> dateConvert) {
        this.dateConvert = dateConvert;
    }

    @Override
    public PeriodAddDto convert(Update source) {
        return new PeriodAddDto(
                new UserTIdDto(source.getMessage().getFrom().getId()),
                this.dateConvert.convert(source.getMessage().getText())
        );
    }
}
