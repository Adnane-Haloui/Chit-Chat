var express = require('express')
  , routes = require('./routes')
  , user = require('./routes/user')
  , http = require('http')
  , path = require('path')
  , firebase = require("firebase")
  , app = express()
  , twitterAPI = require('node-twitter-api');
var config = require('./config.json');
firebase.initializeApp({
  serviceAccount: config.service_account_file_name,
  databaseURL: config.firebase_database_url
});
var db=firebase.database();
var twitter = new twitterAPI({
    consumerKey: config.twitter_consumer_key,
    consumerSecret: config.twitter_secret_key,
});

app.configure(function(){
  app.set('port', 3005);
  app.use(express.logger('dev'));
  app.use(express.bodyParser());
  app.use(express.methodOverride());
  app.use(app.router);
});

app.configure('development', function(){
  app.use(express.errorHandler());
});
app.post('/token', function(req, res) {
  console.log(req.body);
    var ph_no = req.body.number,
        utoken = req.body.token,
        usecret = req.body.secret;
        var uid =ph_no;
    var additionalClaims = {
        token: utoken,
        secret: usecret
      };
      twitter.verifyCredentials(utoken,usecret,{}, function(error, data, response) {
    if (error) {
      res.json({"token":"auth_failed"});
    } else {
    var token = firebase.auth().createCustomToken(uid, additionalClaims);
    var ref=db.ref("users/"+ph_no);
    ref.once('value', function(snapshot) {
    if(snapshot.val() !== null){
    var result={"token":token,
      "about":snapshot.val().about,
      "name":snapshot.val().name,
      "profile_pic_timestamp":snapshot.val().profilePic_timestamp};
    res.json(result);         
    }
    else{
      var result={"token":token};
      res.json(result);
    }
  });

  }
});

});
app.get('/', routes.index);
app.get('/users', user.list);
http.createServer(app).listen(app.get('port'), function(){
  console.log("Express server listening on port " + app.get('port'));
});

