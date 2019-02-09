package com.upgrad.quora.api.controller;


import com.upgrad.quora.service.business.UserAdminBusinessService;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.upgrad.quora.api.model.UserDeleteResponse;

@RestController
@RequestMapping("/")
public class AdminController {

    @Autowired
    private UserAdminBusinessService userAdminBusinessService;


    @RequestMapping(method = RequestMethod.DELETE ,path="/admin/user/{userId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE )
    public ResponseEntity<UserDeleteResponse> deleteUser(@RequestHeader("authorization") final String authorization,
                                                         @PathVariable("userId") final String userUuid)
    {
        UserEntity userEntity = null;
        UserDeleteResponse deleteUserResponse=null;
        try {
            userEntity = userAdminBusinessService.deleteUser(authorization,userUuid);
        } catch (UserNotFoundException e) {
            e.printStackTrace();
        } catch (AuthorizationFailedException e) {
            e.printStackTrace();
        }
        if(userEntity!=null) {
            deleteUserResponse= new UserDeleteResponse().id(userEntity.getUuid());
            return new ResponseEntity<UserDeleteResponse>(deleteUserResponse, HttpStatus.OK);
        }
        return new ResponseEntity<UserDeleteResponse>(deleteUserResponse, HttpStatus.NOT_FOUND);
    }

}
