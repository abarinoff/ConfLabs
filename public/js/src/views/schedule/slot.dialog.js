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
            this.data = options.data;
            this.callback = options.callback;
        },

        render: function() {
            this.renderTemplate();
            this.renderCustomTemplate();
            this.renderData();
            this.attachEventHandlers();
            this.showDialog();
        },

        confirm: function () {
            this.attachConfirmationHandler();
            this.hideDialog();
        },

        confirmationHandler: function(data) {
            if(!_.isUndefined(this.callback)) {
                this.callback(data);
            }
        },

        renderTemplate: function () {
            this.$el = $(this.template());
            this.$el.appendTo(this.PARENT_SELECTOR);
        },

        renderCustomTemplate: function() {
        },

        renderData: function() {
            var start = _.isUndefined(this.data) ? "9:00" : this.data.start;
            this.initializeTimePicker("#slot-start-time", start);

            var end = _.isUndefined(this.data) ? "10:00" : this.data.end;
            this.initializeTimePicker("#slot-end-time", end);
        },

        initializeTimePicker: function(selector, defaultTime) {
            $(selector).timepicker({
                defaultTime: defaultTime,
                minuteStep: 5,
                showInputs: false,
                showMeridian: false
            });
        },

        attachEventHandlers: function () {
            this.$el.one('hidden.bs.modal', _.bind(this.remove, this));
            this.$(this.CONFIRM_BUTTON_SELECTOR).click(_.bind(this.confirm, this));
        },

        attachConfirmationHandler: function () {
            var data = {
                start: $("#slot-start-time").val(),
                end: $("#slot-end-time").val()};
            var handler = _.bind(this.confirmationHandler, this, data);

            this.$el.one("hidden.bs.modal", handler);
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