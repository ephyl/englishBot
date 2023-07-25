package com.ephyl.SpringDemoBot.controller;

import com.ephyl.SpringDemoBot.dto.RequestLevelOfStudyDto;
import com.ephyl.SpringDemoBot.dto.ResponseGlossaryDto;
import com.ephyl.SpringDemoBot.dto.ResponseLevelOfStudyDto;
import com.ephyl.SpringDemoBot.service.GlossaryService;
import com.ephyl.SpringDemoBot.service.LevelOfStudyService;

import java.util.*;

public class Command {
    private static final int MAX_QUESTIONS = 3;

    static HashMap<String, Object> timeCommandReceived(long chatId, String userName,
                                                       LevelOfStudyService levelOfStudyService) {
        HashMap<String, Object> send = new HashMap<>();
        if (levelOfStudyService.findByUserName(userName) != null) {
            levelOfStudyService.updateStudy(userName, true, System.currentTimeMillis());
        } else {
            levelOfStudyService.create(Options.pollSetNull(chatId, userName, true));
        }
        String answer = "Введите количество минут, которые Вы готовы уделить изучению (целое число)";
        send.put("chatId", chatId);
        send.put("answer", answer);
        send.put("replyKeyboardMarkup", Options.keyboardTime());
        return send;
    }

    static String levelCommandReceived(long chatId, String userName, String level,
                                       LevelOfStudyService levelOfStudyService) {
        RequestLevelOfStudyDto requestLevelOfStudyDto = new RequestLevelOfStudyDto();
        requestLevelOfStudyDto.setUserName(userName);
        requestLevelOfStudyDto.setLevelOfStudy(level);
        requestLevelOfStudyDto.setChatId(String.valueOf(chatId));
        requestLevelOfStudyDto.setStudy(false);
        levelOfStudyService.create(requestLevelOfStudyDto);
        String answer = "I am making a program, you can type /teach command to start learning \n " +
                "Training time is 1 minute, if you want to change it, click /time";
        return answer;
    }

    static HashMap<String, Object> startCommandReceived(String name, String userName,
                                                        LevelOfStudyService levelOfStudyService) {
        HashMap<String, Object> send = new HashMap<>();
        StringBuilder answer = new StringBuilder("Hi, " + name + ", nice to meet you!");
        if (levelOfStudyService.findByUserName(userName) == null) {
            answer.append('\n' + "Indicate your level of knowledge");
            send.put("answer", answer.toString());
            send.put("keyboard", Options.keyboardKnowledgeLevel());
        } else {
            send.put("answer", answer.toString());
        }
        return send;
    }

    static HashMap<String, Object> updateLevel() {
        HashMap<String, Object> send = new HashMap<>();
        StringBuilder answer = new StringBuilder("Indicate your level of knowledge");
        send.put("answer", answer.toString());
        send.put("keyboard", Options.keyboardKnowledgeLevel());
        return send;
    }


    static String exitCommandReceived(String name) {
        return "Bye, " + name + "!";
    }

    static String timeUpdate(LevelOfStudyService levelOfStudyService, String userName, String messageText) {
        try {
            if (levelOfStudyService.findByUserName(userName) != null &&
                    levelOfStudyService.findByUserName(userName).isStudy()) {
                Integer num = Integer.parseInt(messageText);
                if (num > 0) {
                    levelOfStudyService.update(updateTimeFalse(userName, num, false));
                    return "/teach";
                } else {
                    return "/time";
                }
            }
        } catch (NoSuchElementException | NumberFormatException e) {
            e.getMessage();
        }
        return messageText;
    }

    static HashMap<String, Object> teachCommandReceived(String userName,
                                                        LevelOfStudyService levelOfStudyService,
                                                        GlossaryService glossaryService) {
        ResponseLevelOfStudyDto responseLevelOfStudyDto = levelOfStudyService.findByUserName(userName);
        String level = responseLevelOfStudyDto.getLevelOfStudy();
        Set<ResponseGlossaryDto> glossarySet = glossaryService.findByLevelAll(level);
        List<ResponseGlossaryDto> glossaryDtoList = new ArrayList<>(glossarySet);
        return generatorPoll(glossaryDtoList, userName);
    }

    private static HashMap<String, Object> generatorPoll(List<ResponseGlossaryDto> glossaryDtoList, String userName) {
        HashMap<String, Object> send = new HashMap<>();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Выберите перевод слова ");
        List<String> options = new ArrayList<>();
        Set<Integer> random = Options.generatorIndex(MAX_QUESTIONS, glossaryDtoList.size());
        int indexQuestion = (int) (Math.random() * MAX_QUESTIONS);
        int i = 0;
        for (Integer item : random) {
            options.add(glossaryDtoList.get(item).getWord().trim());
            if (i == indexQuestion) {
                stringBuilder.append(glossaryDtoList.get(item).getTranslate().trim());
            }
            i++;
        }
        send.put("question", stringBuilder.toString());
        send.put("options", options);
        send.put("indexQuestion", indexQuestion);
        send.put("userName", userName);
        return send;
    }

    static RequestLevelOfStudyDto updateTimeFalse(String userName, Integer number, boolean isStudy) {
        RequestLevelOfStudyDto requestLevelOfStudyDto = new RequestLevelOfStudyDto();
        requestLevelOfStudyDto.setStudy(isStudy);
        requestLevelOfStudyDto.setUserName(userName);
        requestLevelOfStudyDto.setTimeClass(number);
        return requestLevelOfStudyDto;
    }
}
