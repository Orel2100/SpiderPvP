package particles;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Particlecommand implements CommandExecutor {

    private final ParticleEffectManager particleManager;

    public Particlecommand(ParticleEffectManager particleManager) {
        this.particleManager = particleManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command!");
            return true;
        }
        Player player = (Player) sender;
        if (cmd.getName().equalsIgnoreCase("disableparticles")) {
            particleManager.disableParticlesForPlayer(player);
            player.sendMessage("Particles have been disabled for you!");
            return true;
        }
        return false;
    }
}



