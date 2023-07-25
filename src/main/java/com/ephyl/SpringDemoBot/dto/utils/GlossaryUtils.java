package com.ephyl.SpringDemoBot.dto.utils;

import com.ephyl.SpringDemoBot.dto.ResponseGlossaryDto;
import com.ephyl.SpringDemoBot.entity.GlossaryId;
import com.ephyl.SpringDemoBot.dto.RequestGlossaryDto;
import com.ephyl.SpringDemoBot.entity.GlossaryEntity;

import java.util.Set;
import java.util.stream.Collectors;

public class GlossaryUtils {
    public static ResponseGlossaryDto levelOfStudyEntityToDto(GlossaryEntity glossaryEntity) {
        ResponseGlossaryDto responseGlossaryDto = new ResponseGlossaryDto();
        responseGlossaryDto.setWord(glossaryEntity.getId().getWord());
        responseGlossaryDto.setTranslate(glossaryEntity.getId().getTranslate());
        responseGlossaryDto.setLevel(glossaryEntity.getLevel());
        return responseGlossaryDto;
    }

    public static Set<ResponseGlossaryDto> levelOfStudyEntityToDtos(Set<GlossaryEntity> studyEntities) {
        return studyEntities
                .stream()
                .map(GlossaryUtils::levelOfStudyEntityToDto)
                .collect(Collectors.toSet());
    }

    public static GlossaryEntity levelOfStudyDtoToEntity(RequestGlossaryDto requestGlossaryDto) {
        GlossaryEntity glossaryEntity = new GlossaryEntity();
        GlossaryId glossaryId = new GlossaryId();
        glossaryId.setWord(requestGlossaryDto.getWord());
        glossaryId.setTranslate(requestGlossaryDto.getTranslate());
        glossaryEntity.setId(glossaryId);
        glossaryEntity.setLevel(requestGlossaryDto.getLevel());
        return glossaryEntity;
    }
}
