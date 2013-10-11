define([
    'underscore',
    'jquery',
    'backbone',
    'models/model',
    'backbone.validation',
    'validation/validation.handler',
    'require.text!templates/speakers.list.html'
],
function(_, $, Backbone, Model, Validation, ValidationHandler, speakersListTemplate) {
    var SpeakersListView = Backbone.View.extend({
        ID_SELECTOR         : "#hdn-speaker-id",
        NAME_SELECTOR       : "#speaker-name",
        POSITION_SELECTOR   : "#speaker-position",
        DESCRIPTION_SELECTOR: "#speaker-description",

        DIALOG_SELECTOR     : "#dlg-speaker",

        speakersListTemplate : _.template(speakersListTemplate),

        el : "#speakers",

        events: {
            "click button[id^='btn-edit-speaker-']"     : "showModal",
            "click button[id^='btn-remove-speaker-']"   : "removeSpeaker",
            "click button#save-speaker"                 : "saveSpeaker"
        },

        initialize: function(options) {
            this.eventModel = options.eventModel;
            _.bindAll(this, "speakerSaved", "speakerRemoved", "errorSaveSpeaker", "errorRemoveSpeaker", "dialogHidden");
        },

        render : function() {
            this.$el.html(this.speakersListTemplate({speakers : this.eventModel.getSpeakers()}));

            $(this.DIALOG_SELECTOR).on('hidden.bs.modal', this.dialogHidden);

            return this;
        },

        showModal : function(event) {
            var $target = $(event.target),
                view = this,
                buttonId = $target.attr('id'),
                id = buttonId.replace("btn-edit-speaker-", '');

            if (id == "new") {
                $(view.ID_SELECTOR).val('');
                $(view.NAME_SELECTOR).val('');
                $(view.POSITION_SELECTOR).val('');
                $(view.DESCRIPTION_SELECTOR).val('');
            }
            else {
                var speaker = view.eventModel.getSpeaker(id);

                $(view.ID_SELECTOR).val(speaker.getId());
                $(view.NAME_SELECTOR).val(speaker.getName());
                $(view.POSITION_SELECTOR).val(speaker.getPosition());
                $(view.DESCRIPTION_SELECTOR).val(speaker.getDescription());
            }
            $(view.DIALOG_SELECTOR).modal();
        },

        saveSpeaker: function() {
            var view = this,
                id = $(this.ID_SELECTOR).val(),
                attributes = view.collectFields(),
                speaker = isNaN(parseInt(id)) ? null : view.eventModel.getSpeaker(id);

            view.model = speaker
                ? new Model.Speaker(speaker.toJSON(), {eventId: view.eventModel.id})
                : new Model.Speaker({}, {eventId: view.eventModel.id});

            Validation.bind(view, new ValidationHandler("speaker."));

            view.model.set(attributes);

            view.model.save({}, {
                success : view.speakerSaved,
                error   : view.errorSaveSpeaker
            });
        },

        removeSpeaker : function(event) {
            var $target = $(event.target),
                view = this,
                speakers = view.eventModel.getSpeakers(),
                buttonId = $target.attr('id'),
                id = buttonId.replace("btn-edit-speaker-", '');
            console.log("remove speaker with id: " + id);
            /*view.render();*/
        },

        speakerSaved: function(model, respose, options) {
            var view = this;
            view.eventModel.saveSpeaker(model);
            $(view.DIALOG_SELECTOR).modal('hide');
        },

        speakerRemoved: function(model, response, options) {
            console.log("speaker removed");
        },

        errorSaveSpeaker: function() {
            console.log("Error saving Speaker on the remote server");
        },

        errorRemoveSpeaker: function() {
            console.log("Error removing Speaker from a remote server");
        },

        dialogHidden: function() {
            var view = this;
            view.render();
        },

        collectFields: function() {
            var view = this;
            return {
                name        : $(view.NAME_SELECTOR).val(),
                position    : $(view.POSITION_SELECTOR).val(),
                description : $(view.DESCRIPTION_SELECTOR).val()
            }
        }
    });

    return SpeakersListView;
});
