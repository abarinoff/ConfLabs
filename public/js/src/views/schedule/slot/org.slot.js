define([
    'underscore',
    "views/schedule/slot/slot",
    'require.text!templates/org.slot.html'
],

function(_, SlotView, template) {

    var OrgSlotView = SlotView.extend({
        TYPE: "org",

        template: _.template(template),

        getType: function() {
            return this.TYPE;
        },

        getTemplate: function() {
            return this.template;
        }
    });

    return OrgSlotView;
});
