<?php

	$dbc = mysqli_connect("localhost", "root", "kitproject", "lostnfound") or die("DB에 연결할 수 없습니다.");

	mysqli_query($dbc, "set names utf8");

	$id = $_POST['s_id'];

	$query = "select nickname, email from user where id='$id'";
	$result = mysqli_query($dbc, $query);
	$row = mysqli_fetch_assoc($result);
	
	echo "{\"nickname\":\"$row[nickname]\",\"email\":\"$row[email]\"}";

	mysqli_close($dbc);
?>