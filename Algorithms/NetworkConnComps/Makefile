all:
	javac network/NetConCompsDriver.java network/SubNet.java
	jar -cvfm conncomp.jar MANIFEST.MF network/NetConCompsDriver.class network/SubNet.class

clean: 
	touch network/dummy.class
	rm -rf network/*.class 
	touch conncomp.jar
	rm conncomp.jar
