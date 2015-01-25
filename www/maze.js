var DungeoneerMaze = function() {
  this.flagShow = false;
};

DungeoneerMaze.prototype.init = function(success,fail) {
	cordova.exec(function(message) { success( message ); } , function(error) { fail( error ); } , "MazePlugin" , "init" , [] );
};

DungeoneerMaze.prototype.show = function(success,fail) {
        var self = this;
        cordova.exec(function(message) { self.flagShow = true; success( message ); } , function(error) { fail( error ); } , "MazePlugin" , "show" , [] );
};

DungeoneerMaze.prototype.hide = function(success,fail) {
        var self = this;
        cordova.exec(function(message) { self.flagShow = false; success( message ); } , function(error) { fail( error ); } , "MazePlugin" , "remove" , [] );
};

DungeoneerMaze.prototype.forward = function(success,fail) {
	cordova.exec(function(message) { success( message ); } , function(error) { fail( error ); } , "MazePlugin" , "forward" , [] );
};

DungeoneerMaze.prototype.left = function(success,fail) {
	cordova.exec(function(message) { success( message ); } , function(error) { fail( error ); } , "MazePlugin" , "turnleft" , [] );
};

DungeoneerMaze.prototype.right = function(success,fail) {
	cordova.exec(function(message) { success( message ); } , function(error) { fail( error ); } , "MazePlugin" , "turnright" , [] );
};

module.exports = new DungeoneerMaze();



