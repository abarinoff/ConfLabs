define([
    'underscore',
    'jquery',
    'backbone',
    'require.text!templates/speakers.list.html',
    'require.text!templates/speeches.dropdown.html',
    'require.text!templates/new.speech.html'
],
function(_, $, Backbone, speakersListTemplate, speechesDropdownTemplate, newSpeechTemplate) {
    var SpeakersListView = Backbone.View.extend({
        speakersListTemplate : _.template(speakersListTemplate),
        speechDropdownTemplate: _.template(speechesDropdownTemplate),
        addNewSpeechTemplate: _.template(newSpeechTemplate),

        el : "#speakers",

        render : function() {
            this.$el.html(this.speakersListTemplate({speakers : this.model.get('speakers')}));

            $("button[id^='btn-edit-speaker-']").click(this, this.showModal);
            $("button[id^='btn-remove-speaker-']").click(this, this.removeSpeaker);
            $("#btn-add-speech").click(this, this.addSpeech);

            this.$("#dlg-speaker").on('change', '.speech-dropdown', this, this.selectSpeech);
            this.$("#dlg-speaker").on('click', '.btn-remove-speech', this, this.removeSpeech);
        },

        showModal : function(event) {
            var $this = $(this),
                view = event.data,
                buttonId = $this.attr('id'),
                id = buttonId.replace("btn-edit-speaker-", '');

            if (id == "new") {
                $("#hdn-speaker-id").val('');
                $("#speaker-name").val('');
                $("#speaker-position").val('');
                $("#speaker-description").val('');
            }
            else {
                var speakers = view.model.get('speakers'),
                    speaker = _.findWhere(speakers, {'id' : parseInt(id)})

                $("#hdn-speaker-id").val(speaker.id);
                $("#speaker-name").val(speaker.name);
                $("#speaker-position").val(speaker.position);
                $("#speaker-description").val(speaker.description);
            }
            $("#dlg-speaker").modal();
        },

        removeSpeaker : function(event) {
            var $this = $(this),
                view = event.data,
                speakers = view.model.get('speakers'),
                buttonId = $this.attr('id'),
                id = buttonId.replace("btn-edit-speaker-", '');

            var speaker = _.findWhere(speakers, {'id' : parseInt(id)});
            var speakerIndex = _.indexOf(speakers, speaker);

            speakers.splice(speakerIndex, 1);
            view.model.trigger('change:speakers');

            view.render();
        },

        addSpeech : function(event) {
            var $this = $(this),
                view = event.data,
                speeches = view.model.get('speeches');

            if (view.hasEmptySpeechContainers()) {
                return;
            }

            var html = view.speechDropdownTemplate({"speeches" : speeches}),
                buttonsContainer = $this.parents(".item-buttons")[0];

            $(buttonsContainer).before($(html));
        },

        selectSpeech : function(event) {
            var $this = $(this),
                speechItemRow = $this.parents('.speech-item')[0],
                view = event.data;

            if ($this.val() == 'new') {
                if ($(speechItemRow).find('.new-speech-title').length == 0) {
                    var html = view.addNewSpeechTemplate();
                    $(speechItemRow).append($(html));
                }
            }
            else {
                $(speechItemRow).find(".new-speech-title").remove()
            }
        },

        removeSpeech : function(event) {
            var $this = $(this),
                speechItemRow = $this.parents('.speech-item')[0];
            $(speechItemRow).remove();
        },

        hasEmptySpeechContainers : function() {
            // We do not allow adding new dropdowns for a new speech while there are empty ones
            var speechDropdowns = $("#dlg-speaker select.speech-dropdown"),
                emptySpeeches = false;

            $.each(speechDropdowns, function (index, speechDropdown) {
                if ($(speechDropdown).val() == '-1') {
                    emptySpeeches = true;
                    return false;
                }
            });

            return emptySpeeches;
        }
    });

    return SpeakersListView;
});
