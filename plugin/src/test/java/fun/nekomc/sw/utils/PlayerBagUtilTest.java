package fun.nekomc.sw.utils;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.mockito.Mockito.*;

/**
 * create at 2022/8/30 12:03
 *
 * @author Chiru
 */
public class PlayerBagUtilTest {

    @Test
    void playerBagTest() {
        // Mock
        Player mockPlayer = mock(Player.class);
        World mockWorld = mock(World.class);
        PlayerInventory mockInventory = mock(PlayerInventory.class);
        ItemStack mockItem = mock(ItemStack.class);
        Location mockLocation = mock(Location.class);
        HashMap<Integer, ItemStack> oneItemMap = new HashMap<>(1);
        oneItemMap.put(0, mockItem);
        // Stub
        when(mockPlayer.getWorld()).thenReturn(mockWorld);
        when(mockPlayer.getLocation()).thenReturn(mockLocation);
        when(mockPlayer.getInventory()).thenReturn(mockInventory);
        when(mockInventory.addItem(mockItem)).thenReturn(new HashMap<>());

        // 背包有空间时，不掉落
        PlayerBagUtils.givePlayerItem(mockPlayer, mockItem);
        verify(mockWorld, times(0)).dropItem(mockLocation, mockItem);
        verify(mockInventory, times(1)).addItem(mockItem);

        // 背包没空间时，掉落
        when(mockInventory.addItem(mockItem)).thenReturn(oneItemMap);
        PlayerBagUtils.givePlayerItem(mockPlayer, mockItem);
        verify(mockWorld, times(1)).dropItem(mockLocation, mockItem);
        verify(mockInventory, times(2)).addItem(mockItem);
    }
}
