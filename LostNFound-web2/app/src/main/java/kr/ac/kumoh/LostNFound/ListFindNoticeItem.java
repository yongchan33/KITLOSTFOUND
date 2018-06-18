package kr.ac.kumoh.LostNFound;

public class ListFindNoticeItem { // 물건찾기, 주인찾기 아이템
    private Integer no;
    private String title, category, type, date, lostdate, reward, content, imgpath, imgname1, imgname2, imgname3, writer, lostplace;
    private Double latitude, longitude;

    //"주인찾기"에서 사용할 생성자
    public ListFindNoticeItem(Integer no, String title, String category, String type, String date, String content, String imgpath, String imgname1, String imgname2, String imgname3, String writer) {
        this.no = no;
        this.title = title;
        this.category = category;
        this.type = type;
        this.date = date;
        this.content = content;
        this.imgpath = imgpath;
        this.imgname1 = imgname1;
        this.imgname2 = imgname2;
        this.imgname3 = imgname3;
        this.writer = writer;
    }

    //"물건찾기"에서 사용할 생성자
    public ListFindNoticeItem(Integer no, String title, String category, String type, String date, String lostdate, String reward, String content, String imgpath, String imgname1, String imgname2, String imgname3, String writer, String lostplace, Double latitude, Double longitude) {
        this.no = no;
        this.title = title;
        this.category = category;
        this.type = type;
        this.date = date;
        this.lostdate = lostdate;
        this.reward = reward;
        this.content = content;
        this.imgpath = imgpath;
        this.imgname1 = imgname1;
        this.imgname2 = imgname2;
        this.imgname3 = imgname3;
        this.writer = writer;
        this.lostplace = lostplace;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Integer getNo() {
        return no;
    }

    public String getTitle() {
        return title;
    }

    public String getCategory() {
        return category;
    }

    public String getType() {
        return type;
    }

    public String getDate() {
        return date;
    }

    public String getLostdate() {
        return lostdate;
    }

    public String getReward() {
        return reward;
    }

    public String getContent() {
        return content;
    }

    public String getImgpath() {
        return imgpath;
    }

    public String getImgname1() {
        return imgname1;
    }

    public String getImgname2() {
        return imgname2;
    }

    public String getImgname3() {
        return imgname3;
    }

    public String getWriter() {
        return writer;
    }

    public String getLostplace() {
        return lostplace;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }
}
