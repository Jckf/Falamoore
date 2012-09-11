package com.falamoore.plugin.conversations;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.ValidatingPrompt;
import org.bukkit.entity.Player;

import com.falamoore.plugin.Main;

public class WarpPrompt extends ValidatingPrompt {

    @Override
    public String getPromptText(ConversationContext arg0) {
        return "Select destination: Redcrest, Ermiron, Karaz Ankor";
    }
    
    @Override
    public boolean blocksForInput(ConversationContext context) {
        return true;
    }

    @Override
    protected Prompt acceptValidatedInput(ConversationContext cc, String in) {
        Player tpme = ((Player) cc.getForWhom());
        String[] l = Main.warps.get(in).split(",");
        Location loc = new Location(Bukkit.getWorld(l[0]), 25, 100, 25);
        tpme.teleport(loc);
        tpme.sendMessage("You traveled to " + in);
        return END_OF_CONVERSATION;
    }

    @Override
    protected boolean isInputValid(ConversationContext arg0, String in) {
        if (in.equalsIgnoreCase("Redcrest") || in.equalsIgnoreCase("Ermiron") || in.equalsIgnoreCase("Karaz Ankor")) { return true; }
        return false;
    }
    
    @Override
    protected String getFailedValidationText(ConversationContext context, String invalidInput) {
        return "You entered a invalid option!";
    }

}
