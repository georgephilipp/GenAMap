<?php
#echo "Hello world";

$PARAM = array_merge($_GET,$_POST);

$usr = $PARAM['user'];
$pwd = $PARAM['pwd'];
if(!is_null($usr) && !is_null($pwd))
{
	$myvar = 1;
	if(strlen($usr) > 20 || strlen($pwd) > 25)
	{
		$myvar = 0;
	}
#	echo "c1";
}

#My sql variables
$host = "localhost";
$database = "geneassoc";
$user = "assocmap";
$password = "Thisisadumbpassword*";

if($myvar)
{
	mysql_connect(localhost, $user, $password);
	@mysql_select_db($database) or die("Unable to select database");

	$loginquery = "SELECT db FROM userid WHERE uid=\"".$usr."\" AND password=PASSWORD(\"".$pwd."\");";
	$result = mysql_query($loginquery);
}

if($myvar)
{
	if(mysql_numrows($result) > 0)
	{
		print "OK\n";
	}
	else
	{
		print "LOGIN FAILED\n";
		$myvar = 0;
	}
}

$dir = $PARAM['dir'];
$file = $PARAM['file'];
$text = $PARAM['text'];
if($myvar && !is_null($dir) && !is_null($file) && !is_null($text))
{
	mkdir("/home/rcurtis/jobs/".$dir);
	$ourfilehandle = fopen("/home/rcurtis/jobs/".$dir."/".$file,'w') or die ("Can't open file ".$dir."/".$file);
	fwrite($ourfilehandle, $text);
	fclose($ourfilehandle);
	chmod("/home/rcurtis/jobs/".$dir."/".$file,0777);
	chmod("/home/rcurtis/jobs/".$dir,0777);
	echo "/home/rcurtis/jobs/";
}

mysql_close();
php?>
