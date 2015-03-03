package controllers;

import models.PaymentProcessor;
import play.data.DynamicForm;
import play.mvc.Result;
import play.mvc.Security;

import static play.data.Form.form;
import static play.mvc.Controller.flash;
import static play.mvc.Results.redirect;

@Security.Authenticated(Secured.class)
public class Payment {

    public static Result chargeCard(){
        //get form input
        DynamicForm dynamicForm = form().bindFromRequest();
        long needId = Long.parseLong(dynamicForm.get("needId"));
        try {
            String token = dynamicForm.get("stripeToken");
            String email = dynamicForm.get("stripeEmail");
            double amount = Double.parseDouble(dynamicForm.get("donationAmount"));
            double preeFeeamount = Double.parseDouble(dynamicForm.get("preFeeAmount"));


            PaymentProcessor paymentProcessor = new PaymentProcessor();
            //process payment and get status
            int status = paymentProcessor.paymentThroughStripe(token, email, amount, needId, preeFeeamount);
            switch (status) {
                case 1:
                    flash("success", "Your donation has been made. Thank you!");
                    break;
                case 2:
                    flash("error", "You tried to donated too much, please try again.");
                    break;
                case 3:
                    flash("error", "Sorry, minimum donation is €0.20");
                    break;
                case 4:
                    flash("error", "Something has gone wrong, donation has not been made.");
                    break;
            }
            return redirect(routes.Needs.viewNeed(needId));
        }
        catch (Exception e){
            flash("error", "Something has gone wrong, donation has not been made.");
            return redirect(routes.Needs.viewNeed(needId));
        }
    }
}


