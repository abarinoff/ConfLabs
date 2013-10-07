define([
    "jquery",
    "underscore",
    "backbone",
    "validation",
    'require.text!templates/location.html'],

function($, _, Backbone, Validation, template) {

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

        render: function() {
            var renderedTemplate = this.template({location: this.model.getLocation()});
            this.$el.html(renderedTemplate);
            return this;
        },

        editLocation: function() {
            var location = this.model.getLocation();

            if(_.isUndefined(location)) {
                this.showDialog("", "");
            } else {
                this.showDialog(location.getTitle(), location.getAddress());
            }
        },

        saveLocation: function() {
            var currentLocation = this.model.getLocation();
            var updatedLocation = $.extend(true, {}, currentLocation);

            updatedLocation.setTitle($(this.TITLE_SELECTOR).val());
            updatedLocation.setAddress($(this.ADDRESS_SELECTOR).val());

            this.model.setLocation(updatedLocation, {validate: true});

            if(this.model.isValid()) {
                this.hideDialog();
            }
        },

        removeLocation: function() {
            this.model.removeLocation();
            this.render();
        },

        showDialog: function(title, address) {
            Validation.bind(this);

            $(this.TITLE_SELECTOR).val(title);
            $(this.ADDRESS_SELECTOR).val(address);

            $(this.DIALOG_SELECTOR).modal();
        },

        hideDialog: function() {
            var dialog = $(this.DIALOG_SELECTOR);

            dialog.one("hidden.bs.modal", _.bind(this.render, this));
            dialog.modal("hide");
        }
    });

    return LocationView;
});