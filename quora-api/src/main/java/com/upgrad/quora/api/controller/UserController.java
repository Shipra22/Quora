package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.SigninResponse;
import com.upgrad.quora.api.model.SignoutResponse;
import com.upgrad.quora.api.model.SignupUserRequest;
import com.upgrad.quora.api.model.SignupUserResponse;
import com.upgrad.quora.service.business.AuthenticationService;
import com.upgrad.quora.service.business.SignoutBusinessService;
import com.upgrad.quora.service.business.SignupBusinessService;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthenticationFailedException;
import com.upgrad.quora.service.exception.SignOutRestrictedException;
import com.upgrad.quora.service.exception.SignUpRestrictedException;
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
import java.util.UUID;

@RestController
@RequestMapping("/")

public class UserController {
    @Autowired
    private SignupBusinessService signupBusinessService;
    @Autowired
    private SignoutBusinessService signoutBusinessService;
    @Autowired
    private AuthenticationService authenticationService;

    // signup method is mapped to '/user/signup'.It's POST method and  takes a SignupUserRequest as parameters
    // create a userEntity object and set all the fields with the corresponding SignupRequest entity
    // call the business service on userEntity
    // create the SignupResponse instance with id = uuid of the userEntity and statuc registered
    // return the ResponseEntity of type SignupResponse and
    // parameters as SignupResponse object ,HttpStatus code CREATED
    @RequestMapping(path = "/user/signup", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SignupUserResponse> signup(final SignupUserRequest signupUserRequest) throws SignUpRestrictedException {
        final UserEntity userEntity = new UserEntity();
        userEntity.setUuid(UUID.randomUUID().toString());
        userEntity.setFirstName(signupUserRequest.getFirstName());
        userEntity.setLastName(signupUserRequest.getLastName());
        userEntity.setEmail(signupUserRequest.getEmailAddress());
        userEntity.setUserName(signupUserRequest.getUserName());
        userEntity.setPassword(signupUserRequest.getPassword());
        userEntity.setCountry(signupUserRequest.getCountry());
        userEntity.setAboutMe(signupUserRequest.getAboutMe());
        userEntity.setDob(signupUserRequest.getDob());
        userEntity.setContactNumber(signupUserRequest.getContactNumber());

        userEntity.setRole("nonadmin");
        final UserEntity createdUserEntity = signupBusinessService.signup(userEntity);
        SignupUserResponse userResponse = new SignupUserResponse()
                .id(createdUserEntity.getUuid())
                .status("REGISTERED");
        return new ResponseEntity<SignupUserResponse>(userResponse, HttpStatus.CREATED);

    }
    // signin method is mapped to '/user/signin' url .It's POST method
    // it takes authorization header as parameters. in the format "Basic dXNlcm5hbWU6cGFzc3dvcmQ="
    // here dXNlcm5hbWU6cGFzc3dvcmQ= is a Base64 encoded text in format "username:password"
    // first seperate the 'Basic' word from the authorization and decode it then seperate the username and password field
    // call the authentication service on username an password which will return userAuthEntity
    // create the SigninResponse instance with id = uuid of the userEntity and message as "SIGNED IN SUCCESSFULLY"
    // create a Http header object with headername "access-token" and pass access token userAuthEntity
    // return the ResponseEntity of type SigninResponse and
    // parameters as SigninResponse object ,header object ,HttpStatus code OK

    @RequestMapping(path = "/user/signin", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SigninResponse> signin(@RequestHeader("authorization") final String authorization) throws AuthenticationFailedException {

        byte[] decode = Base64.getDecoder().decode(authorization.split("Basic ")[1]);
        String decodedText = new String(decode);
        String[] decodedArray = decodedText.split(":");
        UserAuthEntity userAuthEntity = authenticationService.authenticate(decodedArray[0], decodedArray[1]);
        UserEntity user = userAuthEntity.getUser();
        SigninResponse signinResponse = new SigninResponse().id(user.getUuid()).message("SIGNED IN SUCCESSFULLY");
        HttpHeaders headers = new HttpHeaders();
        headers.add("access-token", userAuthEntity.getAccessToken());
        return new ResponseEntity<SigninResponse>(signinResponse, headers, HttpStatus.OK);
    }


    // signout method is mapped to '/user/signout' url .It's POST method
    // it takes authorization header as parameters which is a JWT access token.
    // call the signout business service on access token which will return userAuthEntity
    // get the User of the userAuthEntity
    // create the SignoutResponse instance with id = uuid of the user and message as "SIGNED OUT SUCCESSFULLY"
    // return the ResponseEntity of type SignoutResponse and
    // parameters as SignoutResponse object,HttpStatus code OK
    @RequestMapping(path = "/user/signout", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SignoutResponse> signout(@RequestHeader("authorization") final String accessToken) throws SignOutRestrictedException {
        UserAuthEntity userAuthEntity = signoutBusinessService.signout(accessToken);
        UserEntity user = userAuthEntity.getUser();

        SignoutResponse signedOutResponse = new SignoutResponse().id(user.getUuid()).message("SIGNED OUT SUCCESSFULLY");
        return new ResponseEntity<SignoutResponse>(signedOutResponse, HttpStatus.OK);

    }
}
