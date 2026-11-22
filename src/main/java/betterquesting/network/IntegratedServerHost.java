package betterquesting.network;

import java.util.function.BooleanSupplier;

import net.minecraft.client.Minecraft;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * Tells client-side sync handlers whether this client is hosting the integrated server (singleplayer or Open to LAN).
 * <p>
 * The logical client and the integrated server run in the same JVM, so they share every BetterQuesting database
 * singleton ({@code QuestDatabase.INSTANCE}, {@code PartyManager.INSTANCE}, ...). On the host, sync packets from the
 * server describe data the server has already written into those same objects. Applying them again on the client is
 * at best a no-op. At worst it destroys server state: a full sync clears the shared database and rebuilds it from a
 * payload that only carries the host's own progress, and a delayed snapshot overwrites newer values. With other
 * players connected over LAN, that wipes or rolls back their quest progress and parties.
 * <p>
 * Handlers that copy server snapshots into a database must skip the copy when this returns {@code true}, and only
 * notify the GUI that data changed.
 */
@SideOnly(Side.CLIENT)
public final class IntegratedServerHost {

    private static final BooleanSupplier DEFAULT_CHECK = () -> Minecraft.getMinecraft()
        .isIntegratedServerRunning();

    private static BooleanSupplier check = DEFAULT_CHECK;

    private IntegratedServerHost() {}

    /**
     * @return {@code true} if this client is running the integrated server and therefore shares the server's
     *         databases.
     */
    public static boolean isHost() {
        return check.getAsBoolean();
    }

    /** Test hook. Pass {@code null} to restore the default check. */
    public static void setCheckForTesting(BooleanSupplier testCheck) {
        check = testCheck == null ? DEFAULT_CHECK : testCheck;
    }
}
