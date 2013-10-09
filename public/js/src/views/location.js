define([
    "jquery",
    "underscore",
    "backbone",
    "backbone.validation",
    "models/model",
    "validation/validation.handler",
    "require.text!templates/location.html"],

function($, _, Backbone, Validation, Model, ValidationHandler, template) {

    var LocationView = Backbone.View.extend({
        DIALOG_SELECTOR: "#dlg-location",
        TITLE_SELECTOR: "#location-title",
        ADDRESS_SELECTOR: "#location-address",

        template : _.template(template),
        el : '#location',

        events: {
            "click button#btn-add-location" : "editLocation",
            "click button#btn-edit-location" : "editLocation",
            "click button#btn-save-location": "saveLocation",
            "click button#btn-remove-location" : "removeLocation"
        },

        initialize: function(options) {
            this.eventModel = options.eventModel;
            this.setModelFromEvent();
        },

        setModelFromEvent: function() {
            this.model = this.eventModel.getLocation();
        },

        render: function() {
            var renderedTemplate = this.template({location: this.model});
            this.$el.html(renderedTemplate);
            return this;
        },

        editLocation: function() {
            if(_.isEmpty(this.model)) {
                this.showDialog("", "");
            } else {
                this.showDialog(this.model.getTitle(), this.model.getAddress());
            }
        },

        saveLocation: function() {
            if(_.isEmpty(this.model)) {
                this.model = new Model.Location({}, {eventId: this.eventModel.id});
            }
            Validation.bind(this, new ValidationHandler("location."));

            var values = {title: $(this.TITLE_SELECTOR).val(), address: $(this.ADDRESS_SELECTOR).val()};
            this.model.save(values, {
                success: _.bind(this.onSaveSuccess, this),
                error: _.bind(this.onError, this)
            });
        },

        removeLocation: function() {
            this.model.destroy({
                success: _.bind(this.onDestroySuccess, this),
                error: _.bind(this.onError, this)
            });
        },

        showDialog: function(title, address) {
            $(this.TITLE_SELECTOR).val(title);
            $(this.ADDRESS_SELECTOR).val(address);

            $(this.DIALOG_SELECTOR).modal();
        },

        hideDialog: function(callback) {
            var dialog = $(this.DIALOG_SELECTOR);

            dialog.one("hidden.bs.modal", _.bind(callback, this));
            dialog.modal("hide");
        },

        onSaveSuccess: function() {
            this.eventModel.setLocation(this.model);
            this.hideDialog(this.render);
        },

        onDestroySuccess: function() {
            this.eventModel.removeLocation();
            this.setModelFromEvent();
            this.render();
        },

        onError: function(model, response) {
            this.hideDialog(Model.ErrorHandler[response.status]);
        }

    });

    return LocationView;
});