package com.esi.adviserservice.controller;


import com.esi.adviserservice.dto.RPLRequestDto;
import com.esi.adviserservice.service.RPLAdvisorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class RPLAdvisorController {

    @Autowired
    private RPLAdvisorService rPLAdvisorService;

    @PutMapping("/rplresponse")
    public ResponseEntity<String> updateRPLResponse(@RequestBody RPLRequestDto rPLRequestDto) {
        rPLAdvisorService.updateRPLResponse(rPLRequestDto);
        return ResponseEntity.ok("An RPL Request is under review by the committee");
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleMyException(IllegalArgumentException e) {
        log.info("Invalid request {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }
}
