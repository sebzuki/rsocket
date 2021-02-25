https://www.vinsguru.com/rsocket-websocket-spring-boot/

CREATE TABLE person
(id uuid PRIMARY KEY,
last_name VARCHAR(255),
first_name VARCHAR(255));

zkServer.cmd
.\bin\windows\kafka-server-start.bat .\config\server.properties


.\bin\windows\kafka-topics.bat --zookeeper localhost:2181 --delete --topic test

.\bin\windows\kafka-topics.bat --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic test
