package PasswordValidator;

import models.PasswordValidator;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PasswordValidatorTest {
    PasswordValidator passwordValidator = new PasswordValidator();
    @Test
    public void testPasswordValidation(){
        assertEquals(true,passwordValidator.validate("secret1"));
        //must have num
        assertEquals(false,passwordValidator.validate("secret"));
    }

}
