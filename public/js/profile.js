$("#addNeedButton").click(function() {
    $.post(
        "/profile/addNeed",
        {
            "title": $("#needTitle").val(),
            "description": $("#needDescription").val(),
            "amount": $("#amount").val(),
            "location": $("#location").val(),
            "urgency" : $("#urgency option:selected").not("option:selected disabled").text(),
            "category": $("#category option:selected").not("option:selected disabled").text()
        }
    )
        .error(function(xhr, status, error) {
            $("#status").show();
            $("#needAdded").text("An error occurred");
            //xhr.responseText.
        })
        .success(function() {
            $("#addNeed").hide();
            $("#status").show();
            $("#needAdded").text("Need has been added successfully");
            $("#showAddNeed").removeClass("active");
        });
});
$("#addVolButton").click(function() {
    $.post(
        "/profile/addVolunteer",
        {
            "email": $("#volEmail").val()
        }

    )
        .error(function(xhr, status, error) {
            $("#status").show();
            $("#needAdded").text("An error occurred");
            //xhr.responseText.
        })
        .success(function() {
            $("#addVolunteer").hide();
            $("#status").show();
            $("#needAdded").text("Volunteer added.");
            $("#showAddVol").removeClass("active");
        });
});
function update(text)
{
    $("#addNeed").hide();
    $("#status").show();
    $("#needAdded").text(text);
    $("#showAddNeed").removeClass("active");
    $("#showEditCharity").removeClass("active");
}

$("#showAddNeed").click(function() {
    $("#showAddNeed").addClass("active");
    $("#showAddVol").removeClass("active");
    $("#showEditCharity").removeClass("active");
    $("#status").hide();
    $("#addVolunteer").hide();
    $("#editCharity").hide();
    $("#addNeed").toggle();
});
$("#closeNeed").click(function() {
    $("#addNeed").hide();
    $("#status").hide();
    $("#showAddNeed").removeClass("active");
});
$("#showAddVol").click(function() {
    $("#addNeed").hide();
    $("#editCharity").hide();
    $("#addVolunteer").toggle();
    $("#showAddVol").addClass("active");
    $("#showAddNeed").removeClass("active");
    $("#showEditCharity").removeClass("active");
});
$("#closeVol").click(function() {
    $("#addVolunteer").hide();
    $("#showAddVol").removeClass("active");
});
$("#showEditCharity").click(function() {
    $("#editCharity").toggle();
    $("#addVolunteer").hide();
    $("#addNeed").hide();
    $("#showEditCharity").addClass("active");
    $("#showAddVol").removeClass("active");
    $("#showAddNeed").removeClass("active");
});
$("#closeEditCharity").click(function() {
    $("#editCharity").hide();
    $("#showEditCharity").removeClass("active");
});
