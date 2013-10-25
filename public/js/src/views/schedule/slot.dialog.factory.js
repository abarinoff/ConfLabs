define([
    "underscore",
    "views/schedule/org.slot.dialog",
    "views/schedule/speech.slot.dialog"
], function(_, OrgSlotDialogView, SpeechSlotDialogView) {

    var SlotDialogFactory = function() {
        this.slotDialogTypes = {
            org: OrgSlotDialogView,
            speech: SpeechSlotDialogView
        };

        this.build = function(type, options) {
            var slotDialogView = this.slotDialogTypes[type];
            return new slotDialogView(options);
        }
    }

    return SlotDialogFactory;
});
