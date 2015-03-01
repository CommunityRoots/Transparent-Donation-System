package models;


import com.stripe.Stripe;
import com.stripe.exception.*;
import com.stripe.model.Charge;
import play.Play;

import java.util.HashMap;
import java.util.Map;

public class PaymentProcessor {

    final String API_KEY = Play.application().configuration().getString("stripe.api.key");

    public void paymentThroughStripe(String token, String email, double amount,long needId){
        //amount to cents
        int amountInCents = (int)(amount*100);

        //need being donated to
        Need need = Need.find.byId(needId);
        //user that is donating to the need
        User user = User.find.byId(email);

        //process Stripe payment
        try {
            //Stripes bindings
            Stripe.apiKey = API_KEY;

            Map<String, Object> chargeParams = new HashMap<String, Object>();
            chargeParams.put("amount", amountInCents);
            chargeParams.put("currency", "eur");
            chargeParams.put("source", token); // obtained with Stripe.js
            chargeParams.put("description", "Donation to CommunityRoots.net for "+need.title+". Thank you");
            Charge charge = Charge.create(chargeParams);

        } catch (CardException e) {
            // Since it's a decline, CardException will be caught
            System.out.println("Status is: " + e.getCode());
            System.out.println("Message is: " + e.getParam());
        } catch (InvalidRequestException e) {
            // Invalid parameters were supplied to Stripe's API
        } catch (AuthenticationException e) {
            // Authentication with Stripe's API failed
            // (maybe you changed API keys recently)
        } catch (APIConnectionException e) {
            // Network communication with Stripe failed
        } catch (StripeException e) {
            // Display a very generic error to the user, and maybe send
            // yourself an email
        } catch (Exception e) {
            // Something else happened, completely unrelated to Stripe
        }

        //add donation to system
        need.addDonation(need,user,amount);
    }
}
