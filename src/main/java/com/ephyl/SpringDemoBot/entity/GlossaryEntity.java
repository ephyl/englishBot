package com.ephyl.SpringDemoBot.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = GlossaryEntity.TABLE)
@Getter
@Setter
public class GlossaryEntity {
    public static final String TABLE = "glossary";
    @EmbeddedId
    private GlossaryId id;
    private String level;
}
