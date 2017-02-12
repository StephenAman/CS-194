var moment = require('moment');

var Instance = require('../models/instance.js');
var Mic = require('../models/mic.js');

/**
 * MicController is responsible for handling API requests that rely upon
 * the Mic and Instance models. 
 */
var MicController = function() {};

/**
 * Returns a list of tuples containing all open mics. Each tuple has
 * the form (micId, nextInstanceStatus, lat, lng).
 */
MicController.getMics = function() {
	// TODO(joachimr): Implement.
};

/**
 * Returns a list of tuples containing all open mics in a given area.
 * Each tuple has the form (micId, nextInstanceStatus, lat, lng).
 */
MicController.getMicsByArea = function() {
	// TODO(joachimr): Implement.
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
			MicController.createNextInstance(mic);
			res.send();	
		}
	});
};

/**
 * Creates the next instance of a mic.
 */
MicController.createNextInstance = function(mic) {
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
			switch(mic.get('meetingBasis')) {
				case 'daily':
					date = date.add(1, 'days');
					break;
				case 'weekly':
					date = date.add(1, 'weeks');
					break;
				case 'biweekly':
					date = date.add(2, 'weeks');
					break;
				case 'monthly':
					date = date.add(1, 'months');
					break;
				default:
					console.log('Fatal error: invalid meetingBasis');
					return;
			}
		}
	}
	var instanceData = {
		'micId': mic.get('id'),
		'startDate': date.format('YYYY-MM-DD HH:mm:ss'),
		'endDate': date.add(mic.get('duration'), 'minutes')
			.format('YYYY-MM-DD HH:mm:ss'),
		'cancelled': false,
		'numSlots': mic.get('numSlots'),
		'setTime': mic.get('setTime'),
	};
	Instance.create(instanceData, function(err, instance) {
		if (err) {
			throw err;
		}
	});
}

MicController.canEdit = function(userId, micId) {
	// TODO(joachimr): Implement once Mic.findOne has been added.
	// Should fetch the mic and check whether userId == createdBy.
	return false;
}

/**
 * Returns a mic along with the next instance which has yet to occur.
 */
MicController.getMic = function() {
	// TODO(joachimr): Implement.
};

/**
 * Updates an open mic and associated instances.
 */
MicController.updateMic = function() {
	// TODO(joachimr): Implement.
};

/**
 * Deletes an open mic and associated instances.
 */
MicController.deleteMic = function() {
	// TODO(joachimr): Implement.
};

/**
 * Returns a mic instance and its signup list.
 */
MicController.getInstance = function(req, res) {
	console.log(req.hasEditPermissions);
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
			// Check that slot is in range
			if (req.body.slotNumber >= instance.numSlots) {
				res.status(400).send('Slot does not exist.');
				return;
			}
			// Check that slot is available
			if (instance.signups[req.body.slotNumber] !== null) {
				res.status(400).send('Slot has already been taken.');
				return;
			}
			// Check that user isn't already signed up for a different slot
			var found = false;
			for (var i = 0; i < instance.signups.length; i++) {
				if (instance.signups[i] != null && 
					instance.signups[i].userId === req.user.get('id')) 
				{
					res.status(400).send('You have already signed up for a slot.');
					return;
				}	
			}

			// Query the DB
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
			// Check that slot is in range
			var slot = req.body.slotNumber;
			if (slot >= instance.numSlots) {
				res.status(400).send('Slot does not exist.');
				return;
			}
			// Check that slot is not available
			if (instance.signups[slot] === null) {
				res.status(400).send('Slot is already free.');
				return;
			}
			// Check that user is authorized
			if (!req.hasEditPermissions &&
				req.user.get('id') !== instance.signups[slot].userId) 
			{
				res.status(403).send();
				return;
			}

			// Query the DB
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
	// TODO(joachimr): Implement.
};

module.exports = MicController;
