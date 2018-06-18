<?php

	$dbc = mysqli_connect("localhost", "root", "kitproject", "lostnfound") or die("DB에 연결할 수 없습니다.");
	mysqli_query($dbc, "set names utf8");
 
	$id = $_POST['s_id'];
	$password = $_POST['s_password'];
	$androidid = $_POST['s_androidId'];
		
	$query = "select token from tokenmanager where androidid='$androidid'";
	$result = mysqli_query($dbc, $query);
	$row = mysqli_fetch_assoc($result);
	
	$token = $row['token'];
	
	$query = "select * from user where id='$id' and password=SHA('$password')";
	$result = mysqli_query($dbc, $query);

	if(mysqli_num_rows($result) == 1){
		mysqli_free_result($result);
		$query = "update user set token='$token' where id='$id'";
		$result = mysqli_query($dbc, $query);
		mysqli_free_result($result);
		echo 1;    //로그인 성공
	}
	else
		echo 0;    //로그인 실패
	
	mysqli_close($dbc);
?>