<?php
	$dbc = mysqli_connect("localhost", "root", "kitproject", "lostnfound") or die("DB에 연결할 수 없습니다.");
	mysqli_query($dbc, "set names utf8");

	$id = $_POST['s_id'];
	$originpassword = mysqli_real_escape_string($dbc, trim($_POST['s_originpassword']));
			
	$query = "select nickname from user where id='$id' and password=SHA('$originpassword')";
	$result = mysqli_query($dbc, $query);
	
	$row = mysqli_fetch_assoc($result);
	
	$writer = $row['nickname'];
	
	$row = mysqli_num_rows($result);
	if($row == 0){
		echo 0;        //비밀번호 틀림
	}
	else
	{
		$query = mysqli_query($dbc, "delete from user where id='$id'");
		$query = mysqli_query($dbc, "update findnotice set visible='0' where writer='$writer'");
		$query = mysqli_query($dbc, "update comment set visible='0' where writer='$writer'");
		echo 1;   // 기존 비밀번호 맞음
	}
	
	mysqli_close($dbc);
?>