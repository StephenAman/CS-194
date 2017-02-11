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
	};
	Instance.create(instanceData, function(err, instance) {
		if (err) {
			throw err;
		}
	});
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
MicController.getInstance = function() {
	// TODO(joachimr): Implement.
};

/**
 * Updates a mic instance and its signup list.
 */
MicController.updateInstance = function() {
	// TODO(joachimr): Implement.
};

module.exports = MicController;
