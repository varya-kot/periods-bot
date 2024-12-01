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

package aget.periodsbot.bot.command;

import aget.periodsbot.bot.send.KeyboardTgSend;
import aget.periodsbot.bot.send.SendMsg;
import com.github.artemget.teleroute.command.Cmd;
import com.github.artemget.teleroute.send.Send;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.bots.AbsSender;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

public class KeyboardCmd implements Cmd<Update, AbsSender> {
    private final ReplyKeyboardMarkup keyboardMarkup;

    public KeyboardCmd(String... buttons) {
        this(new KeyboardRow(Arrays.stream(buttons).map(KeyboardButton::new).toList()));
    }

    public KeyboardCmd(KeyboardRow row) {
        this(new ReplyKeyboardMarkup(Collections.singletonList(row)));
    }

    public KeyboardCmd(ReplyKeyboardMarkup keyboardMarkup) {
        this.keyboardMarkup = keyboardMarkup;
        keyboardMarkup.setResizeKeyboard(true);
    }

    @Override
    public Optional<Send<AbsSender>> execute(Update update){
        return Optional.of(
            new SendMsg(
                new KeyboardTgSend(
                    update.getMessage().getFrom().getId().toString(),
                    "Выберите действие на клавиатуре",
                    keyboardMarkup
                )
            )
        );
    }
}
