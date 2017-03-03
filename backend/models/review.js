var moment = require('moment');

var db = require('../database.js');

/**
 * Review model
 */
var Review = function(data) {
	this.data = data;	
};

Review.prototype.data = {};

Review.prototype.get = function(property) {
	return this.data[property];
};

Review.prototype.set = function(property, value) {
	this.data[property] = value;
};

/**
 * Adds a new review to the database and returns the new review object.
 */
Review.create = function(review, callback) {
	db.query(
		'INSERT INTO reviews SET ?', review,
		function(err, result) {
			if (err) {
				return callback(err, false);
			} else {
				return callback(null, new Review(review));
			}
		}
	);
};

/**
 * Fetches a list of all reviews associated with a given mic.
 */
Review.findAll = function(micId, callback) {
	db.query(
		'SELECT userId, users.name AS userName, reviewText, time \
		 FROM reviews INNER JOIN users on reviews.userId = users.id \
		 WHERE micId = ?', [micId],
		function(err, results, fields) {
			if (err) {
				return callback(err, false);
			} else {
				return callback(null, results);
			}
		}
	);
};

module.exports = Review;
