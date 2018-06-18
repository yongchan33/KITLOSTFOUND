<?php

	$dbc = mysqli_connect("localhost", "root", "kitproject", "lostnfound") or die("DB에 연결할 수 없습니다.");
	mysqli_query($dbc, "set names utf8");
	
	$no_notice = $_POST['no_notice'];
	
	if(isset($_POST['findnotice'])){ // 물건찾기, 주인찾기 게시물

		$query = mysqli_query($dbc, "delete from findnotice where no='$no_notice'");
		$query = mysqli_query($dbc, "delete from comment where no_notice='$no_notice'");
	
		mysqli_close($dbc);
	}else if(isset($_POST['sellnotice'])){ // 벼룩시장 게시물
	
		$query = mysqli_query($dbc, "delete from sellnotice where no='$no_notice'");
		$query = mysqli_query($dbc, "delete from comment where no_notice='$no_notice'");
	
		mysqli_close($dbc);
	}
?>