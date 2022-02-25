cd sound/java-opus-wrapper
call mvn clean package install

cd ../utils
call mvn clean package install

cd ..
call mvn clean package install

cd ../vocal-common/communication
call mvn clean package install

cd ../messenger
call mvn clean package install

cd ..
call mvn clean package install

cd ..
call mvn clean package install

pause