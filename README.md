VideoMapRed
===========

A video MS based on hadoop platform 

based on hadoop ,system can handle many large videos : transcode , video split, get the keyframe and so on.

We define Video InputFormat and OutputFormat, so hadoop Job tracker can split the large video.

Web server accept the request, launch the hadoop job .

using ffmpeg to handle videos.

environment: Linux ,hadoop 1.0.4 , tomcat 6.0

version 0.1

TODO: add a search engine for user .
    
