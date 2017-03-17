var express = require('express');
var validate = require('express-jsonschema').validate;
var bodyParser = require('body-parser');
var jwt = require('jsonwebtoken');
var passport = require('passport');
var passportFacebook = require('passport-facebook');
var passportJwt = require('passport-jwt');
var request = require('request');

var config = require('./config.js');
var database = require('./database.js');
var MicController = require('./controllers/mics.js');
var User = require('./models/user.js');
var schemas = require('./schemas.js');

var app = express();

app.use(bodyParser.json());
app.use(passport.initialize());

/**
 * Configure Facebook authentication
 */
var FacebookStrategy = passportFacebook.Strategy;
passport.use(new FacebookStrategy(
	{
		clientID: config.fb_client_id,
		clientSecret: config.fb_client_secret,
		callbackURL: config.fb_callback_url,
	},
	function(accessToken, refreshToken, profile, done) {
		User.findOrCreate(
		{
			id: profile.id, 
			name: profile.displayName, 
		},
		function(err, user) {
			console.log(user);
			if (err) {
				return done(err, false);
			}
			var token = jwt.sign(
				{ id: user.get('id') }, config.jwt_secret
			);
			return done(null, {token: token});
		});
	}
));

app.get('/auth/facebook', passport.authenticate('facebook', { session: false }));
app.get('/auth/facebook/callback',
	passport.authenticate('facebook', { session: false }), function(req, res) {
		res.send(req.user);
	}
);

/**
 * Configure JWT authentication
 */
var JwtStrategy = passportJwt.Strategy;
var ExtractJwt = passportJwt.ExtractJwt;
var jwtOptions = {};
jwtOptions.jwtFromRequest = ExtractJwt.fromAuthHeader();
jwtOptions.secretOrKey = config.jwt_secret;

passport.use(new JwtStrategy(jwtOptions, function(jwtPayload, done) {
	User.findById(jwtPayload.id, function(err, user) {
		if (err) {
			return done(err, false);
		}
		if (user) {
			done(null, user);
		} else {
			done(null, false);
		}
	});
}));

/**
 * Temporary routes to test authentication schemes
 * TODO: Remove these once API is implemented
 */
app.get('/', function(req, res) {
	res.send('<a href="/auth/facebook">Login with Facebook</a>');
});

app.get('/secret', passport.authenticate('jwt', { session: false }), function(req, res) {
	res.send('You are logged in!');
});

/**
 * This function is used by the Android client to log users in.
 * POST: Exchange a FB token with a JWT.
 *
 * TODO: Make this callback-mess nicer.
 */
app.post('/auth/mobile', validate({body: schemas.MobileAuth}), function(req, res) {
	// Get app token from FB
	request(
		''.concat('https://graph.facebook.com/oauth/access_token?client_id=', 
			    config.fb_client_id, '&client_secret=', config.fb_client_secret, 
			    '&grant_type=client_credentials'),
		function(error, response, body) {
			if (error) {
				return res.status(500).send();
			}
			access_token = body.substring(13);

			// Use app token to inspect user-provided token
			request(
				''.concat('https://graph.facebook.com/debug_token?input_token=',
						  req.body.token, '&access_token=', access_token),
				function(error, response, body) {
					if (error) {
						return res.status(500).send();
					}
					var data = JSON.parse(body);
					// Reject if token or id is invalid
					if (!data.data.is_valid || data.data.user_id !== req.body.id) {
						return res.status(401).send();
					}

					// Retrieve the name of the user
					request(
						'https://graph.facebook.com/v2.8/me?access_token=' + req.body.token,
						function(error, response, body) {
							if (error) {
								return res.status(500).send();
							}
							name = JSON.parse(body).name;
							User.findOrCreate({id: req.body.id, name: name}, function(err, user) {
								if (err) {
									return res.status(500).send();
								}

								// Create, sign and return JWT
								var token = jwt.sign(
									{ id: user.get('id') }, config.jwt_secret
								);
								res.set('Content-Type', 'application/json');
								return res.send('{"jwt": "' + token + '"}');
							});
						}
					);
				}
			);
		}
	);
});

/**
 * Require authentication for all API calls
 */
app.all('/api/*', passport.authenticate('jwt', { session: false }), 
	function (req, res, next) {
		next();
	}
);

/**
 * Set flag to indicate whether user has edit privileges to mic page
 */
app.all('/api/mics/:micId/*', function (req, res, next) {
	MicController.canEdit(
		req.user.get('id'),
		req.params.micId, 
		function(result) {
			req.hasEditPermissions = result;
			next();
		}
	);
});

/**
 * '/api/users'
 *  POST: Create new user
 */
app.post('/api/users', function(req, res) {
});

/**
 * '/api/users/:userId'
 *  GET: Fetch user by id
 *  PUT: Update user firebaseToken or location.
 */
app.get('/api/users/:userId', function(req, res) {
	User.findById(req.params.userId, function(err, user) {
		if (err) {
			res.status(404).send();
			return;
		}
		result = {
			id: user.get('id'),
			name: user.get('name')
		};
		res.send(result);
	});
});

app.put('/api/users/:userId', validate({body: schemas.UpdateUser}), function(req, res) {
	// Check that you are updating your own user
	if (req.params.userId != req.user.get("id")) {
		res.status(401).send();
		return;
	}

	// Update and save user
	if (req.body.lastLocation) {
		req.user.set('lastLocationLat', req.body.lastLocation.lastLocationLat);
		req.user.set('lastLocationLng', req.body.lastLocation.lastLocationLng);
	}
	if (req.body.firebaseToken) {
		req.user.set('firebaseToken', req.body.firebaseToken);
	} 
	req.user.save(function(err) { if (err) throw err; });
	res.send();
});

/**
 * '/api/mics'
 *  GET: Fetch all open mics
 *  POST: Create new open mic
 */
app.get('/api/mics', function(req, res) {
	MicController.getMics(req, res);
});

app.post('/api/mics', validate({body: schemas.CreateMic}), function(req, res) {
	MicController.createMic(req, res);
});

/**
 * '/api/mics/:micId'
 *  GET: Fetch open mic by id
 *  PUT: Update open mic by id
 *  DELETE: Delete open mic by id, and all dependent instances
 */
app.get('/api/mics/:micId', function(req, res) {
	MicController.getMic(req, res);
});
app.put('/api/mics/:micId', function(req, res) {
});
app.delete('/api/mics/:micId', function(req, res) {
});

/**
 * '/api/mics/:micId/instances'
 *  GET: Fetch all instances of this open mic
 */
app.get('/api/mics/:micId/instances', function(req, res) {
});

/**
 * '/api/mics/:micId/instances/:instanceId'
 *  GET: Get open mic instance
 *  PUT: Update open mic instance
 */
app.get('/api/mics/:micId/instances/:instanceId', function(req, res) {
	MicController.getInstance(req, res);
});
app.put('/api/mics/:micId/instances/:instanceId', validate({body: schemas.UpdateInstance}), function(req, res) {
	MicController.updateInstance(req, res);
});

/**
 * '/api/mics/:micId/instances/:instanceId/signups'
 *  POST: Sign up to slot
 *  DELETE: Delete sign up
 */
app.post('/api/mics/:micId/instances/:instanceId/signups', validate({body: schemas.Signup}), function(req, res) {
	MicController.createSignup(req, res);
});
app.delete('/api/mics/:micId/instances/:instanceId/signups', validate({body: schemas.Signup}), function(req, res) {
	MicController.deleteSignup(req, res);
});

/**
 * '/api/mics/:micId/reviews'
 *  GET: Get reviews for this mic
 *  POST: Create new review
 */
app.get('/api/mics/:micId/reviews', function(req, res) {
	MicController.getReviews(req, res);
});
app.post('/api/mics/:micId/reviews', validate({body: schemas.CreateReview}), function(req, res) {
	MicController.createReview(req, res);
});

/**
 * Handle invalid JSON requests
 */
app.use(function(err, req, res, next) {
 	if (err.name === 'SyntaxError') {
 		res.status(400).send('SyntaxError: Request contains invalid JSON.');
 	}
 	else if (err.name === 'JsonSchemaValidation' &&
 	   (req.xhr || req.get('Content-Type') == 'application/json')) {
        res.status(400);
        var responseData = {
        	statusText: 'Bad Request',
        	jsonSchemaValidation: true,
       	};
        res.json(err.validations.body);
    } 
    else {
        next(err);
    }
});

app.listen(8080, function() {
	console.log('MicSpot server listening on port 8080.')
})
