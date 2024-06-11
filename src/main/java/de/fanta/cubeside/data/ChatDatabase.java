package de.fanta.cubeside.data;

import de.fanta.cubeside.CubesideClientFabric;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.text.Text;
import org.dizitart.no2.Nitrite;
import org.dizitart.no2.collection.FindOptions;
import org.dizitart.no2.common.SortOrder;
import org.dizitart.no2.common.mapper.JacksonMapperModule;
import org.dizitart.no2.exceptions.TransactionException;
import org.dizitart.no2.mvstore.MVStoreModule;
import org.dizitart.no2.repository.ObjectRepository;
import org.dizitart.no2.transaction.Session;
import org.dizitart.no2.transaction.Transaction;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ChatDatabase {
    private static MVStoreModule storeModule;
    private static Nitrite database;
    private static ObjectRepository<ChatRepo> chatRepo;
    private static ObjectRepository<CommandRepo> commandRepo;
    private int currentMessageId = 0;
    private int currentCommandId = 0;

    public ChatDatabase(String server) {
        storeModule = MVStoreModule.withConfig().filePath(new File(CubesideClientFabric.getConfigDirectory(), "/chatStorage/" + server.toLowerCase() + ".db")).compress(true).build();
        database = Nitrite.builder().loadModule(storeModule).loadModule(new JacksonMapperModule()).openOrCreate();

        chatRepo = database.getRepository(ChatRepo.class);
        commandRepo = database.getRepository(CommandRepo.class);

        List<ChatRepo> chatRepos = chatRepo.find(FindOptions.orderBy("id", SortOrder.Descending)).toList();
        if (chatRepos != null && !chatRepos.isEmpty()) {
            currentMessageId = chatRepos.getLast().getMessageID() + 1;
            System.out.println("MessageID: " + currentMessageId);
        }

        List<CommandRepo> commandRepos = commandRepo.find(FindOptions.orderBy("id", SortOrder.Descending)).toList();
        if (commandRepos != null && !commandRepos.isEmpty()) {
            currentMessageId = commandRepos.getLast().getCommandID() + 1;
            System.out.println("CommandID: " + currentCommandId);
        }
    }

    public void addMessageEntry(String message) {
        int id = this.currentMessageId++;
        System.out.println("Add Message with id: " + id);
        ChatRepo entry = new ChatRepo(id, message, System.currentTimeMillis());
        chatRepo.insert(entry);
    }

    public void addCommandEntry(String command) {
        int id = this.currentCommandId++;
        System.out.println("Add Command with id: " + id);
        CommandRepo entry = new CommandRepo(id, command, System.currentTimeMillis());
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
