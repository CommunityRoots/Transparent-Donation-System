package PasswordValidator;

import models.FormValidator;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class FormValidatorTest {
    FormValidator formValidator = new FormValidator();
    @Test
    public void testPasswordValidation(){
        assertEquals(true, formValidator.validate("secret1"));
        //must have num
        assertEquals(false, formValidator.validate("secret"));
    }

}
