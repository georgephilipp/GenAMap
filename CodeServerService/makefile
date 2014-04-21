build:
	javac service/*.java
	jar cfm dist/service.jar manifest.txt service/*.class
	cp lib/mysql-connector-java-5.1.10/mysql-connector-java-5.1.10-bin.jar dist/mysql-connector.jar

exe: build
	java -jar dist/service.jar geneassoc

gam1: build
	java -jar dist/service.jar genamap_beta1
gam2: build
	java -jar dist/service.jar genamap_beta2

clean:
	touch service/dummy.class
	rm -f service/*.class
	touch dist/service.jar
	rm -f dist/service.jar
