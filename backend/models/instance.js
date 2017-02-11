var db = require('../database.js');

/**
 * Instance model
 */
var Instance = function(data) {
	this.data = data;	
};

Instance.prototype.data = {};

Instance.prototype.get = function(property) {
	return this.data[property];
};

Instance.prototype.set = function(property, value) {
	this.data[property] = value;
};

/**
 * Adds a new instance to the database and returns the new instance object.
 */
Instance.create = function(instance, callback) {
	db.query(
		'INSERT INTO instances SET ?', instance,
		function(err, result) {
			if (err) {
				return callback(err, false);
			} else {
				var obj = new Instance(instance);
				obj.set('id', result.insertId);
				return callback(null, obj);
			}
		}
	);
};

/**
 * Fetches a specific instance from the database.
 */
Instance.findOne = function(id, callback) {
	// TODO(joachimr): Implement
};

/**
 * Fetches a list of all instances associated with a given mic.
 */
Instance.findAll = function(micId, callback) {
	// TODO(joachimr): Implement
};

/**
 * Fetches the next instance of a given mic, i.e., the first one that
 * has not happened yet. Returns null if no such instance exists.
 */
Instance.findNext = function(micId, callback) {
	// TODO(joachimr): Implement
};

/**
 * Updates this instance in the database.
 */
Instance.prototype.save = function(callback) {
	// TODO(joachimr): Implement
};

/**
 * Deletes this instance from the database.
 */
Instance.prototype.delete = function(callback) {
	// TODO(joachimr): Implement
};

module.exports = Instance;
