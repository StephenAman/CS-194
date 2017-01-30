var config = require('./config.js');
var mysql = require('mysql')

var database;

/**
 * Returns a MySQL database connection
 */
function connect() {
	if (!database) {
		database = mysql.createConnection({
			host 		: config.mysql_host,
			user 		: config.mysql_user,
			password 	: config.mysql_password,
			database    : config.mysql_database,
		});
		database.connect(function(err) {
			if (err) {
				console.error('Error connecting to MySQL database:\n' + err.stack);
				return;
			}
			console.log('Connected to MySQL database.');
		});
	}
	return database;
}

module.exports = connect();
