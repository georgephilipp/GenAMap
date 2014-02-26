<?php
#echo "Hello world";

$PARAM = array_merge($_GET,$_POST);

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

	$loginquery = "SELECT name FROM team";
	$result = mysql_query($loginquery);
}

if($myvar)
{
	if(mysql_numrows($result) > 0)
	{
		print "OK\n";

        	$num = mysql_numrows($result);

        	$v = mysql_num_fields($result);
        	$i=0;
        	if($result)
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
                        	echo mysql_result($result,$i,$j)."  ";
                        	$j++;
                	}
                	echo "\n";
                	$i++;
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
