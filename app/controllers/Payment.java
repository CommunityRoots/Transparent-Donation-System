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
        String token = dynamicForm.get("stripeToken");
        String email = dynamicForm.get("stripeEmail");
        double amount = Double.parseDouble(dynamicForm.get("donationAmount"));
        long needId = Long.parseLong(dynamicForm.get("needId"));

        PaymentProcessor paymentProcessor = new PaymentProcessor();
        paymentProcessor.paymentThroughStripe(token,email,amount,needId);

        flash("success", "Your donation has been made. Thank you!");
        return redirect(routes.Needs.viewNeed(needId));
    }
}


