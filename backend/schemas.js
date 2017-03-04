var MobileAuth = {
	type: 'object',
	properties: {
		id: {
			type: 'string',
			required: true
		},
		token: {
			type: 'string',
			required: true
		}
	}
}

var CreateMic = {
	type: 'object',
	properties: {
		micName: {
			type: 'string',
			required: true,
			maxLength: 255,
		},
		venueName: {
			type: 'string',
			required: true,
			maxLength: 255,
		},
		venueAddress: {
			type: 'string',
			required: true,
			maxLength: 255,
		},
		venueLat: {
			type: 'float',
			required: true,
		},
		venueLng: {
			type: 'float',
			required: true,
		},
		startDate: {
			type: 'string',
			format: 'date-time',
			required: true,
		},
		duration: {
			type: 'int',
			required: true,
			minimum: 0,
		},
		meetingBasis: {
			type: 'string',
			required: false,
			maxLength: 45,
		},
		setTime: {
			type: 'int',
			required: true,
			minimum: 0,
		},
		numSlots: {
			type: 'int',
			required: true,
			minimum: 0,
		}
	}
};

var UpdateInstance = {
	type: 'object',
	properties: {
		eventDate: {
			type: 'object',
			properties: {
				startDate: {
					type: 'string',
					format: 'date-time',
					required: true,
				},
				duration: {
					type: 'int',
					required: true,
					minimum: 0,
				},
				updateDefaultStartDate: {
					type: 'int',
					required: true,
					minimum: 0,
					maximum: 0,
				}
			},
			required: false,
		},
		signupsOpenDate: {
			type: 'string',
			format: 'date-time',
			required: false,
		},
		numSlots: {
			type: 'int',
			required: false,
			minimum: 0,
		},
		setTime: {
			type: 'int',
			required: false,
			minimum: 0,
		},
		cancelled: {
			type: 'int',
			required: false,
			minimum: 0,
			maximum: 1,
		},
		meetingBasis: {
			type: 'string',
			required: false,
			maxLength: 45,
		}
	}
};

var Signup = {
	type: 'object',
	properties: {
		slotNumber: {
			type: 'int',
			required: true,
			minimum: 0,
		}
	}
};

var CreateReview = {
	type: 'object',
	properties: {
		reviewText: {
			type: 'string',
			required: true,
		}
	}
};

exports.MobileAuth = MobileAuth;
exports.CreateMic = CreateMic;
exports.Signup = Signup;
exports.CreateReview = CreateReview;
exports.UpdateInstance = UpdateInstance;
