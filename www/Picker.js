/**
  Maycur Picker Plugin
  https://github.com/easyfisher/maycur-plugin-picker

  Copyright (c) Easter Dong 2016
*/

var exec = require('cordova/exec');

var Picker = function() {

}

Picker.show = function(nodes, defaultIndexes, callback) {
	var params = [nodes];
	if (defaultIndexes && defaultIndexes.length > 0) {
		params.push(defaultIndexes);
	}
    exec(callback,
       null,
       "Picker",
       "show",
       params
    );
};

module.exports = Picker;