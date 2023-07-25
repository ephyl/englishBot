package com.ephyl.SpringDemoBot.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequestLevelOfStudyDto {
    private String userName;
    private String levelOfStudy;
    private Integer timeClass;
    private String chatId;
    private String pollId;
    private Long startPollTime;
    private boolean study;
}
