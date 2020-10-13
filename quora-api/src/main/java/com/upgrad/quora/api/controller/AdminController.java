package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.UserDeleteResponse;
import com.upgrad.quora.service.business.AdminBusinessService;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/")
public class AdminController {
    @Autowired
    private AdminBusinessService adminBusinessService;

    // to delete a user. only admin can use this endpoint
    // its a DELETE request and mapped to "/admin/user/{userId}" url
    // it requests the path variable 'userId' of corresponding user which is to be deleted and  access token of the signed in user
    //create UserDeleteResponse and set id and status field with uuid and "USER SUCCESSFULLY DELETED" respectively
    //return  UserDeleteResponse and HttpStatus.OK in ResponseEntity
    @RequestMapping(path= "/admin/user/{userId}", method = RequestMethod.DELETE,produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<UserDeleteResponse> delete(@PathVariable("userId") final String userId, @RequestHeader("authorization") final String authorization ) throws AuthorizationFailedException, UserNotFoundException {
              adminBusinessService.deleteUser(userId,authorization);
        UserDeleteResponse userSuccessfullyDeleted = new UserDeleteResponse().id(userId).status("USER SUCCESSFULLY DELETED");
        return new ResponseEntity<UserDeleteResponse>(userSuccessfullyDeleted,HttpStatus.OK);

    }
}
