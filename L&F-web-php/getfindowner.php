<?php

	$dbc = mysqli_connect("localhost", "root", "kitproject", "lostnfound") or die("DB에 연결할 수 없습니다.");
	mysqli_query($dbc, "set names utf8");
			
	if(isset($_POST['mynotice'])){  // 내 주인찾기 게시물 불러올 때
		$mynotice = $_POST['mynotice'];
		if($mynotice == 1){
			$id = $_POST['s_id'];
			
			$query = "select nickname from user where id='$id'";
			$result = mysqli_query($dbc, $query);
			$row = mysqli_fetch_assoc($result);
	
			$writer = $row['nickname'];
			
			$result = mysqli_query($dbc, "select * from findnotice where category='주인찾기' and writer='$writer' and visible=1 order by no desc");
			$total_record = mysqli_num_rows($result);
	
			echo "{\"status\":\"OK\",\"results\":[";
	
			for($i=0; $i<$total_record; $i++)
			{
				mysqli_data_seek($result, $i);
		
				$row = mysqli_fetch_array($result);
		
				echo "{\"no\":\"$row[no]\",\"title\":\"$row[title]\",
				\"category\":\"$row[category]\",\"type\":\"$row[type]\",
				\"date\":\"$row[date]\",\"content\":\"$row[content]\",
				\"writer\":\"$row[writer]\",\"imgpath\":\"$row[imgpath]\",
				\"imgname1\":\"$row[imgname1]\",\"imgname2\":\"$row[imgname2]\",
				\"imgname3\":\"$row[imgname3]\"}";
		
				if($i < $total_record-1) echo ",";
			}
	
			echo "]}";
	
			mysqli_close($dbc);
		}
	}
	else{ // 모든 주인찾기 게시물 불러올 때
		$result = mysqli_query($dbc, "select * from findnotice where category='주인찾기' and visible=1 order by no desc");
		$total_record = mysqli_num_rows($result);
	
		echo "{\"status\":\"OK\",\"results\":[";
	
		for($i=0; $i<$total_record; $i++)
		{
			mysqli_data_seek($result, $i);
		
			$row = mysqli_fetch_array($result);
		
			echo "{\"no\":\"$row[no]\",\"title\":\"$row[title]\",
			\"category\":\"$row[category]\",\"type\":\"$row[type]\",
			\"date\":\"$row[date]\",\"content\":\"$row[content]\",
			\"writer\":\"$row[writer]\",\"imgpath\":\"$row[imgpath]\",
			\"imgname1\":\"$row[imgname1]\",\"imgname2\":\"$row[imgname2]\",
			\"imgname3\":\"$row[imgname3]\"}";
		
			if($i < $total_record-1) echo ",";
		}
	
		echo "]}";
	
		mysqli_close($dbc);
	}
?>