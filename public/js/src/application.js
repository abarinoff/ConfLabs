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

        "backbone.paginator": {
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
        "backbone.paginator": "../lib/backbone.paginator",
        "backbone.validation": "../lib/backbone.validation",
        "bootstrap": "../lib/bootstrap",
        "require.text": "../lib/require.text"
    },

    enforceDefine: true
});

define([
    "backbone",
    "routers/router",
    "validation/validation.config",
    "bootstrap"],

function(Backbone, Router) {
    var Application = function() {
        this.router = new Router();
    }

    window.application = new Application();
    Backbone.history.start();
});