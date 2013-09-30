module.exports = function(config) {
  config.set({
    basePath: '',

    frameworks: ['jasmine', 'requirejs'],

    files: [
      'test/test.application.js',
      {pattern: 'lib/**/*.js', included: false},
      {pattern: 'models/**/*.js', included: false},
      {pattern: 'test/**/*.test.js', included: false}
    ],

    exclude: [
      'application.js'
    ],

    reporters: ['progress', 'junit'],

    junitReporter: {
        outputFile: 'js.test.results.xml'
    },

    port: 9876,

    colors: true,

    logLevel: config.LOG_INFO,

    autoWatch: false,

    browsers: ['Chrome'],

    captureTimeout: 60000,

    singleRun: true
  });
};
