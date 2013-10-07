requirejs.config({
    shim: {
        "underscore": {
            exports: "_"
        },

        "backbone": {
            deps: [
                "underscore",
                "jquery"
            ],
            exports: "Backbone"
        },

        "paginator": {
            deps: ["backbone"],
            exports: "Backbone.Paginator"
        },

        "bootstrap": {
            deps: ["jquery"],
            exports: "$.fn.popover"
        }
    },

    paths: {
        "jquery": "../lib/jquery",
        "underscore": "../lib/underscore",
        "backbone": "../lib/backbone",
        "paginator": "../lib/backbone.paginator",
        "validation": "../lib/backbone.validation",
        "bootstrap": "../lib/bootstrap",
        "require.text": "../lib/require.text"
    },

    enforceDefine: true
});

define([
    "backbone",
    "routers/router",
    "utils/validation",
    "bootstrap"],

function(Backbone, Router) {
    var Application = function() {
        this.router = new Router();
    }

    window.application = new Application();
    Backbone.history.start();
});