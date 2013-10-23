define([
    "underscore",
    "jquery",
    "backbone",
    "require.text!templates/slot.dialog.html"
],

function(_, $, Backbone, template) {

    var SlotDialog = Backbone.View.extend({
        PARENT_SELECTOR: "body",
        CONFIRM_BUTTON_SELECTOR: "#btn-slot-save",

        template: _.template(template),

        initialize: function(options) {
            this.callback = options.callback;
        },

        render: function() {
            this.renderTemplate();

            $.widget("ui.timespinner", $.ui.spinner, {
                options: {
                    // seconds
                    step: 60 * 1000,
                    // hours
                    page: 60
                },

                _parse: function( value ) {
/*
                    if ( typeof value === "string" ) {
                        // already a timestamp
                        if ( Number( value ) == value ) {
                            return Number( value );
                        }
                        return +Globalize.parseDate( value );
                    }
*/
                    return value;
                },

                _format: function( value ) {
                    return "aaa";
                }
            });

            $("#slot-start-time").timespinner();

            this.attachEventHandlers();
            this.showDialog();
        },

        confirm: function () {
            this.attachConfirmationHandler();
            this.hideDialog();
        },

        renderTemplate: function () {
            this.$el = $(this.template());
            this.$el.appendTo(this.PARENT_SELECTOR);
        },

        attachEventHandlers: function () {
            this.$el.one('hidden.bs.modal', _.bind(this.remove, this));
            this.$(this.CONFIRM_BUTTON_SELECTOR).click(_.bind(this.confirm, this));
        },

        attachConfirmationHandler: function () {
            if(!_.isUndefined(this.callback)) {
                this.$el.one("hidden.bs.modal", this.callback);
            }
        },

        showDialog: function () {
            this.$el.modal();
        },

        hideDialog: function () {
            this.$el.modal("hide");
        }
    });

    return SlotDialog;
});