package com.falamoore.plugin.conversations;

import org.bukkit.Location;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.ValidatingPrompt;
import org.bukkit.entity.Player;

import com.falamoore.plugin.serializable.SerialWarp;

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
        final Player tpme = ((Player) cc.getForWhom());
        final Location loc = SerialWarp.getLocation(in);
        tpme.teleport(loc);
        tpme.sendMessage("You traveled to " + in);
        return END_OF_CONVERSATION;
    }

    @Override
    protected boolean isInputValid(ConversationContext arg0, String in) {
        switch (in.toLowerCase()) {
            case "redcrest":
            case "ermiron":
            case "karaz ankor":
                return true;
        }
        return false;
    }

    @Override
    protected String getFailedValidationText(ConversationContext context, String invalidInput) {
        return "You entered an invalid option!";
    }
}
