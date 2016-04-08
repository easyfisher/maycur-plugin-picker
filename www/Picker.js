/**
  Maycur Picker Plugin
  https://github.com/easyfisher/maycur-plugin-picker

  Copyright (c) Easter Dong 2016
*/

var exec = require('cordova/exec');

var Picker = function() {

}

Picker.show = function(addresses, callback) {
    exec(callback,
      null,
      "Picker",
      "show",
      [addresses]
    );
};

module.exports = Picker;