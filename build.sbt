name := """knolPlay"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.1"

libraryDependencies ++= Seq( jdbc, cache, ws)
  								
libraryDependencies ++= Seq( 	 "postgresql"          %         "postgresql"			%       "9.1-901.jdbc4",
                                 "com.typesafe.play"   %%  		 "play-jdbc" 			% 	  	"2.3.1",
    							 "com.typesafe.play"   %%		 "play-json" 			% 	  	"2.3.1",
    							 "com.typesafe.play"   %% 		 "play-slick"		    % 	  	"0.8.1",
                                 "com.typesafe.slick"  %%        "slick"                %       "2.1.0",
                                 "org.webjars" 		   %% 		 "webjars-play" 	    % 		"2.3.0-2",
  								 "org.webjars" 		   %	     "bootstrap" 		    % 		"3.1.1-2",
  								 "org.webjars" 		   % 		 "bootswatch-cosmo"     % 		"3.3.1+2",
  								 "org.webjars" 		   % 		 "html5shiv" 			% 		"3.7.0",
  								 "org.webjars" 		   % 	     "respond" 				% 		"1.4.2",
  								     "com.github.tminglei" 	%% "slick-pg" 			% "0.8.1"
)
