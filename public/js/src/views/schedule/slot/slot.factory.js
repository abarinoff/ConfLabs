define([
    "underscore",
    "views/schedule/slot/org.slot",
    "views/schedule/slot/speech.slot"
],

function(_, OrgSlotView, SpeechSlotView) {
    var SlotViewFactory = function() {
        this.supportedSlots = {
            org: OrgSlotView,
            speech: SpeechSlotView
        };

        this.build = function(type, stagesNumber, data) {
            var slotClass = this.supportedSlots[type];
            return new slotClass(stagesNumber, data);
        };
    };

    return SlotViewFactory;
});