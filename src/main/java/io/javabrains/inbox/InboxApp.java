package io.javabrains.inbox;

import java.nio.file.Path;
import java.util.Arrays;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.cassandra.CqlSessionBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.datastax.oss.driver.api.core.uuid.Uuids;

import io.javabrains.inbox.emaillist.EmailListItem;
import io.javabrains.inbox.emaillist.EmailListItemKey;
import io.javabrains.inbox.emaillist.EmailListItemRepository;
import io.javabrains.inbox.folders.Folder;
import io.javabrains.inbox.folders.FolderRepository;

@SpringBootApplication
@RestController
public class InboxApp {

	@Autowired FolderRepository folderRepository;
	@Autowired EmailListItemRepository emailListItemRepository;

	public static void main(String[] args) {
		SpringApplication.run(InboxApp.class, args);
	}

	@Bean
	public CqlSessionBuilderCustomizer sessionBuilderCustomizer(DataStaxAstraProperties astraProperties){
		Path bundle = astraProperties.getSecureConnectBundle().toPath();
		return builder -> builder.withCloudSecureConnectBundle(bundle);
	}
	
	@PostConstruct
	public void init(){
		Folder folder = new Folder("vykhy", "Billions", "blue");
		folderRepository.save(folder);
		folderRepository.save(new Folder("vykhy", "Company", "yellow"));
		folderRepository.save(new Folder("vykhy", "Sent", "Green"));
		folderRepository.save(new Folder("vykhy", "Spam", "red"));

		for(int i=0 ;i< 10; i++){
			EmailListItemKey key = new EmailListItemKey();
			key.setId("vykhy");
			key.setLabel("Inbox");
			key.setTimeUUID(Uuids.timeBased());

			EmailListItem item = new EmailListItem();
			item.setKey(key);
			item.setTo(Arrays.asList("vykhy"));
			item.setSubject("Subject: " + i);
			item.setIsUnread(true);

			emailListItemRepository.save(item);

		}
		
	}

}
