package models;


import com.stripe.Stripe;
import com.stripe.exception.*;
import com.stripe.model.Charge;
import play.Logger;
import play.Play;
import java.util.HashMap;
import java.util.Map;

public class PaymentProcessor {

    final String API_KEY = Play.application().configuration().getString("stripe.api.key");

    /**
     *
     * @param token token sent from stripe
     * @param email email of donator
     * @param amount amount donator wants to give
     * @param needId id of the need donator wants to donate to
     * @return status code
     * 1 = successful payment
     * 2 = donation amount too much
     * 3 = minimum donation of 20cents
     * 4 = error occured
     */
    public int paymentThroughStripe(String token, String email, double amount,long needId){
        //amount to cents
        int amountInCents = (int)(amount*100);

        //need being donated to
        Need need = Need.find.byId(needId);
        //user that is donating to the need
        User user = User.findByEmail(email);

        //amount needed to complete need in cents
        int amountNeededInCents = (int) (need.askAmount - need.donatedAmount)*100;

        //are they trying to donate too much or too little
        if(amountInCents > amountNeededInCents){
            return 2;
        }
        else if(amountInCents <20){
            return 3;
        }


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
            // Declined
            Logger.error("Stripe declined");
            Logger.error("Status is: " + e.getCode());
            Logger.error("Message is: " + e.getParam());
            return 4;
        } catch (InvalidRequestException e) {
            // Invalid parameters were supplied to Stripe's API
            Logger.error("Invalid parametres sent to stripe");
            return 4;
        } catch (AuthenticationException e) {
            // Authentication with Stripe's API failed
            Logger.error("Authentication with Stripe's API failed");
            return 4;
        } catch (APIConnectionException e) {
            // Network communication with Stripe failed
            Logger.error("Network connection with Stripe failed");
            return 4;
        } catch (StripeException e) {
            Logger.error("Stripe exception");
            return 4;
        } catch (Exception e) {
            // Something else happened, completely unrelated to Stripe
            Logger.error("App side payment failure");
            return 4;
        }

        //add donation to system
        need.addDonation(need,user,amount);

        //send email
        EmailService emailService = new EmailService();
        String message = "Thank you for your donation. You donated €"+amount+" to a need titled "+need.title+".";
        emailService.sendEmail(user.firstName,email,"Donation",message);

        return 1;
    }
}
