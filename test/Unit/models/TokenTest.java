package Unit.models;

import models.Token;
import models.User;
import org.junit.Before;
import org.junit.Test;

import java.util.Calendar;

import static org.junit.Assert.*;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.running;

public class TokenTest {

    @Test
    public void findByTokenTest(){
        running(fakeApplication(), new Runnable() {
            public void run() {
                Token token = Token.findByToken("e23a4efa-5a96-4048-9f22-e0b42076548a");
                assertNotNull(token);
            }
        });
    }

    @Test
    public void isExpiredTest(){
        running(fakeApplication(), new Runnable() {
            public void run() {
                //generate an invalid token
                User user = User.findByEmail("bob@gmail.com");
                Token token = Token.findByToken("e23a4efa-5a96-4048-9f22-e0b42076548a");
                //assert that it is expired
                assertTrue(token.isExpired());
                //generate valid token
                Token token2 = Token.getNewToken(user);
                //assert that it is not expired
                assertFalse(token2.isExpired());
            }
        });
    }

    @Test
    public void getNewTokenTest(){
        running(fakeApplication(), new Runnable() {
            public void run() {
                User user = User.findByEmail("bob@gmail.com");
                Token token = Token.getNewToken(user);
                assertEquals("bob@gmail.com",token.email);
                assertNotNull(token);
            }
        });
    }

    @Test
    public void checkIfTokensAreValidTest(){
        running(fakeApplication(), new Runnable() {
            public void run() {
                //count how many tokens there is
                int numTokens = Token.find.findRowCount();

                //add an invalid token
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.DATE, -2);
                Token token = new Token();
                token.token = "c23a4efa-5b96-4048-9f22-e0b42576548a";
                token.dateCreated = cal.getTime();
                token.email = "bob@gmail.com";
                //add to database
                token.save();

                //assert number of tokens is one more
                int numTokensAfterAdd = Token.find.findRowCount();
                assertNotEquals(numTokens, numTokensAfterAdd);
                assertEquals((numTokens+1), numTokensAfterAdd);
                //call method
                Token.checkIfTokensAreValid();
                //assert number is one less
                int numTokensAfterCheck = Token.find.findRowCount();
                assertTrue(numTokensAfterCheck<numTokensAfterAdd);
            }
        });
    }

}
