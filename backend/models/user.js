var db = require('../database.js');

/**
 * Retrieves a user from the database using 'id'.
 */
var findById = function(id, callback) {
	db.query('SELECT id, name FROM users WHERE id = ?', [ id ], function(err, results, fields) {
		if (err) {
			return callback(err, false);
		}
		else if (results.length == 0) {
			return callback('User does not exist.', false);
		} else {
			return callback(null, results[0]);
		}
	});
};

/**
 * Retrieves a user from the DB using 'user.id'. If no such user
 * exists, we create a new user and store him in the database.
 */
var findOrCreate = function(user, callback) {
	findById(user.id, function(err, result) {
		if (err) {
			db.query(
				'INSERT INTO users (id, name) VALUES (?, ?)', 
				[user.id, user.name], 
				function(err, results, fields) {
					if (err) {
						return callback(err, false);
					} else {
						return callback(null, user);
					}
				}
			);
		} else {
			return callback(null, result);
		}
	});
};

exports.findById = findById;
exports.findOrCreate = findOrCreate;
