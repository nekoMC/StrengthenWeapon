package sw.utils;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

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
        Player mockPlayer = Mockito.mock(Player.class);
        World mockWorld = Mockito.mock(World.class);
        PlayerInventory mockInventory = Mockito.mock(PlayerInventory.class);
        ItemStack mockItem = Mockito.mock(ItemStack.class);
        Location mockLocation = Mockito.mock(Location.class);
        HashMap<Integer, ItemStack> oneItemMap = new HashMap<>(1);
        oneItemMap.put(0, mockItem);
        // Stub
        Mockito.when(mockPlayer.getWorld()).thenReturn(mockWorld);
        Mockito.when(mockPlayer.getLocation()).thenReturn(mockLocation);
        Mockito.when(mockPlayer.getInventory()).thenReturn(mockInventory);
        Mockito.when(mockInventory.addItem(mockItem)).thenReturn(new HashMap<>());

        // 背包有空间时，不掉落
        PlayerBagUtils.givePlayerItem(mockPlayer, mockItem);
        Mockito.verify(mockWorld, Mockito.times(0)).dropItem(mockLocation, mockItem);
        Mockito.verify(mockInventory, Mockito.times(1)).addItem(mockItem);

        // 背包没空间时，掉落
        Mockito.when(mockInventory.addItem(mockItem)).thenReturn(oneItemMap);
        PlayerBagUtils.givePlayerItem(mockPlayer, mockItem);
        Mockito.verify(mockWorld, Mockito.times(1)).dropItem(mockLocation, mockItem);
        Mockito.verify(mockInventory, Mockito.times(2)).addItem(mockItem);
    }
}
