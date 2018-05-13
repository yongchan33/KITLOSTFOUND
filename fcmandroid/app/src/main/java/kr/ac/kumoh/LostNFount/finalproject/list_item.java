package kr.ac.kumoh.LostNFount.finalproject;

public class list_item {
    private String notice_title;
    private String notice_Content;
    private String notice_reward;

    public list_item(String notice_title) {
        this.notice_title = notice_title;
        this.notice_reward = notice_reward;
    }

    public String getNotice_title() {
        return notice_title;
    }

    public String getNotice_Content() {
        return notice_Content;
    }

    public String getNotice_reward() {
        return notice_reward;
    }
}
