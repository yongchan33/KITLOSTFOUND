package kr.ac.kumoh.LostNFound;

public class ListCommentItem {
    private Integer no_comment, no_notice;
    private String commnet_writer, comment_content, comment_date;

    public ListCommentItem(Integer no_comment, Integer no_notice, String commnet_writer, String comment_content, String comment_date) {
        this.no_comment = no_comment;
        this.no_notice = no_notice;
        this.commnet_writer = commnet_writer;
        this.comment_content = comment_content;
        this.comment_date = comment_date;
    }

    public Integer getNo_comment() {
        return no_comment;
    }

    public Integer getNo_notice() {
        return no_notice;
    }

    public String getCommnet_writer() {
        return commnet_writer;
    }

    public String getComment_content() {
        return comment_content;
    }

    public String getComment_date() {
        return comment_date;
    }
}
