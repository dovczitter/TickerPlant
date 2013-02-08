set cpath_work="C:\workspace\TickerPlant"
set cpath_bin="%cpath_work%\bin"
set cpath_src="%cpath_work%\src"
set log_path="C:\Program Files\Java\jre7\lib\log4j-1.2.17.jar"
set cpath_run=%log_path%;"C:\workspace\hibernate-distribution-3.6.10.Final\hibernate3.jar";"C:\Program Files\MySQL\Connector J 5.1.20.0\mysql-connector-java-5.1.20-bin.jar";".\..\hibernate-distribution-3.6.10.Final\lib\bytecode\javassist\javassist-3.12.0.GA.jar";".\..\hibernate-distribution-3.6.10.Final\lib\required";".\..\hibernate-distribution-3.6.10.Final\lib\jpa\hibernate-jpa-2.0-api-1.0.1.Final.jar";%cpath_src%
::
copy C:\workspace\log4j.properties		%cpath_bin%
copy C:\workspace\log4j.xml			%cpath_bin%
copy C:\workspace\TickerPlant\cfg\Client.cfg	%cpath_src%\client
copy C:\workspace\TickerPlant\cfg\Client.cfg	%cpath_bin%\client
copy C:\workspace\TickerPlant\cfg\Client.cfg	%cpath_work%
copy C:\workspace\TickerPlant\cfg\Server.cfg	%cpath_src%\server
copy C:\workspace\TickerPlant\cfg\Server.cfg	%cpath_bin%\server
copy C:\workspace\TickerPlant\cfg\Server.cfg	%cpath_work%
copy C:\workspace\TickerPlant\data\*.raw	%cpath_work%
copy C:\workspace\TickerPlant\xml\*.xml		%cpath_src%\client\xml
copy C:\workspace\TickerPlant\xml\*.xml		%cpath_bin%\client\xml
::
call "%JAVA_HOME%\bin\javac.exe" -cp %cpath_run% -d %cpath_bin%  src\common\*.java
call "%JAVA_HOME%\bin\javac.exe" -cp %cpath_run% -d %cpath_bin%  src\Client\*.java
call "%JAVA_HOME%\bin\javac.exe" -cp %cpath_run% -d %cpath_bin%  src\Server\*.java
