package com.esi.studentservice.service;

import com.esi.studentservice.dto.RPLRequestDto;
import com.esi.studentservice.model.RPLRequest;
import com.esi.studentservice.model.RPLRequestStatus;
import com.esi.studentservice.repository.RPLRequestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RPLRequestService {

    private final KafkaTemplate<String, RPLRequestDto> kafkaTemplate;
    @Autowired
    private RPLRequestRepository RPLRequestRepository;

    public List<RPLRequestDto> getAllRPLRequest() {
        List<RPLRequest> rPLRequests = RPLRequestRepository.findAll();
        log.info("GET request for all RPL requests, {} results", rPLRequests.size());
        return rPLRequests.stream().map(this::mapToRPLRequestsDto).toList();
    }

    public void addRPLRequest(RPLRequestDto rPLRequestDto) {
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
