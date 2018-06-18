<?php

	$dbc = mysqli_connect("localhost", "root", "kitproject", "lostnfound") or die("DB에 연결할 수 없습니다.");
	mysqli_query($dbc, "set names utf8");
 
 	if(isset($_POST['comment'])){
		$comment = $_POST['comment'];   //댓글 작성, 수정 구분
		if($comment == 0){   //댓글 작성
			$id = $_POST['s_id'];    //로그인한 아이디
			$flag = $_POST['flag']; // 0:물건찾기 주인찾기 댓글
			$no_notice = $_POST['no_notice'];
			$content = $_POST['s_content'];
			$date = $_POST['s_date'];
			$noticewriter = $_POST['s_noticewriter'];
	
			$query = "select token from user where nickname='$noticewriter'";
			$result = mysqli_query($dbc, $query);
			$row = mysqli_fetch_assoc($result);
	
			echo "{\"token\":\"$row[token]\"}";
	
			$query = "select nickname from user where id='$id'";
			$result = mysqli_query($dbc, $query);
			$row = mysqli_fetch_assoc($result);
	
			$writer = $row['nickname'];

			mysqli_free_result($result);

			$query = "insert into comment(flag, no_notice, writer, content, date) values('$flag', '$no_notice', '$writer', '$content', '$date')";
			$result = mysqli_query($dbc, $query);

			if(!$result)
				die("mysql query error");
			else
				echo 1;  //댓글 쓰기 성공
		}
		else if($comment == 1){   //댓글 수정
			$id = $_POST['s_id'];    //로그인한 아이디
			$flag = $_POST['flag']; // 0:물건찾기 주인찾기 댓글
			$no_comment = $_POST['no_comment'];
			$content = $_POST['s_content'];
			$date = $_POST['s_date'];
			
			$query = "select nickname from user where id='$id'";
			$result = mysqli_query($dbc, $query);
			$row = mysqli_fetch_assoc($result);
	
			$writer = $row['nickname'];

			mysqli_free_result($result);
				
			$query = mysqli_query($dbc, "update comment set content='$content', date='$date' where flag='$flag' and no='$no_comment' and writer='$writer'");
			$row = mysqli_affected_rows($dbc);

			if($row <= 0)
				echo 0;  //댓글 수정 실패
			else
				echo 1;  //댓글 수정 성공
		}
		else if($comment == 2){   //댓글 삭제
			$id = $_POST['s_id'];    //로그인한 아이디
			$flag = $_POST['flag']; // 0:물건찾기 주인찾기 댓글
			$no_comment = $_POST['no_comment'];
			$content = $_POST['s_content'];
			
			$query = "select nickname from user where id='$id'";
			$result = mysqli_query($dbc, $query);
			$row = mysqli_fetch_assoc($result);
	
			$writer = $row['nickname'];

			mysqli_free_result($result);
			
			$query = mysqli_query($dbc, "delete from comment where flag='$flag' and writer='$writer' and no='$no_comment'");
			$row = mysqli_affected_rows($dbc);

			if($row <= 0)
				echo 0;  //댓글 삭제 실패
			else
				echo 1;  //댓글 삭제 성공
		}
	}
?>