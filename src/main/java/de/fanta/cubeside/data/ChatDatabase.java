package de.fanta.cubeside.data;

import de.fanta.cubeside.CubesideClientFabric;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.text.Text;
import org.dizitart.no2.Nitrite;
import org.dizitart.no2.collection.FindOptions;
import org.dizitart.no2.common.SortOrder;
import org.dizitart.no2.mvstore.MVStoreModule;
import org.dizitart.no2.repository.ObjectRepository;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ChatDatabase {
    private static MVStoreModule storeModule;
    private static Nitrite database;
    private static ObjectRepository<ChatRepo> chatRepo;
    private static ObjectRepository<CommandRepo> commandRepo;
    private static int currentMessageId = 0;
    private static int currentCommandId = 0;

    public ChatDatabase(String server) {
        storeModule = MVStoreModule.withConfig().filePath(new File(CubesideClientFabric.getConfigDirectory(), "/chatStorage/" + server.toLowerCase() + ".db")).compress(true).build();
        database = Nitrite.builder().loadModule(storeModule).openOrCreate();

        chatRepo = database.getRepository(ChatRepo.class);
        commandRepo = database.getRepository(CommandRepo.class);

        List<ChatRepo> chatRepos = chatRepo.find(FindOptions.orderBy("id", SortOrder.Descending)).toList();
        if (chatRepos != null && !chatRepos.isEmpty()) {
            currentMessageId = chatRepos.getLast().getMessageID() + 1;
        }

        List<CommandRepo> commandRepos = commandRepo.find(FindOptions.orderBy("id", SortOrder.Descending)).toList();
        if (commandRepos != null && !commandRepos.isEmpty()) {
            currentMessageId = commandRepos.getFirst().getCommandID() + 1;
        }
    }

    public void addMessageEntry(String message) {
        ChatRepo entry = new ChatRepo(currentMessageId++, message, System.currentTimeMillis());
        chatRepo.insert(entry);
    }

    public void addCommandEntry(String command) {
        CommandRepo entry = new CommandRepo(currentCommandId++, command, System.currentTimeMillis());
        commandRepo.insert(entry);
    }

    public void close() {
        if (database != null) {
            database.close();
        }
    }

    public List<Text> loadMessages() {
        List<Text> entries = new ArrayList<>();
        for (ChatRepo entry : chatRepo.find(FindOptions.orderBy("id", SortOrder.Descending))) {
            entries.add(Text.Serialization.fromJson(entry.getMessage(), DynamicRegistryManager.EMPTY));
        }
        return entries;
    }

    public List<String> loadCommands() {
        List<String> entries = new ArrayList<>();
        for (CommandRepo entry : commandRepo.find(FindOptions.orderBy("id", SortOrder.Descending))) {
            entries.add(entry.getCommand());
        }
        return entries;
    }
}
