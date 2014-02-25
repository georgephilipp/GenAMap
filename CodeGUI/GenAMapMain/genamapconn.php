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
		$db = mysql_result($result,0,0);
		@mysql_select_db($db) or die("Unable to use this db");
	}
	else
	{
		print "LOGIN FAILED\n";
		$myvar = 0;
	}
}

$query = $PARAM['query'];
if($myvar && !is_null($query))
{
	$execute = mysql_query($query) or die("Error! ".mysql_error());
	$num = mysql_numrows($execute);

	$v = mysql_num_fields($execute);
	$i=0;
	if($execute)
	{
		echo "\n";
	}
	else
	{
		echo mysql_error()."\n";
	}
	while($i<$num)
	{
		$j=0;
		while($j<$v)
		{
			echo mysql_result($execute,$i,$j)." ";
			$j++;
		}
		echo "\n";
		$i++;
	}
}

mysql_close();
php?>
