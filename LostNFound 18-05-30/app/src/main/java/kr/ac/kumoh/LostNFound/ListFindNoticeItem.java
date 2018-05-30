package kr.ac.kumoh.LostNFound;

public class ListFindNoticeItem {
    private Integer no;
    private String title, category, type, date, lostdate, reward, content, writer, lostplace;
    private Double latitude, longitude;

    public ListFindNoticeItem(Integer no, String title, String category, String type, String date, String content, String writer) {  //"주인찾기"에서 사용할 생성자
        this.no = no;
        this.title = title;
        this.category = category;
        this.type = type;
        this.date = date;
        this.content = content;
        this.writer = writer;
    }
    //"물건찾기"에서 사용할 생성자
    public ListFindNoticeItem(Integer no, String title, String category, String type, String date, String lostdate, String reward, String content, String writer, String lostplace, Double latitude, Double longitude) {
        this.no = no;
        this.title = title;
        this.category = category;
        this.type = type;
        this.date = date;
        this.lostdate = lostdate;
        this.reward = reward;
        this.content = content;
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
