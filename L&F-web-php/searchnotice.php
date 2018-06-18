<?php

	$dbc = mysqli_connect("localhost", "root", "kitproject", "lostnfound") or die("DB에 연결할 수 없습니다.");
	mysqli_query($dbc, "set names utf8");
   
	$keyword = strip_tags($_POST['s_keyword']);
	if(strcmp($_POST['search'],"1") == 0){
		// 물건찾기
		$result = mysqli_query($dbc, "select * from findnotice where category='물건찾기' and (title like '%$keyword%' or content like '%$keyword%') and visible=1 order by no desc");
		$total_record = mysqli_num_rows($result);

		echo "{\"status\":\"OK\",\"results\":[";
		
		for($i=0; $i<$total_record; $i++)
		{
			mysqli_data_seek($result, $i);

			$row = mysqli_fetch_array($result);
			echo "{\"no\":\"$row[no]\",
			\"title\":\"$row[title]\",
			\"category\":\"$row[category]\",
			\"type\":\"$row[type]\",
			\"date\":\"$row[date]\",
			\"lostdate\":\"$row[lostdate]\",
			\"reward\":\"$row[reward]\",
			\"content\":\"$row[content]\",
			\"imgpath\":\"$row[imgpath]\",
			\"imgname1\":\"$row[imgname1]\",
			\"imgname2\":\"$row[imgname2]\",
			\"imgname3\":\"$row[imgname3]\",
			\"writer\":\"$row[writer]\",
			\"lostplace\":\"$row[lostplace]\",
			\"latitude\":\"$row[latitude]\",
			\"longitude\":\"$row[longitude]\"}";

			if($i < $total_record-1) echo ",";
		}
	}else if(strcmp($_POST['search'],"2") == 0){
		// 주인찾기
		$result = mysqli_query($dbc, "select * from findnotice where category='주인찾기' and (title like '%$keyword%' or content like '%$keyword%') and visible=1 order by no desc");
		$total_record = mysqli_num_rows($result);
		
		echo "{\"status\":\"OK\",\"num_results\":\"$total_record\",\"results\":[";
		
		for($i=0; $i<$total_record; $i++)
		{
			mysqli_data_seek($result, $i);

			$row = mysqli_fetch_array($result);
			echo "{\"no\":\"$row[no]\",
			\"title\":\"$row[title]\",
			\"category\":\"$row[category]\",
			\"type\":\"$row[type]\",
			\"date\":\"$row[date]\",
			\"content\":\"$row[content]\",
			\"imgpath\":\"$row[imgpath]\",
			\"imgname1\":\"$row[imgname1]\",
			\"imgname2\":\"$row[imgname2]\",
			\"imgname3\":\"$row[imgname3]\",
			\"writer\":\"$row[writer]\"}";
		
			if($i < $total_record-1) echo ",";
		}
		
	}
      echo "]}";
   
   mysqli_close($dbc);
?>