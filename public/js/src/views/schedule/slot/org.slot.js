define([
    'underscore',
    "views/schedule/slot/slot",
    'require.text!templates/org.slot.html'
],

function(_, SlotView, template) {

    var OrgSlotView = SlotView.extend({
        template: _.template(template),

        getTemplate: function() {
            return this.template;
        }
    });

    return OrgSlotView;
});
