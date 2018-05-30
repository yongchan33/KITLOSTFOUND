package kr.ac.kumoh.LostNFound;

public class ListSellNoticeItem {
    private Integer no;
    private String title, category, type, date, thingname, trademethod, price, content, writer;

    public ListSellNoticeItem(Integer no, String title, String category, String type, String date, String thingname, String trademethod, String price, String content, String writer) {
        this.no = no;
        this.title = title;
        this.category = category;
        this.type = type;
        this.date = date;
        this.thingname = thingname;
        this.trademethod = trademethod;
        this.price = price;
        this.content = content;
        this.writer = writer;
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

    public String getThingname() {
        return thingname;
    }

    public String getTrademethod() {
        return trademethod;
    }

    public String getPrice() {
        return price;
    }

    public String getContent() {
        return content;
    }

    public String getWriter() {
        return writer;
    }
}
