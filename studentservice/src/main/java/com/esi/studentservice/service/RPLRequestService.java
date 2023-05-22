package com.esi.studentservice.service;

import com.esi.studentservice.dto.RPLRequestDto;
import com.esi.studentservice.model.RPLRequest;
import com.esi.studentservice.model.RPLRequestStatus;
import com.esi.studentservice.repository.RPLRequestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class RPLRequestService {

    private final KafkaTemplate<String, RPLRequestDto> kafkaTemplate;
    @Autowired
    private RPLRequestRepository RPLRequestRepository;

    @KafkaListener(topics = "AdvResSubmitted", groupId = "requestSubmittedEventGroup")
    public void consumeAdvisorSubmit(RPLRequestDto rPLRequestDto) {
        log.info("Message from topic AdvResSubmitted: {}", rPLRequestDto);
        RPLRequestRepository.save(mapRequestsDtoToRPL(rPLRequestDto));
        log.info("Update database value to underReview");
    }

    public List<RPLRequestDto> getAllRPLRequest() {
        List<RPLRequest> rPLRequests = RPLRequestRepository.findAll();
        log.info("GET request for all RPL requests, {} results", rPLRequests.size());
        return rPLRequests.stream().map(this::mapToRPLRequestsDto).toList();
    }

    public void addRPLRequest(RPLRequestDto rPLRequestDto) {
        Optional<RPLRequest> databaseEntry = RPLRequestRepository.findById(rPLRequestDto.getId());
        if (databaseEntry.isPresent() && Set.of(RPLRequestStatus.UnderReview, RPLRequestStatus.Refused, RPLRequestStatus.Accepted).contains(databaseEntry.get().getRPLRequestStatus())) {
            log.warn("Modifying application no longer allowed: {}", rPLRequestDto);
            throw new IllegalArgumentException("Modifying application no longer allowed");
        }
        rPLRequestDto.setRPLRequestStatus(RPLRequestStatus.Submitted);
        RPLRequest rplRequest = mapRequestsDtoToRPL(rPLRequestDto);
        RPLRequestRepository.save(rplRequest);
        log.info("Request saved to database");
        kafkaTemplate.send("StdRequestSubmitted", rPLRequestDto);
        log.info("Send StdRequestSubmitted: {}", rPLRequestDto);
    }

    private RPLRequestDto mapToRPLRequestsDto(RPLRequest rPLRequest) {
        return RPLRequestDto.builder()
                .id(rPLRequest.getId())
                .userId(rPLRequest.getUserId())
                .courseToSubstituteName(rPLRequest.getCourseToSubstituteName())
                .courseToSubstituteCode(rPLRequest.getCourseToSubstituteCode())
                .courseToSubstituteVolume(rPLRequest.getCourseToSubstituteVolume())
                .courseToBeSubstitutedName(rPLRequest.getCourseToBeSubstitutedName())
                .courseToBeSubstitutedCode(rPLRequest.getCourseToBeSubstitutedCode())
                .advisoryDescription(rPLRequest.getAdvisoryDescription())
                .rPLRequestStatus(rPLRequest.getRPLRequestStatus())
                .build();
    }

    private RPLRequest mapRequestsDtoToRPL(RPLRequestDto rPLRequestdto) {
        return RPLRequest.builder()
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
