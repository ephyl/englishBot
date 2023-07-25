package com.ephyl.SpringDemoBot.service;

import com.ephyl.SpringDemoBot.entity.GlossaryId;
import com.ephyl.SpringDemoBot.repository.GlossaryRepository;
import com.ephyl.SpringDemoBot.dto.RequestGlossaryDto;
import com.ephyl.SpringDemoBot.dto.ResponseGlossaryDto;
import com.ephyl.SpringDemoBot.dto.utils.GlossaryUtils;
import com.ephyl.SpringDemoBot.entity.GlossaryEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;
import java.util.Set;

@Service
@Transactional
public class GlossaryService {
    @Autowired
    private GlossaryRepository glossaryRepository;
    public Set<ResponseGlossaryDto> findByLevelAll(String level) {
        Set<GlossaryEntity> glossaryEntities = glossaryRepository.findAllByLevel(level);
        return GlossaryUtils.levelOfStudyEntityToDtos(glossaryEntities);
    }
    public ResponseGlossaryDto updateLevel(RequestGlossaryDto requestGlossaryDto){
        GlossaryId glossaryId = new GlossaryId();
        glossaryId.setTranslate(requestGlossaryDto.getTranslate());
        glossaryId.setWord(requestGlossaryDto.getWord());
        if(!glossaryRepository.findById(glossaryId).isPresent()){
            throw new NoSuchElementException("Такой пары еще не существует");
        }else{
            GlossaryEntity glossaryEntity = glossaryRepository.findById(glossaryId).get();
            glossaryEntity.setLevel(requestGlossaryDto.getLevel());
            glossaryEntity = glossaryRepository.save(glossaryEntity);
            return GlossaryUtils.levelOfStudyEntityToDto(glossaryEntity);
        }
    }

    public ResponseGlossaryDto create(RequestGlossaryDto requestGlossaryDto){
        GlossaryId glossaryId = new GlossaryId();
        glossaryId.setTranslate(requestGlossaryDto.getTranslate());
        glossaryId.setWord(requestGlossaryDto.getWord());
        if(glossaryRepository.findById(glossaryId).isPresent()){
            throw new NoSuchElementException("Такая пара уже существует");
        }else{
            GlossaryEntity glossaryEntity = new GlossaryEntity();
            glossaryEntity.setId(glossaryId);
            glossaryEntity.setLevel(requestGlossaryDto.getLevel());
            glossaryEntity = glossaryRepository.save(glossaryEntity);
            return GlossaryUtils.levelOfStudyEntityToDto(glossaryEntity);
        }
    }
    public void delete(String word, String translate){
        GlossaryId glossaryId = new GlossaryId();
        glossaryId.setTranslate(translate);
        glossaryId.setWord(word);
        glossaryRepository.deleteById(glossaryId);
    }
}
