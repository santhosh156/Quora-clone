package com.upgrad.quora.api.controller;


import com.upgrad.quora.api.model.SignoutResponse;
import com.upgrad.quora.service.business.SignoutBusinessService;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.SignOutRestrictedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.time.ZonedDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/")
public class SignoutController {

    @Autowired
    private SignoutBusinessService signoutBusinessService;

    @RequestMapping(method= RequestMethod.POST, path="/user/signout",produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SignoutResponse>signout(@RequestHeader("authorization") final String authorization) throws SignOutRestrictedException {

        String[] bearerToken = authorization.split( "bearer");
        final UserAuthTokenEntity userAuthTokenEntity = signoutBusinessService.Signout(bearerToken[1]);
        UserEntity userEntity = userAuthTokenEntity.getUser();

        userAuthTokenEntity.setLogoutAt(ZonedDateTime.now());
        userAuthTokenEntity.setExpiresAt(ZonedDateTime.now());

        SignoutResponse signoutResponse = new SignoutResponse().id(userEntity.getUuid()).message("USER SUCCESSFULLY SignOut");

        return new ResponseEntity<SignoutResponse>(signoutResponse, HttpStatus.CREATED);

    }
}
