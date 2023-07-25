package com.ephyl.SpringDemoBot.repository;

import com.ephyl.SpringDemoBot.entity.GlossaryEntity;
import com.ephyl.SpringDemoBot.entity.GlossaryId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;

public interface GlossaryRepository extends JpaRepository<GlossaryEntity, GlossaryId> {
    Set<GlossaryEntity> findAllByLevel(String level);
}
