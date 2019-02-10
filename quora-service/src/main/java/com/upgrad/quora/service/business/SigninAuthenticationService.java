package com.upgrad.quora.service.business;


import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthenticationFailedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


import java.time.ZonedDateTime;

@Service
public class SigninAuthenticationService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private PasswordCryptographyProvider cryptographyProvider;

    @Transactional(propagation = Propagation.REQUIRED)
    public UserAuthTokenEntity authenticate(final String username , final String password) throws AuthenticationFailedException {
        //Call the getUserByName method in UserDao class for userDao object and pass username as argument
        // Receive the value returned by getUserByName() method in UserEntity type object(name it as userEntity)
        UserEntity userEntity = userDao.getUserByName(username);
        if(userEntity == null) {
            throw new AuthenticationFailedException("ATH-001", "This username does not exist");
        }

        //After that you have got userEntity from users table, we need to compare the password entered by the user with the password stored in the database
        //Since the password stored in the database is encrypted, so we also encrypt the password entered by the user using the Salt attribute in the database
        //Call the encrypt() method in PasswordCryptographyProvider class for CryptographyProvider object

        final String encryptedPassword = cryptographyProvider.encrypt(password,userEntity.getSalt());
        //Now encryptedPassword contains the password entered by the user in encryppted form
        //And userEntity.getPassword() gives the password stored in the database in encrypted form
        //We compare both the passwords (Note that in this Assessment we are assuming that the credentials are correct)
        if(encryptedPassword.equals(userEntity.getPassword())) {
            //Implementation of JwtTokenProvider class
            JwtTokenProvider jwtTokenProvider = new JwtTokenProvider(encryptedPassword);
            //Now UserAuthTokenEntity type object is to be persisted in a databse
            //Declaring an object userAuthTokenEntity of type UserAuthTokenEntity and setting its attributes
            UserAuthTokenEntity userAuthTokenEntity = new UserAuthTokenEntity();
            userAuthTokenEntity.setUser(userEntity);
            final ZonedDateTime now = ZonedDateTime.now();
            final ZonedDateTime expiresAt = now.plusHours(8);

            userAuthTokenEntity.setAccessToken(jwtTokenProvider.generateToken(userEntity.getUuid(), now, expiresAt));

            userAuthTokenEntity.setLoginAt(now);
            userAuthTokenEntity.setExpiresAt(expiresAt);
            userAuthTokenEntity.setUuid(userEntity.getUuid());

            //Call the createAuthToken() method in UserDao class for userDao
            //Pass userAuthTokenEntity as an argument

            userDao.createAuthToken(userAuthTokenEntity);

            //To update the last login time of user
            //Carefully read how to update the existing record in a database(will be asked in later Assessments)
            //When the persistence context is closed the entity becomes detached and any further changes to the entity will not be saved
            //You need to associate the detached entity with a persistence context through merge() method to update the entity
            //updateUser() method in UserDao class calls the merge() method to update the userEntity

            userDao.updateUser(userEntity);
            // userEntity.setLastLoginAt(now);
            return userAuthTokenEntity;

        }
        else{
            //throw exception
            throw new AuthenticationFailedException("ATH-002", "Password Failed");
        }


    }

}