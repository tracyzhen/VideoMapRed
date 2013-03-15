     var myFunc = function(){
	   var myPlayer = _V_("example_video_1");
	   myPlayer.src("http://video-js.zencoder.com/oceans-clip.webm");
	   myPlayer.play();
	  // Do something when the event is fired
	 }; 
     _V_("example_video_1").ready(function(){

	 // alert("here");
	  var myPlayer = this;
//	  myPlayer.addEvent("ended",myFunc);

	  // EXAMPLE: Start playing the video.
//	  myPlayer.play();

	});
