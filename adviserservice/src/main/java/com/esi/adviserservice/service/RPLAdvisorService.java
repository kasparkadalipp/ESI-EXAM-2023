package com.esi.adviserservice.service;


import com.esi.adviserservice.dto.RPLRequestDto;
import com.esi.adviserservice.model.RPLAdvisor;
import com.esi.adviserservice.repository.RPLAdvisorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RPLAdvisorService {

    @Autowired
    private RPLAdvisorRepository RPLAdvisorRepository;

    @KafkaListener(topics = "StdRequestSubmitted", groupId = "requestSubmittedEventGroup")
    public void updateRPLResponse(RPLRequestDto rPLRequestDto) {
        log.info("Received message from topic StdRequestSubmitted: {}", rPLRequestDto);
        RPLAdvisorRepository.save(mapRequestsDtoToRPL(rPLRequestDto));
    }

    private RPLAdvisor mapRequestsDtoToRPL(RPLRequestDto rPLRequestdto) {
        return RPLAdvisor.builder()
                .id(rPLRequestdto.getId())
                .userId(rPLRequestdto.getUserId())
                .courseToSubstituteName(rPLRequestdto.getCourseToSubstituteName())
                .courseToSubstituteCode(rPLRequestdto.getCourseToSubstituteCode())
                .courseToSubstituteVolume(rPLRequestdto.getCourseToSubstituteVolume())
                .courseToBeSubstitutedName(rPLRequestdto.getCourseToBeSubstitutedName())
                .courseToBeSubstitutedCode(rPLRequestdto.getCourseToBeSubstitutedCode())
                .advisoryDescription(rPLRequestdto.getAdvisoryDescription())
                .rPLRequestStatus(rPLRequestdto.getRPLRequestStatus())
                .build();
    }
}

