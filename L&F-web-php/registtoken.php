<?php 

	$dbc = mysqli_connect("localhost", "root", "kitproject", "lostnfound") or die("DB에 연결할 수 없습니다.");
	mysqli_query($dbc, "set names utf8");
 
	$androidid = $_POST['s_androidId'];
	$token = $_POST['s_token'];

	$query = "select androidid from tokenmanager where androidid='$androidid'";
	$result = mysqli_query($dbc, $query);
	
	if(mysqli_num_rows($result) == 1)    //안드로이드 고유 ID값 중복
	{   
		$query = "update tokenmanager set token='$token' where androidid='$androidid'";
		$result = mysqli_query($dbc, $query);
		mysqli_free_result($result);
	}
	else
	{
		$query = "insert into tokenmanager(androidid, token) values('$androidid', '$token')";
		$result = mysqli_query($dbc, $query);
 
		if(!$result)
			die("mysql query error");
		else
			echo 1;    //테이블 Insert 작업 성공
	}
?>