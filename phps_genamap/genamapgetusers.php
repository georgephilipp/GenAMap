<?php
#echo "Hello world";

$PARAM = array_merge($_GET,$_POST);

$usr = $PARAM['user'];
$myvar = 1;

#My sql variables
$host = ":/export/userdb/mysql.sock";
$database = "geneassoc";
$user = "assocmap";
$password = "Thisisadumbpassword*";

if($myvar)
{
	mysql_connect($host, $user, $password);
	@mysql_select_db($database) or die("Unable to select database");

	$loginquery = "SELECT db FROM user WHERE uid=\"".$usr."\";";
	$result = mysql_query($loginquery);
}

if($myvar)
{
	if(mysql_numrows($result) > 0)
	{
		print "OK\n";
		print "OK2\n";
		$db = mysql_result($result,0,0);
		@mysql_select_db($db) or die("Unable to use this db");
	}
	else
	{
		print "LOGIN FAILED\n";
		$myvar = 0;
	}
}

mysql_close();
php?>
