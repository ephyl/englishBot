package com.ephyl.SpringDemoBot.dto.utils;

import com.ephyl.SpringDemoBot.dto.RequestLevelOfStudyDto;
import com.ephyl.SpringDemoBot.dto.ResponseLevelOfStudyDto;
import com.ephyl.SpringDemoBot.entity.LevelOfStudyEntity;


public class LevelOfStudyUtils {
    public static ResponseLevelOfStudyDto levelOfStudyEntityToDto(LevelOfStudyEntity studyEntity) {
        ResponseLevelOfStudyDto responseLevelOfStudyDto = new ResponseLevelOfStudyDto();
        responseLevelOfStudyDto.setLevelOfStudy(studyEntity.getLevelOfStudy());
        responseLevelOfStudyDto.setUserName(studyEntity.getUserName());
        responseLevelOfStudyDto.setTimeClass(studyEntity.getTimeClass());
        responseLevelOfStudyDto.setStudy(studyEntity.isStudy());
        responseLevelOfStudyDto.setChatId(studyEntity.getChatId());
        responseLevelOfStudyDto.setPollId(studyEntity.getPollId());
        responseLevelOfStudyDto.setStartPollTime(studyEntity.getStartPollTime());
        return responseLevelOfStudyDto;
    }

    public static LevelOfStudyEntity levelOfStudyDtoToEntity(RequestLevelOfStudyDto requestLevelOfStudyDto) {
        LevelOfStudyEntity studyEntity = new LevelOfStudyEntity();
        studyEntity.setLevelOfStudy(requestLevelOfStudyDto.getLevelOfStudy());
        studyEntity.setUserName(requestLevelOfStudyDto.getUserName());
        studyEntity.setStudy(requestLevelOfStudyDto.isStudy());
        studyEntity.setChatId(requestLevelOfStudyDto.getChatId());
        studyEntity.setTimeClass(requestLevelOfStudyDto.getTimeClass());
        studyEntity.setPollId(requestLevelOfStudyDto.getPollId());
        studyEntity.setStartPollTime(requestLevelOfStudyDto.getStartPollTime());
        return studyEntity;
    }
}
