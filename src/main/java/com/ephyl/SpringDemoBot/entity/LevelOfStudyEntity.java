package com.ephyl.SpringDemoBot.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = LevelOfStudyEntity.TABLE)
@Getter
@Setter
public class LevelOfStudyEntity {
    public static final String TABLE = "level_of_study";
    @Id
    private String userName;
    private String levelOfStudy;
    private int timeClass;
    private boolean study;
    private String chatId;
    @Column(name = "poll_id")
    private String pollId;
    private Long startPollTime;
}
