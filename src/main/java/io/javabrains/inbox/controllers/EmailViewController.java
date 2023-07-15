package io.javabrains.inbox.controllers;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import io.javabrains.inbox.email.Email;
import io.javabrains.inbox.email.EmailRepository;
import io.javabrains.inbox.emaillist.EmailListItem;
import io.javabrains.inbox.emaillist.EmailListItemKey;
import io.javabrains.inbox.emaillist.EmailListItemRepository;
import io.javabrains.inbox.folders.Folder;
import io.javabrains.inbox.folders.FolderRepository;
import io.javabrains.inbox.folders.FolderService;
import io.javabrains.inbox.folders.UnreadEmailStatsRepository;

@Controller
public class EmailViewController {

    @Autowired
    private FolderRepository folderRepository;
    @Autowired
    private FolderService folderService;
    @Autowired
    private EmailRepository emailRepository;
    @Autowired
    EmailListItemRepository emailListItemRepository;
    @Autowired
    UnreadEmailStatsRepository unreadEmailStatsRepository;

    @GetMapping("/emails/{id}")
    public String emailView(
            @RequestParam String folder,
            @PathVariable UUID id,
            @AuthenticationPrincipal OAuth2User principal,
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

        // Fetch email
        Optional<Email> optionalEmail = emailRepository.findById(id);
        if (!optionalEmail.isPresent()) {
            model.addAttribute("stats", folderService.mapCountToLabels(userId));
            return "inbox-page";
        }
        Email email = optionalEmail.get();
        String toIds = String.join(", ", email.getTo());

        // Check whether user is allowed to see the email
        if (!userId.equals(email.getFrom()) && !email.getTo().contains(userId)) {
            return "redirect:/";
        }

        model.addAttribute("email", email);
        model.addAttribute("toIds", toIds);

        EmailListItemKey key = new EmailListItemKey();
        key.setId(userId);
        key.setLabel(folder);
        key.setTimeUUID(email.getId());

        Optional<EmailListItem> optionalEmailListItem = emailListItemRepository.findById(key);
        if (optionalEmail.isPresent()) {
            EmailListItem emailListItem = optionalEmailListItem.get();
            if (emailListItem.getIsUnread()) {
                emailListItem.setIsUnread(false);
                emailListItemRepository.save(emailListItem);

                unreadEmailStatsRepository.decrementUnreadCounter(userId, folder);
            }
        }
        model.addAttribute("stats", folderService.mapCountToLabels(userId));

        return "email-page";
    }
}
