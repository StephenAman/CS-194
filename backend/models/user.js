var db = require('../database.js');

/**
 * User model
 */
var User = function(data) {
	this.data = data;
};

User.prototype.data = {};

User.prototype.get = function(property) {
	return this.data[property];
};

User.prototype.set = function(property, value) {
	this.data[property] = value;
};

User.prototype.save = function(callback) {
	db.query('UPDATE users SET ? WHERE id = ?', 
		[this.data, this.get('id')], 
		function(err, results, fields) {
		if (err) {
			return callback(err);
		} else {
			return callback(null);
		}
	});
};

/**
 * Retrieves a user from the database using 'id'.
 */
User.findById = function(id, callback) {
	db.query('SELECT * FROM users WHERE id = ?', [id], function(err, results, fields) {
		if (err) {
			return callback(err, false);
		}
		else if (results.length == 0) {
			return callback('User does not exist.', false);
		} else {
			return callback(null, new User(results[0]));
		}
	});
};

/**
 * Retrieves a user from the DB using 'user.id'. If no such user
 * exists, we create a new user and store him in the database.
 */
User.findOrCreate = function(user, callback) {
	User.findById(user.id, function(err, result) {
		if (err) {
			db.query(
				'INSERT INTO users (id, name) VALUES (?, ?)', 
				[user.id, user.name], 
				function(err, results, fields) {
					if (err) {
						return callback(err, false);
					} else {
						return callback(null, new User(user));
					}
				}
			);
		} else {
			return callback(null, result);
		}
	});
};

module.exports = User;
