package studio.magemonkey.fabled.gui.customization.tools.instances;

import lombok.Getter;
import org.bukkit.inventory.ItemStack;
import studio.magemonkey.fabled.api.player.PlayerAccounts;

@Getter
public class AccountInstance extends CustomInstance {

    private final PlayerAccounts account;

    public AccountInstance(ItemStack icon, PlayerAccounts account) {
        super(icon);
        this.account = account;
    }
}
