package com.esi.studentservice.controller;

import com.esi.studentservice.dto.RPLRequestDto;
import com.esi.studentservice.service.RPLRequestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class RPLRequestController {

    @Autowired
    private RPLRequestService rPLRequestService;

    @GetMapping("/rplrequests")
    public ResponseEntity<List<RPLRequestDto>> getRPLRequest() {
        return ResponseEntity.ok().body(rPLRequestService.getAllRPLRequest());
    }

    @PostMapping("/rplrequests")
    public ResponseEntity<String> addRPLRequest(@RequestBody RPLRequestDto rPLRequestDto) {
        rPLRequestService.addRPLRequest(rPLRequestDto);
        return ResponseEntity.ok("An RPL Request has been created");
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleMyException(IllegalArgumentException e) {
        log.info("Invalid request {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }
}
