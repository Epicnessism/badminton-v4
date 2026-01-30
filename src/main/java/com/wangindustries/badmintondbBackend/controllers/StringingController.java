package com.wangindustries.badmintondbBackend.controllers;

import com.wangindustries.badmintondbBackend.exceptions.InvalidStateTransitionException;
import com.wangindustries.badmintondbBackend.models.Stringing;
import com.wangindustries.badmintondbBackend.requests.CreateStringingRequest;
import com.wangindustries.badmintondbBackend.requests.UpdateStringingRequest;
import com.wangindustries.badmintondbBackend.services.StringingService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/stringing")
public class StringingController {

    @Autowired
    private StringingService stringingService;

    @PostMapping
    public ResponseEntity<Stringing> createStringing(@Valid @RequestBody CreateStringingRequest request) {
        log.info("Received create stringing request: {}", request);
        try {
            Stringing createdStringing = stringingService.createStringing(request);
            return new ResponseEntity<>(createdStringing, HttpStatus.CREATED);
        } catch (Exception e) {
            log.error("Failed to create stringing", e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{stringingId}")
    public ResponseEntity<Stringing> getStringing(@PathVariable UUID stringingId) {
        log.info("Received get stringing request for id: {}", stringingId);
        Stringing stringing = stringingService.getStringing(stringingId);
        if (stringing == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(stringing, HttpStatus.OK);
    }

    @GetMapping("/stringer/{stringerUserId}")
    public ResponseEntity<List<Stringing>> getStringingsByStringer(@PathVariable UUID stringerUserId) {
        log.info("Received get stringings by stringer request for: {}", stringerUserId);
        List<Stringing> stringings = stringingService.getStringingsByStringerUserId(stringerUserId);
        return new ResponseEntity<>(stringings, HttpStatus.OK);
    }

    @GetMapping("/owner/{ownerUserId}")
    public ResponseEntity<List<Stringing>> getStringingsByOwner(@PathVariable UUID ownerUserId) {
        log.info("Received get stringings by owner request for: {}", ownerUserId);
        List<Stringing> stringings = stringingService.getStringingsByOwnerUserId(ownerUserId);
        return new ResponseEntity<>(stringings, HttpStatus.OK);
    }

    @PutMapping("/{stringingId}")
    public ResponseEntity<?> updateStringing(
            @PathVariable UUID stringingId,
            @Valid @RequestBody UpdateStringingRequest request) {
        log.info("Received update stringing request for id {}: {}", stringingId, request);
        try {
            Stringing updatedStringing = stringingService.updateStringing(stringingId, request);
            if (updatedStringing == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(updatedStringing, HttpStatus.OK);
        } catch (InvalidStateTransitionException e) {
            log.warn("Invalid state transition: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            log.error("Failed to update stringing", e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
