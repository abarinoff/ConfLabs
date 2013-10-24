define([
    'underscore',
    "views/schedule/slot/slot",
    'require.text!templates/speech.slot.html'
],

function(_, SlotView, template) {

    var SpeechSlotView = SlotView.extend({
        template: _.template(template),

        getTemplate: function() {
            return this.template;
        }
    });

    return SpeechSlotView;
});
