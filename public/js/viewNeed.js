$("#donateAmount").click(function() {
    $("#enterAmount").toggle();
});
$("#donationAmount").bind("keyup change", function(e) {
    var amount = parseFloat($("#donationAmount").val());
    var amountNeeded = $("#amountNeeded").text();
    amountNeeded = parseFloat(amountNeeded.slice(1,amount.length));
    if(!isNaN(amount)){
        if(amount>amountNeeded){
            $("#showAmount").text("Amount more than needed");
            $("#stripeFee").text("€0.00");
            $("#stripeTotal").text("€0.00");
            $("#donationFinal").val(0);
        }
        else {
            var paymentFee = ((amount *2.4)/100) +.24;
            var VAT_PERCENT =23;
            var vat= (paymentFee * VAT_PERCENT)/100;
            paymentFee = paymentFee + vat;
            var total = amount +paymentFee;
            $("#showAmount").text("€"+amount.toFixed(2));
            $("#stripeFee").text("€"+paymentFee.toFixed(2));
            $("#stripeTotal").text("€"+total.toFixed(2));
            $("#donationFinal").val(total.toFixed(2));
        }
    }
    else {
        $("#showAmount").text("€0.00");
        $("#stripeFee").text("€0.00");
        $("#stripeTotal").text("€0.00");
        $("#donationFinal").val(0);
    }
});

