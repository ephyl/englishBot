package com.ephyl.SpringDemoBot.entity;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Embeddable
@Getter
@Setter
@RequiredArgsConstructor
public class GlossaryId implements Serializable {
    private String word;
    private String translate;
}
