var Instance = ('../models/instance.js');
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
MicController.createMic = function() {
	// TODO(joachimr): Implement.
};

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
