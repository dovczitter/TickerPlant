::cd \workspace\TickerPlant
if %1 == build  call buildall
if %1 == client call tc
if %1 == server call ts