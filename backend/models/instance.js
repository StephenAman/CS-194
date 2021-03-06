var moment = require('moment');

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
	db.query('SELECT * FROM instances WHERE id = ?', [id], function(err, results, fields) {
		if (err) {
			return callback(err, false);
		}
		else if (results.length == 0) {
			return callback('Instance does not exist.', false);
		} else {
			return callback(null, new Instance(results[0]));
		}
	});
};

/**
 * Fetches a list of all instances associated with a given mic.
 */
Instance.findAll = function(micId, callback) {
	// TODO: Implement
};

/**
 * Fetches the id of the next instance of a given mic, i.e., the first one that
 * has not happened yet. Returns null if no such instance exists.
 */
Instance.findNext = function(micId, callback) {
	var now = moment().format('YYYY-MM-DD HH:mm:ss');
	db.query(
		'SELECT id FROM instances WHERE micId = ? AND endDate > ? \
		 ORDER BY id ASC LIMIT 1',
		[micId, now],
		function(err, results, fields) {
			if (err) {
				return callback(err, false);
			}
			if (results.length == 0) {
				return callback(null, false);
			} else {
				return callback(null, results[0].id);
			}
		}
	);
};

/**
 * Returns the signup list associated with this instance.
 */
Instance.prototype.getSignups = function(callback) {
	db.query(
	   'SELECT instanceId, userId, name, slotNumber \
		FROM signups \
		INNER JOIN users on signups.userId = users.id \
		WHERE instanceId = ?',
		[this.get('id')],
		function(err, results, fields) {
			if (err) {
				return callback(err, false);
			} else {
				return callback(null, results);
			}
		}
	);
}

/**
 * Adds a new signup for this instance.
 */
Instance.addSignup = function(userId, instanceId, slot, callback) {
	db.query(
		'INSERT INTO signups (userId, instanceId, slotNumber) VALUES (?, ?, ?)',
		[userId, instanceId, slot],
		function(err, result) {
			if (err) {
				return callback(err);
			} else {
				return callback(null);
			}
		}
	);
}

/**
 * Deletes a signup for this instance.
 */
Instance.deleteSignup = function(instanceId, slot, callback) {
	db.query(
		'DELETE FROM signups WHERE instanceId = ? AND slotNumber = ?',
		[instanceId, slot],
		function(err, result) {
			if (err) {
				return callback(err);
			} else {
				return callback(null);
			}
		}
	);
}

/**
 * Updates this instance in the database.
 */
Instance.prototype.save = function(shouldDeleteSignups, callback) {
	var numSlots = this.get('numSlots');
	var id = this.get('id');
	db.query(
		'UPDATE instances SET ? WHERE id = ?',
		[this.data, this.get('id')],
		function(err) {
			if (err) {
				return callback(err);
			} else {
				if (shouldDeleteSignups) {
					db.query(
						'DELETE FROM signups WHERE instanceId = ? AND slotNumber >= ?',
						[id, numSlots],
						function(err) {
							return callback(null);
						}
					)
				} else {
					return callback(null);
				}
			}
		}
	);
};

module.exports = Instance;
