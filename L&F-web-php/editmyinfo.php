<?php

	$dbc = mysqli_connect("localhost", "root", "kitproject", "lostnfound") or die("DB에 연결할 수 없습니다.");
	mysqli_query($dbc, "set names utf8");

	if(isset($_POST['editnickname'])){
		$id = $_POST['s_id'];
		$originnickme = $_POST['s_originnickname'];
		$newnickname = mysqli_real_escape_string($dbc, trim($_POST['s_newnickname']));
		
		$query = "select nickname from user where nickname='$newnickname'";
		$result = mysqli_query($dbc, $query);
		
		if(mysqli_num_rows($result))
		{
			echo 3;    //닉네임 중복
			mysqli_free_result($result);
		}
		else
		{
			$query1 = mysqli_query($dbc, "update user set nickname='$newnickname' where id='$id'");
			$query2 = mysqli_query($dbc, "update findnotice set writer='$newnickname' where writer='$originnickme'");
			$query3 = mysqli_query($dbc, "update comment set writer='$newnickname' where writer='$originnickme'");
			echo 1;  //닉네임 변경 성공
		}
	}
	else if(isset($_POST['editpassword'])){
		$id = $_POST['s_id'];
		$originpassword = mysqli_real_escape_string($dbc, trim($_POST['s_originpassword']));
		$newpassword = mysqli_real_escape_string($dbc, trim($_POST['s_newpassword']));
		$newpasscheck = mysqli_real_escape_string($dbc, trim($_POST['s_newpasscheck']));
		
		$query = "select * from user where id='$id' and password=SHA('$originpassword')";
		$result = mysqli_query($dbc, $query);
		
		$row = mysqli_num_rows($result);
		if($row == 0){
			echo 0;        //기존 비밀번호가 일치하지 않음
		}
		else{
			$query = mysqli_query($dbc, "update user set password=SHA('$newpassword'), passcheck=SHA('$newpasscheck') where id='$id'");
			echo 1;        //기존 비밀번호가 일치함
		}
	}
	mysqli_close($dbc);
?>