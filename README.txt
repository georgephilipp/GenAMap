The GenAMap software ecosystem has four components:

 - The GenAMap GUI client : This is the main software that is run locally on the users machine
 - A Web Server : A server that can receive requests from the client via the internet
 - An SQL database : A database that can be accessed by both the Web Server and the cluster.
 - A cluster: This is used for executing algorithms. The "ServerService" should be running there.

GenAMap is distributed in two different packages: The developer package and the user package. If you are reading this, you have obtained the developer package. The user package is available at http://sailing.cs.cmu.edu/genamap/join.html.

The developer package has the following items:

 - phps_genamap: These PHP scripts are hosted by the web server and are called from the GUI to communicate between the users local machine and the database. 
 - makeDistro.sh: Running this script will create the user package from the developer package. The user package is a subset of the developer package including mainly the executables / libraries and some of the documentation.
 - Documentation: This is a repository for various documents created over time describing how to use / develop GenAMap. Only some of the contents of this folder are contained in the user package.
 - DistroFiles: These files are only relevant for the user package and are used by makeDistro.sh. In particular, this folder contains another README which is meant primarily for GenAMap users but is also useful for GenAMap developers.
 - CodeServerService: This is the process run by the remote cluster that runs algorithms. This cluster must have access to the SQL database as well as the condor scheduler. 
 - CodeGUI: This is the GenAMap GUI client. Inside the CodeGUI folder, the folder `GenAMap' contain most of code that was written by GenAMap developers in the past. The other folders contain external libraries, some of which may have been edited / extended for use with GenAMap. 
 - Algorithms: This is the code that contains the algorithms that can be run by GenAMap. The folder contains a subfolder exe. That subfolder contains scripts. All other folders contain source code that can be compiled with the various makefiles in these folders.  

*****Developing the GUI*****

The important code in CodeGUI/GenAMap is a NetBeans project. Hence, it is convenient to continue developing GenAMap in NetBeans. Also, `JUNGGraphFix', `GPLClasses' and `libraries/javastat' are NetBeans projects. (I know that `GPLClasses' has already been edited through NetBeans.)

If you want to move the code off NetBeans, you can still load the projects into NetBeans to determine the dependencies amongst them and the dependencies on other libraries. These dependencies can be viewed in the `Projects' tab in NetBeans.

The best way to learn about the GUI is to start it and learn from the users perspective. There is an ample amount of documentation for users in the `Documentation' folder as well as on the GenAMap website: sailing.cs.cmu.edu/genamap. Unfortuantely, there is currently no documentation on the code itself (except for comments inside in the code).

*****Setting up the web server*****

The web server must host the PHP scripts in the `phps_genamap' folder. Currently, the address they are hosted under is http://cogito.ml.cmu.edu/test/genamap_beta/phps_genamap/<myPHPName>. If you want to change that address, you need to change the GUI code to call those php scripts under the new address. I am pretty sure the only place you need to change is line 41 of CodeGUI/GenAMapDist/src/realdata/Data1.java, but there might be others.

*****Setting up the SQL database*****

The database has to be accessible by both the web server and by the remote cluster. The web server accesses it through the php scripts, and the cluster accesses it via the service and also via the Algorithms. To change the location of the database, you need to edit the php scripts (of which there are only a few) to reflect that new address. Also, to ensure that the algorithms find the new location of the database, you must edit Algorithms/exe/SQLparms.txt. The four lines in that file represent address of the SQL server (including port), the name of the database, the username used to log into the server and the password used to log into the server. Editing Algorithms/exe/SQLparms.txt SHOULD make all the algorithms find the new database, but I have never tried to move the database so there may be issues. Finally, edit the database call in CodeServerService/service/SQLCommander.java.

Currently, the SQL server is running on cogito.ml.cmu.edu. To access it, log into cogito as `genamap' and type "mysql -u assocmap -p" or log into cogito as anyone else and type "mysql  --host=127.0.0.1  --port=4306  --user=assocmap  -p". The password is Thisisadumbpassword*. The name of the database is `geneassoc'. There is also a root account for this SQL server. Please contact authorized developers to obtain it.  

*****Setting up the remote cluster*****

Currently, I am running the server service out of /home/genamap on cogito.ml.cmu.edu. Here is how I set it up there:

ServerService
 - compile the code using the makefile in `CodeServerService'
 - create a folder called `service' and a folder called `log' in /home/genamap
 - move the contents of CodeServerService/dist to /home/genamap/service
 - move CodeServerService/startserver.sh to /home/genamap
 - run "/sbin/service genamap start" to start the service

Algorithms Code
 - move the `Algorithms' directory to /home/genamap (creating /home/genamap/Algorithms)
 - create an empty folder `jobs' under /home/genamap
 - move /home/genamap/Algorithms/execp.sh, /home/genamap/Algorithms/exerm.sh, /home/genamap/Algorithms/makecleanall.sh and the folder /home/genamap/Algorithms/r_libraries to /home/genamap (...one level up)
 - move /home/genamap/Algorithms/exe to /home/genamap/jobs
 - execute `makecleanall.sh' and then `execp.sh'

Reasons:

The server service is set up so that when cogito starts, the service is started automatically, alleviating the need for manual startup every time cogito is rebooted. /sbin/service is the manager for this procedure. "/sbin/service genamap start" in turn executes startserver.sh, which in turn starts the server.

I keep `execp.sh', `exerm.sh' and `makecleanall.sh' in the top level directory because I use them a lot. These scripts are just for convenience and you can move / change them easily. To move the `r_libraries' folder, you have to change all references to these libraries in the algorithm code as those references point directly to that folder (all the scripts that use these libraries are of form exe/*.r). The `jobs' folder will be used by the service to store all kinds of information when algorithms are executed. Whenever an algorithms is started in GenAMap, a new subdirectory is created in the `jobs' folder. Having the `exe' folder inside the jobs folder is a bit odd but a huge number of scripts make that assumption (all the jobs are started from some directory home/genamap/jobs/XXX and they call the scripts via ../exe/XXX). 

Note that on cogito, a lot of software is installed in the /opt directory. Explicit paths to programs like matlab, condor and R involving /opt are in several places, the most prominent being CodeServerService/service/Condor.java and Algorithms/exe/runmatlab.sh. That might actually be all of them, but I can't guarantee.

The remote cluster must be able to access the database. As described above under `Setting up the SQL database', you should edit Algorithms/exe/SQLparms.txt accordingly. 


--------

For LICENSE information see CodeGUI/GenAMapMain/LICENSE.txt















