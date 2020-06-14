package pl.betoncraft.betonquest.api;

import org.bukkit.Bukkit;
import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.exceptions.QuestRuntimeException;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

abstract public class ForceSyncHandler<T> {
    private final boolean forceSync;

    public ForceSyncHandler(final boolean forceSync) {
        this.forceSync = forceSync;
    }

    protected abstract T execute(String playerID) throws QuestRuntimeException;

    public T handle(String playerID) throws QuestRuntimeException {
        if(forceSync) {
            Future<T> returnFuture = Bukkit.getScheduler().callSyncMethod(BetonQuest.getInstance(), () -> execute(playerID));
            try {
                return returnFuture.get();
            } catch (InterruptedException e) {
                throw new QuestRuntimeException("Thread was Interrupted!");
            } catch (ExecutionException e) {
                if (e.getCause() instanceof QuestRuntimeException) {
                    throw (QuestRuntimeException) e.getCause();
                }
                throw new QuestRuntimeException(e);
            }
        }
        else {
            return execute(playerID);
        }
    }
}
