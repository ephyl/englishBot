package com.ephyl.SpringDemoBot.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseLevelOfStudyDto {
    private String userName;
    private String levelOfStudy;
    private int timeClass;
    private boolean study;
    private String chatId;
    private String pollId;
    private Long startPollTime;
}
