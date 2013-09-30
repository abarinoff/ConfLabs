define(["models/restful.model"], function (Model) {

    describe("Simple test", function () {
        it("Events model URL should point to /events route", function () {
            var eventModel = new Model.Event();
            expect(eventModel.urlRoot).toEqual("/events");
        });
    });

});