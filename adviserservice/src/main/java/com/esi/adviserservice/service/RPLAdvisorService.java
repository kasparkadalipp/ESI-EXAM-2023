package com.esi.adviserservice.service;


import com.esi.adviserservice.dto.RPLRequestDto;
import com.esi.adviserservice.dto.RPLRequestStatus;
import com.esi.adviserservice.model.RPLAdvisor;
import com.esi.adviserservice.repository.RPLAdvisorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RPLAdvisorService {

    private final KafkaTemplate<String, RPLRequestDto> kafkaTemplate;
    @Autowired
    private RPLAdvisorRepository RPLAdvisorRepository;

    @KafkaListener(topics = "StdRequestSubmitted", groupId = "requestSubmittedEventGroup")
    public void consumeRequestSubmit(RPLRequestDto rPLRequestDto) {
        log.info("Message from topic StdRequestSubmitted: {}", rPLRequestDto);
        RPLAdvisorRepository.save(mapRequestsDtoToRPL(rPLRequestDto));
    }

    public void updateRPLResponse(RPLRequestDto rPLRequestDto) {
        rPLRequestDto.setRPLRequestStatus(RPLRequestStatus.UnderReview);
        RPLAdvisor rplAdvisor = mapRequestsDtoToRPL(rPLRequestDto);
        RPLAdvisorRepository.save(rplAdvisor);
        log.info("Save to database: {}", rplAdvisor);
        kafkaTemplate.send("AdvResSubmitted", rPLRequestDto);
        log.info("Send message to topic AdvResSubmitted: {}", rPLRequestDto);
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

