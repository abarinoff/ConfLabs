define([
    "underscore",
    "models/restful.model",
    "views/schedule/slot/org.slot",
    "views/schedule/slot/speech.slot"
],

function(_, Model, OrgSlotView, SpeechSlotView) {
    var SlotViewFactory = function() {
        this.slotViews = {
            org: OrgSlotView,
            speech: SpeechSlotView
        };

        this.buildView = function(type, stagesNumber, data) {
            var slotClass = this.slotViews[type];
            return new slotClass(stagesNumber, data);
        };

        this.buildModel = function(type, stagesNumber, data) {
            return new Model.Slot(data);
        }
    };

    return SlotViewFactory;
});