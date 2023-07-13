package io.javabrains.inbox.folders;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FolderService {

    @Autowired
    UnreadEmailStatsRepository unreadEmailStatsRepository;

    public List<Folder> fetchDefaultFolders(String userId) {
        return Arrays.asList(
                new Folder(userId, "Inbox", "white"),
                new Folder(userId, "Sent Items", "green"),
                new Folder(userId, "Important", "blue"),
                new Folder(userId, "Spam", "red"));
    }

    public Map<String, Integer> mapCountToLabels(String userId) {
        List<UnreadEmailStats> stats = unreadEmailStatsRepository.findAllById(userId);
        return stats.stream().collect(Collectors.toMap(UnreadEmailStats::getLabel, UnreadEmailStats::getUnreadCount));
    }
}
