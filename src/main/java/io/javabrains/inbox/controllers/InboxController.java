package io.javabrains.inbox.controllers;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.ocpsoft.prettytime.PrettyTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;

import com.datastax.oss.driver.api.core.uuid.Uuids;
import org.springframework.web.bind.annotation.GetMapping;

import io.javabrains.inbox.emaillist.EmailListItem;
import io.javabrains.inbox.emaillist.EmailListItemRepository;
import io.javabrains.inbox.folders.Folder;
import io.javabrains.inbox.folders.FolderRepository;
import io.javabrains.inbox.folders.FolderService;

@Controller
public class InboxController {

    @Autowired
    private FolderRepository folderRepository;
    @Autowired
    private FolderService folderService;
    @Autowired
    EmailListItemRepository emailListItemRepository;

    @GetMapping("/")
    public String homePage(@AuthenticationPrincipal OAuth2User principal,
            Model model) {
        if (principal == null || !StringUtils.hasText(principal.getAttribute("login"))) {
            return "index";
        }
        String userId = principal.getAttribute("login");

        // Fetch folders
        List<Folder> userFolders = folderRepository.findAllById(userId);
        model.addAttribute("userFolders", userFolders);
        List<Folder> defaultFolders = folderService.fetchDefaultFolders(userId);
        model.addAttribute("defaultFolders", defaultFolders);

        // Fetch messages
        String folderLabel = "Inbox";
        List<EmailListItem> emailList = emailListItemRepository.findAllByKey_IdAndKey_Label(userId, folderLabel);

        PrettyTime prettyTime = new PrettyTime();

        emailList.stream().forEach(emailItem -> {
            UUID timeUuid = emailItem.getKey().getTimeUUID();
            Date emailDateTime = new Date(Uuids.unixTimestamp(timeUuid));
            emailItem.setAgoTimeString(prettyTime.format(emailDateTime));
        });
        model.addAttribute("emailList", emailList);

        return "inbox-page";
    }
}
