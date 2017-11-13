package com.shellshellfish.account.repositories;


import com.shellshellfish.account.commons.MD5;
import com.shellshellfish.account.model.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.UUID;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles(profiles="prod")
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Before
    public void setUp(){

    }

    @Test
    
    public void testCrud() {
        User user = new User();
        user.setActivated(true);
        user.setBirthAge("91");
        user.setCellPhone("13611442221");
        user.setOccupation("登录密码232");
        user.setPasswordHash(MD5.getMD5("abccd4djsN-999"));
        user.setCreatedBy("dev2");
        user.setUuid(UUID.randomUUID().toString());
        user.setId(29);
        userRepository.save(user);
        
        
    }
}
