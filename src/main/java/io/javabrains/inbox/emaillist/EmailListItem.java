package io.javabrains.inbox.emaillist;

import java.util.List;

import org.springframework.data.annotation.Transient;
import org.springframework.data.cassandra.core.mapping.CassandraType;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;
import org.springframework.data.cassandra.core.mapping.CassandraType.Name;

@Table(value = "messges_by_user_folder")
public class EmailListItem {

    @PrimaryKey
    private EmailListItemKey key;

    @CassandraType(type = Name.LIST, typeArguments = Name.TEXT)
    private List<String> to;

    @CassandraType(type = Name.TEXT)
    private String subject;

    @Transient
    private String agoTimeString;

    public String getAgoTimeString() {
        return agoTimeString;
    }

    public void setAgoTimeString(String agoTimeString) {
        this.agoTimeString = agoTimeString;
    }

    public EmailListItemKey getKey() {
        return key;
    }

    public void setKey(EmailListItemKey key) {
        this.key = key;
    }

    public List<String> getTo() {
        return to;
    }

    public void setTo(List<String> to) {
        this.to = to;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public Boolean getIsUnread() {
        return isUnread;
    }

    public void setIsUnread(Boolean isUnread) {
        this.isUnread = isUnread;
    }

    @CassandraType(type = Name.BOOLEAN)
    private Boolean isUnread;
}
