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

	$loginquery = "SELECT max(id) as a FROM team;";
	$result = mysql_query($loginquery);
}

if($myvar)
{
	if(mysql_numrows($result) > 0)
	{
		print "OK\n";
		$db = mysql_result($result,0,0);

		$db=$db+1;
		$cd = "1";
		switch($db)
		{
			case 2: $cd = "2"; break;
                        case 3: $cd = "3"; break;
                        case 4: $cd = "4"; break;
                        case 5: $cd = "5"; break;
                        case 6: $cd = "6"; break;
                        case 7: $cd = "7"; break;
                        case 8: $cd = "8"; break;
                        case 9: $cd = "9"; break;
                        case 10: $cd = "0"; break;
                        case 11: $cd = "a"; break;
                        case 12: $cd = "b"; break;
                        case 13: $cd = "c"; break;
                        case 14: $cd = "d"; break;
                        case 15: $cd = "e"; break;
                        case 16: $cd = "f"; break;
                        case 17: $cd = "g"; break;
                        case 18: $cd = "h"; break;
                        case 19: $cd = "i"; break;
                        case 20: $cd = "j"; break;
                        case 21: $cd = "k"; break;
                        case 22: $cd = "l"; break;
                        case 23: $cd = "m"; break;
                        case 24: $cd = "n"; break;
                        case 25: $cd = "o"; break;
                        case 26: $cd = "p"; break;
                        case 27: $cd = "q"; break;
                        case 28: $cd = "r"; break;
                        case 29: $cd = "s"; break;
                        case 30: $cd = "t"; break;
                        case 31: $cd = "u"; break;
                        case 32: $cd = "v"; break;
                        case 33: $cd = "w"; break;
                        case 34: $cd = "x"; break;
                        case 35: $cd = "y"; break;
                        case 36: $cd = "z"; break;
                        case 37: $cd = "A"; break;
                        case 38: $cd = "B"; break;
                        case 39: $cd = "C"; break;
                        case 40: $cd = "D"; break;
                        case 41: $cd = "E"; break;
                        case 42: $cd = "F"; break;
                        case 43: $cd = "G"; break;
                        case 44: $cd = "H"; break;
                        case 45: $cd = "I"; break;
                        case 46: $cd = "J"; break;
                        case 47: $cd = "K"; break;
                        case 48: $cd = "L"; break;
                        case 49: $cd = "M"; break;
                        case 50: $cd = "N"; break;
                        case 51: $cd = "O"; break;
                        case 52: $cd = "P"; break;
                        case 53: $cd = "Q"; break;
                        case 54: $cd = "R"; break;
                        case 55: $cd = "S"; break;
                        case 56: $cd = "T"; break;
                        case 57: $cd = "U"; break;
                        case 58: $cd = "V"; break;
                        case 59: $cd = "W"; break;
                        case 60: $cd = "X"; break;
                        case 61: $cd = "Y"; break;
                        case 62: $cd = "Z"; break;

		}
		
		$query = "INSERT INTO team (name, passcode, keycode) VALUES (\"".$usr."\",\"".$pcd."\",\"".$cd."\");";
		$result = mysql_query($query);
		#print($query);
	}
	else
	{
		print "LOGIN FAILED\n";
		$myvar = 0;
	}
}

mysql_close();
php?>
