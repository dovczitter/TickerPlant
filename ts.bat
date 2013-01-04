::call buildall
set cpath="C:\Program Files\Java\jre7\lib\log4j-1.2.17.jar;C:\workspace\TickerPlant\bin"
cd C:\workspace\TickerPlant\src\serverData
call "C:\Program Files\Java\jre7\bin\java.exe" -cp %cpath% serverData.ServerData
cd C:\workspace\TickerPlant
