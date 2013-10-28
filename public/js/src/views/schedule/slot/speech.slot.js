define([
    'underscore',
    "views/schedule/slot/slot",
    'require.text!templates/speech.slot.html'
],

function(_, SlotView, template) {

    var SpeechSlotView = SlotView.extend({
        TYPE: "speech",

        customTemplate: _.template(template),

        getType: function() {
            return this.TYPE;
        },

        renderCustomTemplate: function(stages, data) {
            return $(this.customTemplate({stages: stages, data: data}));
        }
    });

    return SpeechSlotView;
});
