package sw.utils;

import fun.nekomc.sw.StrengthenWeapon;
import org.bukkit.Server;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * create at 2022/8/30 12:03
 *
 * @author Chiru
 */
public class PlayerHolderTest {

    @Test
    void playerHolderTest() {
        ConsoleCommandSender mockSender = Mockito.mock(ConsoleCommandSender.class);
        Server mockServer = Mockito.mock(Server.class);
        Mockito.when(mockServer.getConsoleSender()).thenReturn(mockSender);
        Player mockPlayer1 = Mockito.mock(Player.class);
        Player mockPlayer2 = Mockito.mock(Player.class);

        try (MockedStatic<StrengthenWeapon> strengthenWeaponMockedStatic = Mockito.mockStatic(StrengthenWeapon.class)) {
            strengthenWeaponMockedStatic.when(StrengthenWeapon::server).thenReturn(mockServer);

            assertEquals(mockSender, PlayerHolder.getSender(), "未设置 Player 时，取默认 CommandSender");
            PlayerHolder.setPlayer(mockPlayer1);
            assertEquals(mockPlayer1, PlayerHolder.getSender(), "设置 Player 时，取设置的 Player");
            PlayerHolder.setPlayer(mockPlayer2);
            assertEquals(mockPlayer2, PlayerHolder.getSender(), "重复设置 Player 时，覆盖");
            PlayerHolder.release();
            assertEquals(mockSender, PlayerHolder.getSender(), "释放后，和没设置一样");
        }
    }
}
