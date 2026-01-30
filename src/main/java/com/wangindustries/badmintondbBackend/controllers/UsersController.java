package com.wangindustries.badmintondbBackend.controllers;

import com.wangindustries.badmintondbBackend.models.User;
import com.wangindustries.badmintondbBackend.requests.CreateUserRequest;
import com.wangindustries.badmintondbBackend.responses.GetUserResponse;
import jakarta.validation.Valid;
import com.wangindustries.badmintondbBackend.services.UsersService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/user")
public class UsersController {
    @Autowired
    UsersService usersService;

    @GetMapping
    public ResponseEntity<String> getUsers() {
        return new ResponseEntity<>("Test Got ALL users", HttpStatus.OK);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<GetUserResponse> getUser(@PathVariable UUID userId) {
        log.info("Got to get user request for userId: {}", userId);
        GetUserResponse userResponse = new GetUserResponse(userId);
        return new ResponseEntity<>(userResponse, HttpStatus.ACCEPTED);
    }
//
    @PostMapping
    public ResponseEntity<User> createUser(@Valid @RequestBody CreateUserRequest createUserRequest) {
        log.info("Received create user request: {}", createUserRequest);
        try {
            User createdUser = usersService.createUser(createUserRequest);
            return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
        } catch (Exception e) {
            log.error("Failed to create user", e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST); //todo should either log and handle or throw, not both
        }
    }
//    @PutMapping("/user/{userId}")
//    public ResponseEntity<BaseUserResponse> updateUserInformation(
//            @PathVariable(value="userId") UUID userId,
//            @RequestBody UpdateUserRequestBody updateRequestBody
//    ) {
//        logger.info(String.valueOf(updateRequestBody)); //log request body
//
//        usersService.updateAllowedUserFields(userId, updateRequestBody);
//        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
//    }
//
//    @GetMapping("/user/{userId}/stringings")
//    public ResponseEntity<ListStringingsResponse> getStringingsForUser(
//            @PathVariable(value = "userId") UUID userId,
//            @RequestParam(value = "completedOnly", required = false) Boolean completedOnly, //todo completed only with StringingStatus
//            @RequestParam(value = "stringerOnly", required = false) Boolean stringerOnly,
//            @RequestParam(value = "ownerOnly", required = false) Boolean ownerOnly
//    ) {
//        logger.info("Got UserId: {} and completedOnly as: {}", userId, completedOnly);
//
//        List<Stringing> listOfStringings;
//
//
//        if(completedOnly != null) {
//            if(stringerOnly != null && stringerOnly) {
//                listOfStringings = stringingService.getAllStringingByRequesterUserId(userId, completedOnly);
//            } else if(ownerOnly != null && ownerOnly) {
//                listOfStringings = stringingService.getAllStringingByStringerUserId(userId, completedOnly);
//            } else {
//                listOfStringings = stringingService.getAllStringingByUserIdOrRequesterUserId(userId, completedOnly);
//            }
//        } else {
//            if(stringerOnly != null && stringerOnly) {
//                listOfStringings = stringingService.getAllStringingByStringerUserId(userId);
//            } else if(ownerOnly != null && ownerOnly) {
//                listOfStringings = stringingService.getAllStringingByRequesterUserId(userId);
//            } else {
//                listOfStringings = stringingService.getAllStringingByUserIdOrRequesterUserId(userId);
//            }
//        }
//
//        logger.info(listOfStringings.toString());
//        List<StringingResponse> stringingResponses = listOfStringings.stream().map(StringingResponseConverter::convertToStringingResponse).toList();
//        return new ResponseEntity<>(new ListStringingsResponse(stringingResponses.size(), stringingResponses), HttpStatus.OK);
//    }
//
//    @GetMapping("/user/{userId}/analytical/aggregate/stringer")
//    public ResponseEntity<List<AggregateStringingDataByStringerUserId>> getAggregateStringingDataForStringerUser(@PathVariable(value = "userId") UUID userId) { //todo implement stringerUser vs requesterUser, etc, etc
//        return new ResponseEntity<>(stringingService.getAggregateStringingDataByStringerUserId(userId), HttpStatus.OK);
//    }
//
//    @GetMapping("/user/{userId}/analytical/aggregate/requester")
//    public ResponseEntity<List<AggregateStringingDataByRequesterUserId>> getAggregateStringingDataForRequesterUser(@PathVariable(value = "userId") UUID userId) { //todo implement stringerUser vs requesterUser, etc, etc
//        return new ResponseEntity<>(stringingService.getAggregateStringingDataByRequesterUserId(userId), HttpStatus.OK);
//    }
//
//    @GetMapping("/user/{userId}/racket/all")
//    public ResponseEntity<List<RacketDetails>> getAllRacketsForAnUser(@PathVariable(value = "userId") UUID userId) {
//        return new ResponseEntity<>(racketService.getAllRacketsByOwnerId(userId), HttpStatus.OK);
//    }
}