package io.javabrains.inbox.email;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.datastax.oss.driver.api.core.uuid.Uuids;

import io.javabrains.inbox.emaillist.EmailListItemKey;
import io.javabrains.inbox.emaillist.EmailListItemRepository;
import io.javabrains.inbox.emaillist.EmailListItem;

@Service
public class EmailService {

    @Autowired
    EmailRepository emailRepository;
    @Autowired
    EmailListItemRepository emailListItemRepository;

    public void sendEmail(String from, List<String> to, String subject, String body) {
        Email email = new Email();
        email.setTo(to);
        email.setFrom(from);
        email.setSubject(subject);
        email.setBody(body);
        email.setId(Uuids.timeBased());
        emailRepository.save(email);

        to.forEach(toId -> {
            EmailListItem item = createEmailListItem(to, subject, email, body, toId, "Inbox");
            emailListItemRepository.save(item);
        });

        EmailListItem sentItemEntry = createEmailListItem(to, subject, email, body, from, "Sent Items");
        emailListItemRepository.save(sentItemEntry);
    }

    private EmailListItem createEmailListItem(List<String> to, String subject, Email email, String body, String ownerId,
            String folder) {
        EmailListItemKey key = new EmailListItemKey();
        key.setId(ownerId);
        key.setLabel(folder);
        key.setTimeUUID(email.getId());
        EmailListItem item = new EmailListItem();
        item.setKey(key);
        item.setTo(to);
        item.setSubject(subject);
        item.setIsUnread(true);

        return item;
    }
}
