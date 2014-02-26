<?php
#echo "Hello world";

$PARAM = array_merge($_GET,$_POST);

$usr = $PARAM['user'];
$pwd = $PARAM['pwd'];
$org = $PARAM['org'];
$email = $PARAM['email'];
$name = $PARAM['name'];
$team = $PARAM['team'];

if(!is_null($usr) && !is_null($pwd) 
 && !is_null($org) && !is_null($email) &&
   !is_null(name) && !is_null($team))
{
	$myvar = 1;
	if(strlen($usr) > 20 || strlen($pwd) > 25)
	{
		$myvar = 0;
	}
#	echo "c1";
}

#My sql variables
$host = ":/export/userdb/mysql.sock";
$database = "geneassoc";
$user = "assocmap";
$password = "Thisisadumbpassword*";

if($myvar)
{
	mysql_connect($host, $user, $password);
	@mysql_select_db($database) or die("Unable to select database");

	$loginquery = "SELECT id FROM team WHERE name=\"".$team."\";";
	$result = mysql_query($loginquery);
}

if($myvar)
{
	if(mysql_numrows($result) > 0)
	{
		print "OK\n";
		$tid = mysql_result($result,0,0);
	}
	else
	{
		print "LOGIN FAILED\n";
		$myvar = 0;
	}
}

$query = "INSERT INTO user (uid, password, org, email, name, teamid) VALUES(\"".$usr."\",PASSWORD(\"".$pwd."\"),\"".$org."\",\"".$email."\",\"".$name."\",".$tid.");";
#print $query;
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
			echo mysql_result($execute,$i,$j)."  ";
			$j++;
		}
		echo "\n";
		$i++;
	}
}

mysql_close();
php?>
