var moment = require('moment');

var Instance = require('../models/instance.js');
var Mic = require('../models/mic.js');
var User = require('../models/user.js');
var Review = require('../models/review.js');

/**
 * MicController is responsible for handling API requests that rely upon
 * the Mic and Instance models. 
 */
var MicController = function() {};

/**
 * Returns a list of tuples containing all open mics with future instances.
 * Each tuple has the form (micId, nextInstanceStatus, lat, lng).
 */
MicController.getMics = function(req, res) {
	Mic.findAll(function(err, mics) {
		if (err) {
			throw err;
		}
		tasks = []
		for (i = 0; i < mics.length; i++) {
			tasks.push(GetMicSummary(mics[i]));
		}
		Promise.all(tasks).then(
			function(results) {
				res.send(
					results.filter(
						function(x) { return x !== null; }
					)
				);
			}
		).catch(
			function(err) {
				res.status(500).send();
			}
		);
	});
};

/**
 * Finds the next instance id of this mic, or creates it if it doesn't exist.
 * New instances are only created if the mic is set to repeat.
 */
MicController.findNextOrCreate = function(mic, callback) {
	Instance.findNext(mic.get('id'), function(err, instanceId) {
		if (err) {
			return callback(err, null);
		}
		if (instanceId) {
			return callback(null, instanceId);
		}
		// Don't create a new instance if the mic is not repeating.
		if (!mic.isRepeating()) {
			return callback(null, null);
		}
		MicController.createNextInstance(mic, function(instanceId) {
			return callback(null, instanceId);
		});
	});
} 

/**
 * Returns a list of tuples containing all open mics in a given area.
 * Each tuple has the form (micId, nextInstanceStatus, lat, lng).
 */
MicController.getMicsByArea = function() {
	// TODO: Implement.
};

/**
 * Creates a new open mic and its first instance.
 */
MicController.createMic = function(req, res) {
	var micData = {
		'createdBy': req.user.get('id'),
		'micName': req.body.micName,
		'venueName': req.body.venueName,
		'venueAddress': req.body.venueAddress,
		'venueLat': req.body.venueLat,
		'venueLng': req.body.venueLng,
		'startDate': moment(req.body.startDate).format('YYYY-MM-DD HH:mm:ss'),
		'duration': req.body.duration,
		'meetingBasis': req.body.meetingBasis,
		'setTime': req.body.setTime,
		'numSlots': req.body.numSlots,
	}
	Mic.create(micData, function(err, mic) {
		if (err) {
			throw err;
		} else {
			MicController.createNextInstance(mic, function(result) {
				res.send();
			});
		}
	});
};

/**
 * Creates the next instance of a mic.
 */
MicController.createNextInstance = function(mic, callback) {
	var date;
	var startDate = moment(mic.get('startDate'));
	var now = moment();
	if (now < startDate) {
		date = startDate;		
	} else {
		// Calculate the date of the next instance based on the mic's
		// start date and meeting basis.
		var date = startDate;
		while (date < now) {
			switch(mic.get('meetingBasis').toLowerCase()) {
				case 'daily':
					date.add(1, 'days');
					break;
				case 'weekly':
					date.add(1, 'weeks');
					break;
				case 'biweekly':
					date.add(2, 'weeks');
					break;
				case 'monthly':
					date.add(1, 'months');
					break;
				default:
					console.log('Mic is not repeating, no instance needed.');
					return callback(null);
			}
		}
	}
	var instanceData = {
		'micId': mic.get('id'),
		'startDate': date.format('YYYY-MM-DD HH:mm:ss'),
		'endDate': date.add(mic.get('duration'), 'minutes')
			.format('YYYY-MM-DD HH:mm:ss'),
		'cancelled': 0,
		'numSlots': mic.get('numSlots'),
		'setTime': mic.get('setTime'),
	};
	Instance.create(instanceData, function(err, instance) {
		if (err) {
			return callback(null);
		}
		return callback(instance.get('id'));
	});
}

MicController.canEdit = function(userId, micId, callback) {
	Mic.findOne(micId, function(err, mic) {
		if (err) {
			callback(null);
		} else {
			callback(mic.get('createdBy') === userId);
		}
	});
}

/**
 * Returns a mic along with the next instance which has yet to occur.
 */
MicController.getMic = function(req, res) {
	Mic.findOne(req.params.micId, function(err, mic) {
		if (err) {
			res.status(404).send();
			return;
		}
		mic.set('nextInstance', null);
		MicController.findNextOrCreate(mic, function(err, instanceId) {
			if (err || !instanceId) {
				res.send(mic.data);
			} else {
				MicController.getInstanceAndSignups(instanceId, function(err, instance) {
					if (err) {
						throw err;
					}
					mic.set('nextInstance', instance);
					res.send(mic.data);
				});
			}
		}); 
	});
};

/**
 * Updates an open mic and associated instances.
 */
MicController.updateMic = function() {
	// TODO: Implement.
};

/**
 * Deletes an open mic and associated instances.
 */
MicController.deleteMic = function() {
	// TODO: Implement.
};

/**
 * Returns a mic instance and its signup list.
 */
MicController.getInstance = function(req, res) {
	MicController.getInstanceAndSignups(
		req.params.instanceId, 
		function(err, responseData) {
			if (err) {
				res.status(404).send();
				return;
			}
			res.send(responseData);
		}
	);
}

/**
 * Merges an instance and its signup list into one JSON structure.
 */
MicController.getInstanceAndSignups = function(instanceId, callback) {
	Instance.findOne(instanceId, function(err, instance) {
		if (err) {
			callback(err, null);
		} else {
			var instanceData = {
				micId: instance.get('micId'),
				instanceId: instance.get('id'),
				startDate: instance.get('startDate'),
				endDate: instance.get('endDate'),
				numSlots: instance.get('numSlots'),
				setTime: instance.get('setTime'),
				cancelled: instance.get('cancelled'),
				signups: new Array(instance.get('numSlots')).fill(null),
			}
			instance.getSignups(function(err, results) {
				if (err) {
					throw err;
				} else {
					results.forEach(function(row) {
						instanceData.signups[row.slotNumber] = {userId: row.userId, name: row.name};
					});
					callback(false, instanceData);
				}
			});
		}
	});
};

MicController.createSignup = function(req, res) {
	MicController.getInstanceAndSignups(req.params.instanceId, function(err, instance) {
		if (err) {
			res.status(404).send();
			return;
		} else {
			// Check that slot is in range.
			if (req.body.slotNumber >= instance.numSlots) {
				res.status(400).send('Slot does not exist.');
				return;
			}
			// Check that slot is available.
			if (instance.signups[req.body.slotNumber] !== null) {
				res.status(400).send('Slot has already been taken.');
				return;
			}
			// Check that user isn't already signed up for a different slot.
			var found = false;
			for (var i = 0; i < instance.signups.length; i++) {
				if (instance.signups[i] != null && 
					instance.signups[i].userId === req.user.get('id')) 
				{
					res.status(400).send('You have already signed up for a slot.');
					return;
				}	
			}

			// Query the DB.
			Instance.addSignup(
				req.user.get('id'),
				req.params.instanceId,
				req.body.slotNumber,
				function(err) {
					if (err) {
						throw err;
					} else {
						res.send();
					}
				}
			);
		}
	});
}

MicController.deleteSignup = function(req, res) {
	MicController.getInstanceAndSignups(req.params.instanceId, function(err, instance) {
		if (err) {
			res.status(404).send();
			return;
		} else {
			// Check that slot is in range.
			var slot = req.body.slotNumber;
			if (slot >= instance.numSlots) {
				res.status(400).send('Slot does not exist.');
				return;
			}
			// Check that slot is not available.
			if (instance.signups[slot] === null) {
				res.status(400).send('Slot is already free.');
				return;
			}
			// Check that user is authorized.
			if (!req.hasEditPermissions &&
				req.user.get('id') !== instance.signups[slot].userId) 
			{
				res.status(403).send();
				return;
			}

			// Query the DB.
			Instance.deleteSignup(
				req.params.instanceId,
				req.body.slotNumber,
				function(err) {
					if (err) {
						throw err;
					} else {
						res.send();
					}
				}
			);
		}
	});
}

/**
 * Updates a mic instance.
 */
MicController.updateInstance = function(req, res) {
	Instance.findOne(req.params.instanceId, function(err, instance) {
		if (err) {
			return res.status(500).send();
		}
		if (req.body.signupsOpenDate) {
			instance.set('signupsOpenDate', req.body.signupsOpenDate);
		}
		removeSlots = false;
		if (req.body.numSlots) {
			removeSlots = (instance.get('numSlots') > req.body.numSlots);
			instance.set('numSlots', req.body.numSlots);
		}
		if (req.body.setTime) {
			instance.set('setTime', req.body.setTime);
		}
		if (req.body.cancelled) {
			instance.set('cancelled', req.body.cancelled);
		}
		if (req.body.eventDate) {
			var startDate = moment(req.body.eventDate.startDate);
			var endDate = startDate.clone();
			endDate.add(req.body.eventDate.duration, 'minutes');
			instance.set('startDate', startDate.format('YYYY-MM-DD HH:mm:ss'));
			instance.set('endDate', endDate.format('YYYY-MM-DD HH:mm:ss'));
		}
		instance.save(removeSlots, function(err) {
			if (err) {
				return res.status(500).send();
			}
			res.send();	
		});
	});
};

/**
 * Adds a new review for this mic.
 */
MicController.createReview = function(req, res) {
	var reviewData = {
		'userId': req.user.get('id'),
		'micId': req.params.micId,
		'reviewText': req.body.reviewText,
	}
	Review.create(reviewData, function(err, review) {
		if (err) {
			res.status(400).send("You have already reviewed this mic.");
		} else {
			res.send();
		}
	});
}

/**
 * Returns all reviews associated with the requested mic.
 */
MicController.getReviews = function(req, res) {
	Review.findAll(req.params.micId, function(err, mics) {
		if (err) {
			res.status(500).send();
		}
		res.send(mics);
	});
}

/**
 * Helper function which returns a promise to convert a full mic object
 * to a shorthand map {micId, status, venueLat, venueLng}.
 * Resolves to null if the mic has no future instances.
 */
GetMicSummary = function(mic) {
	return new Promise(function(resolve, reject) {
		MicController.findNextOrCreate(mic, function(err, instanceId) {
			if (instanceId === null) {
				// This mic will never occur again; skip it.
				return resolve(null);
			}
			// Fetch the actual instance from the database.
			Instance.findOne(instanceId, function(err, instance) {
				if (err) {
					reject(err);
				} else {
					// Fetch the name of the person who created the mic
					User.findById(mic.get('createdBy'), function(err, user) {
						if (err) {
							reject(err);
						} else {
							// Determine status of the next instance.
							// This is 'green' if the event is happening in less
							// than 24 hours, and 'yellow' if it is happening in more
							// than 24 hours.
							now = moment();
							var status = 'yellow';
							var begin = moment(instance.get('startDate')).subtract(24, 'hours');
							var end = moment(instance.get('endDate'));
							if (now.isBetween(begin, end)) {
								status = 'green';
							}
							data = {
								micId: mic.get('id'),
								status: status,
								venueLat: mic.get('venueLat'),
								venueLng: mic.get('venueLng'),
								micName: mic.get('micName'),
								createdBy: user.get('name'),
								meetingBasis: mic.get('meetingBasis'),
								startDate: instance.get('startDate'),
								endDate: instance.get('endDate')
							}
							resolve(data);
						}
					});
				}
			});
		});
	});
}

module.exports = MicController;
