var express = require('express');
var jwt = require('jsonwebtoken');
var passport = require('passport');
var passportFacebook = require('passport-facebook');
var passportJwt = require('passport-jwt');

var config = require('./config.js');
var database = require('./database.js');
var User = require('./models/user.js');

var app = express();

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
				{ id: user.id }, config.jwt_secret
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
 * Require authentication for all API calls
 */
app.all('/api/*', passport.authenticate('jwt', { session: false }), 
	function (req, res, next) {
		next();
	}
);

/**
 * '/api/users'
 *  POST: Create new user
 */
app.post('/api/users', function(req, res) {
});

/**
 * '/api/users/:userId'
 *  GET: Fetch user by id
 *  PUT: Update user by id
 *  DELETE: Delete user by id
 */
app.get('/api/users/:userId', function(req, res) {
	User.findById(req.params.userId, function(err, user) {
		if (err) {
			res.status(404).send();
		} else {
			res.send(user);	
		}
	});
});

app.put('/api/users/:userId', function(req, res) {
});
app.delete('/api/users/:userId', function(req, res) {
});

/**
 * '/api/mics'
 *  GET: Fetch all open mics
 *  POST: Create new open mic
 */
app.get('/api/mics', function(req, res) {
});
app.post('/api/mics', function(req, res) {
});

/**
 * '/api/mics/:micId'
 *  GET: Fetch open mic by id
 *  PUT: Update open mic by id
 *  DELETE: Delete open mic by id, and all dependent instances
 */
app.get('/api/mics/:micId', function(req, res) {
});
app.put('/api/mics/:micId', function(req, res) {
});
app.delete('/api/mics/:micId', function(req, res) {
});

/**
 * '/api/mics/:micId/instances'
 *  GET: Fetch all instances of this open mic
 *  POST: Create new instance
 */
app.get('/api/mics/:micId/instances', function(req, res) {
});
app.post('/api/mics/:micId/instances', function(req, res) {
});

/**
 * '/api/mics/:micId/instances/:instanceId'
 *  GET: Get open mic instance
 *  PUT: Update open mic instance
 */
app.get('/api/mics/:micId/instances/:instanceId', function(req, res) {
});
app.put('/api/mics/:micId/instances/:instanceId', function(req, res) {
});

app.listen(8080, function() {
	console.log('MicSpot server listening on port 8080.')
})
