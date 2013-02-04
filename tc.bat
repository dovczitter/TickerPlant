::call buildall
set cpath_run="C:\Program Files\Java\jre7\lib\log4j-1.2.17.jar;C:\Program Files\MySQL\Connector J 5.1.20.0\mysql-connector-java-5.1.20-bin.jar;C:\workspace\TickerPlant\bin"
cd C:\workspace\TickerPlant\src\client
call "C:\Program Files\Java\jre7\bin\java.exe" -cp %cpath_run% client.Client
cd C:\workspace\TickerPlant
