<?php
#echo "Hello world";

$PARAM = array_merge($_GET,$_POST);

$usr = $PARAM['team'];
$pcd = $PARAM['passcode'];
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

	$loginquery = "SELECT passcode FROM team WHERE name=\"".$usr."\";";
	$result = mysql_query($loginquery);
}

if($myvar)
{
	if(mysql_numrows($result) > 0)
	{
		print "OK\n";
		$db = mysql_result($result,0,0);
		if(strcmp($db, $pcd)==0)
		{
			print "OK2";
		}
		else
		{
			print "VERY BAD!!!!";
		}
	}
	else
	{
		print "LOGIN FAILED\n";
		$myvar = 0;
	}
}

mysql_close();
php?>
