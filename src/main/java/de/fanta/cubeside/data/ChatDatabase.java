package de.fanta.cubeside.data;

import com.google.gson.JsonParseException;
import de.fanta.cubeside.CubesideClientFabric;
import de.fanta.cubeside.config.Configs;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.text.Text;
import org.dizitart.no2.Nitrite;
import org.dizitart.no2.collection.FindOptions;
import org.dizitart.no2.common.SortOrder;
import org.dizitart.no2.common.mapper.JacksonMapperModule;
import org.dizitart.no2.mvstore.MVStoreModule;
import org.dizitart.no2.repository.Cursor;
import org.dizitart.no2.repository.ObjectRepository;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ChatDatabase {
    private static Nitrite database;
    private static ObjectRepository<ChatRepo> chatRepo;
    private static ObjectRepository<CommandRepo> commandRepo;
    private final String server;
    private int currentMessageId = 0;
    private int currentCommandId = 0;
    private ChatRepo lastEntry;

    public ChatDatabase(String server) {
        this.server = server;
        MVStoreModule storeModule = MVStoreModule.withConfig().filePath(new File(CubesideClientFabric.getConfigDirectory(), "/chatStorage/" + server.toLowerCase() + ".db")).compress(true).build();
        database = Nitrite.builder().loadModule(storeModule).loadModule(new JacksonMapperModule()).openOrCreate();

        chatRepo = database.getRepository(ChatRepo.class);
        commandRepo = database.getRepository(CommandRepo.class);

        deleteOldMessages(Configs.Chat.DaysTheMessagesAreStored.getIntegerValue());
        deleteOldCommands(Configs.Chat.DaysTheMessagesAreStored.getIntegerValue());

        List<ChatRepo> chatRepos = chatRepo.find(FindOptions.orderBy("id", SortOrder.Descending)).toList();
        if (chatRepos != null && !chatRepos.isEmpty()) {
            currentMessageId = chatRepos.getLast().getMessageID() + 1;
        }

        List<CommandRepo> commandRepos = commandRepo.find(FindOptions.orderBy("id", SortOrder.Descending)).toList();
        if (commandRepos != null && !commandRepos.isEmpty()) {
            currentCommandId = commandRepos.getLast().getCommandID() + 1;
        }
    }

    public void addMessageEntry(String message) {
        int id = this.currentMessageId++;
        ChatRepo entry = new ChatRepo(id, message, System.currentTimeMillis());
        lastEntry = entry;
        chatRepo.insert(entry);
        database.commit();
    }

    public void addCommandEntry(String command) {
        int id = this.currentCommandId++;
        CommandRepo entry = new CommandRepo(id, command, System.currentTimeMillis());
        commandRepo.insert(entry);
        database.commit();
    }

    public void close() {
        if (database != null) {
            database.close();
        }
    }

    public List<Text> loadMessages(DynamicRegistryManager manager) {
        List<Text> entries = new ArrayList<>();
        for (ChatRepo entry : chatRepo.find(FindOptions.orderBy("id", SortOrder.Descending))) {
            try {
                entries.add(Text.Serialization.fromJson(entry.getMessage(), manager));
            } catch (JsonParseException ignore) {
            }
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

    public void deleteNewestMessage() {
        if (lastEntry != null) {
            chatRepo.remove(lastEntry);
            currentMessageId = lastEntry.getMessageID();
            database.commit();
        }
    }

    private void deleteOldMessages(int days) {
        int count = 0;
        Cursor<ChatRepo> cursor = chatRepo.find();
        for (ChatRepo entry : cursor) {
            if (entry.getTimestamp() <= System.currentTimeMillis() - days * 24 * 60 * 60 * 1000L) {
                chatRepo.remove(entry);
                count++;
            }
        }
        database.commit();
        CubesideClientFabric.LOGGER.info(count + " commands were deleted from server " + server);
    }

    private void deleteOldCommands(int days) {
        int count = 0;
        Cursor<CommandRepo> cursor = commandRepo.find();
        for (CommandRepo entry : cursor) {
            if (entry.getTimestamp() <= System.currentTimeMillis() - days * 24 * 60 * 60 * 1000L) {
                commandRepo.remove(entry);
                count++;
            }
        }
        database.commit();
        CubesideClientFabric.LOGGER.info(count + " messages were deleted from server " + server);
    }

    public String getServer() {
        return server;
    }
}
