<?php

	$dbc = mysqli_connect("localhost", "root", "kitproject", "lostnfound") or die("DB에 연결할 수 없습니다.");
	mysqli_query($dbc, "set names utf8");
	
	if(isset($_POST['findnotice'])){
		$findnotice = $_POST['findnotice'];   //물건찾기, 주인찾기 게시물 작성, 수정 구분
		if($findnotice == 0){   //물건찾기, 주인찾기 게시물 작성
			$id = $_POST['s_id'];
			$title = $_POST['s_title'];
			$category = $_POST['s_category'];
			$type = $_POST['s_type'];
			$date = $_POST['s_date'];
			$lostdate = $_POST['s_lostdate'];
			$reward = $_POST['s_reward'];
			$content = $_POST['s_content'];
			$imgpath = "uploadedIamge/";
			$imgname1 = $_POST['imgname1'];
			$imgname2 = $_POST['imgname2'];
			$imgname3 = $_POST['imgname3'];
			$lostplace = $_POST['s_lostplace'];
			$latitude = $_POST['d_latitude'];
			$longitude = $_POST['d_longitude'];
		
			$query = "select nickname from user where id='$id'";
			$result = mysqli_query($dbc, $query);
			$row = mysqli_fetch_assoc($result);
	
			$writer = $row['nickname'];

			if(mysqli_num_rows($result)){
				mysqli_free_result($result);
		
				if(strcmp($lostdate, " ") == 0)
				{
					if($imgname1 != null){
						if(imgname2 != null){
							if(imgname3 != null){
								$query = "insert into findnotice(title, category, type, date, lostdate, reward, content, imgpath, imgname1, imgname2, imgname3, writer) values('$title', '$category', '$type', '$date', null, '$reward', '$content', '$imgpath', '$imgname1', '$imgname2', '$imgname3', '$writer')";
							}else{
								$query = "insert into findnotice(title, category, type, date, lostdate, reward, content, imgpath, imgname1, imgname2, writer) values('$title', '$category', '$type', '$date', null, '$reward', '$content', '$imgpath', '$imgname1', '$imgname2', '$writer')";
							}
						}else{
							$query = "insert into findnotice(title, category, type, date, lostdate, reward, content, imgpath, imgname1, writer) values('$title', '$category', '$type', '$date', null, '$reward', '$content', '$imgpath', '$imgname1', '$writer')";
						}
					} else{
						$query = "insert into findnotice(title, category, type, date, lostdate, reward, content, writer) values('$title', '$category', '$type', '$date', null, '$reward', '$content', '$writer')";
					}
				}
				else{
					if($imgname1 != null){
						if($imgname2 != null){
							if($imgname3 != null){
								$query = "insert into findnotice(title, category, type, date, lostdate, reward, content, imgpath, imgname1, imgname2, imgname3, writer, lostplace, latitude, longitude) values('$title', '$category', '$type', '$date', '$lostdate', '$reward', '$content', '$imgpath', '$imgname1', '$imgname2', '$imgname3', '$writer', '$lostplace', '$latitude', '$longitude')";
							}else{
								$query = "insert into findnotice(title, category, type, date, lostdate, reward, content, imgpath, imgname1, imgname2, writer, lostplace, latitude, longitude) values('$title', '$category', '$type', '$date', '$lostdate', '$reward', '$content', '$imgpath', '$imgname1', '$imgname2', '$writer', '$lostplace', '$latitude', '$longitude')";
							}
						}else{
							$query = "insert into findnotice(title, category, type, date, lostdate, reward, content, imgpath, imgname1, writer, lostplace, latitude, longitude) values('$title', '$category', '$type', '$date', '$lostdate', '$reward', '$content', '$imgpath', '$imgname1', '$writer', '$lostplace', '$latitude', '$longitude')";
							}
					}else{
						$query = "insert into findnotice(title, category, type, date, lostdate, reward, content, writer, lostplace, latitude, longitude) values('$title', '$category', '$type', '$date', '$lostdate', '$reward', '$content', '$writer', '$lostplace', '$latitude', '$longitude')";
					}
				}
				$result = mysqli_query($dbc, $query);
 
				if(!$result)
					die("mysql query error");
				else
					echo 1;  //글쓰기 성공
			}
			else
				echo 0;   //글쓰기 실패
		
			mysqli_close($dbc);
		}
		else if($findnotice == 1){   //물건찾기, 주인찾기 게시물 수정
			$no_notice = $_POST['no_notice'];
			$id = $_POST['s_id'];
			$title = $_POST['s_title'];
			$category = $_POST['s_category'];
			$type = $_POST['s_type'];
			$date = $_POST['s_date'];
			$lostdate = $_POST['s_lostdate'];
			$reward = $_POST['s_reward'];
			$content = $_POST['s_content'];
			$imgpath = "uploadedIamge/";
			$imgname1 = $_POST['imgname1'];
			$imgname2 = $_POST['imgname2'];
			$imgname3 = $_POST['imgname3'];
			$lostplace = $_POST['s_lostplace'];
			$latitude = $_POST['d_latitude'];
			$longitude = $_POST['d_longitude'];
		
			$query = "select nickname from user where id='$id'";
			$result = mysqli_query($dbc, $query);
			$row = mysqli_fetch_assoc($result);
	
			$writer = $row['nickname'];
	
			if(mysqli_num_rows($result)){
				mysqli_free_result($result);
		
				if(strcmp($lostdate, " ") == 0)
				{
					if($imgname1 != null){
						if(imgname2 != null){
							if(imgname3 != null){
								$query = "update findnotice set title='$title', category='$category', type='$type', date='$date', reward='$reward', content='$content', imgname1='$imgname1', imgname2='$imgname2', imgname3='$imgname3' where no='$no_notice'";
							}
							$query = "update findnotice set title='$title', category='$category', type='$type', date='$date', reward='$reward', content='$content', imgname1='$imgname1', imgname2='$imgname2' where no='$no_notice'";
						}
						$query = "update findnotice set title='$title', category='$category', type='$type', date='$date', reward='$reward', content='$content', imgname1='$imgname1' where no='$no_notice'";
					}
					else{
						$query = "update findnotice set title='$title', category='$category', type='$type', date='$date', reward='$reward', content='$content' where no='$no_notice'";
					}
				}
				else{
					if($imgname1 != null){
						if($imgname2 != null){
							if($imgname3 != null){
								$query = "update findnotice set title='$title', category='$category', type='$type', date='$date', lostdate='$lostdate', reward='$reward', content='$content', imgname1='$imgname1', imgname2='$imgname2', imgname3='$imgname3', lostplace='$lostplace', latitude='$latitude', longitude='$longitude' where no='$no_notice'";
							}
							$query = "update findnotice set title='$title', category='$category', type='$type', date='$date', lostdate='$lostdate', reward='$reward', content='$content', imgname1='$imgname1', imgname2='$imgname2', lostplace='$lostplace', latitude='$latitude', longitude='$longitude' where no='$no_notice'";
						}
						$query = "update findnotice set title='$title', category='$category', type='$type', date='$date', lostdate='$lostdate', reward='$reward', content='$content', imgname1='$imgname1', lostplace='$lostplace', latitude='$latitude', longitude='$longitude' where no='$no_notice'";
					}
					else{
						$query = "update findnotice set title='$title', category='$category', type='$type', date='$date', lostdate='$lostdate', reward='$reward', content='$content', lostplace='$lostplace', latitude='$latitude', longitude='$longitude' where no='$no_notice'";
					}
				}
				$result = mysqli_query($dbc, $query);
				$row = mysqli_affected_rows($dbc);
 
				if($row <= 0)
					echo 0;  //글 수정 실패
				else
					echo 1;  //글 수정 성공
			}
			else
				echo 0;   //글수정 실패
		
			mysqli_close($dbc);
		}
	}
?>