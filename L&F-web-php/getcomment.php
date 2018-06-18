<?php

	$dbc = mysqli_connect("localhost", "root", "kitproject", "lostnfound") or die("DB에 연결할 수 없습니다.");
	mysqli_query($dbc, "set names utf8");
	
	$flag = $_POST['flag']; // 0:물건찾기 주인찾기 댓글
	$no_notice = $_POST['no_notice'];
	
	$result = mysqli_query($dbc, "select * from comment where flag='$flag' and no_notice='$no_notice' and visible='1' order by date asc");
	$total_record = mysqli_num_rows($result);
	
	echo "{\"status\":\"OK\",\"num_results\":\"$total_record\",\"results\":[";
	
	for($i=0; $i<$total_record; $i++)
	{
		mysqli_data_seek($result, $i);
		
		$row = mysqli_fetch_array($result);
		
		echo "{\"no\":\"$row[no]\",\"no_notice\":\"$row[no_notice]\",\"writer\":\"$row[writer]\",\"content\":\"$row[content]\",\"date\":\"$row[date]\"}";
		
		if($i < $total_record-1) echo ",";
	}
	
	echo "]}";
	
	mysqli_close($dbc);
?>