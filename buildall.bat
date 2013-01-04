set cpath_run="C:\Program Files\Java\jre7\lib\log4j-1.2.17.jar;C:\workspace\TickerPlant\src"
set cpath_work="C:\workspace\TickerPlant"
set cpath_bin="C:\workspace\TickerPlant\bin"
set cpath_src="C:\workspace\TickerPlant\src"
copy C:\workspace\log4j.properties		%cpath_bin%
copy C:\workspace\log4j.xml			%cpath_bin%
copy C:\workspace\DataClient\Client.cfg		%cpath_src%\clientParser
copy C:\workspace\DataServer\Server.cfg		%cpath_src%\serverData
copy C:\workspace\DataClient\Client.cfg		%cpath_work%
copy C:\workspace\DataServer\Server.cfg		%cpath_work%
copy C:\workspace\DataServer\data\CtsData.raw	%cpath_work%
copy C:\workspace\DataServer\data\CqsData.raw	%cpath_work%
::
call "C:\Program Files\Java\jdk1.7.0_09\bin\javac.exe" -cp %cpath_run% -d %cpath_bin%  src\common\*.java
call "C:\Program Files\Java\jdk1.7.0_09\bin\javac.exe" -cp %cpath_run% -d %cpath_bin%  src\ClientParser\*.java
call "C:\Program Files\Java\jdk1.7.0_09\bin\javac.exe" -cp %cpath_run% -d %cpath_bin%  src\ServerData\*.java
