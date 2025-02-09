package de.fanta.cubeside.data;

import com.google.gson.JsonParseException;
import de.fanta.cubeside.CubesideClientFabric;
import de.fanta.cubeside.config.Configs;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.text.Text;
import org.dizitart.no2.Nitrite;
import org.dizitart.no2.collection.FindOptions;
import org.dizitart.no2.common.SortOrder;
import org.dizitart.no2.common.mapper.JacksonMapperModule;
import org.dizitart.no2.filters.FluentFilter;
import org.dizitart.no2.mvstore.MVStoreModule;
import org.dizitart.no2.repository.ObjectRepository;

public class ChatDatabase {
    private static Nitrite database;
    private static ObjectRepository<ChatRepo> chatRepo;
    private static ObjectRepository<CommandRepo> commandRepo;
    private final String server;
    private int currentMessageId = 0;
    private int currentCommandId = 0;
    private ChatRepo lastEntry;

    public ChatDatabase(String server) {
        long time = System.nanoTime();
        this.server = server;
        MVStoreModule storeModule = MVStoreModule.withConfig().filePath(new File(CubesideClientFabric.getConfigDirectory(), "/chatStorage/" + server.toLowerCase() + "_1_" + ".db")).compress(true).build();
        database = Nitrite.builder().loadModule(storeModule).loadModule(new JacksonMapperModule()).openOrCreate();

        chatRepo = database.getRepository(ChatRepo.class);
        commandRepo = database.getRepository(CommandRepo.class);

        long delta = System.nanoTime() - time;
        CubesideClientFabric.LOGGER.info("Init chatdatabase for " + server + " in " + (delta / 1000) + " micros");

        deleteOldMessages(Configs.Chat.DaysTheMessagesAreStored.getIntegerValue());
        deleteOldCommands(Configs.Chat.DaysTheMessagesAreStored.getIntegerValue());

        time = System.nanoTime();

        List<ChatRepo> chatRepos = chatRepo.find(FindOptions.orderBy("id", SortOrder.Ascending).limit(1)).toList(); // ??? Descending should be correct
        if (chatRepos != null && !chatRepos.isEmpty()) {
            currentMessageId = chatRepos.getLast().getMessageID() + 1;
        }

        List<CommandRepo> commandRepos = commandRepo.find(FindOptions.orderBy("id", SortOrder.Ascending).limit(1)).toList(); // ??? Descending should be correct
        if (commandRepos != null && !commandRepos.isEmpty()) {
            currentCommandId = commandRepos.getLast().getCommandID() + 1;
        }

        delta = System.nanoTime() - time;
        CubesideClientFabric.LOGGER.info("Get last messageid (" + currentMessageId + "," + currentCommandId + ") in " + (delta / 1000) + " micros");
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
        long time = System.nanoTime();
        List<Text> entries = new ArrayList<>();
        for (ChatRepo entry : chatRepo.find(FindOptions.orderBy("id", SortOrder.Descending))) {
            try {
                entries.add(Text.Serialization.fromJson(entry.getMessage(), manager));
            } catch (JsonParseException ignore) {
            }
        }
        long delta = System.nanoTime() - time;
        CubesideClientFabric.LOGGER.info(entries.size() + " messages were loaded in " + (delta / 1000) + " micros");
        return entries;
    }

    public List<String> loadCommands() {
        long time = System.nanoTime();
        List<String> entries = new ArrayList<>();
        for (CommandRepo entry : commandRepo.find(FindOptions.orderBy("id", SortOrder.Descending))) {
            entries.add(entry.getCommand());
        }
        long delta = System.nanoTime() - time;
        CubesideClientFabric.LOGGER.info(entries.size() + " commands were loaded in " + (delta / 1000) + " micros");
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
        long time = System.nanoTime();
        int count = chatRepo.remove(FluentFilter.where("timestamp").lt(System.currentTimeMillis() - days * 24 * 60 * 60 * 1000L)).getAffectedCount();
        database.commit();
        long delta = System.nanoTime() - time;
        CubesideClientFabric.LOGGER.info(count + " commands were deleted from server " + server + " in " + (delta / 1000) + " micros");
    }

    private void deleteOldCommands(int days) {
        long time = System.nanoTime();
        int count = commandRepo.remove(FluentFilter.where("timestamp").lt(System.currentTimeMillis() - days * 24 * 60 * 60 * 1000L)).getAffectedCount();
        database.commit();
        long delta = System.nanoTime() - time;
        CubesideClientFabric.LOGGER.info(count + " messages were deleted from server " + server + " in " + (delta / 1000) + " micros");
    }

    public String getServer() {
        return server;
    }
}
