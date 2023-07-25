package com.ephyl.SpringDemoBot.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequestGlossaryDto {
    private String word;
    private String translate;
    private String level;
}
