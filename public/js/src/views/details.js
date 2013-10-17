define([
    "underscore",
    "backbone",
    "backbone.validation",
    "models/model",
    "validation/validation.handler",
    "require.text!templates/details.html"
],

function(_, Backbone, Validation, Model, ValidationHandler, template) {
    var DetailsView = Backbone.View.extend({
        DIALOG_SELECTOR: "#dlg-details",
        TITLE_SELECTOR: "#event-title",
        DESCRIPTION_SELECTOR: "#event-description",

        template : _.template(template),
        el : '#details',

        events: {
            "click button#btn-edit-details" : "editDetails",
            "click button#btn-save-details": "saveDetails"
        },

        render: function() {
            var renderedTemplate = this.template({event: this.model});
            this.$el.html(renderedTemplate);
            return this;
        },

        editDetails: function() {
            this.showDialog();
        },

        saveDetails: function() {
            Validation.bind(this, new ValidationHandler("event."));

            var values = {title: $(this.TITLE_SELECTOR).val(), description: $(this.DESCRIPTION_SELECTOR).val()};
            this.model.save(values, {
                wait: true,
                success: _.bind(this.onSaveSuccess, this),
                error: _.bind(this.onError, this)
            });
        },

        showDialog: function() {
            $(this.TITLE_SELECTOR).val(this.model.getTitle());
            $(this.DESCRIPTION_SELECTOR).val(this.model.getDescription());

            $(this.DIALOG_SELECTOR).modal();
        },

        hideDialog: function(callback) {
            var dialog = $(this.DIALOG_SELECTOR);

            dialog.one("hidden.bs.modal", _.bind(callback, this));
            dialog.modal("hide");
        },

        onSaveSuccess: function() {
            this.hideDialog(_.bind(this.render, this));
        },

        onError: function(model, response) {
            this.hideDialog(Model.ErrorHandler[response.status]);
        }
    });

    return DetailsView;
});