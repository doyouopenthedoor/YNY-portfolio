package main.java.com.YNY.Happytails.community.reply.service;

public enum ReplyCode {
    L("내 새꾸 자랑"),
    C("떠들개"),
    O("집사 회의"),
    R("릴스");

    private final String description;

    ReplyCode(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
