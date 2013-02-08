set lib1="C:\Program Files\Java\jre7\lib\log4j-1.2.17.jar"
set msq1="C:\Program Files\MySQL\Connector J 5.1.20.0\mysql-connector-java-5.1.20-bin.jar"
set bin1="C:\workspace\TickerPlant\bin"
set src1="C:\workspace\TickerPlant\src"
set hbn1="C:\workspace\hibernate-distribution-3.6.10.Final\lib\required\slf4j-api-1.6.1.jar"
set hbn2="C:\workspace\hibernate-distribution-3.6.10.Final\lib\required\antlr-2.7.6.jar"
set hbn3="C:\workspace\hibernate-distribution-3.6.10.Final\lib\required\commons-collections-3.1.jar"
set hbn4="C:\workspace\hibernate-distribution-3.6.10.Final\lib\required\dom4j-1.6.1.jar"
set hbn5="C:\workspace\hibernate-distribution-3.6.10.Final\lib\required\jta-1.1.jar"
set hbn6="C:\workspace\hibernate-distribution-3.6.10.Final\lib\jpa\hibernate-jpa-2.0-api-1.0.1.Final.jar"
set hbn7="C:\workspace\hibernate-distribution-3.6.10.Final\lib\bytecode\javassist\javassist-3.12.0.GA.jar"
set hbn8="C:\workspace\hibernate-distribution-3.6.10.Final\hibernate3.jar"

set cpath_run=%lib1%;%hbn1%;%hbn2%;%hbn3%;%hbn4%;%hbn5%;%hbn6%;%hbn7%;%hbn8%;%msq1%;%bin1%;%src1%;
cd C:\workspace\TickerPlant\src\client
call "C:\Program Files\Java\jre7\bin\java.exe" -cp %cpath_run% client.Client
cd C:\workspace\TickerPlant
