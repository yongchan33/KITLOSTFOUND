<?php

	$dbc = mysqli_connect("localhost", "root", "kitproject", "lostnfound") or die("DB에 연결할 수 없습니다.");
	mysqli_query($dbc, "set names utf8");
	
	if(isset($_POST['emailcheck'])){
		$email = $_POST['s_email'];

		$query = "select email from user where email='$email'";
		$result = mysqli_query($dbc, $query);
	
		if(mysqli_num_rows($result) == 1)
		{
			echo 4;       //이메일 중복
			mysqli_free_result($result);
		}
		else
			echo 1;    //이메일 중복 아님
		
	}else if(isset($_POST['signup'])){
		$id = $_POST['s_id'];
		$nickname = $_POST['s_nickname'];
		$password = $_POST['s_pw'];
		$passcheck = $_POST['s_pwch'];
		$email = $_POST['s_email'];
	
		$query = "select id from user where id='$id'";
		$result = mysqli_query($dbc, $query);
	
		if(mysqli_num_rows($result) == 1)
		{
			echo 2;       //아이디 중복
			mysqli_free_result($result);
		}
		else
		{
			$query = "select nickname from user where nickname='$nickname'";
			$result = mysqli_query($dbc, $query);
	
			if(mysqli_num_rows($result))
			{
				echo 3;       //닉네임 중복
				mysqli_free_result($result);
			}
			else
			{
				$query = "select email from user where email='$email'";
				$result = mysqli_query($dbc, $query);

				if(mysqli_num_rows($result) == 0)
				{
					mysqli_free_result($result);

					$query = "insert into user(id, nickname, password, passcheck, email) values('$id', '$nickname', SHA('$password'), SHA('$passcheck'), '$email')";

					$result = mysqli_query($dbc, $query);
 
					if(!$result)
						die("mysql query error");
					else
						echo 1;    //가입 성공
				}
			}
		}
	}
	mysqli_close($dbc);
?>