package com.falamoore.plugin.conversations;

import java.sql.ResultSet;
import java.util.ArrayList;

import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.ValidatingPrompt;
import org.bukkit.entity.Player;

import com.falamoore.plugin.Main;

public class RacePrompt extends ValidatingPrompt {

    ArrayList<String> acceptable = new ArrayList<String>();

    @Override
    public String getPromptText(ConversationContext cc) {
        final StringBuilder sb = new StringBuilder();
        sb.append("Welcome to Falamoore!\nSelect your race: ");
        final int dw = getDwarfCount();
        final int el = getElfCount();
        final int hu = (getHumanCount() + 2);
        if (dw <= hu) {
            sb.append("Dwarf, ");
            acceptable.add("dwarf");
        }
        if (el <= hu) {
            sb.append("Elf, ");
            acceptable.add("elf");
        }
        sb.append("Human");
        acceptable.add("human");
        return sb.toString();
    }

    @Override
    protected Prompt acceptValidatedInput(ConversationContext cc, String in) {
        setRace((Player) cc.getForWhom(), in.toLowerCase());
        ((Player) cc.getForWhom()).sendMessage("You are now a " + in.toLowerCase());
        return END_OF_CONVERSATION;
    }

    @Override
    protected boolean isInputValid(ConversationContext cc, String in) {
        return acceptable.contains(in.toLowerCase());
    }
    
    @Override
    protected String getFailedValidationText(ConversationContext context, String invalidInput) {
        return "You entered a invalid option!";
    }

    private void setRace(Player s, String race) {
        try {
            Main.mysql.query("INSERT INTO playerinfo (Name, Race, LastIP, Rank) VALUES ('" + s.getName() + "', '" + race + "', '" + s.getAddress().getAddress().getHostAddress() + "', 'TRAVELER')");
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    private int getElfCount() {
        try {
            final ResultSet rs = Main.mysql.query("SELECT COUNT(*) FROM playerinfo WHERE Race='elf'");
            rs.next();
            final int i = rs.getInt(1);
            rs.close();
            return i;
        } catch (final Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    private int getDwarfCount() {
        try {
            final ResultSet rs = Main.mysql.query("SELECT COUNT(*) FROM playerinfo WHERE Race='dwarf'");
            rs.next();
            final int i = rs.getInt(1);
            rs.close();
            return i;
        } catch (final Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    private int getHumanCount() {
        try {
            final ResultSet rs = Main.mysql.query("SELECT COUNT(*) FROM playerinfo WHERE Race='human'");
            rs.next();
            final int i = rs.getInt(1);
            rs.close();
            return i;
        } catch (final Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
}
