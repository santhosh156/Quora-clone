package com.upgrad.quora.api.controller;


import com.upgrad.quora.api.model.SigninResponse;
import com.upgrad.quora.service.business.SigninAuthenticationService;
import com.upgrad.quora.service.business.SigninAuthenticationService;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthenticationFailedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


import java.util.Base64;


//The annotation which adds both the @Controller and @ResponseBody annotation.
@RestController
//The annotation to map with URL request of type '/'
@RequestMapping("/")
public class SigninAuthenticationController {

    @Autowired
    private SigninAuthenticationService authenticationService;

    //An annotation to map this method with Http request of POST type, map it with URL request of type '/user/signin' and it produces Json
    @RequestMapping(method = RequestMethod.POST , path="/user/signin" ,produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SigninResponse> signin(@RequestHeader("authorization")  final String authorization) throws AuthenticationFailedException {

        // byte[] decode = Base64.getDecoder().decode(authorization);
        byte[] decode = Base64.getDecoder().decode(authorization.split("Basic ")[1]);
        String decodedText = new String(decode);
        String[] decodedArray = decodedText.split(":");

        UserAuthTokenEntity userAuthToken = authenticationService.authenticate(decodedArray[0] , decodedArray[1]);

        UserEntity user = userAuthToken.getUser();

        SigninResponse authorizedUserResponse = new SigninResponse().id(user.getUuid()).message("OK - Authenticated successfully");

        HttpHeaders headers = new HttpHeaders();
        headers.add("access-token", userAuthToken.getAccessToken());
        return new ResponseEntity<SigninResponse>(authorizedUserResponse,headers, HttpStatus.OK);


    }


}
