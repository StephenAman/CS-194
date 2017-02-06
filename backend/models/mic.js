var db = require('../database.js');

/**
 * Mic model
 */
var Mic = function(data) {
	this.data = data;
};

Mic.prototype.data = {};

Mic.prototype.get = function(property) {
	return this.data[property];
};

Mic.prototype.set = function(property, value) {
	this.data[property] = value;
};

/**
 * Adds a new open mic to the database.
 */
Mic.create = function(mic, callback) {
	// TODO(joachimr): Implement.
};

/**
 * Fetches a specific open mic from the database.
 */
Mic.findOne = function(id, callback) {
	// TODO(joachimr): Implement
};

/**
 * Fetches a list of all open mics in the database.
 */
Mic.findAll = function(callback) {
	// TODO(joachimr): Implement
};

/**
 * Fetches a list of all open mics that are located within
 * the given geographical area.
 */
Mic.findAllInArea = function(min_lat, min_lng, max_lat, max_lng, callback) {
	// TODO(joachimr): Implement
}

/**
 * Updates this open mic in the database.
 * Note that this does not update any dependent instances. 
 */
Mic.prototype.save = function(callback) {
	// TODO(joachimr): Implement
};

/**
 * Deletes this open mic from the database.
 * Note that this does not delete any dependent instances.
 */
Mic.prototype.delete = function(callback) {
	// TODO(joachimr): Implement
}

module.exports = Mic;
