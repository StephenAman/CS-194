var express = require("express");
var app = express();

/**
 * "/api/users"
 *  POST: Create new user
 */
app.post("/api/users", function(req, res) {
});

/**
 * "/api/users/:userId"
 *  GET: Fetch user by id
 *  PUT: Update user by id
 *  DELETE: Delete user by id
 */
app.get("/api/users/:userId", function(req, res) {
});
app.put("/api/users/:userId", function(req, res) {
});
app.delete("/api/users/:userId", function(req, res) {
});

/**
 * "/api/mics"
 *  GET: Fetch all open mics
 *  POST: Create new open mic
 */
app.get("/api/mics", function(req, res) {
});
app.post("/api/mics", function(req, res) {
});

/**
 * "/api/mics/:micId"
 *  GET: Fetch open mic by id
 *  PUT: Update open mic by id
 *  DELETE: Delete open mic by id, and all dependent instances
 */
app.get("/api/mics/:micId", function(req, res) {
});
app.put("/api/mics/:micId", function(req, res) {
});
app.delete("/api/mics/:micId", function(req, res) {
});

/**
 * "/api/mics/:micId/instances"
 *  GET: Fetch all instances of this open mic
 *  POST: Create new instance
 */
app.get("/api/mics/:micId/instances", function(req, res) {
});
app.post("/api/mics/:micId/instances", function(req, res) {
});

/**
 * "/api/mics/:micId/instances/:instanceId"
 *  GET: Get open mic instance
 *  PUT: Update open mic instance
 */
app.get("/api/mics/:micId/instances/:instanceId", function(req, res) {
});
app.put("/api/mics/:micId/instances/:instanceId", function(req, res) {
});

app.listen(8080, function() {
	console.log('MicSpot server listening on port 8080.')
})
