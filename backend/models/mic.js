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
 * Adds a new open mic to the database and returns the new mic object.
 */
Mic.create = function(mic, callback) {
	db.query(
		'INSERT INTO mics SET ?', mic,
		function(err, result) {
			if (err) {
				return callback(err, false);
			} else {
				var obj = new Mic(mic);
				obj.set('id', result.insertId);
				return callback(null, obj);
			}
		}
	);
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
Mic.findAllInArea = function(minLat, minLng, maxLat, maxLng, callback) {
	// TODO(joachimr): Implement
};

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
};

module.exports = Mic;
