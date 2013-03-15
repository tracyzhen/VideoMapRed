<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<head>
  <title>Video.js | HTML5 Video Player</title>

  <!-- Chang URLs to wherever Video.js files will be hosted -->
  <link href="video/video-js.css" rel="stylesheet" type="text/css">
  <!-- video.js must be in the <head> for older IEs to work. -->
  <script src="video/video.js"></script>
  

  <!-- Unless using the CDN hosted version, update the URL to the Flash SWF -->
  <script>
    _V_.options.flash.swf = "video-js.swf";
  </script>


</head>
<body>
 <video id="example_video_1" class="video-js vjs-default-skin" controls preload="auto" width="640" height="264"
      poster="http://video-js.zencoder.com/oceans-clip.png"
      data-setup="{}">
     <!--<source src="http://video-js.zencoder.com/oceans-clip.webm" type='video/webm' />-->
     <source src="http://localhost:8080/fileupload/upload?getfile=web/web_01.webm" type='video/webm' /> 
    
    <track kind="captions" src="video/captions.vtt" srclang="en" label="English" />
  </video>
  <script src="video/videohandle.js"></script>
</body>
</html>