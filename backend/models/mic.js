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
	db.query('SELECT * FROM mics WHERE id = ?', [id], function(err, results, fields) {
		if (err) {
			return callback(err, false);
		}
		else if (results.length == 0) {
			return callback('Mic does not exist.', false);
		} else {
			return callback(null, new Mic(results[0]));
		}
	});
};

/**
 * Fetches a list of all open mics in the database.
 */
Mic.findAll = function(callback) {
	db.query('SELECT * FROM mics', function(err, results, fields) {
		if (err) {
			return callback(err, false);
		}
 		return callback(
 			null, results.map(function(row) { return new Mic(row); })
 		);
 	});
};

/**
 * Fetches a list of all open mics that are located within
 * the given geographical area.
 */
Mic.findAllInArea = function(minLat, minLng, maxLat, maxLng, callback) {
	// TODO: Implement
};

/**
 * Returns true if the mic is repeating, false otherwise.
 */
Mic.prototype.isRepeating = function() {
	if (this.get('meetingBasis') == null) {
		return false;
	}
	switch(this.get('meetingBasis').toLowerCase()) {
		case 'daily':
		case 'weekly':
		case 'biweekly':
		case 'monthly':
			return true;
		default:
			return false;
	}
}

/**
 * Updates this open mic in the database.
 * Note that this does not update any dependent instances. 
 */
Mic.prototype.save = function(callback) {
	db.query(
		'UPDATE mics SET ? WHERE id = ?',
		[this.data, this.get('id')],
		function(err) {
			if (err) {
				return callback(err);
			} else {
				return callback(null);
			}
		}
	);
};

/**
 * Deletes this open mic from the database.
 * Note that this does not delete any dependent instances.
 */
Mic.prototype.delete = function(callback) {
	// TODO: Implement
};

module.exports = Mic;
