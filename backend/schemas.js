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
		},
		meetingBasis: {
			type: 'string',
			required: false,
			maxLength: 45,
		},
		setTime: {
			type: 'int',
			required: true,
		},
		numSlots: {
			type: 'int',
			required: true,
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
}

exports.MobileAuth = MobileAuth;
exports.CreateMic = CreateMic;
exports.Signup = Signup;
