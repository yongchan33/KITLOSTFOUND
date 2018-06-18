<?php

	$dbc = mysqli_connect("localhost", "root", "kitproject", "lostnfound") or die("DB에 연결할 수 없습니다.");
	mysqli_query($dbc, "set names utf8");
	
	$id = $_POST['s_id'];    //로그인한 아이디
			$no_comment = $_POST['no_comment'];
			$no_notice = $_POST['no_notice'];
			$content = $_POST['s_content'];
			$date = $_POST['s_date'];
	$no_notice = $_POST['no_notice'];

	$query = mysqli_query($dbc, "delete from comment where no_notice='$no_notice'");
	
	mysqli_close($dbc);
?>