define([
    'underscore',
    "views/schedule/slot/slot",
    'require.text!templates/speech.slot.html'
],

function(_, SlotView, template) {

    var SpeechSlotView = SlotView.extend({
        TYPE: "speech",

        template: _.template(template),

        getType: function() {
            return this.TYPE;
        },

        getTemplate: function() {
            return this.template;
        }
    });

    return SpeechSlotView;
});
