<?php

	$dbc = mysqli_connect("localhost", "root", "kitproject", "lostnfound") or die("DB에 연결할 수 없습니다.");
	mysqli_query($dbc, "set names utf8");
	
    $file_path = "uploadedIamge/";

    $file_path = $file_path . basename($_FILES['uploaded_file']['name']);
    if(move_uploaded_file($_FILES['uploaded_file']['tmp_name'], $file_path)) {
        echo 1;  // 사진 업로드 성공
    } else{
        echo 0; // 사진 업로드 실패
    }
 ?>