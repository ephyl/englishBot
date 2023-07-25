package com.ephyl.SpringDemoBot.controller;

import com.ephyl.SpringDemoBot.dto.RequestLevelOfStudyDto;
import com.ephyl.SpringDemoBot.dto.ResponseLevelOfStudyDto;
import com.ephyl.SpringDemoBot.service.LevelOfStudyService;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.api.objects.polls.Poll;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class Options {
    private static final int MILLISECOND_IN_MINUTE = 60_000;
    static ReplyKeyboardMarkup keyboardKnowledgeLevel() {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setOneTimeKeyboard(true);
        List<KeyboardRow> keyboardRows = new ArrayList<>();
        KeyboardRow keyboardButtons = new KeyboardRow();
        keyboardButtons.add("A0");
        keyboardButtons.add("A1");
        keyboardButtons.add("A2");
        keyboardRows.add(keyboardButtons);
        keyboardButtons = new KeyboardRow();
        keyboardButtons.add("B1");
        keyboardButtons.add("B2");
        keyboardRows.add(keyboardButtons);
        keyboardButtons = new KeyboardRow();
        keyboardButtons.add("C1");
        keyboardButtons.add("C2");
        keyboardRows.add(keyboardButtons);
        replyKeyboardMarkup.setKeyboard(keyboardRows);
        return replyKeyboardMarkup;
    }

    public static ReplyKeyboardMarkup keyboardTime() {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow keyboardRow = new KeyboardRow();
        keyboardRow.add(new KeyboardButton("Отправить"));
        keyboard.add(keyboardRow);
        replyKeyboardMarkup.setKeyboard(keyboard);
        replyKeyboardMarkup.setResizeKeyboard(true);
        return replyKeyboardMarkup;
    }


    static Set<Integer> generatorIndex(int maxValue, int sizeSet) {
        Set<Integer> indexes = new HashSet<>();
        while (indexes.size() < maxValue) {
            int ind = ThreadLocalRandom.current().nextInt(0, sizeSet);
            indexes.add(ind);
        }
        return indexes;
    }

    static SetMyCommands generatorMenu() {
        List<BotCommand> commands = new ArrayList<>();
        commands.add(new BotCommand("/start", "Начало работы и выбор уровня"));
        commands.add(new BotCommand("/time", "Длительность одного урока"));
        commands.add(new BotCommand("/teach", "Начало обучения"));
        commands.add(new BotCommand("/exit", "Завершение обучения"));
        commands.add(new BotCommand("/level", "Изменение уровня изучения"));
        commands.add(new BotCommand("/help", "Подсказка о командах"));
        return new SetMyCommands(commands, new BotCommandScopeDefault(), null);
    }

    static boolean isPoolSend(Poll poll, LevelOfStudyService levelOfStudyService) {
        ResponseLevelOfStudyDto responseLevelOfStudyDto =
                levelOfStudyService.findByPollId(poll.getId());
        if (pollIsOpen(poll, levelOfStudyService) && timeIsExit(responseLevelOfStudyDto)) {
            return true;
        } else {
            return false;
        }
    }

    static boolean isExitPoolSend(Poll poll, LevelOfStudyService levelOfStudyService) {
        ResponseLevelOfStudyDto responseLevelOfStudyDto = levelOfStudyService.findByPollId(poll.getId());
        if (pollIsOpen(poll, levelOfStudyService) && !timeIsExit(responseLevelOfStudyDto)) {
            String userName = levelOfStudyService.findByPollId(poll.getId()).getUserName();
            levelOfStudyService.updateStudy(userName, false, null);
            return true;
        } else {
            return false;
        }
    }

    static boolean pollIsOpen(Poll poll, LevelOfStudyService levelOfStudyService) {
        boolean isPoll = (poll != null);
        boolean pollSave = (levelOfStudyService.findByPollId(poll.getId()) != null);
        if (pollSave && isPoll) {
            return true;
        } else {
            return false;
        }
    }

    static boolean timeIsExit(ResponseLevelOfStudyDto responseLevelOfStudyDto) {
        return responseLevelOfStudyDto.getStartPollTime() +
                MILLISECOND_IN_MINUTE * responseLevelOfStudyDto.getTimeClass() > System.currentTimeMillis();
    }

    public static RequestLevelOfStudyDto pollSetNull(long chatId, String userName, boolean isStudy) {
        RequestLevelOfStudyDto requestLevelOfStudyDto = new RequestLevelOfStudyDto();
        requestLevelOfStudyDto.setChatId(String.valueOf(chatId));
        requestLevelOfStudyDto.setUserName(userName);
        requestLevelOfStudyDto.setStudy(isStudy);
        return requestLevelOfStudyDto;
    }

}
